var mm = require('../lib/debug');

var repeat = require('mout/string/repeat');
var assertPartialMatch = require('referee').assert.match;

var fs = require('fs');
var path = require('path');

var replayFiles = fs.readdirSync('replays');

var reverseLookup = function(obj) {
  var reverseObj = {};
  Object.keys(obj).forEach(function(key) {
    reverseObj[obj[key]] = key;
  });
  return reverseObj;
};

var stringifyLayout = function(reverseLegend, layout) {
  var stringifiedRows = layout.map(function(row) {
    return '|' + row.map(function(entity) {
      return reverseLegend[entity];
    }).join('') + '|';
  });
  var separator = ' ' + repeat('-', stringifiedRows[0].length - 2) + ' ';
  return separator + '\n' + stringifiedRows.join('\n') + '\n' + separator;
};

suite('replays', function() {
  replayFiles.forEach(function(replayFile) {
    test(replayFile, function() {
      var replay = require(path.resolve(path.join('replays', replayFile)));
      var level = require(path.resolve(path.join('levels', replay.level + '.json')));
      var state = mm.createGameState(replay.teams, level);
      var reverseLegend = reverseLookup(level.legend);
      replay.turns.forEach(function(commands) {
        var parsedCommands = commands.map(function(command) {
          return mm.parseCommand(state, command);
        });
        state = mm.runCommands(state, parsedCommands);
      });
      assertPartialMatch(mm.gameStateForRenderer(state), replay.outcome);
    });
  });
});

