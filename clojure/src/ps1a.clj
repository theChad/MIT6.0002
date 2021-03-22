(ns ps1a
  (:require [utils :as utils]
            [clojure.pprint]))

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
(defn greedy-fill-knapsacks
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
      (greedy-fill-knapsacks
       (conj knapsacks knapsack) remaining-items limit))))
  ;; Initial call, start with empty list of knapsacks
  ([items limit]
   (greedy-fill-knapsacks [] items limit)))

(def el-list [["a" 23] ["b" 34] ["c" 36] ["d" 45] ["e" 56] ["f" 68]])
(clojure.pprint/pprint (greedy-fill-knapsacks el-list 102))

  
     
     
