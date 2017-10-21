package mycontroller;

import java.util.HashMap;

import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial.Direction;

public interface Navigation {
	
	//update the navigation
	public void update(HashMap<Coordinate, MapTile> view, Coordinate currentCoordinate);
	
	//get the next coordinate from the navigation
	public Coordinate getNextCoordinate();

	public void setDirection(Direction travelDirection);
}
