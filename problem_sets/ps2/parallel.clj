(ns ps2.parallel
  (:require [ps2.graph :as graph])
  (:gen-class))

(def counter (atom 0))

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
   path best-dist next-edge]
  (let [new-max-total-dist (-  max-total-distance
                              (:total-distance next-edge))
        new-max-dist-outdoors (- max-distance-outdoors
                                 (:outdoor-distance next-edge))
        new-start (:end-node next-edge)
        new-path (update-path path (:end-node next-edge) (:total-distance next-edge))]
    (get-best-path digraph new-start end new-max-total-dist
                   new-max-dist-outdoors new-path best-dist)
    ))

(defn choose-shortest-path
  "Choose the shortest from among a collection of paths.
  Nil if no paths"
  [paths]
  (if (every? nil? paths) nil
      (apply min-key second (filter some? paths))))

(defn path-too-long?
  "Determine whether the current path is too long, given
  the best distance. Check both the total best distance
  for a finished path and the best distance for the
  endpoint of the given path."
  [path best-dist]
  (or (if-let [final-best-dist (:final @best-dist)]
        (>= (second path) final-best-dist))
      (if-let [node-best-dist (get @best-dist (last (first path)))]
        (>= (second path) node-best-dist))))

(defn update-node-dist
  "Update the distance for a particular node in a hashmap.
  Only update if the new-dist is a new minimum."
  [node-dist-map node new-dist]
  (assoc node-dist-map node
         (if (get node-dist-map node)
           (min (node-dist-map node) new-dist)
           new-dist)))

(defn update-best-dist
  "Update the best-dist hashmap.
  path: [[path] path-dist]
  best-dist: hashmap of {node: shortest-distance-to-node}
  node: last node of path, or :final if at final goal."
  [path best-dist node]
  (swap! best-dist #(update-node-dist % node (second path))))


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
  (swap! counter inc)
  ;;(println path)
  ;;(println @best-dist)
  ;;(if (< 3 (count (first path))) (println "thread" (.getName (Thread/currentThread))))
  (cond
    (not (and (graph/contains-node? digraph start)
              (graph/contains-node? digraph end)
              (not (path-too-long? path best-dist))))
    ;; Nodes not in digraph, or path is longer than shortest path, return nil
    nil
    (= start end)
    ;; At the end, update best-dist and return the path.
    (do (update-best-dist path best-dist :final)
        path)
    :else
    (let [next-edges (valid-edges digraph max-total-distance max-dist-outdoors
                                  (graph/get-edges-from-source digraph start))
          new-digraph (graph/remove-node digraph start)]

      ;; First update the best-dist hashmap, since we're guaranteed to be
      ;; at the shortest route to the current (start) node.
      (update-best-dist path best-dist start)

      ;; Usually I'd use a map and reduce, but to avoid going down
      ;; pointless paths, each subsequent call to directed-dfs needs
      ;; an updated max-total-distance. That also guarantees that
      ;; new results from directed-dfs will be shorter paths than
      ;; old results (if it doesn't return nil), so or will take care of it.

      (if (empty? next-edges)
        nil
        (let [map-type (if (< (count (first path)) 1) pmap map)]
          (choose-shortest-path
           (doall (map-type (partial next-dfs-call new-digraph start end
                                     max-total-distance max-dist-outdoors
                                     path best-dist)
                            next-edges))))
        ) 
      )))

(defn directed-dfs
  "Perform the depth-first search.
  This function is here for parameter initialization."
  [digraph start end max-total-distance max-dist-outdoors]
  (let [best-path 
        (get-best-path digraph start end max-total-distance max-dist-outdoors [[start] 0] (atom {}))]
    (shutdown-agents)
    best-path))

(defn test-dfs
  [opts]
  (let [digraph (graph/test-file-read)
        start "1"
        end "32"
        max-total-distance 999
        max-dist-outdoors 0]
    (println (time (directed-dfs digraph start end max-total-distance max-dist-outdoors))))
  (println "counter:" @counter))

(defn -main
  [opts]
  (test-dfs nil))
