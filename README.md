# ForestFireSimulation
A forest fire simulation with 2D graphics and random tree generation.

Copyright Peter "Felix" Nguyen and Emmanuel Medina Lopez

# Bugs
1. [FIXED] ignited and newlyIgnitedTrees are added even though they are not on fire. Happens when user clicks on an area that has trees that have already been burnt.
2. [POSSIBLY FIXED] Sometimes trees are not cleared from ignitedTrees
3. [FIXED] Sometimes, a tree positioned beside the northern edge of the map can not be ignited when clicked.
4. Graphically, burnt tree trunks position are different from healthy tree trunks.

#Issues
1. Clicking needs to be more accurate. Should target nearest tree instead of trees within click radius.

# Ideas
1. [DONE] Click on area to start fire at given location
2. Prompt user for numTrees, burnRadius, burnSpeed
3. Notify the end of forest fire by checking for lack of fire.
4. Draw polygon obstacles and terrain.
5. Different trees have different spread rates.
6. Rain, wind, lightning, and weather.
7. Critters, tree size, and elevation.
8. Square bushes and shrubs.
9. Flammable structures.
10. Dynamic weather (rng or God-mode).
11. Map editor and map loader.
12. [DONE] If burning and nearbyTrees added, remove from list of burning trees (or have two sets of burning trees: new burning trees and old burning trees.
