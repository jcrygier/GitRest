'use strict';

/* jasmine specs for controllers go here */

describe('controllers', function(){
  beforeEach(module('gitRest.controllers'));


  it('should StatusController', inject(function(StatusController) {
    expect(StatusController).toEqual("hi");
  }));

  it('should ....', inject(function() {
    //spec body
  }));
});
