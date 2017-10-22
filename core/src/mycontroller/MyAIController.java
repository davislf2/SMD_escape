package mycontroller;

/** * * * * * * * * * * * * * *
 * 	Group 21
 * 	My implementation of the CarController interface
 ** * * * * * * * * * * * * * */

import java.util.HashMap;

import controller.CarController;
import tiles.MapTile;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;
import world.WorldSpatial.Direction;

public class MyAIController extends CarController{
	
/* * * * * * VARIABLES * * * * * */
	
	/** NB We have ommited the TARGET_SPEED cariable from the plan
	 *  We did this because We ended up setting it to the max speed of the car 
	 *  so the variable became a redundency
	 * **/
	
	//Angle margin or error
	private final float ANGLE_ERROR = 1;
	
	//thresholdspeed
	private final float THRESHOLD_SPEED = 1;
	
	//The Navigator
	private Navigation navigator;
	
	//The coordinates of the car's next destination
	private int[] currentDestination;
	
	//Added in conjunction with the currentDestination as it was easier to work with
	//The coordinates of the car's next destination
	private Direction travelDirection;
	
	//Added to help with basic car navigation
	//shows whether the car is reversing or going forward
	private boolean reverse;
	
	/**NB We have ommited the three point turn variable which was originally meant to store
	 * the stage of the three point turn the car is up to
	 * We have done this as we cound it more effective to just have the car reverse
	 * **/
	
	
/* * * * * * CONSTRUCTOR * * * * * */
	
	public MyAIController(Car car) {
		super(car);
		
		//Set the current Destination to the current position
		String position =  this.getPosition();
		this.currentDestination = MapUtilities.intCoordinate(position);
		
		//Set the direction to the current direction facing
		this.travelDirection = this.getOrientation();
		
		//Initialize the Navigation
		this.navigator = new MyAINavigation();
		
		//Set reversing to false
		this.reverse = false;
		
	}
	
/* * * * * * METHODS * * * * * */
	
	//Updates the controller
	@Override
	public void update(float delta) {
		
		//If destination reached reassign key variables
		if(this.hasReachedDestination()){
			this.nextDestination();
		}
		
		this.moveToDestination(delta);
		
	}
	
