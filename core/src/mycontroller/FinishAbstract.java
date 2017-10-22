package mycontroller;

/** * * * * * * * * * * * * * *
 * 	Group 21
 * 	Class which representing a finish tile
 ** * * * * * * * * * * * * * */

import java.util.List;

/**We changed this so that it extended from the floorabstract 
 * This was necessary to make the explore function work
 */
public class FinishAbstract extends FloorAbstract{
	
/* * * * * * VARIABLES * * * * * */
	
	//Denotes the finish as it its own zone
	public static final int FINISH_ZONE = 0;
	
/* * * * * * CONSTRUCTOR * * * * * */
	
	public FinishAbstract(){
		super();
		this.setZoneCount(FINISH_ZONE);
	}
	
/* * * * * * METHODS * * * * * */
	
	//String representation of the tile
	@Override
	public String toString(){
		return "@";
	}
	
/* * * * * * GETTERS AND SETTERS * * * * * */
	
	//Spread the zone to its neighbours
	@Override
	public void spreadZoneCount(List<TileAbstract> adjacentTiles){
		for(TileAbstract abstraction: adjacentTiles){
			abstraction.setZoneCount(this.getZonesFromFinish());
		}
	}

}
