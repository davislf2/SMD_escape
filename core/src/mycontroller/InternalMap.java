package mycontroller;

/** * * * * * * * * * * * * * *
 * 	Group 21
 * 	Class which represents the navigators internal map
 ** * * * * * * * * * * * * * */

import java.util.ArrayList;

/** * * * * * * * * * * * * * *
 * 	Group 21
 * 	Internal map class
 * 	Changed from the name map due to its clashing with the Map data type
 ** * * * * * * * * * * * * * */

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.PriorityQueue;

import tiles.GrassTrap;
import tiles.LavaTrap;
import tiles.MapTile;
import tiles.MudTrap;
import utilities.Coordinate;
import world.World;

public class InternalMap {
	
/* * * * * * VARIABLES * * * * * */
	
	//Pseudo infinity
	//Introduced after design to avoid "Magic numbers"
	private final double PSEUDO_INFINITY = 999999;
	
	//The internal map of abstract Tiles
	private HashMap<Coordinate,TileAbstract> mapMemory;
	
	//The destination next to trap
	protected Coordinate destNextToTrap;
	
/* * * * * * CONSTRUCTOR * * * * * */
	
	public InternalMap(){
		this.mapMemory = new HashMap<Coordinate, TileAbstract>();
		this.destNextToTrap = null;
	}
	
/* * * * * * METHODS * * * * * */
	
	//update the internal map
	public void update(HashMap<Coordinate,MapTile> newView, Coordinate currentCoordinate){
		
		//Add newly discovered tiles to the map
		expandMap(newView, currentCoordinate);
		
		//Spread the walls to the floors
		spreadWalls();
		
		//Spread the accessibility
		spreadAccessibility(currentCoordinate);
		
		//Visit adjacents
		visitAdjacents(currentCoordinate);
		
		//Print the map
		MapUtilities.printMap(mapMemory);
		
		//Expand the finish zones
		//expandFinishZones();
	}
	
	//Refactored out of the update() function to decompose the function when it got too large
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
	
	
	//Refactored out of the update() function to decompose the function when it got too large
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
//				    System.out.println("Accessible:("+coordinate.x+","+coordinate.y+")");
					continue;
				}
				
				//Get the adjacent coordinates
				List<Coordinate> adjacentCoordinates = MapUtilities.getAdjacentCoordinates(coordinate);
				
				//Check each adjacent tile
				for(Coordinate nextCo: adjacentCoordinates){
					
					//if the loaction isnt in the memory next itteration
					if(!this.mapMemory.containsKey(nextCo)){
						continue;
					}
					
					//Get the Abstract tile
					TileAbstract nextTile = mapMemory.get(nextCo);
					
					//if nextTile accessible floor, mark as accessible
//					if((nextTile instanceof FloorAbstract) && (nextTile.accessible())){
//						tile.setAccessible(true);
		            if((nextTile instanceof FloorAbstract) && (nextTile.accessible()) && (tile instanceof FloorAbstract)){
		                tile.setAccessible(true);	
						hasUpdated = true;
						break;
					}
				}	
			}
		}
	}
	
	
	//Refactored out of the update() function to decompose the function when it got too large
	//Visit adjacents
	private void visitAdjacents(Coordinate currentCo){
		
		//Visit the current coordinate
		TileAbstract currentTile = mapMemory.get(currentCo);
		currentTile.visit();
		
		//Get the adjacent tiles and visit them if they are recorded in memort
		List<Coordinate> adjacentTiles = MapUtilities.getSurroundingCoordinates(currentCo);
		for(Coordinate nextCo : adjacentTiles){
			if(mapMemory.containsKey(nextCo)){
				TileAbstract nextTile = mapMemory.get(nextCo);
				nextTile.visit();
			}
		}
	}
	
	//Refactored out of the update() function to decompose the function when it got too large
	//Update Walls
	private void spreadWalls(){
		
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
			if(!(tile instanceof WallAbstract)){
				continue;
			}
			
			//Get the adjacent coordinates
			List<Coordinate> surroundCoordinates = MapUtilities.getSurroundingCoordinates(coordinate);
			
			//Check each adjacent tile
			for(Coordinate nextCo: surroundCoordinates){
				
				//if the loaction isnt in the memory next itteration
				if(!this.mapMemory.containsKey(nextCo)){
					continue;
				}
				
				//Get the Abstract tile
				TileAbstract nextTile = mapMemory.get(nextCo);
				
				//if nextTile accesible floor, mark as accessible
				if((nextTile instanceof FloorAbstract)){
					((FloorAbstract)nextTile).setByWall();
				}
			}	
		}
	}
	
