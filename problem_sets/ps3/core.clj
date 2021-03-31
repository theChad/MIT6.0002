(ns ps3.core)

;;; Problem set three: roomba simulations


;;; Initializing the room

(defn dirty-tiles
  "Return a map of dirty tiles."
  [width height dirt-amount]
  (reduce
   conj
   {}
   (for [w (range width)
         h (range height)]
            {(list w h) dirt-amount})))

(defn get-room
  "Return a data structure representing a room to be cleaned."
  [width height dirt-amount]
  {:width width
   :height height
   :dirt-amount dirt-amount
   :tiles (dirty-tiles width height dirt-amount)})

;;; Furnishing the room

(defn random-nonfilling-slice
  "Get a non-filling range of integers.
  limit: exclusive top of the range
  Returns a range within [0,limit)"
  [limit]
  (let [length (inc (rand-int (dec limit)))
        start (rand-int (- limit (dec length)))]
    (range start (+ start length))))

(defn random-furniture
  "Generate a random piece of furniture and return its tiles."
  [width height]
  (reduce conj #{}
          (for [x (random-nonfilling-slice width)
                y (random-nonfilling-slice height)]
            (list x y)))
  )

(defn add-furniture-tile
  "Convert a single room tile to furniture."
  [room tile]
  (-> room
       (update :tiles #(dissoc % tile))
       (update :furniture-tiles #((fnil conj #{}) % tile))))

(defn furnish-room
  "Add a piece of furniture to an empty room."
  [room]
  (reduce add-furniture-tile room (random-furniture (:width room)
                                                    (:height room))))

;;; Cleaning the room

(defn tile-from-pos
  "Get the tile that position is located on.
  pos: (x,y) coordinates"
  [pos]
  (map int pos))

(defn clean-tile-in-map
  "Clean a tile from a tile map."
  [tiles pos capacity]
  (update tiles (tile-from-pos pos)
          #(max 0
                (- % capacity))))

(defn clean-tile
  "Clean a tile in a room."
  [room pos capacity]
  (update room :tiles
          #(clean-tile-in-map % pos capacity)))

;;; Initializing the robot

(defn get-random-tile
  "Return a random (unfurnished) tile."
  [room]
  (rand-nth (seq (keys (:tiles room)))))

(defn get-random-position
  "Return a random position in a room."
  [room]
  (map #(+ % (rand 1)) (get-random-tile room)))

(defn get-robot
  "Return a data structure representing a robot.
  speed is the distance traveled per time step.
  capacity is the amount cleaned per time step on a tile.
  The robot is initialized with a random position and direction."
  [room speed capacity]
  {:speed speed
   :capacity capacity
   :direction (rand 360)
   :position (get-random-position room)})

;;; Moving the robot

(defn pos-in-one-step
  "Return a position one step in the given direction."
  [start-pos speed direction]
  (map #(+ %1 (* speed %2))
       start-pos
       (list (Math/cos (Math/toRadians direction))
             (Math/sin (Math/toRadians direction)))))

(defn valid-position?
  "True if the given position is a valid one in the room."
  [room pos]
  (let [tile (tile-from-pos pos)]
    (contains? (:tiles room) tile)))

(defn robot-move-one-step
  "Move a robot one step, if valid move.
  Return nil if the move is not valid."
  [robot room]
  (let [new-pos (pos-in-one-step (:position robot)
                                 (:speed robot)
                                 (:direction robot))]
    (if (valid-position? room new-pos)
      (assoc robot :position new-pos
             :direction (rand 360))
      nil)))

(defn robot-take-action
  "Take the action of one time step.
  Inputs: robot and room
  Outputs: [robot room], updated."
  [robot room]
  (if-let [new-robot (robot-move-one-step robot room)]
    [new-robot (clean-tile room (:position new-robot)
                           (:capacity new-robot))]
    [(assoc robot :direction (rand 360)) room]) 
  )

;;; Test room for cleanliness

(defn percent-tiles-clean
  "Percentage of tiles in the room that are clean."
  [room]
  (/ (count (filter #(= 0 (second %)) (:tiles room)))
     (* (:width room) (:height room))))

;;; Run simulation

(defn sim-reducer
  "Reduction function for a single step of the simulation."
  [[robots room] next-robot]
  (let [[updated-robot updated-room]
        (robot-take-action next-robot room)]
    [(conj robots updated-robot) updated-room]))

(defn single-sim-step
  "Run a single single time step of a simulation."
  [robots room]
  (reduce sim-reducer [[] room] robots))

(defn run-one-sim
  "Run a single simulation. Clean every tile."
  [robots room min-coverage]
  (take-while #(> min-coverage (percent-tiles-clean (second %)))
              (iterate #(apply single-sim-step %) [robots room])))

(defn count-one-sim
  "Run one sim and return just the number of time steps."
  [robots room min-coverage]
  (count (run-one-sim robots room min-coverage)))

(defn run-simulation
  "Run several simulations."
  [num-robots room speed capacity min-coverage num-sims]
  (let [robots
        (repeatedly num-robots #(get-robot room speed capacity))
        sim-time-lengths
        (take num-sims (repeatedly
                        #(count-one-sim robots room min-coverage)))]
    (float (/ (apply + sim-time-lengths)
              (count sim-time-lengths)))))

(defn test-fns
  []
  (let [room (get-room 10 10 3)
        robot (get-robot room 1 3)]
    (println room)
    (println (percent-tiles-clean room))
    (run-simulation 1 room 1 1 0.8 50)
    ))

(test-fns)
