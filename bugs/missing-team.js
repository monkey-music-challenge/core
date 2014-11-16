var mmc = require('monkey-music');
var level = {
  "turns": 50,
  "layout": [
    "U  _  ",
    "pM-## ",
    "###a- ",
    "  _# s",
    "-# _ #",
    "M#s# p"
  ],
  "units": {
    "M": "monkey",
    "s": "song",
    "a": "album",
    "p": "playlist",
    "U": "user",
    "#": "wall",
    " ": "empty",
    "_": "empty",
    "-": "empty"
  }
};

var commands = [];

var state = mmc.createGameState(['glenn', 'ada'], level);
commands.push(mmc.parseCommand({
  command: 'move',
  direction: 'left',
  team: 'glenn'
}));
commands.push(mmc.parseCommand({
  command: 'move',
  direction: 'left',
  team: 'ada'
}));
mmc.runCommands(state, commands);