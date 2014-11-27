var mm = require('..');

var path = require('path');

var recursive = require('recursive-readdir');

suite('parsing', function() {
  test('all levels', function(done) {
    recursive('levels', function(err, levelFiles) {
      levelFiles.forEach(function(levelFile) {
        mm.createGameState(['1', '2'], require(path.resolve(levelFile)));
      });
      done();
    });
  });
});
