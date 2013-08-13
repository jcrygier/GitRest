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
package com.crygier.git.rest.util

import com.crygier.git.rest.model.StatusType
import org.eclipse.jgit.api.Status

/**
 * Utility to convert JGit Status object to a tree (represented by a Map of Maps)
 *
 * @author John Crygier
 */
class StatusConversionUtil {

    public static final com.crygier.git.rest.model.Status convertStatusToTree(Status status) {
        com.crygier.git.rest.model.Status answer = new com.crygier.git.rest.model.Status();
        answer.setFileName("/")

        if (status.isClean())
            answer.setStatusType(StatusType.Clean)
        else
            answer.setStatusType(StatusType.Dirty)

        status.getUntracked().each {
            addFileToStatus(splitFileByDirectories(it), answer, StatusType.Untracked);
        }

        status.getModified().each {
            addFileToStatus(splitFileByDirectories(it), answer, StatusType.Modified);
        }

        return answer;
    }

    protected static final List<String> splitFileByDirectories(String fileName) {
        fileName.split("/").reverse();
    }

    protected static final addFileToStatus(List<String> path, com.crygier.git.rest.model.Status currentStatus, StatusType type) {
        if (path) {
            String nextPath = path.pop()
            com.crygier.git.rest.model.Status foundStatus = currentStatus.getChildStatus().find { it.getFileName() == nextPath }

            if (foundStatus) {
                addFileToStatus(path, foundStatus, type);
            } else {
                com.crygier.git.rest.model.Status nextStatus = new com.crygier.git.rest.model.Status();
                nextStatus.setFileName(nextPath)
                nextStatus.setStatusType(type);
                currentStatus.getChildStatus() << nextStatus;

                addFileToStatus(path, nextStatus, type);
            }
        }
    }
}
