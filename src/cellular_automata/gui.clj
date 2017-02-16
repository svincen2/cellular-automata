(ns cellular-automata.gui
  (:require [cellular-automata.core :as core])
  (:import [javax.swing JFrame JPanel WindowConstants Timer])
  (:import [java.awt Dimension Color])
  (:import [java.awt.event KeyAdapter KeyEvent MouseAdapter MouseEvent ActionListener])
  (:gen-class :main false))

;; State.

(def cells (ref '()))
(def cell-width 10)
(def cell-height 10)
(def background-color Color/LIGHT_GRAY)
(def line-color Color/DARK_GRAY)
(def width 640)
(def height 480)
(def update-interval 10)


(defn clear
  "Clear the cell panel"
  [graphics panel]
  (let [width (.getWidth panel)
        height (.getHeight panel)]
    (.setBackground graphics background-color)
    (.clearRect graphics 0 0 width height)))


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



(defn draw-vertical-lines
  "Draw vertical lines of the cell grid"
  [graphics panel]
  (let [width (.getWidth panel)
        height (.getHeight panel)]
    (.setColor graphics line-color)
    (loop [x 0]
      (when (< x width)
        (.drawLine graphics x 0 x height)
        (recur (+ x cell-width))))))

(defn draw-horizontal-lines
  "Draw horizontal lines of the cell grid"
  [graphics panel]
  (let [width (.getWidth panel)
        height (.getHeight panel)]
    (.setColor graphics line-color)
    (loop [y 0]
      (when (< y height)
        (.drawLine graphics 0 y width y)
        (recur (+ y cell-height))))))


;; GUI stuff.

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

(defn create-timer-listener
  [panel automaton]
  (proxy [ActionListener] []
    (actionPerformed [action]
      (dosync
        (alter cells automaton))
      (.repaint panel))))

(def cell-panel (create-cell-panel 0 0 width height))


; Event listeners.
(defn create-mouse-adapter
  "Create the mouse adapter that adds clicked cells"
  [panel]
  (proxy [MouseAdapter][]
  (mousePressed [event]
    (let [x (int (/ (.getX event) cell-width))
          y (int (/ (.getY event) cell-height))]
      (dosync
        (alter cells conj (core/create-cell 1 x y))))
    (.repaint panel))))

(defn create-key-adapter
  "Create the key adapter that will respond to user key presses"
  [frame automaton timer]
  (proxy [KeyAdapter][]
    (keyPressed [event]
      (cond
        (= KeyEvent/VK_SPACE (.getKeyCode event))
          (if (.isRunning timer) (.stop timer) (.start timer))
        (= KeyEvent/VK_C (.getKeyCode event))
          (dosync
            (alter cells (fn [v] '()))))
      (.repaint frame))))

(defn create-gui
  "Create the GUI"
  [automaton]
  (let [title "Cellular Automata!"]
    (def frame (new JFrame))
    (.setDefaultCloseOperation frame WindowConstants/EXIT_ON_CLOSE)
    (.setPreferredSize frame (new Dimension width height))
    (def timer (new Timer update-interval (create-timer-listener cell-panel automaton)))
    (.addKeyListener frame (create-key-adapter frame automaton timer))
    (.addMouseListener cell-panel (create-mouse-adapter cell-panel))
    (.add frame cell-panel)
    (.setVisible frame true)
    (.pack frame)))
