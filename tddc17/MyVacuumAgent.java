package tddc17;


import aima.core.environment.liuvacuum.*;
import sun.security.acl.WorldGroupImpl;
import aima.core.agent.Action;
import aima.core.agent.AgentProgram;
import aima.core.agent.Percept;
import aima.core.agent.impl.*;

import java.awt.Point;
import java.util.Iterator;
import java.util.LinkedList;
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
	public void updatePosition(DynamicPercept p)
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
	private int max_width = 20;
	private int max_height = 20;
	public int iterationCounter = max_height*max_width*2;
	public MyAgentState state = new MyAgentState();
	private boolean init = true;
	private int turnCommand = 0; // 1 -> first turn , 2 -> move forward, 3 -> second turn
	private boolean lastTurnRight = false; // true -> right, false -> left
	private boolean terminated = false;
	private LinkedList<Point> queue = new LinkedList<Point>(); 
	private LinkedList<Integer> actionQueue = new LinkedList<Integer>(); 

    //private HashMap<Point, LinkedList<Point>> adj = new HashMap<Point, LinkedList<Point>>(); 

	// moves the Agent to a random start position
	// uses percepts to update the Agent position - only the position, other percepts are ignored
	// returns a random action
	private Action moveToRandomStartPosition(DynamicPercept percept) {
		int action = random_generator.nextInt(6);
		initnialRandomActions--;
		state.updatePosition(percept);
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
		
		return moveForward(p);
	}
	
	private Action goHome(DynamicPercept percept) {
	    iterationCounter--;
		System.out.println("Going home.");
		state.updatePosition(percept);
		if (!(Boolean)percept.getAttribute("home")) {
			while (state.agent_x_position != 1 ){
				while(state.agent_direction != MyAgentState.WEST){
					return turnRight(percept);
				}
				state.agent_last_action = state.ACTION_MOVE_FORWARD;
				return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
			}
			while (state.agent_y_position != 1 ){
				while(state.agent_direction != MyAgentState.NORTH){
					return turnRight(percept);
				}
				state.agent_last_action = state.ACTION_MOVE_FORWARD;
				return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
			}
		}
		System.out.println("Home.");
		
		if (terminated) {
			System.out.println("All cleared.");
			state.agent_last_action = state.ACTION_NONE;
			init = false;
			return NoOpAction.NO_OP;			
		}
		
		while(state.agent_direction != 1){
			return turnRight(percept);
		}
		init = false;
		state.agent_last_action = state.ACTION_SUCK;
		return LIUVacuumEnvironment.ACTION_SUCK;
	}	
	
	private void goToPoint (Point goal) {
		int x_epsilon = state.agent_x_position - goal.x;
		int y_epsilon = state.agent_y_position - goal.y;
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
			else if (y_epsilon == 1) {// goal under
				// turn 180
				actionQueue.add(state.ACTION_TURN_RIGHT);
				actionQueue.add(state.ACTION_TURN_RIGHT);
				actionQueue.add(state.ACTION_MOVE_FORWARD);

			}
			else if (y_epsilon == -1) { // goal above
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
			else if (y_epsilon == 1) { // goal under
				actionQueue.add(state.ACTION_TURN_RIGHT);
				actionQueue.add(state.ACTION_MOVE_FORWARD);

			}
			else if (y_epsilon == -1) { // goal above
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
			else if (y_epsilon == 1) { // goal under
				actionQueue.add(state.ACTION_MOVE_FORWARD);

			}
			else if (y_epsilon == -1) { // goal above
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
			else if (y_epsilon == 1) { // goal under
				actionQueue.add(state.ACTION_TURN_LEFT);
				actionQueue.add(state.ACTION_MOVE_FORWARD);

			}
			else if (y_epsilon == -1) { // goal above
				actionQueue.add(state.ACTION_TURN_RIGHT);
				actionQueue.add(state.ACTION_MOVE_FORWARD);
			}
			break;			
		}		
	}
 
    // prints BFS traversal from a given source s 
    void BFS(Point start, boolean bump) 
    { 
        // Mark all the vertices as not visited(By default 
        // set as false) 
        //LinkedList<Point> visited = new LinkedList<Point>();
  
        // Mark the current node as visited and enqueue it 
        //visited.add(currentNode); 
        queue.add(start); 
        
        // if the current node is not the one in top of the queue, go back
        if (!start.equals(queue.peek())) {
        	actionQueue.add(state.ACTION_TURN_RIGHT);
        	actionQueue.add(state.ACTION_TURN_RIGHT);
        	actionQueue.add(state.ACTION_MOVE_FORWARD);
        	return;
        }
        	   
        
  
        while (queue.size() != 0) 
        { 
            // Dequeue a vertex from queue and print it 
            Point s = queue.peek(); 
            System.out.print(s.toString()+" "); 
  
            // Get all adjacent vertices of the dequeued vertex s 
            // If a adjacent has not been visited, then mark it 
            // visited and enqueue it 
            LinkedList<Point> adj = new LinkedList<Point>();
            adj.add(new Point(start.x+1,start.y)); // EAST
            adj.add(new Point(start.x,start.y+1)); // NORTH
            adj.add(new Point(start.x-1,start.y)); // WEST
            adj.add(new Point(start.x,start.y-1)); // SOUTH

            Iterator<Point> i = adj.listIterator(); 
            while (i.hasNext()) 
            { 
                Point n = i.next(); 
                if (!bump && state.world[n.x][n.y] != state.CLEAR) // if not visited
                { 
                	// visit it & queue it
                	goToPoint(n);
                    queue.add(n); 
                	return;
                } 
            }
            queue.remove(s);
        } 
    } // end BFS
	
	@Override
	public Action execute(Percept percept) {
		
		// DO NOT REMOVE this if condition!!!
    	if (initnialRandomActions>0) {
    		return moveToRandomStartPosition((DynamicPercept) percept);
    	} else if (initnialRandomActions==0) {
    		// process percept for the last step of the initial random actions
    		initnialRandomActions--;
    		state.updatePosition((DynamicPercept) percept);
			System.out.println("Processing percepts after the last execution of moveToRandomStartPosition()");
			state.agent_last_action=state.ACTION_SUCK;
	    	return LIUVacuumEnvironment.ACTION_SUCK;
    	}
		
    	// This example agent program will update the internal agent state while only moving forward.
    	// START HERE - code below should be modified!
    	System.out.println("init= : " + init);
    	while (init) {
    		return goHome((DynamicPercept)percept);
    	}
    	    	
    	System.out.println("x=" + state.agent_x_position);
    	System.out.println("y=" + state.agent_y_position);
    	System.out.println("dir=" + state.agent_direction);
    	
		
	    iterationCounter--;
	    
	    if (iterationCounter==0)
	    	return NoOpAction.NO_OP;

	    DynamicPercept p = (DynamicPercept) percept;
	    Boolean bump = (Boolean)p.getAttribute("bump");
	    Boolean dirt = (Boolean)p.getAttribute("dirt");
	    Boolean home = (Boolean)p.getAttribute("home");
	    System.out.println("percept: " + p);
	    
	    // State update based on the percept value and the last action
	    state.updatePosition((DynamicPercept)percept);
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

	    	/*if (bump) {
	    		// check if we reach bottom right corner 
	    		System.out.println(state.world[state.agent_x_position+1][state.agent_y_position]);
	    		System.out.println(state.world[state.agent_x_position][state.agent_y_position + 1]);

	    		if (state.world[state.agent_x_position+1][state.agent_y_position] == state.WALL
	    				&& state.world[state.agent_x_position][state.agent_y_position + 1] == state.WALL) {
	    			terminated = true;
	    			init = true;
	    			while (init) {
	    	    		return goHome((DynamicPercept)percept);
	    	    	}	    	    	
	    			
	    		}
	    		
	    		
    			turnCommand = 2;
    			if(lastTurnRight) {
    				return turnLeft((DynamicPercept)percept);
    			}
    			return turnRight((DynamicPercept)percept);	  		
	    	}
	    		
    		if (turnCommand == 2) {
    			turnCommand = 3;
    			return moveForward((DynamicPercept)percept);
    		} 
    		else if (turnCommand == 3) {
    			turnCommand = 0;
    			if(lastTurnRight) {
    				lastTurnRight = false;
    				return turnLeft((DynamicPercept)percept);
    			}
    			lastTurnRight = true;
    			return turnRight((DynamicPercept)percept);
    		}
    		

    		else {
	    		state.agent_last_action=state.ACTION_MOVE_FORWARD;
			    return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
			} */
	    	
	    	// fill action Queue with BFS
	    	BFS(new Point(state.agent_x_position,state.agent_x_position), bump);
	    	
	    	System.out.println(actionQueue.toString());
    		
	    	if (!actionQueue.isEmpty()) {
	    		Integer next = actionQueue.poll();
	    		return doAction(next, (DynamicPercept)percept);
	    	}
    	

	    }
		return moveForward((DynamicPercept) percept); 
	}
}

public class MyVacuumAgent extends AbstractAgent {
    public MyVacuumAgent() {
    	super(new MyAgentProgram());
	}
}
