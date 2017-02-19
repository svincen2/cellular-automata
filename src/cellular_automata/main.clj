(ns cellular-automata.main
  (:require [cellular-automata.core :as core]
            [cellular-automata.gui :as gui])
  (:import [javax.swing SwingUtilities])
  (:gen-class))

; A cell's neighborhood defined by offsets to its location"
(def neighborhood
  '([-1 -1]
    [-1  0]
    [-1  1]
    [ 0 -1]
    [ 0  1]
    [ 1 -1]
    [ 1  0]
    [ 1  1]))


(defn rule
  [cell neighbors]
  (let [loc (key cell)
        state (val cell)
        living (count (filter core/alive? neighbors))]
    (if (core/alive? cell)
      (if (or (< living 2) (> living 3))
        (core/create-cell 0 loc)
        cell)
      (if (= living 3)
        (core/create-cell 1 loc)
        cell))))

; Automaton - Currently the Game of Life
(def automaton (core/make-automaton neighborhood rule))


(defn -main
  "Cellular automata"
  [& args]
  (SwingUtilities/invokeLater (fn [] (gui/create-gui automaton))))
