(ns ps2.core
  (:require [ps2.graph :as graph]))

;;;; Perform a depth-first search on a directed graph.

(graph/test-file-read)

;;; For deciding whether these edges are valid paths to follow.
;;; The nodes must be in the digraph, and the distance
;;; must be below the limits (which will be decremented on
;;; each recursive call).
(defn is-valid-edge?
  "True if end node is in digraph nodes and
  neither distance constraint is broken."
  [digraph max-total-distance max-dist-outdoors edge]
  (and (graph/contains-node? digraph (:end-node edge))
       (>= max-total-distance (:total-distance edge))
       (>= max-dist-outdoors (:outdoor-distance edge))))

(defn valid-edges
  "Return valid edges to explore from a list of edges."
  [digraph max-total-distance max-dist-outdoors edges]
  (filter (partial is-valid-edge? digraph max-total-distance
                   max-dist-outdoors) edges))

(defn update-path
  "Update a [path dist] with the next node and the total dist."
  [path node dist]
  (if (not path) nil
      [(conj (first path) node) (+ (second path) dist)]))

(declare directed-dfs)

(defn next-dfs-call
  "Helper function to call directed-dfs with updated params.
  Parameters must be updated with info from the new edge,
  as well as the previous call to directed-dfs, which could
  yield an entire path with total distance.
  next-edge: edge to explore
  min-total-distance: min distance from dfs calls at this level"
  [digraph start end max-total-distance max-distance-outdoors
   min-total-distance next-edge]
  (let [new-max-total-dist (- max-total-distance
                              (:total-distance next-edge)
                              (if min-total-distance
                                min-total-distance
                                0))
        new-max-dist-outdoors (- max-distance-outdoors
                                 (:outdoor-distance next-edge))
        new-start (:end-node next-edge)]
    (update-path
     (directed-dfs digraph new-start end new-max-total-dist
                   new-max-dist-outdoors)
     start (:total-distance next-edge))
    ))

(defn reduce-min-path
  "Choose the minimum path from two paths. Return nil if
  both nil."
  [path-1 path-2]
  (if (every? nil? [path-1 path-2]) nil
      (apply min-key second (filter some? [path-1 path-2])))
  )

(defn directed-dfs
  "Perform a depth-first search on a directed graph.
  digraph: A directed graph, type graph/Digraph
  start: string representing starting node
  end: string representing ending node
  max-total-dist: maximum distance on path
  max-dist-outdoors: maximum outdoor distance"
  [digraph start end max-total-distance max-dist-outdoors]
  (cond
    (not (and (graph/contains-node? digraph start)
              (graph/contains-node? digraph end)))
    ;; Nodes not in digraph, return nil
    nil
    (= start end)
    ;; At the end, return the path.
    [(list end) 0]
    :else
    (let [next-edges (valid-edges digraph max-total-distance max-dist-outdoors
                                  (graph/get-edges-from-source digraph start))
          new-digraph (dissoc digraph start)]
      ;;(println next-edges)
      (reduce #(reduce-min-path %1
                                (next-dfs-call digraph start end max-total-distance
                                               max-dist-outdoors (second %1) %2))
              nil next-edges))))


(defn test-dfs
  [opts]
  (let [digraph (graph/test-file-read)
        start "1"
        end "32"
        max-total-distance 999
        max-dist-outdoors 0]
    (println (directed-dfs digraph start end max-total-distance max-dist-outdoors))
    (println ((:nodes digraph) "end"))))
