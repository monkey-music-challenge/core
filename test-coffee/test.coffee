assert = require('assert')
mm = require('../')
util = require('./util')

suite 'integration tests', ->

  units =
    'M': 'monkey'
    ' ': 'empty'

  layout = [
    ' M'
    '  '
  ]

  players = ['1']

  state = mm.createGameState players,
    turns: 10
    pickUpLimit: 3
    layout: layout
    units: units

  test 'createGameState', ->
    console.log(mm.gameStateForTeam(state, '1'))
    console.log(mm.parseCommand
      command: 'move'
      team: '1'
      direction: 'left')
    assert.equal("yes", "yes")

#suite 'tests', ->

  #test 'test', ->

    #assert.equal("derp", "dorp")
