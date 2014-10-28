assert = require('assert')
format = require('util').format

exports.lookup = (units, layout) ->
  layout.map (line) ->
    line.split('').map (char) ->
      units[char]

exports.match = (object, matchObject) ->
  Object.keys(matchObject).forEach (key) ->
    assert(key of object,
      format('expected %s in %s',
        key, JSON.stringify(object, null, 2)))
    if typeof matchObject[key] isnt 'object' or Array.isArray(matchObject[key])
      assert.deepEqual(object[key], matchObject[key],
        format('expected %s to be %s but was %s',
          key, JSON.stringify(matchObject[key]), JSON.stringify(object[key])))
    else
      exports.match(object[key], matchObject[key])
