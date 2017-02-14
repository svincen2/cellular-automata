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

(defn get-cell-at
  "Get the cell at the given location"
  [location automaton]
  (let [cell (first (filter (fn [cell] (= (:location cell) location)) automaton))]
	  (if (nil? cell)
		  (create-cell 0 location)
		  cell)))

(defn find-neighbor-locations
  "Find the given cell's neighbor locations"
  [cell neighborhood]
  (map (fn [loc] (map + loc (:location cell))) neighborhood))

(defn find-neighbors
  "Find the given cell's neighbor cells"
  [cell cells neighborhood]
  (let [neighbor-locs (find-neighbor-locations cell neighborhood)]
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
  [cells neighborhood]
  (def distinct-locs
    (set
      (reduce
        concat
        (map (fn [cell] (find-neighbor-locations cell neighborhood)) cells))))
    (def new-cells
      (map
        (fn [loc] (create-cell 0 loc))
        (filter
          (fn [loc] (not (cell-exists? cells loc)))
          distinct-locs)))
    (concat cells new-cells))

(defn update-automaton
  "Update the given automaton by applying the rule to each cell"
  [cells neighborhood rule]
  (let [new-automaton (add-new-neighbors cells neighborhood)]
    (filter alive?
      (map
        (fn [cell] (rule cell (find-neighbors cell cells neighborhood)))
        new-automaton))))

(defn make-automaton-updater
  [neighborhood rule]
  (fn [cells] (update-automaton cells neighborhood rule)))
