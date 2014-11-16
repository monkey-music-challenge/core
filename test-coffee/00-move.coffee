assert = require('assert')
monkeyMusic = require('../')
util = require('./util')

#suite 'move', ->

  #units =
    #'M': 'monkey'
    #'#': 'wall'
    #' ': 'empty'

  #layout = [
    #'# '    
    #' M'    
  #]

  #players = ['p1']

  #state = monkeyMusic.newGameState players,
    #turns: 10
    #units: units
    #layout: layout

  #test 'initial state', ->
    #util.match monkeyMusic.stateForPlayer(state, 'p1'),
      #position: [1, 1]
      #layout: util.lookup units,
        #['# '
         #' M']

  #test 'up', ->
    #state = monkeyMusic.move(state, 'p1', 'up')
    #util.match monkeyMusic.stateForPlayer(state, 'p1'),
      #position: [0, 1]
      #layout: util.lookup units,
        #['#M'
         #'  ']

  #test 'down', ->
    #state = monkeyMusic.move(state, 'p1', 'down')
    #util.match monkeyMusic.stateForPlayer(state, 'p1'),
      #position: [1, 1]
      #layout: util.lookup units,
        #['# '
         #' M']

  #test 'left', ->
    #state = monkeyMusic.move(state, 'p1', 'left')
    #util.match monkeyMusic.stateForPlayer(state, 'p1'),
      #position: [1, 0]
      #layout: util.lookup units,
        #['# '
         #'M ']

  #test 'right', ->
    #state = monkeyMusic.move(state, 'p1', 'right')
    #util.match monkeyMusic.stateForPlayer(state, 'p1'),
      #position: [1, 1]
      #layout: util.lookup units,
        #['# '
         #' M']

  #test 'outside of the level', ->
    #state = monkeyMusic.move(state, 'p1', 'down')
    #util.match monkeyMusic.stateForPlayer(state, 'p1'),
      #position: [1, 1]
      #layout: util.lookup units,
        #['# '
         #' M']

  #test 'into a wall', ->
    #state = monkeyMusic.move(state, 'p1', 'left')
    #util.match monkeyMusic.stateForPlayer(state, 'p1'),
      #position: [1, 0]
      #layout: util.lookup units,
        #['# '
         #'M ']
    #state = monkeyMusic.move(state, 'p1', 'up')
    #util.match monkeyMusic.stateForPlayer(state, 'p1'),
      #position: [1, 0]
      #layout: util.lookup units,
        #['# '
         #'M ']
