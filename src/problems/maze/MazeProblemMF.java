package problems.maze;

import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.Collection;

import learning.*;
import static problems.maze.Maze.HOLE;
import static problems.maze.Maze.WATER;
import static problems.maze.MazeAction.DIVE;
import visualization.*;
import utils.*;

/** 
 * Implements the maze problem as a model free problem.
 */
public class MazeProblemMF extends MFLearningProblem  implements MazeProblem, ProblemVisualizable{
	
	/** Size of the problem. Default value is 10.*/
	protected int size = 10;
	
	/** Maze */
	protected Maze maze;
	
	/** Constructors */
	public MazeProblemMF(){
		this.maze = new Maze(size,0);
		initialState = new MazeState(maze.posHamster.x, maze.posHamster.y);
	}
	public MazeProblemMF(int size){
		this.size = size;
		this.maze = new Maze(size, 0);
	}
	public MazeProblemMF(int size, int seed){
		this.size = size;
		this.maze = new Maze(size,seed);
		initialState = new MazeState(maze.posHamster.x, maze.posHamster.y);
	}
	public MazeProblemMF(Maze maze){
		this.size = maze.size;
		this.maze = maze;
		initialState = new MazeState(maze.posHamster.x, maze.posHamster.y);
	}	
	
	/** Generates a random instance of the problem given the seed. */
	private void generateInstance(int size, int seed) {
		this.size = size;
		this.maze = new Maze(size,seed);
	    initialState = new MazeState(maze.posHamster.x, maze.posHamster.y);
	}		
	
	/** Returns a reference to the maze */
	public Maze getMaze(){
		return this.maze;
	}
	
	// Inherited from Learning Problem
	
	/** Whether the state corresponds to a final state (CAT or CHEESE).*/
	@Override
	public boolean isFinal(State state) {

		// COMPLETAR

                MazeState mazeState = (MazeState) state;
                int X = mazeState.X();
 		int Y = mazeState.Y();
 		// True if there is a cat or there is cheese.
 		return maze.cells[X][Y]==Maze.CAT || maze.cells[X][Y]==Maze.CHEESE;
	}

	/** Returns the set of actions that can be done at each step. */
	@Override
	public ArrayList<Action> getPossibleActions(State state) {
		MazeState mazeState = (MazeState) state;
		ArrayList<Action> possibleActions = new ArrayList<Action>();

		if(maze.cells[mazeState.X()][mazeState.Y()]==HOLE){
                    possibleActions.add(MazeAction.DIVE);
                }                
                if ((mazeState.Y()<maze.size-1) && (maze.cells[mazeState.X()][mazeState.Y()+1]!=Maze.WALL)){		
                    possibleActions.add(MazeAction.DOWN);
                }
		if ((mazeState.Y()>0) && (maze.cells[mazeState.X()][mazeState.Y()-1]!=Maze.WALL)){		
                    possibleActions.add(MazeAction.UP);
                }    
                if ((mazeState.X()<maze.size-1) && (maze.cells[mazeState.X()+1][mazeState.Y()]!=Maze.WALL)){
                    possibleActions.add(MazeAction.RIGHT);
                }
		if ((mazeState.X()>0) && (maze.cells[mazeState.X()-1][mazeState.Y()]!=Maze.WALL)){		
                    possibleActions.add(MazeAction.LEFT);
                }
		// Returns the actions.
		return possibleActions;
	}	
	
	/** Returns the reward of an state. */	
	@Override
	public double getReward(State state){
            // COMPLETAR
		MazeState mazeState = (MazeState) state;
                
                //if it is a cheese-->R= 100
		if(mazeState.X()==maze.posCheese.x && mazeState.Y()==maze.posCheese.y){
                    return 100;
                }
                //if it is a cat-->R= -100
                for(Position cat : maze.posCats){
                    if((mazeState.X()==cat.x)&&(mazeState.Y()==cat.y)){
                        return -100;
                    }
                }
                return 0;
	}	
	