/* * * * * * GETTERS AND SETTERS * * * * * */
	
	//Refactored from the MyAINavigation explore() function to increase cohesion
	//Returns the next accessible non-visited floor tile to explore
	public Coordinate nextToExplore(Coordinate currentCoordinate, boolean byWalls){
		
	    System.out.println("currentCoordinate:("+currentCoordinate.x+","+currentCoordinate.y+")");
		//Create an iterator for the mapMemory
		Iterator<Entry<Coordinate,TileAbstract>> it = this.mapMemory.entrySet().iterator();
		
		//Initialize the storer variables
		Coordinate returnCoordinate = null;
		double returnScore = PSEUDO_INFINITY;
//		double returnScore = 0;
		
		Coordinate boundary = zoneDiscovered(currentCoordinate);
		System.out.println("boundary:("+boundary.x+","+boundary.y+")");
		
		int count = 0;
		//Iterate through the hashmap
		while(it.hasNext()){
			
			//Get the next tuple
			HashMap.Entry<Coordinate,TileAbstract> tuple = (HashMap.Entry<Coordinate,TileAbstract>)it.next();
			
			//Extract the coordinate and maptile
			Coordinate coordinate = tuple.getKey();
			TileAbstract tile = tuple.getValue();
			
			//If its an unvisited, accessible floor tile return the coordinate
			if((tile instanceof FloorAbstract)
					&& tile.accessible()
					&& !tile.visited()
					&& ( ((coordinate.x == boundary.x) && (coordinate.y == boundary.y)) || (tile instanceof FinishAbstract))
//					&& (MapUtilities.absoluteDistance(coordinate, currentCoordinate) < returnScore || (tile instanceof FinishAbstract))
//					&& (MapUtilities.absoluteDistance(coordinate, currentCoordinate) > returnScore || (tile instanceof FinishAbstract))
					&& (!((FloorAbstract)tile).isByWall() || (byWalls) || (tile instanceof FinishAbstract))
					){
				returnCoordinate = coordinate;
				returnScore = MapUtilities.absoluteDistance(coordinate, currentCoordinate);
				System.out.println("Co:("+coordinate.x+","+coordinate.y+") score:"+returnScore);
				count++;
			}
		}
		
		//if there is a null
		if(returnCoordinate==null){
		    returnCoordinate = boundary;
		}
		return returnCoordinate;
	}

    public Coordinate zoneDiscovered(Coordinate currentCoordinate){
      
        //Create an iterator for the map
        Iterator<Entry<Coordinate,TileAbstract>> it = this.mapMemory.entrySet().iterator();
        
        Coordinate returnCoordinate = null;
        double returnScore = PSEUDO_INFINITY;
        
        //Iterate through the hashmap
        while(it.hasNext()){
            
            //Get the next tuple
            HashMap.Entry<Coordinate,TileAbstract> tuple = (HashMap.Entry<Coordinate,TileAbstract>)it.next();
            
            //Extract the coordinate and maptile
            Coordinate coordinate = tuple.getKey();
            TileAbstract tile = tuple.getValue();
            
            //if the tile is already accessible move onto next one
            if(tile.accessible()){
                if(tile instanceof WallAbstract){
                    System.out.print("wallCo:("+coordinate.x+","+coordinate.y+") "+"acc:"+tile.accessible());
                }
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
//                        System.out.print("nextCo:("+intCo[0]+","+intCo[1]+") ");
                        System.out.print("nextCo:("+coordinate.x+","+coordinate.y+") "+"acc:"+tile.accessible());
//                        return nextCo;
                        
                        double reScore = MapUtilities.absoluteDistance(coordinate, currentCoordinate);
                        if(reScore < returnScore){
                            returnScore = reScore;
                            returnCoordinate = coordinate;
                        }                   
//                        return coordinate;
                    }
                    //If the location is in memory, check whether it is possible end point
                    else{
                        TileAbstract nextTile = mapMemory.get(nextCo);
                        //If the nextTile is final
                        if(nextTile instanceof FinishAbstract){
                            return nextCo;
                        //If the nextTile is next to trap
                        }else if(nextTile.getNextToTrap()){                        
                            this.destNextToTrap = nextCo;
                        }
                    }
                }   
            }
        }
        
        return returnCoordinate;
