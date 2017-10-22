package mycontroller;

/** * * * * * * * * * * * * * *
 * 	Group 21
 * 	An interface the controller uses to navigate
 ** * * * * * * * * * * * * * */

import java.util.HashMap;

import tiles.MapTile;
import utilities.Coordinate;

public interface Navigation {
	
	//get the next coordinate from the navigation
	public Coordinate getNextCoordinate();
	
	//Updates the navigation
	void update(HashMap<Coordinate, MapTile> view, Coordinate currentCoordinate, int health, float speed);

}
