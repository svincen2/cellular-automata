(ns cellular-automata.core-test
  (:require [clojure.test :refer :all]
            [cellular-automata.core :refer :all]))

(deftest getting-cells
  (let [cells (hash-map '(0 0) 1 '(0 1) 1 '(0 2) 1)]
    (testing "get-cell"
      (is (= {'(0 0) 1} (get-cell '(0 0) cells)))
      (is (= {'(1 1) 0} (get-cell '(1 1) cells)))
      (is (= {'(2 2) 26} (get-cell '(2 2) cells 26))))))

(deftest automaton-updates
  (testing "add-new-neighborhood"
    (let [cells (hash-map '(0 0) 1 '(1 0) 1 '(2 0) 1)
          neighborhood (list '(-1 0) '(1 0))
          expected (hash-map '(0 0) 1 '(-1 0) 0 '(1 0) 1 '(2 0) 1 '(3 0) 0)]
      (is (= expected (add-new-neighbors neighborhood cells))))))


(run-tests)
