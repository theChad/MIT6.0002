(ns ps1a
  (:require
   [clojure.math.combinatorics :as combo]
   [clojure.pprint]
   [clojure.string :as s]))

;;; Read in cow data file
(defn load-cows
  "Read the cow file into a map."
  [cow-file]
  (->>
   cow-file
   slurp
   s/split-lines
   (map #(s/split % #",")) ; [cow weight] lists
   ;; Make the weight an integer and roll the pairs into a hashmap
   (reduce #(assoc %1 (first %2) (Integer/parseInt (second %2))) {})))

;;; First, the greedy algorithm


;;; Single knapsack filler
(defn greedy-fill-knapsack
  "Greedily add one item to knapsack.
  knapsack is a vector of items in the bag.
  remaining-weight is the weight the bag can still accept
  items are the remaining items outside the bag.
  "
  [knapsack remaining-weight items]
  ;; light-items are light enough to fit in the bag
  (let [[light-items heavy-items]
        (map vec (split-with #(<= (second %) remaining-weight) items))]
    (if (empty? light-items)
      ;; No items light enough to fit in the bag,
      ;; return knapsack and remaining items
      [knapsack items]
      ;; Add the heaviest item that'll fit into the bag.
      ;; Decrement the remaining weight, remove item
      ;; from remaining items.
      (greedy-fill-knapsack
       (conj knapsack (peek light-items))
       (- remaining-weight (second (peek light-items)))
       (into (pop light-items) heavy-items)))))

;;; Greedy knapsack problem
(defn greedy-cow-transport
  "Greedily fill all knapsacks.
  knapsacks is a list of knapsacks filled so far
  items: remaining items and weights, sorted by weight
  limit: weight limit"
  ([knapsacks items limit]
  (if
    (empty? items)
    knapsacks
    ;; With items remaining, fill another knapsack.
    (let [[knapsack remaining-items]
          (greedy-fill-knapsack [] limit items)]
      (greedy-cow-transport
       (conj knapsacks knapsack) remaining-items limit))))
  ;; Initial call, start with empty list of knapsacks
  ([items limit]
   (greedy-cow-transport [] (sort-by val items) limit))
  ([items]
   (greedy-cow-transport items 10)))


;;; Now the brute force algorithm

(defn barge-weight
  "Get the total weight of a barge"
  [barge]
  (apply + (map second barge)))

(defn valid-barges?
  "True if no barges are over the limit."
  [limit barges]
  (every? (partial >= limit) (map barge-weight barges)))

;;; Brute force knapsack problem
(defn brute-force-cow-transport
  "Find the optimal transport situation by brute force.
  Starting with the fewest number of barges and going up,
  test every possible transport situation until one works.
  cows: dictionary of cow-name: weight
  limit: max weight per barge."
  ([cows limit]
   (first
    (filter (partial valid-barges? limit)
            (combo/partitions cows)))
   )
  ([cows] (brute-force-cow-transport cows 10))
  )


;;; Comparing algorithms
(defn compare-cow-transport-algorithms
  "Print out a comparision of the time it took the algos to run."
  []
  
  (let [algos [greedy-cow-transport brute-force-cow-transport]
        cow-list (load-cows "ps1/ps1_cow_data.txt")]
    (doseq [algo algos]
      (print (str algo " running. "))
      (time (algo cow-list)))))

(defn test-algos
  []
  (def cow-list (load-cows "ps1/ps1_cow_data.txt"))
  (println cow-list)
  ;;(def el-list {"a" 23 "b" 34 "c" 36 "d" 45 "e" 56 "f" 68})
  ;;(println (combo/partitions [1 2 3]))
  (print "Greedy: ")
  (clojure.pprint/pprint (greedy-cow-transport cow-list 10))
  (print "Brute-force: ")
  (clojure.pprint/pprint (brute-force-cow-transport cow-list))
  (compare-cow-transport-algorithms))

 (test-algos)
     
     

