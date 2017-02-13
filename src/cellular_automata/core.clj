(ns cellular-automata.core
  (:require [cellular-automata.utils :as utils])
  (:gen-class))


(defn create-cell
  "Create a cell with the given state and location"
  [state & location]
  {:state state :location (vec (flatten location))})

(defn alive?
  "Is the given cell 'alive' (state == 1)"
  [cell]
  (= 1 (:state cell)))

(defn dead?
  "Is the given cell 'dead' (state == 0)"
  [cell]
  (= 0 (:state cell)))

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
  (let [living (count (filter alive? neighbors))
				loc (:location cell)]
    (if (alive? cell)
      (if (or (< living 2) (> living 3))
				(create-cell 0 loc)
				cell)
    	(if (= 3 living)
				(create-cell 1 loc)
				cell))))

(defn get-cell-at
  "Get the cell at the given location"
  [location automaton]
	(def cell
  	(first (filter (fn [cell] (= (:location cell) location)) automaton)))
	(if (nil? cell)
		(create-cell 0 location)
		cell))

(defn find-neighbor-locations
  "Find the given cell's neighbor locations"
  [cell]
  (map (fn [loc] (utils/piecewise-add loc (:location cell))) neighborhood))

(defn find-neighbors
  "Find the given cell's neighbor cells"
  [cell cells]
  (let [neighbor-locs (find-neighbor-locations cell)]
    (map (fn [loc] (get-cell-at loc cells)) neighbor-locs)))


(defn cell-exists?
  "Does a cell at the given location exist?"
  [cells loc]
  (let [next (first cells)]
    (if (nil? next)
      false
      (if (= (:location next) loc)
	true
	(cell-exists? (rest cells) loc)))))

(defn add-new-neighbors
  "Add any new cells that neighbor existing cells
   to the automaton"
  [cells]
    (def distinct-locs
      (set
        (reduce
          concat
	  (map (fn [cell] (find-neighbor-locations cell)) cells))))
    (def new-cells
      (map
        (fn [loc] (create-cell 0 loc))
        (filter
          (fn [loc] (not (cell-exists? cells loc)))
          distinct-locs)))
    (concat cells new-cells))

(defn update-automaton
  "Update the given automaton by applying the rule to each cell"
  [automaton]
  (let [new-automaton (add-new-neighbors automaton)]
    (filter alive?
      (map
        (fn [cell] (rule cell (find-neighbors cell automaton)))
        new-automaton))))
