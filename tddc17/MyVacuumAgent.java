package tddc17;


import aima.core.environment.liuvacuum.*;
import sun.security.acl.WorldGroupImpl;
import aima.core.agent.Action;
import aima.core.agent.AgentProgram;
import aima.core.agent.Percept;
import aima.core.agent.impl.*;

import java.awt.Point;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

class MyAgentState
{
	public int[][] world = new int[30][30];
	public int initialized = 0;
	final int UNKNOWN 	= 0;
	final int WALL 		= 1;
	final int CLEAR 	= 2;
	final int DIRT		= 3;
	final int HOME		= 4;
	final int ACTION_NONE 			= 0;
	final int ACTION_MOVE_FORWARD 	= 1;
	final int ACTION_TURN_RIGHT 	= 2;
	final int ACTION_TURN_LEFT 		= 3;
	final int ACTION_SUCK	 		= 4;
	
	public int agent_x_position = 1;
	public int agent_y_position = 1;
	public int agent_last_action = ACTION_NONE;
	
	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;
	public int agent_direction = EAST;
	
	MyAgentState()
	{
		for (int i=0; i < world.length; i++)
			for (int j=0; j < world[i].length ; j++)
				world[i][j] = UNKNOWN;
		world[1][1] = HOME;
		agent_last_action = ACTION_NONE;
	}
	// Based on the last action and the received percept updates the x & y agent position
	public void updatePoint(DynamicPercept p)
	{
		Boolean bump = (Boolean)p.getAttribute("bump");

		if (agent_last_action==ACTION_MOVE_FORWARD && !bump)
	    {
			switch (agent_direction) {
			case MyAgentState.NORTH:
				agent_y_position--;
				break;
			case MyAgentState.EAST:
				agent_x_position++;
				break;
			case MyAgentState.SOUTH:
				agent_y_position++;
				break;
			case MyAgentState.WEST:
				agent_x_position--;
				break;
			}
	    }
		
	}
	
	public void updateWorld(int x_position, int y_position, int info)
	{
		world[x_position][y_position] = info;
	}
	
	public void printWorldDebug()
	{
		for (int i=0; i < world.length; i++)
		{
			for (int j=0; j < world[i].length ; j++)
			{
				if (world[j][i]==UNKNOWN)
					System.out.print(" ? ");
				if (world[j][i]==WALL)
					System.out.print(" # ");
				if (world[j][i]==CLEAR)
					System.out.print(" . ");
				if (world[j][i]==DIRT)
					System.out.print(" D ");
				if (world[j][i]==HOME)
					System.out.print(" H ");
			}
			System.out.println("");
		}
	}
}

class MyAgentProgram implements AgentProgram {

	private int initnialRandomActions = 10;
	private Random random_generator = new Random();
	
	// Here you can define your variables!
	public int iterationCounter = 20*20*20;
	public MyAgentState state = new MyAgentState();
	private boolean init = true;
	private boolean terminated = false;
	private LinkedList<Point> queue = new LinkedList<Point>(); 
	private LinkedList<Integer> actionQueue = new LinkedList<Integer>(); 
	Map<Point, Point> previousMap = new HashMap<Point, Point>();
	Point startPos;
	Point homePos;
	private int phase = 0;

	// moves the Agent to a random start position
	// uses percepts to update the Agent position - only the position, other percepts are ignored
	// returns a random action
	private Action moveToRandomStartPoint(DynamicPercept percept) {
		int action = random_generator.nextInt(6);
		initnialRandomActions--;
		state.updatePoint(percept);
		if(action==0) {
		    state.agent_direction = ((state.agent_direction-1) % 4);
		    if (state.agent_direction<0) 
		    	state.agent_direction +=4;
		    state.agent_last_action = state.ACTION_TURN_LEFT;
			return LIUVacuumEnvironment.ACTION_TURN_LEFT;
		} else if (action==1) {
			state.agent_direction = ((state.agent_direction+1) % 4);
		    state.agent_last_action = state.ACTION_TURN_RIGHT;
		    return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
		} 
		state.agent_last_action=state.ACTION_MOVE_FORWARD;
		return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
	}
	
	private Action turnRight(DynamicPercept percept) {
		state.agent_direction = ((state.agent_direction+1) % 4);
	    state.agent_last_action = state.ACTION_TURN_RIGHT;
	    return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
	}
	
