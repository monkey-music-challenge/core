(ns monkey-music.buffs)

(def speedy ::speedy)
(def tackled ::tackled)
(def asleep ::asleep)

(def immobilized ::immobilizied)
(derive ::tackled ::immobilized)
(derive ::asleep ::immobilized)

(def duration
  {speedy 3
   tackled 1
   asleep 2})

(def create (constantly {}))
(def has? contains?)

(defn add [buff buffs]
  (if-not (contains? duration buff)
    (throw (js/Error. (str "not a buff: " buff))))
  (assoc buffs buff {:remaining-turns (inc (duration buff))}))

(defn tick [buffs]
  (into {} (for [[buff {remaining-turns :remaining-turns}] buffs
                 :when (pos? (dec remaining-turns))]
             [buff {:remaining-turns (dec remaining-turns)}])))
