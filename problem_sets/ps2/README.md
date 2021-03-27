# Problem Set 2

## Python

The python version of this project follows the advice of the homework.

## Clojure

I have two different clojure versions. The original, [ps2.clj](ps2.clj), builds from the bottom up. Each subsequent recursive call decrements the max distance numbers, as well pruning the digraph (removing the previous starting node), as though it were an entirely new problem with a new start node. 

The parallel clojure version, [parallel.clj](ps2.clj) was an attempt to run the search in parallel. Initially I created a new thread for each recursive call, but that actually took *much* longer. Presumably creation of threads can be somewhat expensive. Limiting new threads to the first level or two of the tree does give a bit of a speedup, though not as much as I might have hoped. I used an atom, `best-dist`, to keep track of the shortest overall path across threads.

There's another redundancy in the tree search, when you reach a node you've already visited in a more efficient way. The original algorithm does check for cycles, but not for repeats of a node that was in a previous search. I updated the atom `best-dist` to be a map that includes the shortest path to each visited node (across all paths covered so far) and the remaining, "unused" outdoor distance. If we've already encountered this node in a shorter distance (with at least as much outdoor distance remaining), we can prune this branch.

## Performance

Even with the extra redundancy cut out of the clojure program, it's still several times slower than the python version. It is a slightly different algorithm, and requires creating a new, pruned digraph each time, as well as atomic access. Those are my best guesses for where the performance distance might lie.
