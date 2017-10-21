package mycontroller;

import java.util.HashMap;

import controller.CarController;
import tiles.MapTile;
import utilities.Coordinate;
import world.Car;
import world.World;
import world.WorldSpatial;
import world.WorldSpatial.Direction;

public class MyAIController extends CarController{
	
/* * * * * * VARIABLES * * * * * */
	
	//The Navigator
	private Navigation navigator;
	
	//The coordinates of the car's next destination
	private int[] currentDestination;
	
	//The coordinates of the car's next destination
	private Direction travelDirection;
	
	private final float CAR_SPEED = 3;
	private int[] previousDestination=null;
	private int[] previousCoordinates=null;
	private int clock;
	
/* * * * * * CONSTRUCTOR * * * * * */
	public MyAIController(Car car) {
		super(car);
		
		//Set the current Destination to the current position
		String position = this.getPosition();
		this.currentDestination = MapUtilities.intCoordinate(position);
		System.out.println(this.currentDestination);
		//Set the direction to the current direction facing
		this.travelDirection = this.getOrientation();
		
		//Initialize the Navigation
		this.navigator = new MyAINavigation();
		
		System.out.println("Constructor");
		this.previousDestination = MapUtilities.intCoordinate(position);
		this.previousCoordinates = MapUtilities.intCoordinate(position);
		this.clock = 0;
	}
	
/* * * * * * METHODS * * * * * */
	
	//Updates the controller
	@Override
	public void update(float delta) {
		
		//If destination reached reassign key variables
	    if(clock%40==0){
//    	    System.out.println("preDes:("+previousDestination[0]+","+previousDestination[1]+")"+" t:"+clock/40);
    //	    if(previousDestination[0]!=currentDestination[0] && previousDestination[1]!=currentDestination[1]){
//    	      System.out.println("curD:("+currentDestination[0]+","+currentDestination[1]+")"+" t:"+clock/40);
    //	    }
    	    String stringPosition = this.getPosition();
    	    int[] currentCoordinates = MapUtilities.intCoordinate(stringPosition);
//    	    System.out.println("prePos:("+previousCoordinates[0]+","+previousCoordinates[1]+")"+" t:"+clock/40);
    //	    if(previousCoordinates[0]!=currentCoordinates[0] && previousCoordinates[1]!=currentCoordinates[1]){
    	      System.out.println("curPos:("+currentCoordinates[0]+","+currentCoordinates[1]+")"+" t:"+clock/40);
    //	    }
    	    
    	    
    	    previousDestination = currentDestination;
    	    previousCoordinates = currentCoordinates;
	    }
	    if(clock%20==0){
	        System.out.println("curSpeed:"+getSpeed()+" t:"+clock/40);
	    }
	    
	    
	    if(this.hasReachedDestination()){
	        System.out.println("hasReachedDest");
	        System.out.println("Des:("+this.currentDestination[MapUtilities.X_POS]+","+this.currentDestination[MapUtilities.Y_POS]+")");	        
			this.nextDestination();
		}
		
		this.moveToDestination(delta);
		clock++;
	}
	
	
	//get new destination and reassign local attributes accordingly
	private void nextDestination(){
		
		//Get the view 
		HashMap<Coordinate,MapTile> view = this.getView();
		
		//Get the current coordinate
		String stringPosition = this.getPosition();
		Coordinate currentCoordinate = MapUtilities.coordinateFromString(stringPosition);
		
		//Update the navigator
		this.navigator.update(view, currentCoordinate);
		
		//get the next destination
		Coordinate destination = this.navigator.getNextCoordinate();
		int[] intDestination = MapUtilities.intCoordinate(destination);
		
        System.out.println("curDes:("+intDestination[0]+","+intDestination[1]+")"+" t:"+clock/40);
        		
		//Update the destination and the direction travelled
		if(this.currentDestination[MapUtilities.X_POS] < intDestination[MapUtilities.X_POS]){
			this.travelDirection = WorldSpatial.Direction.EAST;
			this.currentDestination[MapUtilities.X_POS] = intDestination[MapUtilities.X_POS];
			
		}else if(this.currentDestination[MapUtilities.X_POS] > intDestination[MapUtilities.X_POS]){
			this.travelDirection = WorldSpatial.Direction.WEST;
			this.currentDestination[MapUtilities.X_POS] = intDestination[MapUtilities.X_POS];
			
		}else if(this.currentDestination[MapUtilities.Y_POS] < intDestination[MapUtilities.Y_POS]){
			this.travelDirection = WorldSpatial.Direction.NORTH;
			this.currentDestination[MapUtilities.Y_POS] = intDestination[MapUtilities.Y_POS];
			
		}else if(this.currentDestination[MapUtilities.Y_POS] > intDestination[MapUtilities.Y_POS]){
			this.travelDirection = WorldSpatial.Direction.SOUTH;
			this.currentDestination[MapUtilities.Y_POS] = intDestination[MapUtilities.Y_POS];
		}
	}
	
	
	//Moves the car towards the destination
	private void moveToDestination(float delta){
		
		switch(this.travelDirection){
			case NORTH:
				if(this.getAngle()== WorldSpatial.NORTH_DEGREE){
				    if(getSpeed() < CAR_SPEED){
				        this.applyForwardAcceleration();
				    }
				}else{
					this.applyReverseAcceleration();
					this.goNorth(delta);
				}
				break;
			case SOUTH:
				if(this.getAngle() == WorldSpatial.SOUTH_DEGREE){
				    if(getSpeed() < CAR_SPEED){
				        this.applyForwardAcceleration();
				    }
				}else{
					this.applyReverseAcceleration();
					this.goSouth(delta);
				}
				break;
			case EAST:
				if((this.getAngle()%WorldSpatial.EAST_DEGREE_MAX) == WorldSpatial.EAST_DEGREE_MIN){
				    if(getSpeed() < CAR_SPEED){
				        this.applyForwardAcceleration();
				    }
				}else{
					this.applyReverseAcceleration();
					this.goEast(delta);
				}
				break;
			case WEST:
				if(this.getAngle()== WorldSpatial.WEST_DEGREE){
				    if(getSpeed() < CAR_SPEED){
				        this.applyForwardAcceleration();
				    }
				}else{
					this.applyReverseAcceleration();
					this.goWest(delta);
				}
				break;
		}
	}
	
/* * * * * * TURN METHODS * * * * * */
	
