assert = require('assert')

monkeyMusic = require('../../')

describe 'single-player, basic pick-up and drop-off', ->
  it 'should work as intended', ->

    units =
      'p': 'playlist'
      'M': 'monkey'
      'a': 'album'
      ' ': 'empty'
      's': 'song'
      'U': 'user'
      '#': 'wall'

    reverse = (layout) ->
      layout.map (line) ->
        line.split('').map (char) ->
          units[char]

    level =
      turns: 10
      units: units
      layout: [
        ' Up'
        'Msa'
      ]

    state = monkeyMusic.newGameState(['p1'], level)
    assert.deepEqual monkeyMusic.stateForPlayer(state, 'p1'),
      turns: 10
      position: [1, 0]
      score: 0
      pickedUp: []
      layout: reverse [
        ' Up'
        'Msa'
      ]

    state = monkeyMusic.move(state, 'p1', 'right')
    assert.deepEqual monkeyMusic.stateForPlayer(state, 'p1'),
      turns: 9
      position: [1, 0]
      score: 0
      pickedUp: ['song']
      layout: reverse [
        ' Up'
        'M a'
      ]

    state = monkeyMusic.move(state, 'p1', 'right')
    assert.deepEqual monkeyMusic.stateForPlayer(state, 'p1'),
      turns: 8
      position: [1, 1]
      score: 0
      pickedUp: ['song']
      layout: reverse [
        ' Up'
        ' Ma'
      ]

    state = monkeyMusic.move(state, 'p1', 'up')
    assert.deepEqual monkeyMusic.stateForPlayer(state, 'p1'),
      turns: 7
      position: [1, 1]
      score: 1
      pickedUp: []
      layout: reverse [
        ' Up'
        ' Ma'
      ]

    state = monkeyMusic.move(state, 'p1', 'right')
    assert.deepEqual monkeyMusic.stateForPlayer(state, 'p1'),
      turns: 6
      position: [1, 1]
      score: 1
      pickedUp: ['album']
      layout: reverse [
        ' Up'
        ' M '
      ]

    state = monkeyMusic.move(state, 'p1', 'right')
    assert.deepEqual monkeyMusic.stateForPlayer(state, 'p1'),
      turns: 5
      position: [1, 2]
      score: 1
      pickedUp: ['album']
      layout: reverse [
        ' Up'
        '  M'
      ]

    state = monkeyMusic.move(state, 'p1', 'up')
    assert.deepEqual monkeyMusic.stateForPlayer(state, 'p1'),
      turns: 4
      position: [1, 2]
      score: 1
      pickedUp: ['album', 'playlist']
      layout: reverse [
        ' U '
        '  M'
      ]

    state = monkeyMusic.move(state, 'p1', 'up')
    assert.deepEqual monkeyMusic.stateForPlayer(state, 'p1'),
      turns: 3
      position: [0, 2]
      score: 1
      pickedUp: ['album', 'playlist']
      layout: reverse [
        ' UM'
        '   '
      ]

    state = monkeyMusic.move(state, 'p1', 'left')
    assert.deepEqual monkeyMusic.stateForPlayer(state, 'p1'),
      turns: 0
      position: [0, 2]
      score: 1 + 2 + 3
      pickedUp: []
      layout: reverse [
        ' UM'
        '   '
      ]
