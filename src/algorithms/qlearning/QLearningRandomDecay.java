package algorithms.qlearning;

import java.util.ArrayList;
import java.util.Random;
import learning.*;
import problems.maze.MazeAction;
import problems.maze.MazeProblemMF;
import problems.maze.MazeState;
import utils.Utils;
import static utils.Utils.random;
/** 
 * This class must implement the QLearning algorithm to learn the optimal policy. 
 */
public class QLearningRandomDecay extends LearningAlgorithm{

	/* Table containing the Q values for each pair State-Action. */
	private QTable qTable;
	
	/* Number of iterations used to learn the algorithm.*/
	private int iterations=1000;
	
	/* Alpha parameter. */
	private double alpha=0.1;
	
	/* Probability of doing a random selection of the action (instead of q) */
	private double probGreedy=0;              //change the probability of greedy to 0
	
	/** Sets the number of iterations. */
	public void setIterations(int iterations){ this.iterations = iterations; }
	
	/** Sets the parameter alpha. */
	public void setAlpha(double alpha){ this.alpha = alpha; }
	
	/** 
	 * Learns the policy (notice that this method is protected, and called from the 
	 * public method learnPolicy(LearningProblem problem, double gamma) in LearningAlgorithm.
	 */
	public void learnPolicy(){
		// Creates the QTable
		qTable = new QTable(problem);
		
		// The algorithm carries out a certain number of iterations
		for (int nIteration=0; nIteration<iterations; nIteration++){
			State currentState, newState;         // Current state and new state
			Action selAction=null;                     // Selected action
			double Q, reward, maxQ;               // Values necessary to update the table.
			// Generates a new initial state.
			 currentState = problem.getRandomState();
			// Use fix init point for debugging
			// currentState = problem.getInitialState(); 
			
			// Iterates until it finds a final state.
			MazeProblemMF problem=((MazeProblemMF)this.problem);     
			 // TO DO
                         
                        probGreedy = (nIteration+1)/iterations;//reduce the probability of randomness every iteration
                        //it1=> 1 / 100 = 0.01
                        //it10=> 10 / 100 = 0.1
                        //it100=> 100 / 100 = 1
			                   
                        do{
                            if(!qTable.contains(currentState)) {
				qTable.setQValue(currentState, problem.getPossibleActions(currentState).get(0), 0.0);
                            }
                            selAction=qTable.getActionMaxValue(currentState);   //select action with max reward from QTable from current state
                        
                            if(selAction==null || random.nextInt(100)> this.probGreedy*100){           //TO FIX THE BUG OF THE QLEARNING
                                 //generate a random action
                                //selected action = random(Action);
                                ArrayList<Action> actions = this.problem.getPossibleActions(currentState);
                                int randomaction = Utils.random.nextInt(actions.size());
                                selAction=actions.get(randomaction); 
                            }
                            newState=problem.applyAction(currentState, selAction);//apply chosen action ----------------------------
                            //CALCULATE NEW UTILITY:
                               //read the new state by applying the action chosen---------------
                            reward=problem.getReward(newState);                         //get the reward from the new state
                            maxQ=qTable.getMaxQValue(newState);                         //get the max Q value from the new state
                            if(problem.isFinal(newState)){  
                                Q=(1-alpha)*qTable.getQValue(currentState, selAction)+alpha*reward;
                            }//formula=(1-a)*Q(s,a)+a*R(s')+y*max[Q(s')]
                            else{
                               Q=(1-alpha)*qTable.getQValue(currentState, selAction)+alpha*(reward+problem.gamma*maxQ); 
                            }
                            qTable.setQValue(currentState, selAction, Q);               //update the QTable of our state with the value for the chosen action
                            currentState=newState;                                      //go to the next state
                        }while(!problem.isFinal(currentState));                         //while the state is not final
		}   
		solution = qTable.generatePolicy();                                     //generate the policy for the QTable
	}

	/** Sets the parameters of the algorithm. */
	@Override
	public void setParams(String[] args) {
		if (args.length>0){
			// Alpha
			try{
				alpha = Double.parseDouble(args[0]);
			} 
			catch(Exception e){
				System.out.println("The value for alpha is not correct. Using 0.75.");
			}	
			// Maximum number of iterations.
			if (args.length>1){
				try{
					iterations = Integer.parseInt(args[1]);
				} 
				catch(Exception e){
					System.out.println("The value for the number of iterations is not correct. Using 1000.");
				}   
			}
		}
	}
	
	/** Prints the results */
	public void printResults(){
		// Prints the utilities.
		System.out.println("QLearning \n");
		// Prints the policy
		System.out.println("\nOptimal policy");
		System.out.println(solution);
		// Prints the qtable
		System.out.println("QTable");
		System.out.println(qTable);
	}
	
	/** Main function. Allows testing the algorithm with MDPExProblem */
	public static void main(String[] args){
		LearningProblem mdp = new problems.mdpexample2.MDPExProblem();
		mdp.setParams(null);
		QLearning ql = new QLearning();
		ql.setProblem(mdp);
		ql.learnPolicy(mdp);
		ql.printResults();
	}	
}
