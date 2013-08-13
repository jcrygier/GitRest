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

/* Directives */


angular.module('gitRest.directives', []).
    directive('lazyStyle', function () {
        var loadedStyles = {};
        return {
            restrict: 'E',
            link: function (scope, element, attrs) {
                attrs.$observe('href', function (value) {
                    var stylePath = window.gitRestStaticBaseUrl + value;

                    if (stylePath in loadedStyles) {
                        return;
                    }

                    if (document.createStyleSheet) {
                        document.createStyleSheet(stylePath); //IE
                    } else {
                        var link = document.createElement("link");
                        link.type = "text/css";
                        link.rel = "stylesheet";
                        link.href = stylePath;
                        document.getElementsByTagName("head")[0].appendChild(link);
                    }

                    loadedStyles[stylePath] = true;
                });
            }
        };
    })

    .directive('lazyScript', function() {
        var loadedScripts = {};
        return {
            restrict: 'E',
            link: function(scope, element, attrs) {
                attrs.$observe('href', function(value) {
                    attrs.$observe('onLoad', function(onLoadValue) {
                        var scriptPath = window.gitRestStaticBaseUrl + value;

                        if (scriptPath in loadedScripts) {
                            return;
                        }

                        $script([scriptPath], function() {
                            if (onLoadValue != null && scope[onLoadValue] instanceof Function)
                                scope[onLoadValue].call();

                            loadedScripts[scriptPath] = true;
                        });
                    });
                });
            }
        }
    });
