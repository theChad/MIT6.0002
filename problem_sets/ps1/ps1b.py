###########################
# 6.0002 Problem Set 1b: Space Change
# Name:
# Collaborators:
# Time:
# Author: charz, cdenise

#================================
# Part B: Golden Eggs
#================================

# Problem 1
def dp_make_weight(egg_weights, target_weight, memo = {}):
    """
    Find number of eggs to bring back, using the smallest number of eggs. Assumes there is
    an infinite supply of eggs of each weight, and there is always a egg of value 1.
    
    Parameters:
    egg_weights - tuple of integers, available egg weights sorted from smallest to largest value (1 = d1 < d2 < ... < dk)
    target_weight - int, amount of weight we want to find eggs to fit
    memo - dictionary, OPTIONAL parameter for memoization (you may not need to use this parameter depending on your implementation)
    
    Returns: int, smallest number of eggs needed to make target weight
    """
    # Recursively find minimum eggs.
    # Base case: target weight is zero. Already full.
    if target_weight==0: return 0
    # Check to see if this call is already cached.
    if memo.get((egg_weights, target_weight)):
        return memo[(egg_weights, target_weight)]
    # Possible new target weights, depending on which
    # egg is added.
    new_target_weights = [target_weight - egg_weight
                          for egg_weight in egg_weights
                          if egg_weight <= target_weight]
    # List of possible numbers of eggs left to put in bag
    num_eggs = [dp_make_weight(egg_weights, t_weight, memo)
                for t_weight in new_target_weights]
    # Pick the smallest number remaining,
    # increment due to choosing a weight to add
    # in the current call.
    min_eggs = 1 + min(num_eggs)
    # Update the memoization dictionary
    memo[(egg_weights, target_weight)] = min_eggs
    return min_eggs
    

# EXAMPLE TESTING CODE, feel free to add more if you'd like
#if __name__ == '__main__':
egg_weights = (1, 5, 10, 25)
n = 99
print("Egg weights = (1, 5, 10, 25)")
print("n = 99")
print("Expected ouput: 9 (3 * 25 + 2 * 10 + 4 * 1 = 99)")
print("Actual output:", dp_make_weight(egg_weights, n))
print()