	/**  In this case, the transition reward penalizes distance. */	
	@Override
	public double getTransitionReward(State fromState, Action action, State toState) {
		double reward = 0;
                // If there is no transition, returns 0.
		if ((fromState==null) || (toState==null)){return reward;
                } else{
    
		int aX= ((MazeState)toState).X();
                    int aY = ((MazeState)toState).Y();
                    int deX = ((MazeState)fromState).X();
                    int deY = ((MazeState)fromState).Y();

                    double distancia = Math.sqrt((Math.pow(aX-deX,2))+(Math.pow(aY-deY,2)));

                    reward = distancia*-1;

                    if (maze.cells[deX][deY]==Maze.WATER) {
                            reward = reward*2;
                    }
                    if (action==MazeAction.DIVE) {
                            reward = reward*0.5;
                    }
		}
		return reward;
	}	
	
	// From MFLearningProblem
	
	/** Generates the transition model for a certain state in this particular problem. 
	  * Assumes that the  action can be applied. This method is PRIVATE.*/
	private StateActionTransModel mazeTransitionModel(State state, Action action){
		// Structures contained by the transition model.
		State[] reachable;
		double[] probs;	
		
		// Coordinates of the current state
		int fromX,fromY;
		fromX = ((MazeState) state).X();
		fromY = ((MazeState) state).Y();	
		
		/* First considers diving. */
		if (action==MazeAction.DIVE){
			// It must be a hole. Gets the outputs.
			Position inputHolePos = new Position(fromX,fromY);
			// It considers all holes but one
			reachable = new State[maze.numHoles-1];
			probs = new double[maze.numHoles-1];
			int holeNum=0;
			for (Position holePos: maze.holeList){
				if (holePos.equals(inputHolePos))
					continue;
				reachable[holeNum]=new MazeState(holePos);
				probs[holeNum++] = 1.0/(maze.numHoles-1);
			}
			// Returns 
			return new StateActionTransModel(reachable, probs);
		}
		
	
		// Creates the transition model.
		reachable = new State[4];
		probs = new double[4];
		
		// Probability of error 0.1 times each position.
		double probError = 0.1;
		double probSuccess = 1.0 - probError*(4-1);
		
		int ind=0;
		//
		// Cell X,Y-1
		//
		// Probability
		if (action==MazeAction.UP)
			probs[ind]=probSuccess;
		else
			probs[ind]=probError;
		// Reached state
		if ((fromY>0) && (maze.cells[fromX][fromY-1]!=Maze.WALL))  
			reachable[ind] = new MazeState(fromX,fromY-1); // Can move
		else
			reachable[ind] = new MazeState(fromX,fromY); // Can't move
	
		
		ind++;
		//
		// Cell X,Y+1
		//
		// Probability
		if (action==MazeAction.DOWN)
			probs[ind]=probSuccess;
		else
			probs[ind]=probError;	
		// Reached state
		if ((fromY<maze.size-1) && (maze.cells[fromX][fromY+1]!=Maze.WALL))  
			reachable[ind] = new MazeState(fromX,fromY+1); // Can move
		else
			reachable[ind] = new MazeState(fromX,fromY); // Can't move
	
		
		ind++;
		//
		// Cell X-1,Y
		//
		// Probability
		if (action==MazeAction.LEFT)
			probs[ind]=probSuccess;
		else
			probs[ind]=probError;
		// Reached state
		if ((fromX>0) && (maze.cells[fromX-1][fromY]!=Maze.WALL))  
			reachable[ind] = new MazeState(fromX-1,fromY); // Can move
		else
			reachable[ind] = new MazeState(fromX,fromY); // Can't move

		
		ind++;
		//
		// Cell X,Y+1
		//
		// Probability
		if (action==MazeAction.RIGHT)
			probs[ind]=probSuccess;
		else
			probs[ind]=probError;
		// Reached state
		if ((fromX<maze.size-1) && (maze.cells[fromX+1][fromY]!=Maze.WALL))  
			reachable[ind] = new MazeState(fromX+1,fromY); // Can move
		else
			reachable[ind] = new MazeState(fromX,fromY); // Can't move
				
		
		// Returns 
		return new StateActionTransModel(reachable, probs);
	}	
	
	
	// From MFLearningModel
	
