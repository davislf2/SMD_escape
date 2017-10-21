package mycontroller;

import controller.CarController;
import java.util.HashMap;
import tiles.MapTile;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;
import world.WorldSpatial.Direction;

public class TestAIController extends CarController {

  /* * * * * * VARIABLES * * * * * */
  
  //The Navigator
  private Navigation navigator;
  
  //The coordinates of the car's next destination
  private int[] currentDestination;
  
  //The coordinates of the car's next destination
  private Direction travelDirection;
  
  public TestAIController(Car car) {
    super(car);
    
    //Set the current Destination to the current position
    String position = this.getPosition();
    this.currentDestination = MapUtilities.intCoordinate(position);
    
    //Set the direction to the current direction facing
    this.travelDirection = this.getOrientation();
    
    //Initialize the Navigation
    this.navigator = new MyAINavigation();
    
  }
  @Override
  public void update(float delta) {
    
    
  }
}
