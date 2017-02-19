(ns cellular-automata.core
  (:gen-class))

;(defrecord Cell [location state])

(defn create-cell
  [state loc]
  (hash-map loc state))


(defn get-cell
  "Get the cell at the given location.
   If a cell doesn't exit at the location,
   return a cell at the location with the given default value."
  ([loc cells default]
    (hash-map (vec loc) (get cells loc default)))

  ([loc cells]
     (get-cell loc cells 0)))


(defn get-neighbors
  "Get the given cell's neighboring cells, as defined by the neighborhood."
  [cell neighborhood cells]
  (into
    {}
    (flatten
      (map
        (fn [neighbor-loc] (get-cell (map + (key cell) neighbor-loc) cells))
        neighborhood))))


(defn add-new-neighbors
  "Get the complete community of cells based on the existing cells.
   This included the current cells, and every neighbor of those cells."
  [neighborhood cells]
  (let [all-neighbors (fn [cell] (get-neighbors cell neighborhood cells))]
    (merge cells (into {} (flatten (map all-neighbors cells))))))


(defn alive?
  [cell]
  (= 1 (val cell)))


(defn evolve
  [cells neighborhood rule]
  (let [new-cells (add-new-neighbors neighborhood cells)]
    (into
      {}
      (map
        (fn [cell] (rule cell (get-neighbors cell neighborhood cells)))
        new-cells))))


(defn make-automaton
  [neighborhood rule]
  (fn [cells] (evolve cells neighborhood rule)))

