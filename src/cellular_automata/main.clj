(ns cellular-automata.main
  (:require [cellular-automata.gui :as gui])
  (:require [cellular-automata.core :as core])
  (:require [clojure.pprint :as pprint :refer [pprint]])
  (:import javax.swing.SwingUtilities)
  (:gen-class))

; A cell's neighborhood defined by offsets to its location"
(def neighborhood
  '((-1 -1)
    (-1  0)
    (-1  1)
    ( 0 -1)
    ( 0  1)
    ( 1 -1)
    ( 1  0)
    ( 1  1)))

(defn rule
  "Rule that governs the cells.
   This implementation is the rule for Game of Life"
  [cell neighbors]
  (let [living (count (filter core/alive? neighbors))
        loc (:location cell)]
    (if (core/alive? cell)
      (if (or (< living 2) (> living 3))
        (core/create-cell 0 loc)
        cell)
      (if (= 3 living)
        (core/create-cell 1 loc)
        cell))))

; Automaton updater
(def updater (core/make-automaton-updater neighborhood rule))


(defn -main
  "Cellular automata"
  [& args]
  (SwingUtilities/invokeLater (fn [] (gui/create-gui updater))))
