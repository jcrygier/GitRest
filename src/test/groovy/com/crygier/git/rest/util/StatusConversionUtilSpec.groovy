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
import spock.lang.Specification

/**
 * Specification for StatusConversionUtil
 */
class StatusConversionUtilSpec extends Specification {

    def "convert simple"() {
        when:
        Status s = Mock(Status)
        s.getUntracked() >> ["untrackedFile.txt"]

        def result = StatusConversionUtil.convertStatusToTree(s);

        then:
        result.getStatusType() == StatusType.Dirty
        result.getFileName() == "/"
        result.getChildStatus().size() == 1
        result.getChildStatus()[0].getFileName() == "untrackedFile.txt"
        result.getChildStatus()[0].getStatusType() == StatusType.Untracked
    }

    def "convert hierarchy"() {
        when:
        Status s = Mock(Status)
        s.getUntracked() >> ["untrackedFolder/untrackedFile.txt"]

        def result = StatusConversionUtil.convertStatusToTree(s);

        then:
        result.getStatusType() == StatusType.Dirty
        result.getFileName() == "/"
        result.getChildStatus().size() == 1
        result.getChildStatus()[0].getFileName() == "untrackedFolder"
        result.getChildStatus()[0].getChildStatus().size() == 1
        result.getChildStatus()[0].getChildStatus()[0].getFileName() == "untrackedFile.txt"
        result.getChildStatus()[0].getStatusType() == StatusType.Untracked
    }
}
