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
package com.crygier.git.rest.resources

import com.crygier.git.rest.Main
import com.crygier.git.rest.util.JsonPUtil
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.InitCommand
import org.glassfish.grizzly.http.server.HttpServer
import spock.lang.Shared
import spock.lang.Specification

import javax.ws.rs.client.Client
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.WebTarget

/**
 * Specification for Repository Resources
 */
class RepositoryResourceSpec extends Specification {

    @Shared private HttpServer server;
    @Shared private WebTarget target;
    private File testRepository;
    private Git testGitRepository;

    def setupSpec() {
        server = Main.startServer();
        Client c = ClientBuilder.newClient();
        target = c.target(Main.BASE_URI);
    }

    def cleanupSpec() {
        server.stop();
    }

    def setup() {
        testRepository = File.createTempFile("GitTest", ".git");
        testRepository.delete();            // Delete the file so we can create it as a directory
        testRepository.mkdirs();
        testRepository.deleteOnExit();

        // Create some dummy content
        new File(testRepository, "fileOne.txt") << "This is a simple test"
        new File(testRepository, "fileTwo.txt") << "Testing of a \r\nmultiple line \r\nFile"

        InitCommand initCommand = Git.init()
            .setBare(false)
            .setDirectory(testRepository)

        testGitRepository = initCommand.call();

        testGitRepository.add()
            .addFilepattern("fileOne.txt")
            .addFilepattern("fileTwo.txt")
            .call();

        testGitRepository.commit()
            .setAuthor("John Crygier", "none@none.com")
            .setCommitter("John Crygier", "none@none.com")
            .setMessage("Initial Commit")
            .call();
    }

    def cleanup() {
        testGitRepository.getRepository().close();
        testRepository.deleteDir();
    }

    def "clone the test repository"() {
        setup:
        File cloneToDir = File.createTempFile("GitCloned", ".git")
        cloneToDir.delete();
        cloneToDir.deleteOnExit()

        when:
        String result = target.path("repository/clone")
                .queryParam("url", "file://${testRepository.getAbsolutePath()}")
                .queryParam("directory", cloneToDir.getAbsolutePath())
                .request().get(String.class);

        then:
        result == 'callback({"status":"ok"})'
        cloneToDir.exists()
        cloneToDir.isDirectory()
        new File(cloneToDir, "fileOne.txt").text == "This is a simple test"

        cleanup:
        cloneToDir.delete()
    }

    def "status - new untracked file"() {
        setup:
        new File(testRepository, "NewFileNotAdded.txt") << "This is a new file - and i'm not adding it to git"

        when:
        String result = target.path("repository/status")
                              .queryParam("directory", testRepository.getAbsolutePath())
                              .request().get(String)
        def resultObj = JsonPUtil.parseJsonP(result, "callback");

        then:
        resultObj
        resultObj.clean == false
        resultObj.untracked.size() == 1
        resultObj.untracked[0] == "NewFileNotAdded.txt"
    }

    def "status - new untracked directory and file and touch file"() {
        setup:
        File newDir = new File(testRepository, "NewDirectory")
        newDir.mkdir();
        new File(newDir, "NewFileNotAdded.txt") << "This is a new file - and i'm not adding it to git"
        new File(testRepository, "fileOne.txt") << "changes!"

        when:
        String result = target.path("repository/status")
                .queryParam("directory", testRepository.getAbsolutePath())
                .request().get(String)
        def resultObj = JsonPUtil.parseJsonP(result, "callback");

        then:
        resultObj
        resultObj.clean == false
        resultObj.untracked.size() == 1
        resultObj.untracked[0] == "NewDirectory/NewFileNotAdded.txt"
        resultObj.modified.size() == 1
        resultObj.modified[0] == "fileOne.txt"
        resultObj.untrackedFolders.size() == 1
        resultObj.untrackedFolders[0] == "NewDirectory"
    }


}
