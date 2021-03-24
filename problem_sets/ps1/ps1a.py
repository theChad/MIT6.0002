###########################
# 6.0002 Problem Set 1a: Space Cows 
# Name:
# Collaborators:
# Time:

from ps1_partition import get_partitions
import time
import functools

#================================
# Part A: Transporting Space Cows
#================================

# Separate out the cows from their weight in one line
def cow_line_splitter(cow_line):
    """
    Turn a single line from the cow data file into a list of [cow, weight].
    """
    return cow_line.strip().split(",")
    

# Problem 1
def load_cows(filename):
    """
    Read the contents of the given file.  Assumes the file contents contain
    data in the form of comma-separated cow name, weight pairs, and return a
    dictionary containing cow names as keys and corresponding weights as values.

    Parameters:
    filename - the name of the data file as a string

    Returns:
    a dictionary of cow name (string), weight (int) pairs
    """
 
    with open(filename) as cow_file:
        cow_lines = cow_file.readlines()
        # List of [cow, weight] datapoints.
        cows = [cow_line_splitter(cow_line) for cow_line in cow_lines]
        # Dictionary of {cow: weight} pairs.
        cow_dict = {cow[0]:int(cow[1]) for cow in cows}
    return cow_dict

# Problem 2
def greedy_cow_transport(cows,limit=10):
    """
    Uses a greedy heuristic to determine an allocation of cows that attempts to
    minimize the number of spaceship trips needed to transport all the cows. The
    returned allocation of cows may or may not be optimal.
    The greedy heuristic should follow the following method:

    1. As long as the current trip can fit another cow, add the largest cow that will fit
        to the trip
    2. Once the trip is full, begin a new trip to transport the remaining cows

    Does not mutate the given dictionary of cows.

    Parameters:
    cows - a dictionary of name (string), weight (int) pairs
    limit - weight limit of the spaceship (an int)
    
    Returns:
    A list of lists, with each inner list containing the names of cows
    transported on a particular trip and the overall list containing all the
    trips
    """

    # Get new sorted list; ensure it's still a list of lists rather than tuples.
    sorted_cows = list(map(list, sorted(cows.items(), key=lambda item: item[1], reverse=True)))
    transports = []
    while sorted_cows:
        # New barge
        transport = []
        remaining_weight = limit
        while remaining_weight > 0:
            # Get the heaviest cow that'll fit on the barge.
            next_cow = next(filter(lambda cow: cow[1] <= remaining_weight, sorted_cows), None)
            
            if next_cow:
                # There is a cow that fits, so add it to the transport and
                # remove it from the remaining cows
                transport.append(next_cow)
                sorted_cows.remove(next_cow)
                remaining_weight -= next_cow[1]
            else:
                # No cow fits, so move onto the next transport
                break
        # Add the full transport to the list of transports
        transports.append(transport)
            
    return transports

# Problem 3
def brute_force_cow_transport(cows,limit=10):
    """
    Finds the allocation of cows that minimizes the number of spaceship trips
    via brute force.  The brute force algorithm should follow the following method:

    1. Enumerate all possible ways that the cows can be divided into separate trips 
        Use the given get_partitions function in ps1_partition.py to help you!
    2. Select the allocation that minimizes the number of trips without making any trip
        that does not obey the weight limitation
            
    Does not mutate the given dictionary of cows.

    Parameters:
    cows - a dictionary of name (string), weight (int) pairs
    limit - weight limit of the spaceship (an int)
    
    Returns:
    A list of lists, with each inner list containing the names of cows
    transported on a particular trip and the overall list containing all the
    trips
    """
    # Empty dictionary, return an empty list
    if not(cows): return []
    # Convert the dictionary into a list of tuples (tuples are hashable, and
    # get_partitions uses sets).
    cow_list = [(k,v) for k,v in cows.items()]
    # Generator of all possible partitions
    cow_partitions = get_partitions(cow_list)
    for partition in cow_partitions:
        # Create a list of transport weights for this partition
        barge_weights = [sum([cow[1] for cow in barge]) for barge in partition]
        # If all transports are within the limit, we're done.
        if max(barge_weights) <= limit:
            return partition

    # Some cow must be over the limit.
    return None
        
# Problem 4
def compare_cow_transport_algorithms():
    """
    Using the data from ps1_cow_data.txt and the specified weight limit, run your
    greedy_cow_transport and brute_force_cow_transport functions here. Use the
    default weight limits of 10 for both greedy_cow_transport and
    brute_force_cow_transport.
    
    Print out the number of trips returned by each method, and how long each
    method takes to run in seconds.

    Returns:
    Does not return anything.
    """
    algos = [greedy_cow_transport, brute_force_cow_transport]
    cows = load_cows("/ps1_cow_data.txt")
    for algo in algos:
        start = time.time()
        algo(cows)
        stop = time.time()
        print(algo.__name__, "ran in", 1000*(stop-start), "ms.")
    pass
