(ns ps2.parallel
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

(declare get-best-path)

(defn next-dfs-call
  "Helper function to call directed-dfs with updated params.
  Parameters must be updated with info from the new  edge,
  as well as the previous call to directed-dfs, which could
  yield an entire path with total distance.
  next-edge: edge to explore
  min-total-distance: min distance from dfs calls at this level"
  [digraph start end max-total-distance max-distance-outdoors
   path best-dist min-total-distance next-edge]
  (let [new-max-total-dist (- (or min-total-distance max-total-distance)
                              (:total-distance next-edge))
        new-max-dist-outdoors (- max-distance-outdoors
                                 (:outdoor-distance next-edge))
        new-start (:end-node next-edge)
        new-path (update-path path (:end-node next-edge) (:total-distance next-edge))]
    (get-best-path digraph new-start end new-max-total-dist
                   new-max-dist-outdoors new-path best-dist)
    ))

(defn roll-function
  "Call a function of two variables, returning either the first
  argument or the result of the function.
  Specific to this module, f is given the second element of
  prev-result, which here will be the current best path distance.

  If a new path is found, directed-dfs guarantees it'll be
  shorter than previous explored paths on this branch. So
  return it, otherwise return the previous path prev-result."
  [f prev-result next-arg]
  (let [new-path (f (second prev-result) next-arg)]
    (if (every? nil? [new-path prev-result])
      nil
      (apply min-key second (filter some? [new-path prev-result])))))

(defn choose-shortest-path
  "Choose the shortest from among a collection of paths.
  Nil if no paths"
  [paths]
  (if (every? nil? paths) nil
      (apply min-key second (filter some? paths))))

;;; directed-dfs will call itself recursively, with the help
;;; of next-dfs-call to set up parameters and complete the
;;; path returned by the function. The max total distances
;;; are adjusted for that point in the graph, taking into
;;; account the paths already traversed up the graph, as well
;;; as any completed paths. It will return nil if it can't fine
;;; a path that satisfies the max constraints (which includes
;;; being no longer than a path already found).

(defn get-best-path
  "Perform a depth-first search on a directed graph.
  digraph: A directed graph, type graph/Digraph
  start: string representing starting node
  end: string representing ending node
  max-total-dist: maximum distance on path
  max-dist-outdoors: maximum outdoor distance
  path: [[nodes visited] total-dist outdoor-dist],
        path visited so far."

  [digraph start end max-total-distance max-dist-outdoors path best-dist]
  ;;(if (< 3 (count (first path))) (println "thread" (.getName (Thread/currentThread))))
  (cond
    (not (and (graph/contains-node? digraph start)
              (graph/contains-node? digraph end)
              (< (second path) @best-dist)))
    ;; Nodes not in digraph, or path is longer than shortest path, return nil
    nil
    (= start end)
    ;; At the end, update best-dist and return the path.
    (do (swap! best-dist #(min % (second path)))
        path)
    :else
    (let [next-edges (valid-edges digraph max-total-distance max-dist-outdoors
                                  (graph/get-edges-from-source digraph start))
          new-digraph (dissoc digraph start)]
      ;; Usually I'd use a map and reduce, but to avoid going down
      ;; pointless paths, each subsequent call to directed-dfs needs
      ;; an updated max-total-distance. That also guarantees that
      ;; new results from directed-dfs will be shorter paths than
      ;; old results (if it doesn't return nil), so or will take care of it.

      (let [map-type (if (< (count (first path)) 3) pmap map)]
        (choose-shortest-path
         (doall (map-type (partial next-dfs-call digraph start end
                           max-total-distance max-dist-outdoors
                           path best-dist nil)
                  next-edges)))) 
      )))


(defn directed-dfs
  "Perform the depth-first search.
  This function is here for parameter initialization."
  [digraph start end max-total-distance max-dist-outdoors]
  (let [best-path 
        (get-best-path digraph start end max-total-distance max-dist-outdoors [[start] 0] (atom ##Inf))]
    (shutdown-agents)
    best-path))

(defn test-dfs
  [opts]
  (let [digraph (graph/test-file-read)
        start "1"
        end "32"
        max-total-distance 999
        max-dist-outdoors 90]
    (println (time (directed-dfs digraph start end max-total-distance max-dist-outdoors)))))
