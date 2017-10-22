package mycontroller;

/** * * * * * * * * * * * * * *
 * 	Group 21
 * 	Class which provides Djikstra's algorithm for path plotting
 * 	NB//In original plan was encapsulated in MyAINavigation
 * 	Was refactored to its own class to maintain cohesion within the system classes
 ** * * * * * * * * * * * * * */

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Stack;

import utilities.Coordinate;

public class Dijkstras implements PathPlotter{
	
/* * * * * * VARIABLES * * * * * */
	
	//Pseudo infinity
	private final int PSEUDO_INFINITY = 999999;
	
	//The map the dijkstras uses to navigate
	Map<Coordinate, DijkstraFrame> dMap;
	
/* * * * * * METHODS * * * * * */
	
	//Plots a path through the map from startCo to EndCo, only traversing traps if traps = true
	public ArrayList<Coordinate> plotPath(Map<Coordinate,TileAbstract> oldmap, List<Coordinate> endCos, Coordinate startCo, boolean traps){
		
		//Create the dijkstra map
		formDMap(oldmap);
		
		//Create the dijkstra priority queue and add the source coordinate
		int initialSize = 100;
		FrameComparator comparator = new FrameComparator();
		PriorityQueue<Coordinate> dQ = new PriorityQueue<Coordinate>(initialSize, comparator);
		
		//Set the start coordinate variable and add it to the queue
		this.dMap.get(startCo).setPathcost(0);
		this.dMap.get(startCo).addToQueue();
		dQ.add(startCo);
		
		//inititalize the reached Coordinate
		Coordinate reachedCoordinate = null;
		int count = 0;
		//Loop through the queue
		while(dQ.size() != 0){
			count++;
			//Get the head of the priority queue and the corresponding frame
			Coordinate headCo = dQ.remove();
			DijkstraFrame headFrame = this.dMap.get(headCo);
			
//			if(this.dMap.get(headCo).getPrevious() != null){
//			  System.out.println("headCo:("+headCo.x+","+headCo.y+") pre:("+this.dMap.get(headCo).getPrevious().x+","+this.dMap.get(headCo).getPrevious().y+") "+count);
//			}else{
//			  System.out.println("headCo:("+headCo.x+","+headCo.y+") "+count);
//			}
			
			//Check if it is a destination coordinate break the loop
			for(Coordinate endCo: endCos){
				if(endCo.equals(headCo)){
					reachedCoordinate = headCo;
					break;
				}
			}
			if(reachedCoordinate!=null){
				break;
			}
			
			//get the adjacent coordinates
			List<Coordinate> adjacentCo = MapUtilities.getAdjacentCoordinates(headCo);
			
			//Loop through the adjacent Tiles
			for(Coordinate nextCo: adjacentCo){
				
				//If the coordinate isn't in the map, or isn't a traversable tile then move to the next one
				if(!(this.dMap.containsKey(nextCo))
						|| !((this.dMap.get(nextCo).getTile() instanceof FloorAbstract)
								|| ((this.dMap.get(nextCo).getTile() instanceof TrapAbstract) && traps))){
					continue;
				}
				
				//Get the dijkstraframe
				DijkstraFrame nextFrame = this.dMap.get(nextCo);
				
				//If we've found a better path to the frame then
				if(nextFrame.getPathcost() > headFrame.getPathcost() +1){
					//update the pathcost and previous coordinate
					nextFrame.setPathcost(headFrame.getPathcost() +1);
					nextFrame.setPrevious(headCo);
					
					//If the frame isn't currently in the queue then add its coordinate
					if(!nextFrame.isInQueue()){
						nextFrame.addToQueue();
						dQ.add(nextCo);
					}
				}
			}
		}
		
		//Form the path
		Stack<Coordinate> pathCreator = new Stack<Coordinate>();
		while(dMap.get(reachedCoordinate).getPrevious() != null){
			
			pathCreator.push(reachedCoordinate);
			reachedCoordinate = dMap.get(reachedCoordinate).getPrevious();
		}
		
		//create the list
		ArrayList<Coordinate> returnList = new ArrayList<Coordinate>();
		while(pathCreator.size()!=0){
			returnList.add(pathCreator.pop());
		}
		
		return returnList;
	}
	
