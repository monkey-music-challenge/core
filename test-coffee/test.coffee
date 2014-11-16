assert = require('assert')
mm = require('../')
util = require('./util')

suite 'integration tests', ->

  units =
    'M': 'monkey'
    ' ': 'empty'
    '#': 'wall'
    'b': 'banana'
    'p': 'playlist'
    's': 'song'
    'a': 'album'
    'u': 'user'

  layout = [
    'M  #b'
    '##   '
    'pa us'
  ]

  players = ['1']

  state = mm.createGameState(players, turns: 20, pickUpLimit: 3, layout: layout, units: units)

  test 'bananas', ->
    cmd = mm.parseCommand(command: 'move', team: '1', direction: 'right')
    state = mm.runCommands([cmd])
    #console.log(mm.gameStateForPlayer('1'))
    assert.equal("yes", "yes")

#suite 'tests', ->

  #test 'test', ->

    #assert.equal("derp", "dorp")
