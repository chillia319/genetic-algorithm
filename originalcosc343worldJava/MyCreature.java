import cosc343.assig2.Creature;
import java.util.Random;

/**
* The MyCreate extends the cosc343 assignment 2 Creature.  Here you implement
* creatures chromosome and the agent function that maps creature percepts to
* actions.  
*
* @author  
* @version 1.0
* @since   2017-04-05 
*/
public class MyCreature extends Creature {

  // Random number generator
  Random rand = new Random();
  float[] chromosome;
  
  /* Empty constructor - might be a good idea here to put the code that 
   initialises the chromosome to some random state   
  
   Input: numPercept - number of percepts that creature will be receiving
          numAction - number of action output vector that creature will need
                      to produce on every turn
   Chromosome:float array 0-1
   
   Thirteen: 0:go to where the monster is
             1:go opposite to where the monster is
             2:ignore the monster
             3:go to where the creature is
             4:go opposite to where the creature is
             5:ignore the creature
             6:go to where the food is
             7:go opposite to where the food is
             8:ignore the food
             9:eat the green strawberry
            10:do not eat the green strawberry
            11:eat the red strawberry
            12:do not eat the red strawberry
  */
  public MyCreature(int numPercepts, int numActions) {
    chromosome = new float[13];
    
    for (int i=0;i<13;i++){
      chromosome[i] = rand.nextFloat();
    }
  }
  
  /* This function must be overridden by MyCreature, because it implements
     the AgentFunction which controls creature behavoiur.  This behaviour
     should be governed by a model (that you need to come up with) that is
     parameterise by the chromosome.  
  
     Input: percepts - an array of percepts
            numPercepts - the size of the array of percepts depend on the percept
                          chosen
            numExpectedAction - this number tells you what the expected size
                                of the returned array of percepts should bes
     Returns: an array of actions 
  */

  @Override
  public float[] AgentFunction(int[] percepts, int numPercepts, int numExpectedActions) {
      
      // This is where your chromosome gives rise to the model that maps
      // percepts to actions.  This function governs your creature's behaviour.
      // You need to figure out what model you want to use, and how you're going
      // to encode its parameters in a chromosome.
      
      // At the moment, the actions are chosen completely at random, ignoring
      // the percepts.  You need to replace this code.
      float actions[] = new float[numExpectedActions];
      
      boolean monster;
      boolean food;
      //boolean creature;
      boolean poison;
      int monsterWeight = 5;
      int creatureWeight = 1;
      int foodWeight = 3;
      //int food;
      //int poison;
      
      //whether a monster is nearby
      for(int i=0;i<9;i++){
        if (percepts[i]==1){
          monster = true;
        }else{
          monster = false;
        }
      }
      //whether other creature is nearby
      for(int i=9;i<18;i++){
        if (percepts[i]==1){
          creature = true;
        }else{
          creature = false;
        }
      }
      //whether other food is nearby
      for(int i=18;i<27;i++){
        if (percepts[i]==0){
         food = false;
        }else{
         food = true;
         if (percepts[i]==1){
         poison = true;
         }else{
           poison = false;
         }//if there is food nearby, whether the food is poisoned
        }
      }
      if (monster){
        for (int i=0;i<9;i++){
          if(percepts[i]==1){
            actions[i]=chromosome[i]*monsterWeight;
            //go opposite
          }
        }
      }
      if (creature){
        for (int i=9;i<18;i++){
          if(percepts[i]==1){
            actions[i]=chromosome[i]*creatureWeight;
            //go opposite
          }
        }
      }
      if (food){
        for (int i=18;i<27;i++){
          if(percepts[i]==1){
            actions[i]=chromosome[i]*foodWeight;
            //go opposite
          }
        }
      }
//      for(int i=0;i<10;i++){
//        if(action[i]>action[i+1]){
//        result = action[i];
//        }else{
//        result = action[i+1];
//        }
//        return result;
//      }//which one is better
      
      //      for(int i=0;i<numExpectedActions;i++) {
//         actions[i]=rand.nextFloat();
//      }
      
      return actions;
  }
  
}