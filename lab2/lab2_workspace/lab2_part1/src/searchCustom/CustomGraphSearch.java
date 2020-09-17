package searchCustom;

import java.util.ArrayList;
import java.util.HashSet;

import searchShared.NodeQueue;
import searchShared.Problem;
import searchShared.SearchObject;
import searchShared.SearchNode;

import world.GridPos;

public class CustomGraphSearch implements SearchObject {

	private HashSet<SearchNode> explored;
	private NodeQueue frontier;
	protected ArrayList<SearchNode> path;
	private boolean insertFront;

	/**
	 * The constructor tells graph search whether it should insert nodes to front or back of the frontier 
	 */
    public CustomGraphSearch(boolean bInsertFront) {
		insertFront = bInsertFront;
    }

	/**
	 * Implements "graph search", which is the foundation of many search algorithms
	 */
	public ArrayList<SearchNode> search(Problem p) {
		// The frontier is a queue of expanded SearchNodes not processed yet
		frontier = new NodeQueue();
		/// The explored set is a set of nodes that have been processed 
		explored = new HashSet<SearchNode>();
		// The start state is given
		GridPos startState = (GridPos) p.getInitialState();
		// Initialize the frontier with the start state  
		frontier.addNodeToFront(new SearchNode(startState));

		// Path will be empty until we find the goal.
		path = new ArrayList<SearchNode>();
		
		// Implement this!
		//System.out.println("Implement CustomGraphSearch.java!");
		
		
		/* Some hints:
		 * -Read early part of chapter 3 in the book!
		 * -You are free to change anything how you wish as long as the program runs, but some structure is given to help you.
		 * -You can Google for "javadoc <class>" if you are uncertain of what you can do with a particular Java type.
		 * 
		 * -SearchNodes are the nodes of the search tree and contains the relevant problem state, in this case x,y position (GridPos) of the agent 
		 * --You can create a new search node from a state by: SearchNode childNode = new SearchNode(childState, currentNode);
		 * --You can also extract the state by .getState() method
		 * --All search structures use search nodes, but the problem object only speaks in state, so you may need to convert between them 
		 * 
		 * -The frontier is a queue of search nodes, open this class to find out what you can do with it! 
		 * 
		 * -If you are unfamiliar with Java, the "HashSet<SearchNode>" used for the explored set means a set of SearchNode objects.
		 * --You can add nodes to the explored set, or check if it contains a node!
		 * 
		 * -To get the child states (adjacent grid positions that are not walls) of a particular search node, do: ArrayList<GridPos> childStates = p.getReachableStatesFrom(currentState);
		 * 
		 * -Depending on the addNodesToFront boolean variable, you may need to do something with the frontier... (see book)
		 * 
		 * -You can check if you have reached the goal with p.isGoalState(NodeState)
		 * 
		 *  When the goal is found, the path to be returned can be found by: path = node.getPathFromRoot();
		 */
		/* Note: Returning an empty path signals that no path exists */
		
		//if the frontier is empty then return failure
		while (!frontier.isEmpty()) {
			//choose a leaf node and remove it from the frontier
			SearchNode n = frontier.removeFirst();
			//if the node contains a goal state then return the corresponding solution
			if(p.isGoalState(n.getState())) {
				path = n.getPathFromRoot();
				break;
			}				
			//add the node to the explored set
			explored.add(n);
			
			//expand the chosen node, adding the resulting nodes to the frontier only if not in the frontier or explored set
			ArrayList<GridPos> resultingNodes = p.getReachableStatesFrom(n.getState());
			for (int i=0; i < resultingNodes.size(); ++i) {
				GridPos pos = resultingNodes.get(i);
				SearchNode node = new SearchNode(pos,n);
				if (!frontier.contains(node) && !explored.contains(node)) {
					if (insertFront) {
						frontier.addNodeToFront(node);
					}
					else {
						frontier.addNodeToBack(node);
					}
					
				}
				
			}
				
			
		}
		
		
		
		return path;
	}

	/*
	 * Functions below are just getters used externally by the program 
	 */
	public ArrayList<SearchNode> getPath() {
		return path;
	}

	public ArrayList<SearchNode> getFrontierNodes() {
		return new ArrayList<SearchNode>(frontier.toList());
	}
	public ArrayList<SearchNode> getExploredNodes() {
		return new ArrayList<SearchNode>(explored);
	}
	public ArrayList<SearchNode> getAllExpandedNodes() {
		ArrayList<SearchNode> allNodes = new ArrayList<SearchNode>();
		allNodes.addAll(getFrontierNodes());
		allNodes.addAll(getExploredNodes());
		return allNodes;
	}

}
