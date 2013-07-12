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
package com.crygier.git.rest.util;

import com.crygier.git.rest.Configuration;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utilities to do common Git things.
 */
public class GitUtil {

    public static final Logger logger = Logger.getLogger(GitUtil.class.getName());

    /**
     * Get's the Git object for a given directory on the filesystem.
     *
     * @param repositoryName Name of the repository to work with
     * @return The Git object to work with
     */
    public static Git getGit(String repositoryName) {
        File gitDirectory = Configuration.StoredRepositories.getChildFileValue(repositoryName);
        return getGit(gitDirectory);
    }

    public static Git getGit(File gitDirectory) {
        if (gitDirectory == null)
            return null;

        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        try {
            Repository repository = builder.setGitDir(new File(gitDirectory, ".git"))
                    .readEnvironment() // scan environment GIT_* variables
                    .findGitDir() // scan up the file system tree
                    .build();
            return new Git(repository);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Unable to open git directory", e);
            return null;
        }
    }

    /**
     * A common way to execute commands on the Git object, dealing with error handling in a consistent way.
     *
     * @param repositoryName Name of the registered repository to work with
     * @param gitCallback Callback to use the git object
     * @param <T> Return type of the callback - matches the return type of this method
     * @return Whatever gitCallback returns, unless a GitAPIException is thrown, then null
     */
    public static <T> T doWithGit(String repositoryName, GitCallback<T> gitCallback) {
        Git git = getGit(repositoryName);
        if (git != null)
            try {
                return gitCallback.doWitGit(git);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error running git command for git directory: " + git.getRepository().getDirectory().getAbsolutePath(), e);
                return null;
            }

        return null;
    }

}
