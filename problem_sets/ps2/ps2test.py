from graph import Digraph, Node, WeightedEdge
import ps2
import time


mit_digraph = ps2.load_map("mit_map.txt")
start_time = time.time()
#best_path = ps2.get_best_path(mit_digraph, Node("1"), Node("32"),[],90,9999,[])
best_path = ps2.directed_dfs(mit_digraph, Node("1"), Node("32"), 999,0)
end_time = time.time()
print("Best path: ")
print(best_path)
print("time: ", 1000*(end_time - start_time))
print ("count: ", ps2.counter)
