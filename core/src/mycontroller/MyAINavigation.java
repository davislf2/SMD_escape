package mycontroller;

/** * * * * * * * * * * * * * *
 * 	Group 21
 * 	My implementation of the Navigation interface
 ** * * * * * * * * * * * * * */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tiles.MapTile;
import utilities.Coordinate;

public class MyAINavigation implements Navigation{
	
/* * * * * * VARIABLES * * * * * */
	
	//Constants added to avoid magic numbers
	//Coordinate return commands
	private final int EXPLORE_NO_WALLS = 0;
	private final int EXPLORE_WALLS = 2;
	private final int NEXT_ZONE = 1;
	
	//Where the car is currently
	private Coordinate turnPosition;
	
	//The current planed path
	private List<Coordinate> currentPath;
	
	//The current health
	private int turnHealth;
	
	//The current turnSpeed
	private float turnSpeed;
	
	//The internal mao
	private InternalMap map;
	
	/**
	 * NB we have refactored the crux of the plotPath function to work through 
	 * a newly implemented PathPlotter interface
	 * We did this as the Navigation class became unwildly and uncohesive,
	 * And the added interface made out system more flexible
	 **/
	//The path planner
	private PathPlotter plotter;
	
/* * * * * * Constructor * * * * * */
	
	public MyAINavigation(){
		//Initialise the path;
		this.currentPath = new ArrayList<Coordinate>();
		
		//Initialize the map
		this.map = new InternalMap();
		
		//Set dijkstras as the path plotter
		this.plotter = new Dijkstras();
	}
	
/* * * * * * METHODS * * * * * */	
	
	//Update the Navigator
	@Override
	public void update(HashMap<Coordinate, MapTile> view, Coordinate currentCoordinate, int health, float speed){
		
		//Update the denormalized variables
		this.turnHealth = health;
		this.turnSpeed = speed;
		
		//Update the map
		this.map.update(view, currentCoordinate);
		System.out.println("===================Map update");
		//Update the record of position
		this.turnPosition = currentCoordinate;
	}
	
	//Get the next coordinate
	@Override
	public Coordinate getNextCoordinate() {
		
		int decisionMaker = 0;
		
		while(true){
			//If there is already a path planned return the next coordinate in that path
			if(this.currentPath.size() != 0){
			 	Coordinate nextCoordinate = this.currentPath.get(0);
			 	this.currentPath.remove(0);
			 	return nextCoordinate;
			}
			
			switch(decisionMaker){
				case EXPLORE_NO_WALLS:
				    System.out.println("============EXPLORE_NO_WALLS============");
					explore(false);
					break;
				case EXPLORE_WALLS:
				    System.out.println("============EXPLORE_WALLS============");
					explore(true);
					break;
				case NEXT_ZONE:
				    System.out.println("============NEXT_ZONE============");
					navToNextZone();
					break;
				default:
					return null;
			}
			decisionMaker ++;
		}
	}
	
	
	//Explore to an accessible, unvisited tile
	private void explore(boolean byWalls){	
		
		//Get the next coordinate to be explored
		Coordinate nextDestination = this.map.nextToExplore(this.turnPosition, byWalls);
		System.out.println("nextDestination:("+nextDestination.x+","+nextDestination.y+")");
		//If the next destination is not null plot a path to it
		if(nextDestination != null){
			plotPath(this.turnPosition, nextDestination, false);
		}
		
	}
	
	//Explore to next zone
	private void navToNextZone(){
		
		//Get the list of coordinates in the desired next zone
		List<Coordinate> nextZoneCos = this.map.getNextZones();
		
		//If the next list is not empty plot a path to it
		if(nextZoneCos.size()!=0){
			plotPath(this.turnPosition, nextZoneCos, true);
		}
	}
	
	//The body of this function was refactored through a new interface
	//plot a path from startPos to any of the endposendPos and save it to the currentPath
	private void plotPath(Coordinate startPos, List<Coordinate> endPos, boolean traverseTraps){
		this.currentPath = this.plotter.plotPath(this.map.getMapMemory(), endPos, startPos, traverseTraps);
		
		System.out.println("curPos:("+this.turnPosition.x+","+this.turnPosition.y+"), Speed:"+this.turnSpeed);
		System.out.println("startPos:("+startPos.x+","+startPos.y+")");
		System.out.println("endPos:");
		for(Coordinate c:endPos){
		    System.out.print("("+c.x+","+c.y+") ");
		}
		System.out.println("");
		if(traverseTraps){
		    System.out.println("traverseTraps");
		}
		System.out.println("currentPath");
		for(Coordinate c:currentPath){
		    System.out.print("("+c.x+","+c.y+") ");
		}
		System.out.println("");
	}
	
	//Added to increase polymorphism
	//plot a path from startPos to  the endpos and save it to the CurrentPath
	private void plotPath(Coordinate startPos, Coordinate endPos, boolean traverseTraps){
		List<Coordinate> destList = new ArrayList<Coordinate>();
		destList.add(endPos);
		plotPath(startPos, destList, traverseTraps);
	}

}
