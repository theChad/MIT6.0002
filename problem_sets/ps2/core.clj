(ns ps2.core
  (:require [ps2.graph :as graph]))

;;;; Perform a depth-first search on a directed graph.

(graph/test-file-read)

;;; 
(defn is-valid-edge?
  "True if end node is in digraph nodes and
  neither distance constraint is broken."
  [digraph max-total-distance max-dist-outdoors edge]
  (and (graph/contains-node? digraph (:end edge))
       (>= max-total-distance (:total-distance edge))
       (>= max-dist-outdoors (:outdoor-distance edge))))

(defn valid-edges
  "Return valid edges to explore from a list of edges."
  [digraph max-total-distance max-dist-outdoors edges]
  (filter (partial is-valid-edge? digraph max-total-distance
                   max-distance-outdoors) edges))

(defn directed-dfs
  "Perform a depth-first search on a directed graph.
  digraph: A directed graph, type graph/Digraph
  start: string representing starting node
  end: string representing ending node
  max-total-dist: maximum distance on path
  max-dist-outdoors: maximum outdoor distance"
  [digraph start end max-total-distance max-dist-outdoors]
  (cond
    (not (graph/contains-node? digraph start)
         (graph/contains-node? digraph end))
    ;; Nodes not in digraph, return nil
    nil
    (= start end)
    ;; At the end, return the path.
    [[] 0])
  (let [next-edges (graph/get-edges-from-source digraph start)
        new-digraph (dissoc digraph start)]
    
    ))
