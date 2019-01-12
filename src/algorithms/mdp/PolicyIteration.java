package algorithms.mdp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import learning.*;
import problems.maze.MazeAction;
import problems.maze.MazeProblemMDP;
import utils.Utils;

public class PolicyIteration extends LearningAlgorithm {
	/** Max delta. Controls convergence.*/
	private double maxDelta = 0.01;
	
	/** 
	 * Learns the policy (notice that this method is protected, and called from the 
	 * public method learnPolicy(LearningProblem problem, double gamma) in LearningAlgorithm.
	 */	
	@Override
	protected void learnPolicy() {
		if (!(problem instanceof MDPLearningProblem)){
			System.out.println("The algorithm PolicyIteration can not be applied to this problem (model is not visible).");
			System.exit(0);
		}
		
		// Initializes the policy randomly
                solution = new Policy();                                                //pi'
		Policy policyAux=null;                                                  //pi
                //CREATE EVERYTHING:
                MazeProblemMDP problem=(MazeProblemMDP)this.problem;
                HashMap<State,Double> utilities = new HashMap<State,Double>();
               
		 // TO DO

                 //policyAux<---random line 2 slide 33
                 //INITIALIZE STATES:
		for(State s:problem.getAllStates()){                                    //for each state of the problem
                    if (!this.problem.isFinal(s)) {
                                //generate a random
                                       //sets for each state of the policy a random action
                        ArrayList<Action> actions = this.problem.getPossibleActions(s);
                        if(actions.size()!=0){
                        int randomaction = Utils.random.nextInt(actions.size());
                        solution.setAction(s, problem.getPossibleActions(s).get(randomaction));
                    }
                }
                }
		// Main loop of the policy iteration.

		 // TO DO

                 do{                                                                    
                     policyAux=solution;//check if copies right                         //set the policy as random
                     utilities=this.policyEvaluation(policyAux);                        //update utilities by evaluating the aux policy
                     solution=this.policyImprovement(utilities);                        //sol= improve the policy with the given utilities
                 }while(!policyAux.equals(solution));                                   //do while both policies are different
                                                                                        //if policies dont change-->return solution policy
                 
	}
		
	
	/** 
	 * Policy evaluation. Calculates the utility given the policy 
	 */
	private HashMap<State,Double> policyEvaluation(Policy policy){
		
		// Initializes utilities. In case of terminal states, the utility corresponds to
		// the reward. In the remaining (most) states, utilities are zero.		
		HashMap<State,Double> utilities = new HashMap<State,Double>();

		 // TO DO

                MazeProblemMDP problem=(MazeProblemMDP)this.problem;
                double auxdelta,newU,difference;
                
                for(State state : problem.getAllStates()){
                    if(this.problem.isFinal(state)){
                        utilities.put(state, this.problem.getReward(state));
                    }
                    else{
                        utilities.put(state, 0.0);
                    }
                }
                do{
                    auxdelta=0.0;
                    for(State state : problem.getAllStates()){                                //old state
                        if(this.problem.isFinal(state)){
                            //UPDATE UTILITY FOR THE STATE:
                            utilities.put(state, this.problem.getReward(state));
                            difference=0.0;                                                 
                        }
                        else{
                            difference=utilities.get(state);                                    //get old utility
                            newU=((MDPLearningProblem)problem).getExpectedUtility(state, policy.getAction(state), utilities, this.problem.gamma);
                            //UPDATE UTILITY FOR THE STATE:
                            utilities.put(state, newU);//U(s)=R(s)+y*SUM[ tra(s,a,s')*u(s') ]
                            difference=Math.abs( difference-utilities.get(state) );             //get new utility and make the difference
                        }                     
                        if( difference > auxdelta){                                              //if difference between U(s) and U(s') is greater than auxdelta
                            auxdelta=difference;                                        //->update auxdelta
                        }   
                    }
                }while(auxdelta>this.maxDelta);                                                 //do while the difference is still bigger than the allowed
                 
		return utilities;
	}

	/** 
	 * Improves the policy given the utility 
	 */
	private Policy policyImprovement(HashMap<State,Double> utilities){
		// Creates the new policy
		Policy newPolicy = new Policy();

		 // TO DO

                MazeProblemMDP problem=(MazeProblemMDP)this.problem;
                double aux;
                double max=-10000000;
                Action maxAction=null;                                                    //IT COULD BE NULL IF NO POSSIBLE ACTIONS WHEN NEWPOLICYSETACTION
                for(State state : problem.getAllStates()){                   //MAYBE FIX-->newPolicysetAction inside the IF CONDITION OF THE MAX;
                                                                           //reset (max) best utility for a state
                    for(Action action : problem.getPossibleActions(state)){                       
                        aux=((MDPLearningProblem)problem).getExpectedUtility(state, action, utilities, this.problem.gamma);
                        if(aux>max){                                                               //if the utility is bigger than newU-->update newU
                            max=aux;
                            maxAction=action;                                   //save the action with best utility
                        }
                    }
                    newPolicy.setAction(state, maxAction);  
                    max=-100000;                    //update policy
                }          
		return newPolicy;
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
		System.out.println("Policy Iteration");
		// Prints the policy
		System.out.println("\nOptimal policy");
		System.out.println(solution);
	}	
	
	/** Main function. Allows testing the algorithm with MDPExProblem
	public static void main(String[] args){
		LearningProblem mdp = new problems.mdpexample2.MDPExProblem();
		mdp.setParams(null);
		PolicyIteration pi = new PolicyIteration();
		pi.setProblem(mdp);
		pi.learnPolicy(mdp);
		pi.printResults();
	}	
	 */
}
