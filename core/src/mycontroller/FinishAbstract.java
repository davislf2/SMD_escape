package mycontroller;

import java.util.List;

public class FinishAbstract extends TileAbstract{
	
/* * * * * * VARIABLES * * * * * */
	
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