	//Creates the dijkstra version of the map
	private void formDMap(Map<Coordinate, TileAbstract> oldMap){
		
		//Initialize the new map
		Map<Coordinate, DijkstraFrame> newDMap = new HashMap<Coordinate, DijkstraFrame>();
		
		//Create an iterator for the oldamp
		Iterator<Entry<Coordinate,TileAbstract>> it = oldMap.entrySet().iterator();
		
		//Iterate through the hashmap
		while(it.hasNext()){
			
			//Get the next tuple
			HashMap.Entry<Coordinate,TileAbstract> tuple = (HashMap.Entry<Coordinate,TileAbstract>)it.next();
			
			//Extract the coordinate and maptile
			Coordinate coordinate = tuple.getKey();
			TileAbstract tile = tuple.getValue();
			
			//Create the new Dijkstra rame and add it to the new map
			DijkstraFrame newFrame = new DijkstraFrame(tile);
			newDMap.put(coordinate, newFrame);
		}
		
		this.dMap = newDMap;
	}
	
/* * * * * * HELPER CLASSES * * * * * */
	
	//Used to compare Coordinates in the priority queue
	private class FrameComparator implements Comparator<Coordinate>{

		//Sends items with lower paths towards the peak of the heap
	    @Override
	    public int compare(Coordinate co1, Coordinate co2){
	    	
	    	//Check both are in map memory
	    	if(!dMap.containsKey(co1) || !dMap.containsKey(co2)){
	    		return 0;
	    	}
	    	
	    	//get the tiles
	    	DijkstraFrame dFrame1 = dMap.get(co1);
	    	DijkstraFrame dFrame2 = dMap.get(co2);
	    	
	    	//Give higher priority to those that are closer to the finish
	        if (dFrame1.getPathcost() < dFrame2.getPathcost()){
	            return -1;
	        }else if (dFrame1.getPathcost() > dFrame2.getPathcost()){
	            return 1;
	        }
	       
	        return 0;
	    }
		
	}
	
	//The dikstra fram is used to travers the map
	private class DijkstraFrame{
		
		/* * * VARIABLES * * */
		
		//A dikstraFrame has an AbstractTile, a previouscoordinate and a pathcost
		//Aslo whether the frame has been added to the queue for convenience
		private TileAbstract tile;
		private double pathcost;
		private Coordinate previousCoordinate;
		private boolean isAddedToQueue;
		
		/* * * CONSTRUCTOR * * */
		
		public DijkstraFrame(TileAbstract tile){
			this.tile = tile;
			this.pathcost = PSEUDO_INFINITY;
			this.previousCoordinate = null;
			this.isAddedToQueue = false;
		}
		
		/* * * GETTERS AND SETTERS * * */
		
		//Get the tile
		public TileAbstract getTile(){
			return this.tile;
		}
		
		//Set the pathcost if the new value is lower than the current
		public void setPathcost(double newCost){
			if(newCost<this.pathcost){
				this.pathcost = newCost;
			}
		}
		
		//Get the pathcost
		public double getPathcost(){
			return this.pathcost;
		}
		
		//Get the previous Coordinate
		public Coordinate getPrevious(){
			return this.previousCoordinate;
		}
		
		//Set the previous coordinate
		public void setPrevious(Coordinate prev){
			this.previousCoordinate = prev;
		}
		
		//return whether or not the frame's coordinate is in the queue
		public boolean isInQueue(){
			return this.isAddedToQueue;
		}
		
		//Mark the frame as added to queue
		public void addToQueue(){
			this.isAddedToQueue = true;
		}
		
	}

}
