(ns cellular-automata.gui
  (:require [cellular-automata.core :as core])
  (:import [javax.swing JFrame JPanel WindowConstants])
  (:import [java.awt Dimension Color])
  (:import [java.awt.event MouseAdapter MouseEvent])
  (:import [java.awt.event KeyAdapter KeyEvent])
  (:gen-class :main false))

; The cells of the automaton
(def cells (ref '()))

; Cell panel background color
(def background-color Color/LIGHT_GRAY)

(defn clear
  "Clear the cell panel"
  [graphics panel]
  (let [width (.getWidth panel)
        height (.getHeight panel)]
    (.setBackground graphics background-color)
    (.clearRect graphics 0 0 width height)))

; Cell width and height
(def cell-width 10)
(def cell-height 10)

(defn draw-cells
  "Draw the cells"
  [graphics panel]
  (let [width (.getWidth panel)
        height (.getHeight panel)]
    (.setColor graphics Color/BLACK)
    (dosync
      (doseq [c @cells]
        (let [loc (:location c)]
          (if (core/alive? c)
            (.setColor graphics Color/BLACK)
            (.setColor graphics background-color))
            (.fillRect graphics
            (* cell-width (get loc 0))
            (* cell-height (get loc 1))
            cell-width
            cell-height))))))

; The grid's line color.
(def line-color Color/DARK_GRAY)

(defn draw-vertical-lines
  "Draw vertical lines of the cell grid"
  [graphics panel]
  (let [width (.getWidth panel)
        height (.getHeight panel)]
    (.setColor graphics line-color)
    (loop [x 0]
      (when (< x width)
        (.drawLine graphics x 0 x height)
        (recur (+ x 10))))))

(defn draw-horizontal-lines
  "Draw horizontal lines of the cell grid"
  [graphics panel]
  (let [width (.getWidth panel)
        height (.getHeight panel)]
    (.setColor graphics line-color)
    (loop [y 0]
      (when (< y height)
        (.drawLine graphics 0 y width y)
        (recur (+ y 10))))))

(defn create-mouse-adapter
  "Create the mouse adapter that adds clicked cells"
  [panel]
  (def adapter
    (proxy [MouseAdapter][]
    (mousePressed [event]
      (let [x (int (/ (.getX event) 10))
            y (int (/ (.getY event) 10))]
        (dosync
          (alter cells conj (core/create-cell 1 x y))))
      (.repaint panel))))
  adapter)

(defn create-key-adapter
  "Create the key adapter that will respond to user key presses"
  [frame automaton]
  (proxy [KeyAdapter][]
    (keyPressed [event]
      (cond
        (= KeyEvent/VK_SPACE (.getKeyCode event))
          (dosync
            (alter cells automaton))
        (= KeyEvent/VK_C (.getKeyCode event))
          (dosync
            (alter cells (fn [v] '()))))
      (.repaint frame))))

(defn create-cell-panel
  "Create the JPanel that will draw the cells"
  [x y width height]
  (let [double-buffered true]
    (def cell-panel
      (proxy [JPanel][double-buffered]
        (paintComponent [graphics]
          (clear graphics cell-panel)
          (draw-cells graphics cell-panel)
          (draw-vertical-lines graphics cell-panel)
          (draw-horizontal-lines graphics cell-panel))))
    cell-panel))

(defn create-gui
  "Create the GUI"
  [automaton]
  (let [frame-width 640
        frame-height 480
        title "Cellular Automata!"]
    (def frame (new JFrame))
    (def panel (create-cell-panel 0 0 frame-width frame-height))
    (.setDefaultCloseOperation frame WindowConstants/EXIT_ON_CLOSE)
    (.setPreferredSize frame (new Dimension frame-width frame-height))
    (.addKeyListener frame (create-key-adapter frame automaton))
    (.addMouseListener cell-panel (create-mouse-adapter panel))
    (.add frame cell-panel)
    (.setVisible frame true)
    (.pack frame)))
