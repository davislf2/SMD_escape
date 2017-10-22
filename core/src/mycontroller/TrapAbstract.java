package mycontroller;

/** * * * * * * * * * * * * * *
 * 	Group 21
 * 	A class representing an abstract tile from the world
 ** * * * * * * * * * * * * * */

import java.util.List;

public abstract class TrapAbstract extends TileAbstract{
	
/* * * * * * METHODS * * * * * */
	
	//Spread the zone to its neighbours
	@Override
	public void spreadZoneCount(List<TileAbstract> adjacentTiles){
		for(TileAbstract abstraction: adjacentTiles){
			if(abstraction instanceof TrapAbstract){
				abstraction.setZoneCount(this.getZonesFromFinish());
			}else{
				abstraction.setZoneCount(this.getZonesFromFinish() + 1);
			}
		}
	}

}
