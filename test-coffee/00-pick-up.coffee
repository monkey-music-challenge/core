assert = require('assert')
monkeyMusic = require('../')
util = require('./util')

#suite 'pick up', ->

  #units =
    #'M': 'monkey'
    #'s': 'song'
    #'a': 'album'
    #'p': 'playlist'
    #' ': 'empty'
    #'U': 'user'

  #layout =
    #[' as'    
     #'pMU'    
     #' ss']

  #players = ['p1']

  #state = monkeyMusic.newGameState players,
    #turns: 10
    #units: units
    #layout: layout

  #test 'initial state', ->
    #util.match monkeyMusic.stateForPlayer(state, 'p1'),
      #layout: util.lookup units,
        #[' as'
         #'pMU'    
         #' ss']

  #test 'track', ->
    #state = monkeyMusic.move(state, 'p1', 'down')
    #util.match monkeyMusic.stateForPlayer(state, 'p1'),
      #pickedUp: ['song']
      #layout: util.lookup units,
        #[' as'
         #'pMU'    
         #'  s']