	private Action turnLeft(DynamicPercept percept) {
	    state.agent_direction = ((state.agent_direction-1) % 4);
	    if (state.agent_direction<0) 
	    	state.agent_direction +=4;
	    state.agent_last_action = state.ACTION_TURN_LEFT;
		return LIUVacuumEnvironment.ACTION_TURN_LEFT;
	}
	
	private Action moveForward(DynamicPercept percept) {
		state.agent_last_action=state.ACTION_MOVE_FORWARD;
	    return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
	}
	
	private Action doAction (int next, DynamicPercept p) {
		if (next == state.ACTION_TURN_RIGHT)
			return turnRight(p);
		else if (next == state.ACTION_TURN_LEFT)
			return turnLeft(p);
		
		else if (next == state.ACTION_NONE) {
			state.agent_last_action = state.ACTION_NONE;
			return NoOpAction.NO_OP;
		}
		
		return moveForward(p);
	}
	
	
	private void goToPoint (Point from, Point goal) {
 		System.out.println("FROM : " + from.toString());
 		System.out.println("TO : " + goal.toString());
 		System.out.println("phase : "+phase);
		int x_epsilon = from.x - goal.x;
		int y_epsilon = from.y - goal.y;
		
		// if we are too far from the goal, go to previous
		if (Math.abs(x_epsilon) > 1 || Math.abs(y_epsilon) > 1 || (Math.abs(x_epsilon) >= 1 & Math.abs(y_epsilon) >= 1)) {
			if (phase !=2 && previousMap.containsKey(from) && previousMap.get(from) != null) { // go to start position
				goal = previousMap.get(from);
				if (goal.equals(startPos))
					phase = 2;
					
			}
			else { // we are at our start position, go to goal
				while (!previousMap.get(goal).equals(from)) {
					goal =  previousMap.get(goal);
				}
				
			}
		}
		System.out.println(previousMap.toString());
 		System.out.println("FROM 2: " + from.toString());
 		System.out.println("TO 2 : " + goal.toString());
		
		// recalcul of epsilons
		x_epsilon = from.x - goal.x;
		y_epsilon = from.y - goal.y;
		
		switch (state.agent_direction) {
		case MyAgentState.NORTH:
			if (x_epsilon == 1) { // goal left
				actionQueue.add(state.ACTION_TURN_LEFT);
				actionQueue.add(state.ACTION_MOVE_FORWARD);
			}
			else if (x_epsilon == -1) {// goal right
				actionQueue.add(state.ACTION_TURN_RIGHT);
				actionQueue.add(state.ACTION_MOVE_FORWARD);
			}
			else if (y_epsilon == -1) {// goal under
				// turn 180
				actionQueue.add(state.ACTION_TURN_RIGHT);
				actionQueue.add(state.ACTION_TURN_RIGHT);
				actionQueue.add(state.ACTION_MOVE_FORWARD);

			}
			else if (y_epsilon == 1) { // goal above
				actionQueue.add(state.ACTION_MOVE_FORWARD);
			}
			break;
			
		case MyAgentState.EAST:
			if (x_epsilon == 1) { // goal left
				// turn 180
				actionQueue.add(state.ACTION_TURN_RIGHT);
				actionQueue.add(state.ACTION_TURN_RIGHT);
				actionQueue.add(state.ACTION_MOVE_FORWARD);
			}
			else if (x_epsilon == -1) { // goal right
				actionQueue.add(state.ACTION_MOVE_FORWARD);
			}
			else if (y_epsilon == -1) { // goal under
				actionQueue.add(state.ACTION_TURN_RIGHT);
				actionQueue.add(state.ACTION_MOVE_FORWARD);
			}
			else if (y_epsilon == 1) { // goal above
				actionQueue.add(state.ACTION_TURN_LEFT);
				actionQueue.add(state.ACTION_MOVE_FORWARD);
			}
			break;
			
		case MyAgentState.SOUTH:
			if (x_epsilon == 1) { // goal left
				actionQueue.add(state.ACTION_TURN_RIGHT);
				actionQueue.add(state.ACTION_MOVE_FORWARD);

			}
			else if (x_epsilon == -1) { // goal right
				actionQueue.add(state.ACTION_TURN_LEFT);
				actionQueue.add(state.ACTION_MOVE_FORWARD);
			}
			else if (y_epsilon == -1) { // goal under
				actionQueue.add(state.ACTION_MOVE_FORWARD);

			}
			else if (y_epsilon == 1) { // goal above
				actionQueue.add(state.ACTION_TURN_RIGHT);
				actionQueue.add(state.ACTION_TURN_RIGHT);
				actionQueue.add(state.ACTION_MOVE_FORWARD);
			}
			break;
			
		case MyAgentState.WEST:
			if (x_epsilon == 1) { // goal left
				actionQueue.add(state.ACTION_MOVE_FORWARD);
			}
			else if (x_epsilon == -1) { // goal right
				actionQueue.add(state.ACTION_TURN_RIGHT);
				actionQueue.add(state.ACTION_TURN_RIGHT);
				actionQueue.add(state.ACTION_MOVE_FORWARD);
			}
			else if (y_epsilon == -1) { // goal under
				actionQueue.add(state.ACTION_TURN_LEFT);
				actionQueue.add(state.ACTION_MOVE_FORWARD);

			}
			else if (y_epsilon == 1) { // goal above
				actionQueue.add(state.ACTION_TURN_RIGHT);
				actionQueue.add(state.ACTION_MOVE_FORWARD);
			}
			break;
			
			default :
				break;
		}
		
		System.out.println("actionQueue = " + actionQueue.toString());

	}
	
