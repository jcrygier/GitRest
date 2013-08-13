'use strict';

/* jasmine specs for directives go here */

describe('directives', function() {
  beforeEach(module('gitRest.directives'));

  describe('lazy-style', function() {
    it('should load style', function() {
      inject(function($compile, $rootScope) {
        var element = $compile('<lazy-style href="lib/dynatree/css/ui.dynatree.css"/>')($rootScope);
        //expect(document.getElementsByTagName("link")[0].innerHTML).toEqual('TEST_VER');
      });
    });
  });
});
