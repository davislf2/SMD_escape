package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tiles.MapTile;
import utilities.Coordinate;

public class MyAINavigation implements Navigation{
	
/* * * * * * VARIABLES * * * * * */
	
	//Coordinate return commands
	private final int EXPLORE = 0;
	private final int NEXT_ZONE = 1;
	
	//Where the car is currently
	private Coordinate turnPosition;
	
	//The car's current health
	private int turnHealth;
	
	//The car's current speed
	private float turnSpeed;
	
	//The current planed path
	private List<Coordinate> currentPath;
	
	//The internal mao
	private InternalMap map;
	
/* * * * * * Constructor * * * * * */
	
	public MyAINavigation(){
		//Initialise the path;
		this.currentPath = new ArrayList<Coordinate>();
		
		//Initialize the map
		this.map = new InternalMap();
	}
	
/* * * * * * METHODS * * * * * */	
	
	//Update the Navigator
	@Override
	public void update(HashMap<Coordinate, MapTile> view, Coordinate currentCoordinate){
		
		//Update the map
		this.map.update(view, currentCoordinate);
		
		//Update the record of position
		this.turnPosition = currentCoordinate;
	}
	
	//Get the next coordinate
	@Override
	public Coordinate getNextCoordinate() {
		
		int[] intCo = MapUtilities.intCoordinate(this.turnPosition);
		int newX = intCo[0] + 1;
		return MapUtilities.coordinateFromInt(newX, intCo[1]);
		
		/*int decisionMaker = 0;
		
		while(true){
			//If there is already a path planned return the next coordinate in that path
			if(this.currentPath.size() != 0){
			 	Coordinate nextCoordinate = this.currentPath.get(0);
			 	this.currentPath.remove(0);
			 	return nextCoordinate;
			}
			
			switch(decisionMaker){
				case EXPLORE:
					explore();
					break;
				case NEXT_ZONE:
					navToNextZone();
					break;
				default:
					return null;
			}
			decisionMaker ++;
		}*/
	}
	
	
	//Explore to an accessable, unvisited tile
	private void explore(){	
		
		//Get the next coordinate to be explored
		Coordinate nextDestination = this.map.nextToExplore();
		
		//If the next destination is not null plot a path to it
		if(nextDestination != null){
			plotPath(this.turnPosition, nextDestination, false);
		}
		
	}
	
	//Explore to next zone
	private void navToNextZone(){}
	
	//plot a path from startPos to any of the endposendPos and save it to the currentPath
	private void plotPath(Coordinate startPos, List<Coordinate> endPos, boolean traverseTraps){
		
	}
	
	//plot a path from startPos to  the endpos and save it to the CurrentPath
	private void plotPath(Coordinate startPos, Coordinate endPos, boolean traverseTraps){
		List<Coordinate> destList = new ArrayList<Coordinate>();
		destList.add(endPos);
		plotPath(startPos, destList, traverseTraps);
	}
	
/* * * * * * GETTERS AND SETTER * * * * * */
	
/* * * * * * HELPER FUNCTIONS * * * * * */

}