	/** Updates the environment. */
	@Override
	public void updateEnvironment(State state, Action action) {

	}
	
	/** Generates the new state. */
	@Override
	public State readNewState(State state, Action action) { 
		MazeState mazestate=(MazeState)state;
                MazeAction mazeaction=(MazeAction)action;
                MazeState solution=null;
                switch(mazeaction){
                    case UP:
                        if(this.maze.cells[mazestate.X()][mazestate.Y()-1]==Maze.WALL)return state;
                        else solution=new MazeState(mazestate.X(),mazestate.Y()-1);
                        break;
                    case DOWN:
                        if(this.maze.cells[mazestate.X()][mazestate.Y()+1]==Maze.WALL)return state;
                        else solution=new MazeState(mazestate.X(),mazestate.Y()+1);
                        break;
                    case LEFT:
                        if(this.maze.cells[mazestate.X()-1][mazestate.Y()]==Maze.WALL)return state;
                        else solution=new MazeState(mazestate.X()-1,mazestate.Y());
                        break;
                    case RIGHT:  
                        if(this.maze.cells[mazestate.X()+1][mazestate.Y()]==Maze.WALL)return state;
                        else solution=new MazeState(mazestate.X()+1,mazestate.Y());
                        break;
                    case DIVE:
                        do{
                            solution=(MazeState)getRandomState();
                        }while(this.maze.cells[solution.X()][solution.Y()]!=HOLE);
                        break;
                }
            return solution;
	}
	
	// Utilities
	
	/** Returns a random state. */
	@Override
	public State getRandomState() {
		// Returns only positions corresponding to empty cells.
		int posX, posY;
		boolean validCell = false;
		do{
			posX = Utils.random.nextInt(size);
			posY = Utils.random.nextInt(size);
			// Walls are not valid states. 
			if (maze.cells[posX][posY]==Maze.WALL)
				continue;
			// Sometimes (not very often) there are empty cells surrounded
			// by walls or by the limit of the maze.  Test that there is at least
			// an adjacent position to move.
			if (posX>0  && maze.cells[posX-1][posY]==Maze.EMPTY) validCell = true;
			if (posX<maze.size-1  && maze.cells[posX+1][posY]==Maze.EMPTY) validCell = true;
			if (posY>0  && maze.cells[posX][posY-1]==Maze.EMPTY) validCell = true;
			if (posY>maze.size-1  && maze.cells[posX][posY+1]==Maze.EMPTY) validCell = true;
		} while (!validCell);
		return new MazeState(posX,posY);
	}
	

	/** Generates and instance of the problem.*/
	@Override
	public void setParams(String[] params) {
		try {
			if (params.length==1) generateInstance(Integer.parseInt(params[0]), 0);
			else generateInstance(Integer.parseInt(params[0]), Integer.parseInt(params[1]));		
		}
		catch (Exception E) {
			System.out.println("There has been an error while generating the na new instance of MazeProblem.");
		}
	}	
	
	/* Visualization */
	
	/** Returns a panel with the view of the problem. */
	public ProblemView getView(int sizePx){
		MazeView mazeView = new MazeView(this, sizePx);
		return mazeView;
	}
	
	/* Test */
	public static void main(String[] args) {
		MazeProblemMF mazeProblem = new MazeProblemMF(15, 5);
//		ProblemVisualization mainWindow = new ProblemVisualization(mazeProblem,600);		// Main window
		
		State currentState = mazeProblem.initialState();
		System.out.println("Current state: "+currentState);
		System.out.println("Reward: "+mazeProblem.getReward(currentState));
		System.out.println("Is final: "+mazeProblem.isFinal(currentState));
		
		System.out.println("Possible actions: ");
		ArrayList<Action> actions = mazeProblem.getPossibleActions(currentState);
		for (Action action: actions)
			System.out.println("\t"+action);
		
		System.out.println("Apply action UP.\n");
		State newState = mazeProblem.applyAction(currentState, MazeAction.UP);
		System.out.println("New state: "+newState);
		System.out.println("Transition reward:"+ mazeProblem.getTransitionReward(currentState, MazeAction.UP, newState));
	}

}
