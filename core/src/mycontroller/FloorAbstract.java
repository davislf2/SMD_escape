package mycontroller;

/** * * * * * * * * * * * * * *
 * 	Group 21
 * 	Class which representing a floor tile
 ** * * * * * * * * * * * * * */

import java.util.List;

public class FloorAbstract extends TileAbstract{
	
	//Is by wall, 
	//added after original plan, added when we made the decision we wanted to avoid going by walls
	//Corrseponding getters and setters also added
	private boolean isByWall;
	
/* * * * * * CONSTRUCTOR * * * * * */
	
	public FloorAbstract(){
		super();
		this.isByWall = false;
	}
	
/* * * * * * METHODS * * * * * */	
	
	//Spread the zone to its neighbours
	@Override
	public void spreadZoneCount(List<TileAbstract> adjacentTiles){
		for(TileAbstract abstraction: adjacentTiles){
			abstraction.setZoneCount(this.getZonesFromFinish());
		}
	}
	
	//String representation of the tile
	@Override
	public String toString(){	  
        if(this.accessibleFromCar){
            return "_";
        }else{
            return " ";
	    }
	}
	
/* * * * * * GETTERS AND SETTERS * * * * * */
	
	//Whether or not the floor is by a wall
	public boolean isByWall(){
		return this.isByWall;
	}
	
	//Show that the floor is by a wall
	public void setByWall(){
		this.isByWall = true;
	}
}
