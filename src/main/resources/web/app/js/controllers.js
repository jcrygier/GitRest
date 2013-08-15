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
    .controller('CloneController', function($scope, $routeParams, MainResource, RepositoryResource) {
        $scope.repositoryUrl = $routeParams.url;
        $scope.repositoryName = $routeParams.repositoryName;
        $scope.cloneDirectory = $routeParams.cloneDirectory;
        $scope.browseFiles = function() {
            $('#directorySelectionModal').modal({
                backdrop: true
            });

            $("#directoryChooser").dynatree({
                debugLevel: 1,
                children: [
                    { title: 'Computer', isFolder: true, isLazy: true, key: "/" }
                ],
                onLazyRead: function(node){
                    node.appendAjax( {
                        url: window.gitRestResourceBaseUrl + 'main/directoryBrowse',
                        data: {
                            "parent": node.data.key,
                            "selected": $scope.cloneDirectory
                        },
                        dataType: "jsonp",
                        cache: false // Append random '_' argument to url to prevent caching.
                    });
                },
            });
        }

        $scope.selectNode = function() {
            $scope.cloneDirectory = $("#directoryChooser").dynatree("getActiveNode").data.key;
            $('#directorySelectionModal').modal('hide')
        }

        $scope.clone = function() {
            console.log("Cloning url: " + $scope.repositoryUrl + "\nto: " + $scope.cloneDirectory + "\nWith Alias: " + $scope.repositoryName);
            RepositoryResource.cloneRepository($scope.repositoryName, $scope.repositoryUrl, $scope.cloneDirectory, function(data) {
                if (data.status == "ok") {
                    alert("Repository Successfully Cloned");
                    // TODO: Change Views
                } else {
                    alert("Repository Cloning had issues");
                    console.log(data);
                }
            }, function(data) {
                alert("Repository Cloning had issues");
                console.log(data);
            })
        }

        $scope.$watch('repositoryName + cloneDirectory', function() {
            if ($scope.repositoryName != null)
                $scope.actualClonedDirectory = $scope.cloneDirectory + "\\" + $scope.repositoryName.replace(".git", "").replace("/", "\\");
            else
                $scope.actualClonedDirectory = $scope.cloneDirectory;
        });

        MainResource.getConfiguration(function(config) {
            if ($scope.cloneDirectory == null)
                $scope.cloneDirectory = config.RepositoryDefaultDirectory;
            if (config.RepositoryAutoCloneToDefault != null && config.RepositoryAutoCloneToDefault.toUpperCase() == "TRUE" && $scope.repositoryUrl != null)
                $scope.clone();
        });
    })

    .controller("StatusController", function($scope, $routeParams, RepositoryResource) {
        $scope.repositoryName = $routeParams.repositoryName;

        var addChild = function(parentNode, statusObj) {
            for (var i = 0; i < statusObj.childStatus.length; i++) {
                var childStatus = statusObj.childStatus[i];
                var icon = childStatus.statusType + ".gif";

                var childNode = parentNode.addChild({
                    title: childStatus.fileName,
                    tooltip: "This child node was added programmatically.",
                    isFolder: childStatus.length > 0,
                    icon: icon
                });

                addChild(childNode, childStatus);
            }
        }

        var buildTree = function(statusObj) {
            if (statusObj != null) {
                console.log("Refreshing tree with status", statusObj);
                var rootNode = $("#statusTree").dynatree("getRoot");
                rootNode.removeChildren();
                addChild(rootNode, statusObj);
            }
        }

        $scope.renderTree = function() {
            $("#statusTree").dynatree();
            buildTree($scope.status);
            $scope.$watch('status', buildTree);
        }

        RepositoryResource.status($scope.repositoryName, function(data) {
            if (data.status == "ok") {
                $scope.status = data.gitStatus;
            } else {
                console.log("Error getting status");
                console.log(data);
                alert("Sorry, an error has occurred getting the status");
            }
        });
    });