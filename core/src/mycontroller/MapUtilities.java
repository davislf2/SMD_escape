package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import utilities.Coordinate;
import world.World;

public class MapUtilities {
	
/* * * * * * VARIABLES * * * * * */
	
	//Array Helper variables
	public static final int X_POS = 0;
	public static final int Y_POS = 1;
	
/* * * * * * METHODS * * * * * */
	
	//Returns an ArrayList of the coordinates adjacent to the argument coordinate
	public static List<Coordinate> getAdjacentCoordinates(Coordinate currentCo){
			
		//Initialize the list
		List<Coordinate> returnList = new ArrayList<Coordinate>();
			
		//Get the current coordinate as an integer array
		int[] intCoordinate = intCoordinate(currentCo);
		
		//Create the directional adjacents
		Coordinate nCoordinate = coordinateFromInt(intCoordinate[X_POS],intCoordinate[Y_POS]+1);
		Coordinate sCoordinate = coordinateFromInt(intCoordinate[X_POS],intCoordinate[Y_POS]-1);
		Coordinate eCoordinate = coordinateFromInt(intCoordinate[X_POS]+1,intCoordinate[Y_POS]);
		Coordinate wCoordinate = coordinateFromInt(intCoordinate[X_POS]-1,intCoordinate[Y_POS]);
		
		//Add each to the list
		returnList.add(nCoordinate);
		returnList.add(sCoordinate);
		returnList.add(eCoordinate);
		returnList.add(wCoordinate);
		
		//Return the list
		return returnList;
	}
	
	//Turns the coordinates from a string into an integer array
	public static int[] intCoordinate(String stringCoordinate){
		
		//Our array should have length 2
		int desiredLength  = 2;
		
		//Split the coordinate string
		String[] splitCoordinate = stringCoordinate.split(",");
		
		//If the array isn't length 2 report error and return null;
		if(splitCoordinate.length != desiredLength){
			System.err.println("Could not convert improper coordinate format");
			return null;
		}
		
		//Convert the string array to an integer array
		int[] coordinates = new int[2];
		coordinates[X_POS] = Integer.parseInt(splitCoordinate[X_POS]);
		coordinates[Y_POS] = Integer.parseInt(splitCoordinate[Y_POS]);
		
		//return the integer array
		return coordinates;
	}
	
	//Turns the coordinates from a string into an integer array
	public static int[] intCoordinate(Coordinate coordinate){
		
		//Convert the coordinate to a string
	    if(coordinate!=null){
    	    if(coordinate.x<0 || coordinate.y<0 ){
    	      return null;
    	    }
//    	    System.out.println("MapTuilities-intCoordinate:("+coordinate.x+","+coordinate.y+")");
            String stringCoordinate = coordinate.toString();
            //return the string method
            return intCoordinate(stringCoordinate);
	    }
	    return null;
	}
	
	
	//Create a coordinate from an integer representation
	public static Coordinate coordinateFromInt(int[] intCo){
		if(intCo.length<2){
			System.err.println("Array to small to create coordinate");
			return null;
		}else{
			String stringCo = "" + intCo[X_POS] + "," + intCo[Y_POS];
			return new Coordinate(stringCo);
		}
	}
	
	//Create a coordinate from astring representation
	public static Coordinate coordinateFromString(String stringCoordinate){
		return new Coordinate(stringCoordinate);
	}
	
	//Create a coordinate from an integer representation
	public static Coordinate coordinateFromInt(int xVal, int yVal){
			
		//form the array and pass into the array method
		int[] intArray = new int[2];
		intArray[X_POS] = xVal;
		intArray[Y_POS] = yVal;
		
		return coordinateFromInt(intArray);
		
	}
	
	
	//Turns the coordinate from a relative reference to an absloute based on a reference
	public static Coordinate relativeToAbsolute(Coordinate co, Coordinate refCo){
		
		//Convert both to int Coordinates
		int[] intCo = intCoordinate(co);
		int[] intRefCo = intCoordinate(refCo);
		
		//Create the absolute int co
		int[] absCo = new int[2];
		absCo[X_POS] = intCo[X_POS] + intRefCo[X_POS];
		absCo[Y_POS] = intCo[Y_POS] + intRefCo[Y_POS];
		
		return coordinateFromInt(absCo);
	}
	
	
	//Prints out a representation of the internal Map
	public static void printMap(Map<Coordinate, TileAbstract> map){

		//Initialize the map
		TileAbstract[][] printMap = new TileAbstract[World.MAP_WIDTH][World.MAP_HEIGHT];
		
		//Create an iterator for the view
		Iterator<Entry<Coordinate,TileAbstract>> it = map.entrySet().iterator();
		
		//Iterate through the hashmap
		while(it.hasNext()){
			
			//Get the next tuple
			HashMap.Entry<Coordinate,TileAbstract> tuple = (HashMap.Entry<Coordinate,TileAbstract>)it.next();
			
			//Extract the coordinate and maptile
			Coordinate coordinate = tuple.getKey();
			int[] intCoordinate = intCoordinate(coordinate);
			TileAbstract tile = tuple.getValue();
			
			//Add the tile to map
			printMap[intCoordinate[X_POS]][intCoordinate[Y_POS]] = tile;
		}
		
		//Print out the map
		for(int row = World.MAP_HEIGHT - 1; row>=0; row--){
			for(int col = 0; col< World.MAP_WIDTH; col++ ){
				if(printMap[col][row] == null){
					System.out.print("?");
				}else{
					System.out.print(printMap[col][row].toString());
				}
			}
			System.out.println("");
		}
		System.out.println("");
		
	}

}
