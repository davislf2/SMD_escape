package mycontroller;

/** * * * * * * * * * * * * * *
 * 	Group 21
 * 	A class representing an abstract tile from the world
 ** * * * * * * * * * * * * * */

import java.util.List;

import utilities.Coordinate;

public abstract class TileAbstract {
	
/* * * * * * VARIABLES * * * * * */
	
	//Added in order to avoid magic numbers
	//pseudo infinity value
	private final int PSEUDO_INFINITY = 999999;
	
	//Added in order to avoid magic numbers
	//Default traversal priority
	private final float DEFAULT_TRAVERSAL = 1;
	
	//whether the tile is accessible from the car
	protected boolean accessibleFromCar;
	
	//How many zones the tile is away from the finish
	private int zonesFromFinish;
	
	//Whether the tile has been visited by the car
	private boolean visited;
	
	//The priority of when it should be traversed
	private float traversePriority;
	
	//Whether the tile is next to trap. It's possible to exit
    protected boolean nextToTrap;
    
/* * * * * * Constructor * * * * * */
	
	public TileAbstract(){
		//initialize attributes to defaults
		this.accessibleFromCar = false;
		this.zonesFromFinish = PSEUDO_INFINITY;
		this.visited = false;
		this.traversePriority = DEFAULT_TRAVERSAL;
		this.nextToTrap = false;
	}
	
/* * * * * * METHODS * * * * * */
	
/* * * * * * GETTERS AND SETTERS * * * * * */
	
	//Get whether the tile is accessible from the car
	public boolean accessible(){
		return this.accessibleFromCar;
	}
	
	//Set whether the tile is accessible from the car
	public void setAccessible(boolean accessibility){
		this.accessibleFromCar = accessibility;
	}
	
	//Get the number of zone sfrom the finish
	public int getZonesFromFinish(){
		return this.zonesFromFinish;
	}
	
	//This was added after the plan to help with the InternalMap update function
	//Was added after we made the decision to not explore tiles beside walls in order to best
	//Avoid crashes
	//Spread the tiles zone to its neighbours
	public void spreadZoneCount(List<TileAbstract> adjacentTiles){}
	
	//Set the tile zoneValue
	public void setZoneCount(int zonecount){
		if(zonecount<this.zonesFromFinish){
			this.zonesFromFinish = zonecount;
		}
	}
	
	//has the tile been visited
	public boolean visited(){
		return this.visited;
	}
	
	//Mark the tile as visited
	public void visit(){
		this.visited = true;
	}
	
	//Get the traversal property of the tile
	public float getTraversalPriority(){
		return this.traversePriority;
	}
	
	//Get if the tile at the coordinate is safe to go straight on
	public boolean isSafeToGoStraightOn(Coordinate coordinate, int health, float speed){
		return false;
	}
	
	//Get if the tile at the coordinate is safe to go straight on
	public boolean isSafeToTurnOn(Coordinate coordinate, int health, float speed){
		return false;
	}
	
    //has the tile been visited
    public boolean getNextToTrap(){
        return this.nextToTrap;
    }
    
    //Mark the tile as visited
    public void setNextToTrap(){
        this.nextToTrap = true;
    }

}
