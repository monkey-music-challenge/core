var fs = require('fs');
var path = require('path');

module.exports = fs.readdirSync(__dirname).filter(function(file) {
  return file !== path.basename(__filename);
}).reduce(function(index, levelFolder) {
  index[levelFolder] = fs.readdirSync(path.join(__dirname, levelFolder)).map(function(levelFile) {
    return path.basename(levelFile, '.json');
  });
  return index;
}, {});
