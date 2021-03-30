# Problem Set 3

## Python

I've only implemented the python version here. There's a lot of base and testing code provided. All of my code is in [ps3.py](ps3.py). I chose to implement the tiles as a list of lists, and most of it was pretty straightforward from the suggestions provided in the comments. The one function with any contention was `get_random_position` for the furnished room. I first created a list of unfurnished tiles (which could be created in `add_furniture_to_room` to avoid computing the same list multiple times, but that function specified it was not to be edited). Then I used `random.choice` on that list of unfurnished tiles. The alternative - picking tiles at random from the entire room until finding an unfurnished one - is probably fine and just as fast (or faster) for most cases. But as the room grows in size and gets more furnished, the time it takes could grow pretty quickly, and the theoretically infinite time horizon also makes me nervous.

## Testing notes
The file provided for testing, [ps3_tests_f16.py](ps3_tests_f16.py), uses the deprecated library *imp* to load a complied python module, *test.pyc*. That module may also require an older version of python; I had issues when trying to use a different library to import *test.pyc* as well. I ended up switching my environment to use python 3.5.6, which has solved those issues.

There's a small int vs. float issue in the final testing. You could do some data sanitizing to ensure everything is of the correct type; I assumed parameters would be passed in as the correct type. This was only a problem in the final function provided in [ps3.py](ps3.py), `show_plot_room_shape`. I changed the height line to be `height = int(300/width)`.
