package mycontroller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import tiles.GrassTrap;
import tiles.LavaTrap;
import tiles.MapTile;
import tiles.MudTrap;
import utilities.Coordinate;
import world.World;

public class InternalMap {
	
/* * * * * * VARIABLES * * * * * */
	
	//The internal map of abstract Tiles
	private HashMap<Coordinate,TileAbstract> mapMemory;
	
	protected Coordinate destNextToTrap;
	
/* * * * * * CONSTRUCTOR * * * * * */
	
	public InternalMap(){
		this.mapMemory = new HashMap<Coordinate, TileAbstract>();
		this.destNextToTrap = null;
	}
	
/* * * * * * METHODS * * * * * */
	
	//update the internal map
	public void update(HashMap<Coordinate,MapTile> newView, Coordinate currentCoordinate){
		
	    System.out.println("currentCoordinate:("+currentCoordinate.x+","+currentCoordinate.y+")");
	    
		//Add newly discovered tiles to the map
		expandMap(newView, currentCoordinate);
				
		MapUtilities.printMap(mapMemory);
		
		//Spread the accessibility
		spreadAccessibility(currentCoordinate);
		
		//Visit adjacents
		visitAdjacents(currentCoordinate);
		
		//Expand the finish zones
		//expandFinishZones();
	}
	
	
	//adds newly discovered tiles to the map
	private void expandMap(HashMap<Coordinate,MapTile> newView, Coordinate currentCo){
		//Create an iterator for the view
		Iterator<Entry<Coordinate,MapTile>> it = newView.entrySet().iterator();
				
		//Iterate through the hashmap
		while(it.hasNext()){
			
			//Get the next tuple
			HashMap.Entry<Coordinate,MapTile> tuple = (HashMap.Entry<Coordinate,MapTile>)it.next();
			
			//Extract the coordinate and maptile
			Coordinate coordinate = tuple.getKey();
			int[] intCo = MapUtilities.intCoordinate(coordinate);
			MapTile tile = tuple.getValue();
			
			//Abstract the tile
			TileAbstract abstraction = tileAbstraction(tile);
			
			//If the tile isn't already recorded and is not an empty tile add it to the internal map
			if(!this.mapMemory.containsKey(coordinate) && abstraction != null
					&& intCo[MapUtilities.X_POS]>=0 && intCo[MapUtilities.Y_POS]>=0){
				this.mapMemory.put(coordinate, abstraction);
			}
			
			it.remove();
			
		}
	}
	
	
	//marks tiles as accessible or not from the car
	private void spreadAccessibility(Coordinate currentCoordinate){
		
		//Mark the current coordinate as accessible
		TileAbstract currentTile = this.mapMemory.get(currentCoordinate);
		currentTile.setAccessible(true);
		
		//initialize the has updated variable
		boolean hasUpdated = true;
		
		//Loop until no more updates occur
		while(hasUpdated){
			
			//Set hasUpdated to false
			hasUpdated = false;
			
			//Create an iterator for the view
			Iterator<Entry<Coordinate,TileAbstract>> it = this.mapMemory.entrySet().iterator();
			
			//Iterate through the hashmap
			while(it.hasNext()){
				
				//Get the next tuple
				HashMap.Entry<Coordinate,TileAbstract> tuple = (HashMap.Entry<Coordinate,TileAbstract>)it.next();
				
				//Extract the coordinate and maptile
				Coordinate coordinate = tuple.getKey();
				TileAbstract tile = tuple.getValue();
				
				//if the tile is already accessible move onto next one
				if(tile.accessible()){
					continue;
				}
				
				//Get the adjacent coordinates
				List<Coordinate> adjacentCoordinates = MapUtilities.getAdjacentCoordinates(coordinate);
				
				//Check each adjacent tile
				for(Coordinate nextCo: adjacentCoordinates){
				                 
					//if the location isn't in the memory next iteration
					if(!this.mapMemory.containsKey(nextCo)){
						continue;
					}
					
                    //Get the Abstract tile
                    TileAbstract nextTile = mapMemory.get(nextCo);
                    
                    //Get the Abstract tile
                    if(nextTile instanceof TrapAbstract){
                        tile.setNextToTrap();
                    }
                    
					//if nextTile accessible floor, mark as accessible
					if((nextTile instanceof FloorAbstract) && (nextTile.accessible())){
						tile.setAccessible(true);
						hasUpdated = true;
						break;
					}
				}	
			}
		}
	}
	