	private boolean isWall(Point p) {
		return (state.world[p.x][p.y] == state.WALL);
	}
	
	private boolean isExplored (Point p) {
		boolean explored = true;
		if (state.world[p.x+1][p.y] != state.CLEAR & state.world[p.x+1][p.y] != state.WALL)
			explored = false;
		if (state.world[p.x-1][p.y] != state.CLEAR & state.world[p.x-1][p.y] != state.WALL)
			explored = false;
		if (state.world[p.x][p.y+1] != state.CLEAR & state.world[p.x][p.y+1] != state.WALL)
			explored = false;
		if (state.world[p.x][p.y-1] != state.CLEAR & state.world[p.x][p.y-1] != state.WALL)
			explored = false;
		
		return explored;	
		
	}
 
	
    // BFS algoritm to fill the action queue and explore the whole map
    Action BFS(Point start, DynamicPercept percept) 
    { 

    	System.out.println("Entering BFS");
    	
	    Boolean home = (Boolean)percept.getAttribute("home");

    	if (home == true) {
    		homePos = start;
    	}
    	// initialisation, our start position should have no previous
    	if (previousMap.isEmpty()){
    		previousMap.put(start, null);
    		startPos = start;
    	}
    	
    	// if we have action to perform
    	if (!actionQueue.isEmpty()) {
    		Integer next = actionQueue.poll();
    		return doAction(next, (DynamicPercept)percept);
    	}
        
        // else if our current position it's not explored yet, queue it
        if (!queue.contains(start) && !isExplored(start)){ 
            queue.add(start); 
        }
          

        while (queue.size() != 0) 
        { 
            Point s = queue.peek();
            
            // GO TO THE NODE TO EXPLORE / COME BACK TO IT
            if (queue.size() != 0 && !start.equals(s) && !isWall(s)) {
            	
            	System.out.println("COMING BACK TO " + s.toString());
            	
                System.out.println("queue = {" + queue.toString()+"}");
                System.out.println("startPos = {" + startPos.toString()+"}");

        		goToPoint(start, s);
            	Integer next = actionQueue.poll();
	    		return doAction(next, (DynamicPercept)percept);
            }
            
            
            System.out.print(s.toString()+" "); 
            
            // Neighbors
            LinkedList<Point> adj = new LinkedList<Point>();
            Point east = new Point(start.x+1,start.y);
            if (!previousMap.containsKey(east)) {
            	previousMap.put(east, start);
            }
            Point south = new Point(start.x,start.y+1);
            if (!previousMap.containsKey(south)) {
            	previousMap.put(south, start);
            }

            Point west = new Point(start.x-1,start.y);
            if (!previousMap.containsKey(west)) {
            	previousMap.put(west, start);
            }

            Point north = new Point(start.x,start.y-1);
            if (!previousMap.containsKey(north)) {
            	previousMap.put(north, start);
            }
        	adj.add(east);
        	adj.add(south);
        	adj.add(west);
        	adj.add(north);
         

            Iterator<Point> i = adj.listIterator(); 
            
            System.out.println("adj =" + adj.toString());
            System.out.println("map =" + previousMap.toString());

            
            // checking neighbors
            while (i.hasNext()) { 
                Point n = i.next();
                // if the point is not a wall, unvisited, unqueued
                if (state.world[n.x][n.y] != state.WALL 
                		&& state.world[n.x][n.y] != state.CLEAR && !queue.contains(n)) 
                { 
                	// VISIT PART
                	phase = 1; // we will have to come back
                	
                	// visit it & queue it
                	System.out.println("From = " + start.toString());
                	System.out.println("To = " + s.toString());
                	goToPoint(start, n);
                    queue.add(n);                    
                    Integer next = actionQueue.poll();
                    System.out.println("next=" + next);
                	System.out.println("GOING to visit : " + n.toString());  
                	
                    System.out.println("queue = {" + queue.toString()+"}");
                    System.out.println("startPos = {" + startPos.toString()+"}");
                	
    	    		// RETURN TO EXPLORE NEIGHBOUR, ie it will be marked clear
    	    		return doAction(next, (DynamicPercept)percept);
                } //end if
                
                else if (state.world[n.x][n.y] == state.WALL ) {
                	// if we found that the position we added is a wall
                	queue.remove(n);
                }

            } // end neighbors while
            
            queue.remove(s); // all neighbors checked, dequeue the the current point

        } 
        // end if queue's size is 0, ie if we are finish*
        Point currentPos = new Point(state.agent_x_position, state.agent_y_position);
        while (!currentPos.equals(homePos)) {
            goToPoint(currentPos, homePos);
            Integer next = actionQueue.poll();
    		return doAction(next, (DynamicPercept)percept);
    		}
        // shutdown
        state.agent_last_action = state.ACTION_NONE;
        return NoOpAction.NO_OP;
        
	} // end BFS
	
