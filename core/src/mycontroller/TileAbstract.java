package mycontroller;

import java.util.List;

public abstract class TileAbstract {
	
/* * * * * * VARIABLES * * * * * */
	
	//pseudo infinity value
	private final int PSEUDO_INFINITY = 999999;
	
	//Default traversal priority
	private final float DEFAULT_TRAVERSAL = 1;
	
	//whether the tile is accessible from the car
//	private boolean accessibleFromCar;
	protected boolean accessibleFromCar;
	
	//How many zones the tile is away from the finish
	private int zonesFromFinish;
	
	//Whether the tile has been visited by the car
//	private boolean visited;
	protected boolean visited;
	
	//The priority of when it should be traversed
//	private float traversePriority;
	protected float traversePriority;
	
	//Whether the tile has been explored by the navigator
	protected boolean explored;
	
	//Whether the tile is next to trap. It's possible to exit
    protected boolean nextToTrap;
    
    //Distance from a position to this tile
    protected float distToTile;
	
/* * * * * * Constructor * * * * * */
	
	public TileAbstract(){
		//initialize attributes to defaults
		this.accessibleFromCar = false;
		this.zonesFromFinish = PSEUDO_INFINITY;
		this.visited = false;
		this.traversePriority = DEFAULT_TRAVERSAL;
		this.explored = false;
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
	
	//Set the traversal property of the tile
    public void setTraversalPriority(float p){
      this.traversePriority = p;
    }	
    
    //has the tile been visited
    public boolean explored(){
        return this.explored;
    }
    
    //Mark the tile as visited
    public void explore(){
        this.explored = true;
    }
    
    //has the tile been visited
    public boolean getNextToTrap(){
        return this.nextToTrap;
    }
    
    //Mark the tile as visited
    public void setNextToTrap(){
        this.nextToTrap = true;
    }
 
    //get distance to this tile
    public float getDistToTile(){
       return this.distToTile;
    }  
    
    //set distance to this tile
    public void setDistToTile(float f){
        this.distToTile = f;
    }
    
  
}
