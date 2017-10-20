package mycontroller;

import java.util.List;

public class FloorAbstract extends TileAbstract{
	
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
		return " ";
	}

}
