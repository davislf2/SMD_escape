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
	
	//The navigation strategy between explore(0) or next zone(1)
	private int decisionMaker;
	
/* * * * * * Constructor * * * * * */
	
	public MyAINavigation(){
		//Initialise the path;
		this.currentPath = new ArrayList<Coordinate>();
		
		//Initialize the map
		this.map = new InternalMap();
		
		//Initialize the decision
		this.decisionMaker = 0;
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
//		explore();
//		int newX = intCo[0] + 1;
//		return MapUtilities.coordinateFromInt(newX, intCo[1]);
		Coordinate boundary = this.map.zoneDiscovered(turnPosition);
		
	    if(boundary!=null){
//	        return explore(boundary);
	        int[] intBd = MapUtilities.intCoordinate(boundary);
	        System.out.println("boundary:("+intBd[0]+","+intBd[1]+")");
	        return boundary;
	    }else{
	        return navToNextZone();
	    }

	        
//        switch(decisionMaker){
//          case 0:   // Explore
//              explore();
//              break;
//          case 1:   // Navigate to the next zone
//              navToNextZone();
//              break;
//          default:
//              return null;
//        }
//        int newX = intCo[0] + 1;
//        return MapUtilities.coordinateFromInt(newX, intCo[1]);
            
	    
//      int decisionMaker = 0;        
//		while(true){
//			//If there is already a path planned return the next coordinate in that path
//			if(this.currentPath.size() != 0){
//			 	Coordinate nextCoordinate = this.currentPath.get(0);
//			 	this.currentPath.remove(0);
//			 	return nextCoordinate;
//			}
//			
//			switch(decisionMaker){
//				case EXPLORE:
//					explore();
//					break;
//				case NEXT_ZONE:
//					navToNextZone();
//					break;
//				default:
//					return null;
//			}
//			decisionMaker ++;
//		}
	}
	
	
	//Explore to an accessable, unvisited tile
	private Coordinate explore(Coordinate boundary){	
	  
	    int[] intCo = MapUtilities.intCoordinate(this.turnPosition);
	    int newY = intCo[1] - 1;
	    return MapUtilities.coordinateFromInt(intCo[1], newY);
	    
//    	//Get the next coordinate to be explored
//    	Coordinate nextDestination = this.map.nextToExplore();
//		
//		//If the next destination is not null plot a path to it
//		if(nextDestination != null){
//			plotPath(this.turnPosition, nextDestination, false);
//		}
		
	}
	
	//Explore to next zone
	private Coordinate navToNextZone(){
	    System.out.println("navToNextZone");
//	    Coordinate c = new Coordinate("7,13"); // 7,14   5,15
	    Coordinate c = this.map.shortestPassTrap(turnPosition);
        System.out.println("this.map.destNextToTrap:("+c.x+","+c.y+")");
//	    System.out.println("this.map.destNextToTrap:("+this.map.destNextToTrap.x+","+this.map.destNextToTrap.y+")");
//	    return this.map.destNextToTrap;
	    return c;
	}
	
	/*
		
	//plot a path from startPos to any of the endpos and save it to the currentPath
	private void plotPath(Coordinate startPos, List<Coordinate> endPos, boolean traverseTraps){
        List<Coordinate> destList = new ArrayList<Coordinate>();
	
	}
	
	//plot a path from startPos to the endpos and save it to the CurrentPath
	private void plotPath(Coordinate startPos, Coordinate endPos, boolean traverseTraps){
		List<Coordinate> destList = new ArrayList<Coordinate>();
		destList.add(endPos);
		plotPath(startPos, destList, traverseTraps);
	}
	*/
	
/* * * * * * GETTERS AND SETTER * * * * * */
	
/* * * * * * HELPER FUNCTIONS * * * * * */

}
