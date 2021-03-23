(ns ps1b)



(def memo-dp-make-weight
  (memoize
   (fn [egg-weights target-weight]
     ;; At zero target-weight, we're exactly full.
     (if (<= target-weight 0)
       0
       ;; Compute new possible target weights, depending on
       ;; which weight we add to the bag.
       (let [possible-target-weights
             (filter (partial <= 0) (map (partial - target-weight) egg-weights))]
         (if (empty? possible-target-weights)
           ##Inf
           ;; Just used a weight, so increment solution to the next substage down.
           (inc (apply min
                       (map (partial memo-dp-make-weight egg-weights)
                            possible-target-weights)))))))))

(defn dp-make-weight
  "Compute the fewest number of eggs required to make weight with dynamic programming.
  egg-weights: a collection of weights to choose from.
  target-weight: minimum weight that must be reached.
  There is an arbitrary number of eggs of each weight available."
  [egg-weights target-weight]
  (memo-dp-make-weight egg-weights target-weight))


(defn test-make-weight
  "Test the dp-make-weight function"
  []
  (let [egg-weights [1 5 10 25]
        n 99]
    (time (dp-make-weight egg-weights n))
    (time (dp-make-weight egg-weights n))
    (println "# of eggs:" (dp-make-weight egg-weights n))))

(test-make-weight)
