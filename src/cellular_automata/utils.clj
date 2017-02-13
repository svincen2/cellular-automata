(ns cellular-automata.utils
	(:gen-class))


(defn piecewise-add
  "Add corresponding items in each collection"
  [coll-a coll-b]
  (map + coll-a coll-b))

