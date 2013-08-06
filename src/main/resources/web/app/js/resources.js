angular.module('gitRest.resources', ['ngResource'])
    .factory('MainResource', function ($http) {
        return {
            getConfiguration: function(successCallback, failureCallback) {
                $http.jsonp(window.gitRestResourceBaseUrl + 'main/configuration?callback=JSON_CALLBACK')
                     .success(successCallback);
            }
        }
    });