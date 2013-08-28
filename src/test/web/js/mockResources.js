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
    .factory('MainResource', function ($q) {
        return {
            getConfiguration: function(successCallback, failureCallback) {
                var deferred = $q.defer();

                deferred.resolve({
                    BaseUri: "http://localhost",
                    WebAppLocation: "c:/webapp",
                    RepositoryDefaultDirectory: "c:\\vcs",
                    RepositoryAutoCloneToDefault: "false"
                })

                return deferred.promise;
            }
        }
    })

    .factory('RepositoryResource', function($q) {
        return {
            cloneRepository: function(repositoryName, url, directory, successCallback, errorCallback) {
                var deferred = $q.defer();

                deferred.resolve({
                    status: "ok",
                    repositoryName: repositoryName
                })

                return deferred.promise;
            },

            status: function(repositoryName, successCallback) {
                var deferred = $q.defer();

                deferred.resolve({
                    status: "ok",
                    gitStatus: {

                    }
                });

                return deferred.promise;
            }
        }
    });