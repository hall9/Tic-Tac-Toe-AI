Tic-Tac-Toe AI
=================

By: Alexander Hall

This Tic-Tac-Toe AI learns from moves it's seen and if that set of moves/configuration. It uses a Hash Table to keep the board configurations its seen already. It determines the best next move based on the probability of winning the game, Yet the only faw is that it takes a few games to really see what wins and what does not. In other words, the more games played (i.e. the more moves/configuration seen) the more accurate the probabilities will refelect.

Project Details
-----------

* "Full Details Page":http://www.csee.umbc.edu/courses/undergraduate/341/spring12/projects/project4/index.shtml
* To implement a hash table ADT from scratch
* To design a hash function for game configurations 
* To create and extend ADTs to serve a particular purpose
* To make sound design choices
* Optionally, to create an interactive GUI

How to Play
-----------

* Build the project using the Build.xml. execute '$ ant compile'
* To Run execute '$ ant -Dargs="-h -s -d -15" run'

**Command line arguments:**
* -h 
 