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

import com.crygier.git.rest.util.GitCallback;
import com.crygier.git.rest.util.GitUtil;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.StatusCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.glassfish.jersey.server.JSONP;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Root resource (exposed at "myresource" path)
 * @author John Crygier
 */
@Path("repository")
public class RepositoryResource {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET @Path("/clone")
    @JSONP(queryParam = "callback")
    @Produces({ "application/javascript" })
    public Map<String, Object> cloneRepository(@QueryParam("url") String url, @QueryParam("directory" )File directory) {
        Map<String, Object> answer = new HashMap<String, Object>();

        CloneCommand cloneCommand = Git.cloneRepository()
                .setURI(url)
                .setDirectory(directory)
                .setBare(false)
                .setProgressMonitor(new TextProgressMonitor());

        try {
            Git git = cloneCommand.call();
            answer.put("status", "ok");
            git.getRepository().close();
        } catch (GitAPIException e) {
            answer.put("errorMessage", e.getMessage());
        }

        return answer;
    }

    @GET @Path("/status")
    @JSONP(queryParam = "callback")
    @Produces({ "application/javascript" })
    public Status initRepository(@QueryParam("directory") File directory) {
        return GitUtil.doWithGit(directory, new GitCallback<Status>() {
            @Override
            public Status doWitGit(Git git) throws Exception {
                return git.status().call();
            }
        });
    }
}