    //Returns the whether all tiles in the zone are discovered (no 
    public Coordinate zoneDiscovered(Coordinate currentCoordinate){
        
        //Create an iterator for the view
        Iterator<Entry<Coordinate,TileAbstract>> it = this.mapMemory.entrySet().iterator();
        
        //Iterate through the hashmap
        while(it.hasNext()){
            
            //Get the next tuple
            HashMap.Entry<Coordinate,TileAbstract> tuple = (HashMap.Entry<Coordinate,TileAbstract>)it.next();
            
            //Extract the coordinate and maptile
            Coordinate coordinate = tuple.getKey();
            TileAbstract tile = tuple.getValue();
            
            //if the tile is already accessible move onto next one
            if(tile.accessible()){
        
              //Get the adjacent coordinates
              List<Coordinate> adjacentCoordinates = MapUtilities.getAdjacentCoordinates(coordinate);
              
              //Check each adjacent tile
              for(Coordinate nextCo: adjacentCoordinates){
                  
                  if(!this.mapMemory.containsKey(nextCo)){                          
                      //If the location isn't in the memory next iteration but in the world = boundary of vision
                      if(nextCo.x<0 || nextCo.y<0 || nextCo.x>World.MAP_WIDTH-1 || nextCo.y>World.MAP_HEIGHT-1){
                        continue;
                      }
                      int[] intCo = MapUtilities.intCoordinate(nextCo);
                      System.out.print("nextCo:("+intCo[0]+","+intCo[1]+") ");
//                      if(intCo[0]<World.MAP_WIDTH && intCo[0]>=0
//                          && intCo[1]<World.MAP_HEIGHT && intCo[1]>=0 ){
//                          System.out.println("\nWorld.MAP_WIDTH:"+World.MAP_WIDTH+" World.MAP_HEIGHT:"+World.MAP_HEIGHT);
//                          return nextCo;
//                      }
                      return nextCo;
                  }
                  //If the location is in memory, check whether it is possible end point
                  else{
                      TileAbstract nextTile = mapMemory.get(nextCo);
                      //If the nextTile is final
                      if(nextTile instanceof FinishAbstract){
                          System.out.println("Finish point Destination");
                          return nextCo;
                      //If the nextTile is next to trap
                      }else if(nextTile.getNextToTrap()){
                          
                          this.destNextToTrap = nextCo;
//                          return nextCo;
                      }
                  }
              }   
            }
        }
        
        return null;
    }
	
	//Visit adjacents
	private void visitAdjacents(Coordinate currentCo){
		
		//Visit the current coordinate
		TileAbstract currentTile = mapMemory.get(currentCo);
		currentTile.visit();
		
		//Get the adjacent tiles and visit them if they are recorded in memort
		List<Coordinate> adjacentTiles = MapUtilities.getAdjacentCoordinates(currentCo);
		for(Coordinate nextCo : adjacentTiles){
			if(mapMemory.containsKey(nextCo)){
				TileAbstract nextTile = mapMemory.get(nextCo);
				nextTile.visit();
			}
		}
	}
	
/* * * * * * GETTERS AND SETTERS * * * * * */
	
	//Returns the next accessible non-visited floor tile to explore
	public Coordinate nextToExplore(){
		
		//Create an iterator for the mapMemory
		Iterator<Entry<Coordinate,TileAbstract>> it = this.mapMemory.entrySet().iterator();
		
		//Iterate through the hashmap
		while(it.hasNext()){
			
			//Get the next tuple
			HashMap.Entry<Coordinate,TileAbstract> tuple = (HashMap.Entry<Coordinate,TileAbstract>)it.next();
			
			//Extract the coordinate and maptile
			Coordinate coordinate = tuple.getKey();
			TileAbstract tile = tuple.getValue();
			
			//If its an unvisited, accesible floor tile return the coordinate
			if((tile instanceof FloorAbstract)
					&& tile.accessible()
					&& !tile.visited()){
				return coordinate;
			}
		}
		
		//If no tiles fit the requirement return null
		return null;
	}
	
	
/* * * * * * HELPER FUNCTIONS * * * * * */
	
	//Creates a new abstraction based on a maptile
	private static TileAbstract tileAbstraction(MapTile tile){
		switch(tile.getType()){
			case ROAD:
			case START:
				return new FloorAbstract();
			case WALL:
				return new WallAbstract();
			case TRAP:
				if(tile instanceof LavaTrap){
					return new LavaAbstract();
				}else if(tile instanceof MudTrap){
					return new MudAbstract();
				}else if(tile instanceof GrassTrap){
					return new GrassAbstract();
				}
				break;
			case FINISH:
				return new FinishAbstract();
			default:
				break;
		}
		
		return null;
	}
	
}
