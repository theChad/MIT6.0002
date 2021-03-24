from graph import Digraph, Node, WeightedEdge
import ps2


mit_digraph = ps2.load_map("../resources/mit_map.txt")
best_path = ps2.get_best_path(mit_digraph, Node("8"), Node("50"),[],9999,99999,[])
print("Best path: ")
print(best_path)
