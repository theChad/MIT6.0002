(ns ps2.graph
  (:require [clojure.string :as s]))

;;;; Digraph building functions

(defrecord Edge [start-node end-node total-distance outdoor-distance])
(defrecord Digraph [nodes edges])

(defn nodes-in-edge
  "Return the nodes in a given edge."
  [edge]
  (map #(% edge) [:start-node :end-node]))

(defn add-edge-only
  "Internal. Add an edge to a digraph (but not nodes)."
  [digraph edge]
  (if (every? (:nodes digraph) (nodes-in-edge edge))
    (update-in digraph [:edges (:start-node edge)]
               (fnil #(conj % edge) []))
    (throw (AssertionError. "Node not in graph.")))
  )

(defn add-node
  "Add a node to a digraph."
  [digraph node]
  (update digraph :nodes #(conj % node)))

(defn remove-node
  "Remove a node from the digraph.
  For the sake of efficiency, only removes
  the node from the node set and edges keys.
  Node may still be an end-node of some edges."
  [digraph node]
  (-> digraph
      (update :edges dissoc node)
      (update :nodes disj node)))

(defn add-edge
  "Add an edge (and nodes if required) to a digraph."
  [digraph edge]
  (add-edge-only (reduce #(add-node %1 %2) digraph (nodes-in-edge edge)) edge))

;;; Reading in a digraph from a file
;;; Format should be lines of
;;; start-node-name end-node-name total-distance outside-distance

(defn edge-dist-to-int
  "Convert the last two elements of a seq of edge
  parameters into ints."
  [edge-seq]
  [(first edge-seq)
   (second edge-seq)
   (Integer/parseInt (nth edge-seq 2))
   (Integer/parseInt (nth edge-seq 3))])

(defn edge-from-string
  "Return an edge object from a string of parameters
  edge-str is space delimited and of the form
  start-node-name end-node-name total-distance outside-distance"
  [edge-str]
  (apply ->Edge (edge-dist-to-int (s/split edge-str #" +"))))

(defn digraph-from-file
  "Return a digraph from a file.
  The file should have rows of edges."
  [file-name]
  (->> file-name
       slurp
       s/split-lines
       (map edge-from-string)
       (reduce add-edge (Digraph. #{} {}))))

;;; Access functions
(defn get-edges-from-source
  "Return a collection of edges originating at start-node."
  [digraph start-node]
  (get (:edges digraph) start-node))

(defn contains-node?
  "True if the digraph contains the node in its node set."
  [digraph node]
  (contains? (:nodes digraph) node))
;;; Testing some of the functions

(defn test-add
  []
  (let [edge (Edge. 'a 'b 10 4)
        digraph (Digraph. #{} {})]
    (add-edge digraph edge)))

(defn test-file-read
  []
  (let [file-name "ps2/mit_map.txt"]
    (digraph-from-file file-name)))
