# ForestFireSimulation
A forest fire simulation with 2D graphics and random tree generation.

Copyright Peter "Felix" Nguyen and Emmanuel Medina Lopez

#Current Features
1. Can simulate 50,000 trees.

# Notes
1. The ReplaySlider is partially working, but it is not 100% completed.

# Bugs
1. [FIXED] ignited and newlyIgnitedTrees are added even though they are not on fire. Happens when user clicks on an area that has trees that have already been burnt.
2. [POSSIBLY FIXED] Sometimes trees are not cleared from ignitedTrees
3. [FIXED] Sometimes, a tree positioned beside the northern edge of the map can not be ignited when clicked.
4. [POSSIBLY FIXED] Graphically, burnt tree trunks position are different from healthy tree trunks.
5. [FIXED] Clicking to start a fire does not work correctly (does not logically trigger nearby trees in a logical way).

# Issues
1. [NOT SURE IF I WANT THIS] Clicking needs to be more accurate. Should target nearest tree instead of trees within click radius.
2. [IMPORTANT] Need to make sure to re-render a VolatileImage if it is gone from memory.

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
13. Add a skip button to skip to next instance of a click (Replay Mode)
14. Add seek feature and a timer display
15. Status of map: remaining trees, number of trees on fire, number of trees on map, dead trees.
15. [In-progress] allow unlimited replay of the same replay
16. Save map replays (requires original map).

# Links that helped me

## Hardware Accelerated Graphics
1. http://content.gpwiki.org/index.php/Java:Tutorials:VolatileImage
2. https://docs.oracle.com/javase/8/docs/api/java/awt/image/VolatileImage.html
3. https://docs.oracle.com/javase/tutorial/2d/advanced/compositing.html

## Miscellaneous 
1. http://stackoverflow.com/questions/3680221/how-can-i-get-the-monitor-size-in-java
2. http://stackoverflow.com/questions/11831029/mousemotionlistener-in-child-component-disables-mouselistener-in-parent-componen

## Layout Help
1. http://stackoverflow.com/questions/2411197/setting-panel-at-center-of-screen-by-using-layout

## Look and Feel
http://stackoverflow.com/questions/15260484/java-swing-how-to-change-the-font-size-on-a-jpanels-titledborder
http://stackoverflow.com/questions/4631021/how-to-set-transparent-background-of-jdialog

## Graphics and Drawing
http://www.coderanch.com/t/336616/GUI/java/Center-Align-text-drawString