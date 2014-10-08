(ns monkey-music.level)

(def players-as-string "[\"player1\", \"player2\"]")

(js->clj (JSON/parse players-as-string))

(def level-as-string "{
  \"turns\": 5,
  \"layout\": [
    \"MUp\",
    \"Msa\"
  ],
  \"units\": {
    \"M\": \"monkey\",
    \"s\": \"song\",
    \"a\": \"album\",
    \"p\": \"playlist\",
    \"U\": \"user\",
    \"#\": \"wall\",
    \" \": \"empty\"
  }
}")

(js->clj (JSON/parse level-as-string))

(map vector [1 2] [4 5 3])

(defn new-player [player-name])

(defn new-game*
  [players {turns "turns"
            layout-strings "layout"
            unit-lookup "units"}]
  (let [layout (->> layout-strings
                    (mapv vec)
                    (mapv (partial mapv (comp keyword unit-lookup))))
        layout-height (count layout)
        layout-width (count (get layout 0))
        player-positions (for [y (range layout-height)
                               x (range layout-width)
                               :let [unit (get-in layout [y x])]
                               :when (= unit :monkey)]
                           [y x])
        player-data (mapv (partial assoc {:score 0 :picked-up []} :position) player-positions)
        players (into {} (map vector players player-data))]
    {:turns turns
     :layout layout
     :players players}))

(defn new-game [json-players json-level]
  (new-game* (js->clj json-players) (js->clj json-level)))

(new-game (JSON/parse players-as-string) (JSON/parse level-as-string))
