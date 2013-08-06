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
'use strict';

/* Controllers */

angular.module('gitRest.controllers', ['gitRest.resources'])
    .controller('CloneController', function($scope, $routeParams, MainResource) {
        $scope.repositoryUrl = $routeParams.url;
        $scope.repositoryName = $routeParams.repositoryName;
        $scope.cloneDirectory = $routeParams.cloneDirectory;
        $scope.browseFiles = function() {
            alert("Unimplemented, sorry!");
        }

        $scope.clone = function() {
            console.log("Cloning url: " + $scope.repositoryUrl + "\nto: " + $scope.cloneDirectory + "\nWith Alias: " + $scope.repositoryName);
        }

        MainResource.getConfiguration(function(config) {
            if ($scope.cloneDirectory == null)
                $scope.cloneDirectory = config.RepositoryDefaultDirectory;
            if (config.RepositoryAutoCloneToDefault != null && config.RepositoryAutoCloneToDefault.toUpperCase() == "TRUE" && $scope.repositoryUrl != null)
                $scope.clone();
        });
    });