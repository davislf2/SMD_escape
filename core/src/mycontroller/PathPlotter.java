package mycontroller;

/** * * * * * * * * * * * * * *
 * 	Group 21
 * 	An interface used to plot a path between coordinates
 * 	Added to the original plan to increase system flexibility 
 * 	and increase cohesion within the MyAINavigation class
 ** * * * * * * * * * * * * * */

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import utilities.Coordinate;

public interface PathPlotter {
	
	//Plots a path through the map from startCo to EndCo, only traversing traps if traps = true
	public ArrayList<Coordinate> plotPath(Map<Coordinate,TileAbstract> map, List<Coordinate> endCos, Coordinate startCo, boolean traps);

}
