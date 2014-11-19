var mmc = require('../');
var level = {
  "turns": 50,
  "layout": [
    "U  MM ",
    "p -## ",
    "###a- ",
    "  _# s",
    "-# _ #",
    " #s# p"
  ],
  "legend": {
    "M": "monkey",
    "s": "song",
    "a": "album",
    "p": "playlist",
    "U": "user",
    "#": "wall",
    " ": "empty",
    "_": "empty",
    "-": "empty"
  },
  "inventorySize": 3
};

var commands = [];

var state = mmc.createGameState(['glenn', 'ada'], level);
mmc.gameStateForTeam(state, 'glenn');

commands.push(mmc.parseCommand(state, {
  command: 'move',
  direction: 'right',
  team: 'glenn'
}));
commands.push(mmc.parseCommand(state, {
  command: 'move',
  direction: 'left',
  team: 'ada'
}));
state = mmc.runCommands(state, commands);
