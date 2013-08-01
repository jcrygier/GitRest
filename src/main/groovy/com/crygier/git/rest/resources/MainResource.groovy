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
import groovy.json.StringEscapeUtils
import org.glassfish.grizzly.http.server.StaticHttpHandler

import javax.ws.rs.GET
import javax.ws.rs.Path

/**
 * Resources that don't have to do with Git, but with the application as a whole.
 */
@Path("main")
class MainResource {

    @GET @Path("/templates.js")
    public String getTemplates() {
        StaticHttpHandler staticHandler = Main.INSTANCE.getHttpServer().getServerConfiguration().getHttpHandlers().find {
            it.getKey() instanceof StaticHttpHandler
        }.getKey() as StaticHttpHandler
        File docRoot = staticHandler.getDefaultDocRoot();
        File partialsRoot = new File(docRoot, "partials")

        StringBuilder sb = new StringBuilder("""
            'use strict';

            /* Templates */

            angular.module('gitRest.templates', []).
                run(['\$templateCache', function(\$templateCache) {\n""");

        partialsRoot.eachFileMatch(~/.*\.html/) {
            sb.append("\t\t\t\t\t\$templateCache.put('partials/${it.getName()}', '${StringEscapeUtils.escapeJavaScript(it.text)}');\n")
        }

        sb.append("\t\t}]);")

        return sb.toString();
    }

}