	@Override
	public Action execute(Percept percept) {
		
		// DO NOT REMOVE this if condition!!!
    	if (initnialRandomActions>0) {
    		return moveToRandomStartPoint((DynamicPercept) percept);
    	} else if (initnialRandomActions==0) {
    		// process percept for the last step of the initial random actions
    		initnialRandomActions--;
    		state.updatePoint((DynamicPercept) percept);
			System.out.println("Processing percepts after the last execution of moveToRandomStartPoint()");
			state.agent_last_action=state.ACTION_SUCK;
	    	return LIUVacuumEnvironment.ACTION_SUCK;
    	}
		
    	// This example agent program will update the internal agent state while only moving forward.
    	// START HERE - code below should be modified!
//    	System.out.println("init= : " + init);
//    	while (init) {
//    		return goHome((DynamicPercept)percept);
//    	}
//    	    	
    	System.out.println("x=" + state.agent_x_position);
    	System.out.println("y=" + state.agent_y_position);
    	System.out.println("dir=" + state.agent_direction);
    	
		
	    iterationCounter--;
	    
	    if (iterationCounter==0)
	    	return NoOpAction.NO_OP;

	    DynamicPercept p = (DynamicPercept) percept;
	    Boolean bump = (Boolean)p.getAttribute("bump");
	    Boolean dirt = (Boolean)p.getAttribute("dirt");
	    System.out.println("percept: " + p);
	    
	    // State update based on the percept value and the last action
	    state.updatePoint((DynamicPercept)percept);
	    if (bump) {
			switch (state.agent_direction) {
			case MyAgentState.NORTH:
				state.updateWorld(state.agent_x_position,state.agent_y_position-1,state.WALL);
				break;
			case MyAgentState.EAST:
				state.updateWorld(state.agent_x_position+1,state.agent_y_position,state.WALL);
				break;
			case MyAgentState.SOUTH:
				state.updateWorld(state.agent_x_position,state.agent_y_position+1,state.WALL);
				break;
			case MyAgentState.WEST:
				state.updateWorld(state.agent_x_position-1,state.agent_y_position,state.WALL);
				break;
			}
	    }
	    if (dirt)
	    	state.updateWorld(state.agent_x_position,state.agent_y_position,state.DIRT);
	    else
	    	state.updateWorld(state.agent_x_position,state.agent_y_position,state.CLEAR);
	    
	    state.printWorldDebug();
	    
	    
	    // Next action selection based on the percept value
	    if (dirt)
	    {
	    	System.out.println("DIRT -> choosing SUCK action!");
	    	state.agent_last_action=state.ACTION_SUCK;
	    	return LIUVacuumEnvironment.ACTION_SUCK;
	    } 
	    else
	    {
	    	
	    	System.out.println("actionQueue= " + actionQueue.toString());
	    	
	    	// BFS
	    	return BFS(new Point(state.agent_x_position,state.agent_y_position), (DynamicPercept) percept);
	    }
	}
}

public class MyVacuumAgent extends AbstractAgent {
    public MyVacuumAgent() {
    	super(new MyAgentProgram());
	}
}
