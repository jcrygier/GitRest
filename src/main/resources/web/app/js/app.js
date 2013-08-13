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


// Declare app level module which depends on filters, and services
angular.module('myApp', ['myApp.filters', 'myApp.services', 'gitRest.directives', 'gitRest.controllers', 'gitRest.templates', 'gitRest.resources']).
  config(['$routeProvider', '$httpProvider', function($routeProvider, $httpProvider) {
    $routeProvider.when('/status', {templateUrl: 'partials/status.html', controller: 'StatusController'});
    $routeProvider.when('/clone', {templateUrl: 'partials/cloneRepository.html', controller: 'CloneController'});
    $routeProvider.otherwise({redirectTo: '/clone'});
  }]);
