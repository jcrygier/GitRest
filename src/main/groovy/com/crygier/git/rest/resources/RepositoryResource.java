/**
 * Copyright 2013 John Crygier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.crygier.git.rest.resources;

import com.crygier.git.rest.Configuration;
import com.crygier.git.rest.util.GitCallback;
import com.crygier.git.rest.util.GitUtil;
import com.crygier.git.rest.util.StatusConversionUtil;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.eclipse.jgit.transport.URIish;
import org.glassfish.jersey.server.JSONP;

import javax.ws.rs.*;
import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Resources related to dealing with repositories in Git.  Operations include:
 * <ul>
 *     <li>Register (Non-Git Command)</li>
 *     <li>Clone</li>
 *     <li>Status</li>
 * </ul>
 *
 * @author John Crygier
 */
@Path("repository")
public class RepositoryResource {

    public static final Logger logger = Logger.getLogger(RepositoryResource.class.getName());

    /**
     * Registers a local repository for later use.  This is required to simplify all other calls (Except Clone) that deal
     * with the repository, and keeps the REST-ful calls a bit more simple (so you don't have to pass the directory around).
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET @Path("/{repositoryName}/register")
    @JSONP(queryParam = "callback")
    @Produces({ "application/javascript" })
    public Map<String, Object> registerLocalRepository(@PathParam("repositoryName") String repositoryName, @QueryParam("directory") File directory) {
        Map<String, Object> status = new HashMap<String, Object>();

        if (directory == null) {
            status.put("status", "error");
            status.put("message", "Query Parameter 'directory' is required");
        } else if (directory.exists() == false) {
            status.put("status", "error");
            status.put("message", directory.getAbsolutePath() + " does not exist");
        } else if (GitUtil.getGit(directory) == null) {
            status.put("status", "error");
            status.put("message", directory.getAbsolutePath() + " is not a Git directory");
        } else {
            Configuration.StoredRepositories.addFileValue(repositoryName, directory);
            status.put("status", "ok");
            status.put("repositoryName", repositoryName);
        }

        return status;
    }

    /**
     * Gets all registered repositories
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @JSONP(queryParam = "callback")
    @Produces({ "application/javascript" })
    public Map<String, Object> getAllRegisteredRepositories() {
        Map<String, Object> status = new HashMap<String, Object>();

        status.put("repositories", Configuration.StoredRepositories.listAllChildren());
        status.put("status", "ok");

        return status;
    }

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET @Path("/{repositoryName}/clone")
    @JSONP(queryParam = "callback")
    @Produces({ "application/javascript" })
    public Map<String, Object> cloneRepository(@PathParam("repositoryName") String repositoryName, @QueryParam("url") String url, @QueryParam("directory") File directory) {
        Map<String, Object> answer = new HashMap<String, Object>();

        try {
            URIish uri = new URIish(url);

            CloneCommand cloneCommand = Git.cloneRepository()
                    .setURI(url)
                    .setDirectory(new File(directory, repositoryName.replaceAll(".git", "")))
                    .setBare(false)
                    .setProgressMonitor(new TextProgressMonitor());

            Git git = cloneCommand.call();
            answer.put("status", "ok");
            git.getRepository().close();

            answer.putAll(registerLocalRepository(repositoryName, directory));
        } catch (GitAPIException e) {
            logger.log(Level.SEVERE, "Error Cloning", e);
            answer.put("errorMessage", e.getMessage());
        } catch (URISyntaxException e) {
            logger.log(Level.SEVERE, "Invalid URL", e);
            answer.put("errorMessage", e.getMessage());
        }

        return answer;
    }

    @GET @Path("/{repositoryName}/status")
    @JSONP(queryParam = "callback")
    @Produces({ "application/javascript" })
    public Map<String, Object> status(@PathParam("repositoryName") String repositoryName) {
        Map<String, Object> answer = new HashMap<String, Object>();

        Status gitStatus = GitUtil.doWithGit(repositoryName, new GitCallback<Status>() {
            @Override
            public Status doWitGit(Git git) throws Exception {
                return git.status().call();
            }
        });

        if (gitStatus != null) {
            answer.put("status", "ok");
            answer.put("gitStatus", StatusConversionUtil.convertStatusToTree(gitStatus));
        } else {
            answer.put("status", "error");
            answer.put("message", repositoryName + " is not registered.  Please call " +
                    Configuration.BaseUri.getStringValue() + "repository/" + repositoryName + "/register?directory=<directoryName>");
        }

        return answer;
    }
}
