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

import org.glassfish.jersey.server.JSONP;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.HashMap;
import java.util.Map;

/**
 * Resources related to dealing with files in Git.  Operations include:
 * <ul>
 *     <li>Status</li>
 *     <li>Commit</li>
 *     <li>Add</li>
 *     <li>Diff</li>
 *     <li>Reset</li>
 *     <li>Rm</li>
 *     <li>Mv</li>
 *     <li>Stash</li>
 * </ul>
 *
 * @author John Crygier
 */
@Path("file")
public class FileResource {


}