	//Turn to the north
	private void goNorth(float delta){
		
		//Get the current angle
		float angle = this.getAngle() % 360;
		
		//turn appropriately
		if(angle == 90){
			return;
		}else if((angle>=0 && angle<90) || (angle>=270 && angle<360)){
			this.turnRight(delta);
		}else{
			this.turnLeft(delta);
		}
	}
	
	//Turn to the South
	private void goSouth(float delta){
		//Get the current angle
		float angle = this.getAngle() % 360;
			
		//turn appropriately
		if(angle == 270){
			return;
		}else if((angle>=0 && angle<90) || (angle>=270 && angle<360)){
			this.turnLeft(delta);
		}else{
			this.turnRight(delta);
		}
	}
	
	//Turn to the East
	private void goEast(float delta){
			
		//Get the current angle
		float angle = this.getAngle() % 360;
			
		//turn appropriately
		if(angle == 0){
			return;
		}else if(angle>0 && angle<180){
			this.turnLeft(delta);
		}else{
			this.turnRight(delta);
		}
	}
	
	//Turn to the West
	private void goWest(float delta){
				
		//Get the current angle
		float angle = this.getAngle() % 360;
				
		//turn appropriately
		if(angle == 0){
			return;
		}else if(angle>0 && angle<180){
			this.turnLeft(delta);
		}else{
			this.turnRight(delta);
		}
	}

/* * * * * * GETTERS & SETTERS * * * * * */	
	public int plotPath(){
	  
	  return 0;
	}
	
/* * * * * * GETTERS & SETTERS * * * * * */
	
/* * * * * * HELPER FUNCTIONS * * * * * */
	
	//Returns true if the car has reached its current destination
	private boolean hasReachedDestination(){
		
		//Get the current coordinates
		String stringPosition = this.getPosition();
		int[] currentCoordinates = MapUtilities.intCoordinate(stringPosition);
		
		//return equality to destination coordinates;
		return this.currentDestination[MapUtilities.X_POS] == currentCoordinates[MapUtilities.X_POS]
				&& this.currentDestination[MapUtilities.Y_POS] == currentCoordinates[MapUtilities.Y_POS];
	}

}
