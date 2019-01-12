package algorithms.mdp;

import java.util.HashMap;
import java.util.Map.Entry;

import learning.*;
import problems.maze.MazeProblemMDP;

/** 
 * Implements the value iteration algorithm for Markov Decision Processes 
 */
public class ValueIteration extends LearningAlgorithm {
	
	/** Stores the utilities for each state */;
	private HashMap<State, Double> utilities;
	
	/** Max delta. Controls convergence.*/
	private double maxDelta = 0.01;
	
	/** 
	 * Learns the policy (notice that this method is protected, and called from the 
	 * public method learnPolicy(LearningProblem problem, double gamma) in LearningAlgorithm.
	 */
	@Override
	protected void learnPolicy() {
		// This algorithm only works for MDPs
		if (!(problem instanceof MDPLearningProblem)){
			System.out.println("The algorithm ValueIteration can not be applied to this problem (model is not visible).");
			System.exit(0);
		}
		MazeProblemMDP problem=(MazeProblemMDP)this.problem;
                utilities=new HashMap<State, Double>();
                
                //VARIABLES:
                Double aux,//auxiliar for the maximum 
                ActualUtility,//var to calculate actual utility
                auxdelta,//var to calculate difference between utilities
                difference;//var to calculate the difference in between utility iterations
                Action chosenAction=null;//chosen action for the policy at each state
                //INITIALIZE UTILITIES:
                for(State state : problem.getAllStates()){
                    if(this.problem.isFinal(state)){
                        this.utilities.put(state, this.problem.getReward(state));
                    }
                    else{
                        this.utilities.put(state, 0.0);
                    }
                }
                
                //REPEAT:
                             
                do{
                    auxdelta=0.0;
                    for(State state : problem.getAllStates()){                       //for each state
                        if(this.problem.isFinal(state)){
                            this.utilities.put(state, this.problem.getReward(state));
                        }
                        else{
                            aux=-100000000.0;
                            for(Action a : problem.getPossibleActions(state)){           //for each possible action for that state                             
                               ActualUtility=((MDPLearningProblem)problem).getExpectedUtility(state, a, utilities, this.problem.gamma);
                                 //U(s')=R(s)+y*max[Tra(s,a,s')*U(s')]
                                if( ActualUtility>aux){
                                    aux= ActualUtility;
                                    chosenAction=a;
                                }
                            }
                            difference=Math.abs(aux-this.utilities.get(state));                    //save the difference between utilities
                            //UPDATE UTILITY FOR THE STATE:
                            this.utilities.put(state, aux);                                 //update U(s)
         //U(s')=R(s)+y*max[Tra(s,a,s')*U(s')]
                            
                            if(difference > auxdelta){    //if difference between U(s) and U(s') is greater than auxdelta
                                auxdelta=difference;       //->update auxdelta
                            }


                            this.solution.setAction(state, chosenAction);                  //set the policy for the action
                        }
                    }
                    //STOPS WHEN CONVERGES:
                }while(auxdelta>this.maxDelta);                       //do while the difference is still bigger than the allowed
	}
	
	
	/** 
	 * Sets the parameters of the algorithm. 
	 */
	@Override
	public void setParams(String[] args) {
		// In this case, there is only one parameter (maxDelta).
		if (args.length>0){
			try{
                            maxDelta = Double.parseDouble(args[0]);
			} 
			catch(Exception e){
                            System.out.println("The value for maxDelta is not correct. Using 0.01.");
			}	
		}
	}
	
	/** Prints the results */
	public void printResults(){
		// Prints the utilities.
		System.out.println("Value Iteration\n");
		System.out.println("Utilities");
		for (Entry<State,Double> entry: utilities.entrySet()){
                    State state = entry.getKey();
                    double utility = entry.getValue();
                    System.out.println("\t"+state +"  ---> "+utility);
		}
		// Prints the policy
		System.out.println("\nOptimal policy");
		System.out.println(solution);
	}
	
	
	/** Main function. Allows testing the algorithm with MDPExProblem 
        
	public static void main(String[] args){
		LearningProblem mdp = new problems.mdpexample2.MDPExProblem();
		mdp.setParams(null);
		ValueIteration vi = new ValueIteration();
		vi.setProblem(mdp);
		vi.learnPolicy(mdp);
		vi.printResults();
	
	}*/

}