	//Refactored out of update() in order to maintain cohesive methods
	//get new destination and reassign local attributes accordingly
	private void nextDestination(){
		
		//Get the view 
		HashMap<Coordinate,MapTile> view = this.getView();
		
		//Get the current coordinate
		String stringPosition = this.getPosition();
		Coordinate currentCoordinate = MapUtilities.coordinateFromString(stringPosition);
		
		//Update the navigator
		this.navigator.update(view, currentCoordinate, this.getHealth(), this.getSpeed());
		
		//get the next destination
		Coordinate destination = this.navigator.getNextCoordinate();
		int[] intDestination = MapUtilities.intCoordinate(destination);
		
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
	
	//Refactored out of update() in order to maintain cohesive methods
	//Moves the car towards the destination
	private void moveToDestination(float delta){
		switch(this.travelDirection){
			case NORTH:
				this.goNorth(delta);
				break;
			case SOUTH:
				this.goSouth(delta);	
				break;
			case EAST:
				this.goEast(delta);
				break;
			case WEST:
				this.goWest(delta);
				break;
		}
	}
	
/* * * * * * TURN METHODS * * * * * */
	
	/**NB
	 * We replaced the hook turn and three point turn methods from the original plans with simple
	 * directional commands as we found the initially planned methods to be arduous to implement 
	 * with high error**/
	
	//Turn to the north
	private void goNorth(float delta){
		
		//Get the current angle
		float angle = this.getAngle() % WorldSpatial.EAST_DEGREE_MAX;
		//turn appropriately
		if(angle < WorldSpatial.NORTH_DEGREE + ANGLE_ERROR
				&& angle > WorldSpatial.NORTH_DEGREE - ANGLE_ERROR){
			this.reverse = false;
			this.applyForwardAcceleration();
			return;
		}else if(angle < WorldSpatial.SOUTH_DEGREE + ANGLE_ERROR
				&& angle > WorldSpatial.SOUTH_DEGREE - ANGLE_ERROR){
			this.applyReverseAcceleration();
			this.reverse = true;
			return;
		}else if((angle>=WorldSpatial.EAST_DEGREE_MIN && angle<WorldSpatial.NORTH_DEGREE) 
				|| (angle>=WorldSpatial.SOUTH_DEGREE && angle<WorldSpatial.EAST_DEGREE_MAX)){
			if(this.reverse){
				this.applyForwardAcceleration();
				this.turnRight(delta);
			}else if(this.getSpeed()>THRESHOLD_SPEED){	
				this.applyReverseAcceleration();
				this.turnRight(delta);
			}else{
				this.applyForwardAcceleration();
				this.turnLeft(delta);
			}
		}else{
			if(this.reverse){
				this.applyForwardAcceleration();
				this.turnLeft(delta);
			}else if(this.getSpeed()>THRESHOLD_SPEED){	
				this.applyReverseAcceleration();
				this.turnLeft(delta);
			}else{
				this.applyForwardAcceleration();
				this.turnRight(delta);
			}
		}
	}
	
	//Turn to the South
	private void goSouth(float delta){
		
		//Get the current angle
		float angle = this.getAngle() % WorldSpatial.EAST_DEGREE_MAX;
		//turn appropriately
		if(angle < WorldSpatial.SOUTH_DEGREE + ANGLE_ERROR
				&& angle > WorldSpatial.SOUTH_DEGREE - ANGLE_ERROR){
			this.reverse = false;
			this.applyForwardAcceleration();
			return;
		}else if(angle < WorldSpatial.NORTH_DEGREE + ANGLE_ERROR
				&& angle > WorldSpatial.NORTH_DEGREE - ANGLE_ERROR){
			this.applyReverseAcceleration();
			this.reverse = true;
			return;
		}else if((angle>=WorldSpatial.EAST_DEGREE_MIN && angle<WorldSpatial.NORTH_DEGREE) 
				|| (angle>=WorldSpatial.SOUTH_DEGREE && angle<WorldSpatial.EAST_DEGREE_MAX)){
			if(this.reverse){
				this.applyForwardAcceleration();
				this.turnLeft(delta);
			}else if(this.getSpeed()>THRESHOLD_SPEED){	
				this.applyReverseAcceleration();
				this.turnLeft(delta);
			}else{
				this.applyForwardAcceleration();
				this.turnRight(delta);
			}
		}else{
			if(this.reverse){
				this.applyForwardAcceleration();
				this.turnRight(delta);
			}else if(this.getSpeed()>THRESHOLD_SPEED){	
				this.applyReverseAcceleration();
				this.turnRight(delta);
			}else{
				this.applyForwardAcceleration();
				this.turnLeft(delta);
			}
		}
	}
	
	//Turn to the East
	private void goEast(float delta){
			
		//Get the current angle
		float angle = this.getAngle() % WorldSpatial.EAST_DEGREE_MAX;
		//turn appropriately
		if(angle < WorldSpatial.EAST_DEGREE_MIN + ANGLE_ERROR
				&& angle > WorldSpatial.EAST_DEGREE_MIN - ANGLE_ERROR){
			this.reverse = false;
			this.applyForwardAcceleration();
			return;
		}else if(angle < WorldSpatial.WEST_DEGREE + ANGLE_ERROR
				&& angle > WorldSpatial.WEST_DEGREE - ANGLE_ERROR){
			this.applyReverseAcceleration();
			this.reverse = true;
			return;
		}else if(angle>WorldSpatial.EAST_DEGREE_MIN && angle<WorldSpatial.WEST_DEGREE){
			if(this.reverse){
				this.applyForwardAcceleration();
				this.turnLeft(delta);
			}else if(this.getSpeed()>THRESHOLD_SPEED){	
				this.applyReverseAcceleration();
				this.turnLeft(delta);
			}else{
				this.applyForwardAcceleration();
				this.turnRight(delta);
			}
		}else{
			if(this.reverse){
				this.applyForwardAcceleration();
				this.turnRight(delta);
			}else if(this.getSpeed()>THRESHOLD_SPEED){	
				this.applyReverseAcceleration();
				this.turnRight(delta);
			}else{
				this.applyForwardAcceleration();
				this.turnLeft(delta);
			}
		}
	}
	
	//Turn to the West
	private void goWest(float delta){
				
		//Get the current angle
		float angle = this.getAngle() % WorldSpatial.EAST_DEGREE_MAX;
		//turn appropriately
		if(angle < WorldSpatial.WEST_DEGREE + ANGLE_ERROR
				&& angle > WorldSpatial.WEST_DEGREE - ANGLE_ERROR){
			this.reverse = false;
			this.applyForwardAcceleration();
			return;
		}else if(angle < WorldSpatial.EAST_DEGREE_MIN + ANGLE_ERROR
				&& angle > WorldSpatial.EAST_DEGREE_MIN - ANGLE_ERROR){
			this.applyReverseAcceleration();
			this.reverse = true;
			return;
		}else if(angle>WorldSpatial.EAST_DEGREE_MIN && angle<WorldSpatial.WEST_DEGREE){
			if(this.reverse){
				this.applyForwardAcceleration();
				this.turnRight(delta);
			}else if(this.getSpeed()>THRESHOLD_SPEED){	
				this.applyReverseAcceleration();
				this.turnRight(delta);
			}else{
				this.applyForwardAcceleration();
				this.turnLeft(delta);
			}
		}else{
			if(this.reverse){
				this.applyForwardAcceleration();
				this.turnLeft(delta);
			}else if(this.getSpeed()>THRESHOLD_SPEED){	
				this.applyReverseAcceleration();
				this.turnLeft(delta);
			}else{
				this.applyForwardAcceleration();
				this.turnRight(delta);
			}
		}
	}
	
/* * * * * * HELPER FUNCTIONS * * * * * */
	
	//Refactored out of update() in order to maintain cohesive methods
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
