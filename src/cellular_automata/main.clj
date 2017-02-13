(ns cellular-automata.main
	(:require [cellular-automata.gui :as gui])
	(:require [cellular-automata.core :as core])
  (:require [clojure.pprint :as pprint :refer [pprint]])
  (:import javax.swing.SwingUtilities)
	(:gen-class))


; The initial automaton"
;(def automaton
  ;(list
    ;(core/create-cell 1 0 -1)
    ;(core/create-cell 1 0  0)
    ;(core/create-cell 1 0  1)))

(defn -main
  "Cellular automata"
  [& args]
  (SwingUtilities/invokeLater (fn [] (gui/create-gui))))
