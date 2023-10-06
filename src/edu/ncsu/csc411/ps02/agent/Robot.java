package edu.ncsu.csc411.ps02.agent;

import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import edu.ncsu.csc411.ps02.environment.Tile;
import edu.ncsu.csc411.ps02.environment.TileStatus;
import edu.ncsu.csc411.ps02.environment.Action;
import edu.ncsu.csc411.ps02.environment.Environment;
import edu.ncsu.csc411.ps02.environment.Position;

/**
	Represents an intelligent agent moving through a particular room.	
	The robot only has two sensors - the ability to retrieve the 
	the status of all its neighboring tiles, including itself, and the
	ability to retrieve to location of the TARGET tile.
	
	Your task is to modify the getAction method below so that it reaches
	TARGET with a minimal number of steps.
*/

public class Robot {
	private Environment env;
	private List<Action> precomputedPath = null;  // Store the precomputed path
	private int indx = 0;  // Index to track the robot's progress along the path
	
	/** Initializes a Robot on a specific tile in the environment. */
	public Robot (Environment env) { this.env = env; }
	
	/**
    Problem Set 02 - Modify the getAction method below in order to simulate
    the passage of a single time-step. At each time-step, the Robot decides
    which tile to move to.
    
    Your task for this Problem Set is to modify the method below such that
    the Robot agent is able to reach the TARGET tile on a given Environment. 
    5 out of the 10 graded test cases, with explanations on how to create new
    Environments, are available under the test package.
    
    This method should return a single Action from the Action class.
    	- Action.DO_NOTHING
    	- Action.MOVE_UP
    	- Action.MOVE_DOWN
    	- Action.MOVE_LEFT
    	- Action.MOVE_RIGHT
	 */

	/**
		Replace this docstring comment with an explanation of your implementation.
	 */
	public Action getAction () {
		// This example code demonstrates the available methods and actions,
		// such as retrieving its senses (neighbor positions), getting the status of
		// those tiles, and returning the different available Actions
		
		Position selfPos = env.getRobotPosition(this);
		// For this problem set, the Environment class allows for complete
		// observability. You are able to find all neighbor positions for 
		// any given Position object by passing a Position object to
		// getNeighborPositions
		Map<String, Position> neighbors = env.getNeighborPositions(selfPos);
		Position abovePos = neighbors.get("above"); // Either a Tile or null
		Position belowPos = neighbors.get("below"); // Either a Tile or null
		Position leftPos = neighbors.get("left");   // Either a Tile or null
		Position rightPos = neighbors.get("right"); // Either a Tile or null
		
		// You are able to get their tile using the getTiles method
		Map<Position, Tile> positions = env.getTiles();
		Tile selfTile = positions.get(selfPos);
		
		// The Environment now has a getTarget() method which will return
		// the Position of the Target node.
		Position targetPos = env.getTarget();
		
		// The Position class has also been updated to include an equals method
		if (selfPos.equals(targetPos)) {
			return Action.DO_NOTHING;
		} 
			// You are STRONGLY encouraged to design a search tree.
			// NOTE: There are Java.util implementations for many of the data
			// structures taught in 316. You should create Nodes and utilize
			// these data structures.
		
		
		if (precomputedPath == null) {  // If path is not yet computed, compute it now
			// Find a path to the target using BFS tree search
			precomputedPath = buildTreeSearchTree(selfPos, targetPos);				
			indx = 0;
		}

		// If there's no path or some error occurred, do nothing
		if (precomputedPath == null || precomputedPath.isEmpty()  || indx >= precomputedPath.size()) {
			return Action.DO_NOTHING;
		}
	
		// Return the first action from the path to lead the robot towards the target
		return precomputedPath.get(indx++);
	}

	// create a Treenode class to store the position and the action

	/**
	 * Create  a Treenode class to store the position and the action
	 * This Treenode will be used to create a search tree
	 * using Breadth First Search Algorithm
	 */
	public class TreeNode {
		public Position position;
		public Action action;
		public TreeNode parent;

		public TreeNode(Position position, Action action, TreeNode parent) {
			this.position = position;
			this.action = action;
			this.parent = parent;
		}
	}

	/**
	 * Create a method to check if the position is the goal
	 * @param start the position to be start
	 * @param goal the position to be the goal
	 * @return path if we can find it
	 */
	public List<Action> buildTreeSearchTree (Position start, Position goal) {
		TreeNode root = new TreeNode(start, null, null);
		List<Action> path = breadFirstSearch(root, goal);

		if (path == null) {
			return null;
		} else {
			return path;
		}
	}

	/**
	 * Create a method to implement the Breadth First Search Algorithm
	 * @param root the root of the tree
	 * @param goal the goal of the tree
	 * @return path if we can find it
	 */
	public List<Action> breadFirstSearch(TreeNode root, Position goal) {
		// Create a queue to store the nodes
		Queue<TreeNode> queue = new LinkedList<>();
		// Create a set to store the visited nodes
		HashSet<Position> visited = new HashSet<>();
	
		// Add the root to the queue
		queue.add(root);
		while (!queue.isEmpty()) {
			// Remove the first node from the queue
			TreeNode current = queue.poll();
			// Add the current node to the visited set
			visited.add(current.position);
			
			// Check if the current node is the goal
			if (current.position.equals(goal)) {
				return getPath(current);
			}
	
			// Get the neighbors of the current node
			Map<String, Position> neighbors = env.getNeighborPositions(current.position);
			for (Map.Entry<String, Position> entry : neighbors.entrySet()) {
				Position newPosition = entry.getValue();
				if (!visited.contains(newPosition) && isValidPosition(newPosition)) {
					Action action;
					switch (entry.getKey()) {
						case "above":
							action = Action.MOVE_UP;
							break;
						case "below":
							action = Action.MOVE_DOWN;
							break;
						case "left":
							action = Action.MOVE_LEFT;
							break;
						case "right":
							action = Action.MOVE_RIGHT;
							break;
						default:
							continue;
					}
					queue.add(new TreeNode(newPosition, action, current));
					visited.add(newPosition);
				}
			}
		}
	
		return null;
	}

	/**
	 * Create a method to get the path from the root to the goal
	 * @param current the current node
	 * @return path the path from the root to the goal
	 */
	private List<Action> getPath(TreeNode current) {
		List<Action> path = new ArrayList<>();
    
		while (current.parent != null) {
			path.add(0, current.action);  // Add action to the beginning of the list
			current = current.parent;
		}
		
		return path;
	}


	private boolean isValidPosition(Position pos) {
		if (pos == null || env == null) {
			return false;  // Return false if either pos or env is null
		}
	
		int row = pos.getRow();
		int col = pos.getCol();
		
		// Check if the position is within the boundaries of the environment
		if (row < 0 || row >= env.getRows() || col < 0 || col >= env.getCols()) {
			return false;
		}
	
		// Use getPositionTile to determine if the position exists in the map
		Tile tile = env.getPositionTile(pos);
		
	
		// Check if the tile at the position is IMPASSABLE
		TileStatus tileStatus = tile.getStatus();
		if (tileStatus == TileStatus.IMPASSABLE) {
			return false;
		}
	
		return true;
	}
	
}