//        return null;
    }	
	
	//Returns an arraylist of the first tiles in the next zone(s) the car should travel to
	public List<Coordinate> getNextZones(){
		
		//Saturate the traps with accessibility
		saturateTraps();
		
		//Set up the priorty queue based on 
		int initialSize = 100;
		ZoneComparator compare = new ZoneComparator();
		PriorityQueue<Coordinate> coHeap = new PriorityQueue<Coordinate>(initialSize,compare);
		
		//Add all the next entry tiles to the next zones to the priority queue
		//Create an iterator for the view
		Iterator<Entry<Coordinate,TileAbstract>> it = this.mapMemory.entrySet().iterator();
		
		//Iterate through the hashmap
		while(it.hasNext()){
			
			//Get the next tuple
			HashMap.Entry<Coordinate,TileAbstract> tuple = (HashMap.Entry<Coordinate,TileAbstract>)it.next();
			
			//Extract the coordinate and maptile
			Coordinate coordinate = tuple.getKey();
			TileAbstract tile = tuple.getValue();
			
			//if the tile is already accessible or not a floor tile move onto next one
			if(tile.accessible() || !(tile instanceof FloorAbstract)){
				continue;
			}
			
			//Get the adjacent coordinates
			List<Coordinate> adjacentCoordinates = MapUtilities.getAdjacentCoordinates(coordinate);
			
			//Check each adjacent tile
			for(Coordinate nextCo: adjacentCoordinates){
				
				//if the loaction isnt in the memory next itteration
				if(!this.mapMemory.containsKey(nextCo)){
					continue;
				}
				
				//Get the Abstract tile
				TileAbstract nextTile = mapMemory.get(nextCo);
				
				//if nextTile accesible trap, mark as accessible
				if((nextTile instanceof TrapAbstract)
						&& nextTile.accessible() ){
					coHeap.add(coordinate);
					break;
				}
			}	
		}
		
		//Initialise return list
		List<Coordinate> returnList = new ArrayList<Coordinate>();
		
		//Get the variables of the best tile
		Coordinate best = coHeap.peek();
		boolean desiredVisit = mapMemory.get(best).visited();
		int desiredZones =  mapMemory.get(best).getZonesFromFinish();
		
		//Add all Coordinates that meet the criterion
		while((coHeap.size()>0) && 
				(mapMemory.get(coHeap.peek()).visited() == desiredVisit) &&
				(mapMemory.get(coHeap.peek()).getZonesFromFinish() == desiredZones)
				){
			returnList.add(coHeap.remove());
		}
		
		return returnList;
	}
	
	//Introduced to weaken coupling between Dijkstra's and this class
	//Get the mapMemory
	public HashMap<Coordinate, TileAbstract> getMapMemory(){
		return this.mapMemory;
	}
	
	//Get the traverse priority of the tile at the given coordinate
	public float getTraversePriority(Coordinate coordinate){
		return this.mapMemory.get(coordinate).getTraversalPriority();
	}
	
	//Get whether the tile at the coordinate is accessible from the car
	public boolean isAccessibleFromCar(Coordinate coordinate){
		return this.mapMemory.get(coordinate).accessible();
	}
	
	//Get whether the tile at the coordinate Has been visited
	public boolean hasBeenVisited(Coordinate coordinate){
		return this.mapMemory.get(coordinate).visited();
	}
	
	//Replaces the isAccessibleFromFinish function
	//Get how many zones away from the finish a tile is
	public int zonesFromFinish(Coordinate coordinate){
		return this.mapMemory.get(coordinate).getZonesFromFinish();
	}
	
	/** NB the "getAdjacentTiles()" function was refactored to the MapUtilities helper class*/
	
	//Get if the tile at the coordinate is safe to go straight on
	public boolean isSafeToGoStraightOn(Coordinate coordinate, int health, float speed){
		return this.mapMemory.get(coordinate).isSafeToGoStraightOn(coordinate, health, speed);
	}
	
	//Get if the tile at the coordinate is safe to turn on
	public boolean isSafeToTurntOn(Coordinate coordinate, int health, float speed){
		return this.mapMemory.get(coordinate).isSafeToTurnOn(coordinate, health, speed);
	}
	
/* * * * * * HELPER FUNCTIONS * * * * * */
	
	//Refactored out of the update() function to decompose the function when it got too large
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
	
	//Refactored out of the getNextZones() function to decompose the function when it got too large
	//Propogates accessibility through all adjacent traps
	private void saturateTraps(){
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
				TileAbstract trap = tuple.getValue();
				
				//if the tile is already accessible move onto next one
				if(trap.accessible() || !(trap instanceof TrapAbstract)){
					continue;
				}
				
				//Get the adjacent coordinates
				List<Coordinate> adjacentCoordinates = MapUtilities.getAdjacentCoordinates(coordinate);
				
				//Check each adjacent tile
				for(Coordinate nextCo: adjacentCoordinates){
					
					//if the loaction isnt in the memory next itteration
					if(!this.mapMemory.containsKey(nextCo)){
						continue;
					}
					
					//Get the Abstract tile
					TileAbstract nextTile = mapMemory.get(nextCo);
					
					//if nextTile accesible floor, mark as accessible
					if(nextTile.accessible()){
						trap.setAccessible(true);
						hasUpdated = true;
						break;
					}
				}	
			}
		}
	}
	
	
	//added in order to implement the getNextZones function
	//Allows the abstract tiles to be compared
	private class ZoneComparator implements Comparator<Coordinate>
	{
		//Sends items with higher scores towards the peak of the heap
	    @Override
	    public int compare(Coordinate co1, Coordinate co2){
	    	
	    	//Check both are in map memory
	    	if(!mapMemory.containsKey(co1) || !mapMemory.containsKey(co2)){
	    		return 0;
	    	}
	    	
	    	//get the tiles
	    	TileAbstract tile1 = mapMemory.get(co1);
	    	TileAbstract tile2 = mapMemory.get(co2);
	    	
	    	//Give higher priority to those that are closer to the finish
	        if (tile1.getZonesFromFinish() > tile2.getZonesFromFinish()){
	            return -1;
	        }else if (tile1.getZonesFromFinish() < tile2.getZonesFromFinish()){
	            return 1;
	        }
	        //Then give priorty to those that havent been visited
	        if(tile1.visited() && !tile2.visited()){
	        	return -1;
	        }else if(!tile1.visited() && tile2.visited()){
	        	return 1;
	        }
	        return 0;
	    }
	}

	
	
}