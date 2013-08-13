/*
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
angular.module('gitRest.resources', ['ngResource'])
    .factory('MainResource', function ($http) {
        return {
            getConfiguration: function(successCallback, failureCallback) {
                $http.jsonp(window.gitRestResourceBaseUrl + 'main/configuration?callback=JSON_CALLBACK')
                     .success(successCallback);
            }
        }
    })

    .factory('RepositoryResource', function($http) {
        return {
            cloneRepository: function(repositoryName, url, directory, successCallback, errorCallback) {
                $http.jsonp(window.gitRestResourceBaseUrl + 'repository/' + encodeURIComponent(repositoryName) + '/clone?url=' + encodeURIComponent(url) + '&directory=' + encodeURIComponent(directory) + '&callback=JSON_CALLBACK')
                     .success(successCallback).error(errorCallback);
            },

            status: function(repositoryName, successCallback) {
                $http.jsonp(window.gitRestResourceBaseUrl + 'repository/' + encodeURIComponent(repositoryName) + '/status?callback=JSON_CALLBACK')
                     .success(successCallback);
            }
        }
    });