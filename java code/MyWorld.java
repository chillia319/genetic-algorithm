import cosc343.assig2.World;
import cosc343.assig2.Creature;
import java.util.*;
import java.io.*;

//import org.jfree.data.xy.XYSeries;
//import org.jfree.data.xy.XYSeriesCollection;
//import org.jfree.ui.RefineryUtilities;

/**
* The MyWorld extends the cosc343 assignment 2 World.  Here you can set 
* some variables that control the simulations and override functions that
* generate populations of creatures that the World requires for its
* simulations.
*
* @author  Meiqi Sun
* @version 1.0
* @since   2017-04-05 
*/
public class MyWorld extends World {
 
  /* Here you can specify the number of turns in each simulation
   * and the number of generations that the genetic algorithm will 
   * execute.
  */
  private final int _numTurns = 50;
  private final int _numGenerations = 500;

  
  /* Constructor.  
   
     Input: griSize - the size of the world
            windowWidth - the width (in pixels) of the visualisation window
            windowHeight - the height (in pixels) of the visualisation window
            repeatableMode - if set to true, every simulation in each
                             generation will start from the same state
            perceptFormat - format of the percepts to use: choice of 1, 2, or 3
  */
  public MyWorld(int gridSize, int windowWidth, int windowHeight, boolean repeatableMode, int perceptFormat) {   
      // Initialise the parent class - don't remove this
      super(gridSize, windowWidth,  windowHeight, repeatableMode, perceptFormat);

      // Set the number of turns and generations
      this.setNumTurns(_numTurns);
      this.setNumGenerations(_numGenerations);
      
      
  }
 
  /* The main function for the MyWorld application

  */
  public static void main(String[] args) {
     // Here you can specify the grid size, window size and whether torun
     // in repeatable mode or not
     int gridSize = 24;
     int windowWidth =  1600;
     int windowHeight = 900;
     boolean repeatableMode = false;
     
      /* Here you can specify percept format to use - there are three to
         chose from: 1, 2, 3.  Refer to the Assignment2 instructions for
         explanation of the three percept formats.
      */
     int perceptFormat = 2;     
     
     // Instantiate MyWorld object.  The rest of the application is driven
     // from the window that will be displayed.
     MyWorld sim = new MyWorld(gridSize, windowWidth, windowHeight, repeatableMode, perceptFormat);
  }
  

  /* The MyWorld class must override this function, which is
     used to fetch a population of creatures at the beginning of the
     first simulation.  This is the place where you need to  generate
     a set of creatures with random behaviours.
  
     Input: numCreatures - this variable will tell you how many creatures
                           the world is expecting
                            
     Returns: An array of MyCreature objects - the World will expect numCreatures
              elements in that array     
  */  
  @Override
  public MyCreature[] firstGeneration(int numCreatures) {

    int numPercepts = this.expectedNumberofPercepts();
    int numActions = this.expectedNumberofActions();
      
    // This is just an example code.  You may replace this code with
    // your own that initialises an array of size numCreatures and creates
    // a population of your creatures
    MyCreature[] population = new MyCreature[numCreatures];
    for(int i=0;i<numCreatures;i++) {
        population[i] = new MyCreature(numPercepts, numActions);     
    }
    return population;
  }
  
  /* The MyWorld class must override this function, which is
     used to fetch the next generation of the creatures.  This World will
     proivde you with the old_generation of creatures, from which you can
     extract information relating to how they did in the previous simulation...
     and use them as parents for the new generation.
  
     Input: old_population_btc - the generation of old creatures before type casting. 
                              The World doesn't know about MyCreature type, only
                              its parent type Creature, so you will have to
                              typecast to MyCreatures.  These creatures 
                              have been simulated over and their state
                              can be queried to compute their fitness
            numCreatures - the number of elements in the old_population_btc
                           array
                        
                            
  Returns: An array of MyCreature objects - the World will expect numCreatures
           elements in that array.  This is the new population that will be
           use for the next simulation.  
  */  
  @Override
  public MyCreature[] nextGeneration(Creature[] old_population_btc, int numCreatures) {
    // Typcast old_population of Creatures to array of MyCreatures
     MyCreature[] old_population = (MyCreature[]) old_population_btc;
     
     
     // Create a new array for the new population
     MyCreature[] new_population = new MyCreature[numCreatures];
     
     float[] fitness = new float[numCreatures];
     // Here is how you can get information about old creatures and how
     // well they did in the simulation
     float avgLifeTime=0f;
     float avgFitness;
     int nSurvivors = 0;
     for(MyCreature creature : old_population) {
        // The energy of the creature.  This is zero if creature starved to
        // death, non-negative oterhwise.  If this number is zero, but the 
        // creature is dead, then this number gives the enrgy of the creature
        // at the time of death.
        int energy = creature.getEnergy();

        // This querry can tell you if the creature died during simulation
        // or not.  
        boolean dead = creature.isDead();
        
        if(dead) {
           // If the creature died during simulation, you can determine
           // its time of death (in turns)
           int timeOfDeath = creature.timeOfDeath();
           avgLifeTime += (float) timeOfDeath;
        } else {
           nSurvivors += 1;
           avgLifeTime += (float) _numTurns;
        }
     }
     
     //Calculating fitness
     for(int i = 0; i < numCreatures; i++) {
       MyCreature c = old_population[i];
       if(c.isDead()) {
         fitness[i] = (c.timeOfDeath()*5) + (float)c.getEnergy();
       } else {
         fitness[i] = ((float) _numTurns*5) + (float)c.getEnergy();
        }
     }//for-loop individual fitness ends
     
     float sum = 0f;
     for(int i=0;i<numCreatures;i++) {
       sum += fitness[i];
     }
     avgFitness = sum/(float) numCreatures;
     
     //tournament selection-it only happens when some creatures die
       
         Random random = new Random();
         int sampleNumber = numCreatures/4;
         MyCreature father = new MyCreature();
         MyCreature mother = new MyCreature();
         
         
         //randomly choose 1/4 population from previous generation
         //choosing mother
         float bestFit=0f;
         for (int s = 0; s < sampleNumber; s++) {
           int theOne=rand.nextInt(numCreatures);
           if(fitness[s]>bestFit){
             bestFit=fitness[s];
             mother=old_population[s];
           }
         }//for-loop choosing father ends
         //make an array named survivors and put all the survivors into it and randomly pick one survivor from the array as father
         if(nSurvivors>0){
         MyCreature survivor;
         MyCreature[] survivors = new MyCreature[nSurvivors];
         for(int n = 0; n < numCreatures; n++) {
           survivor = old_population[n];
           if(!survivor.isDead()) {
             for (int s = 0; s < nSurvivors; s++) {
               survivors[s]=survivor;
             }
           }
         }
         father = survivors[rand.nextInt(nSurvivors)];
         }else{
         father = mother;
         }
         //for-loop choosing father ends
         //if there is no survivor then double father's chromosomes as parents
         
         //System.out.println(" after loop chromosome: " + Arrays.toString(mother.chromosome) );
         //System.out.println(" after loop chromosome: " + Arrays.toString(father.chromosome) );
         
         //creating new generation with one crosspoint 
         for (int i=0;i<numCreatures;i++){ 
         //for (int i=0;i<numCreatures-nSurvivors;i++){ 
         //int crossPoint = rand.nextInt()*10;
         float mutationRate = 0.1f;
         //The possibility of mutation is 0.1
         float mutation = rand.nextFloat();
         //crossover and mutation
         MyCreature child =new MyCreature();
         for (int m = 0; m < 11; m++) {
           int crossPoint = rand.nextInt()*11;
           //System.out.println("crossPoint" + crossPoint);
           //child = new MyCreature();
           if (m<crossPoint){
             if(mutation<mutationRate){A
               child.chromosome[m]=father.chromosome[m];
               //System.out.println("child chromosome: " + Arrays.toString(child.chromosome));
             }else{
               if(m<8){
                 child.chromosome[m]=rand.nextInt()*2;
                 //System.out.println("child chromosome: " + Arrays.toString(child.chromosome));
               }else{
                 child.chromosome[m]=rand.nextInt();
                 //System.out.println("child chromosome: " +Arrays.toString(child.chromosome));
               }
             }
           }else{
             if(mutation<mutationRate){
               child.chromosome[m]=mother.chromosome[m];
               //System.out.println("child chromosome: " + Arrays.toString(child.chromosome));
             }else{
               if(m<9){
                 child.chromosome[m]=rand.nextInt()*2;
                 //System.out.println("child chromosome: " + Arrays.toString(child.chromosome));
               }else{
                 child.chromosome[m]=rand.nextInt();
                 //System.out.println("child chromosome: " + Arrays.toString(child.chromosome));
               }
             }
           }
         } //System.out.println("child after crossover: " + Arrays.toString(child.chromosome) );
           new_population[i] = child;
           //}
         }//end tournament selection
    
     
  
     // Right now the information is used to print some stats...but you should
     // use this information to access creatures fitness.  It's up to you how
     // you define your fitness function.  You should add a print out or
     // some visual display of average fitness over generations.
     avgLifeTime /= (float) numCreatures;
     System.out.println("Simulation stats:");
     System.out.println("  Survivors    : " + nSurvivors + " out of " + numCreatures);
     System.out.println("  Avg life time: " + avgLifeTime + " turns");
     System.out.println("  Avg fitness score: " + avgFitness);
     //for (int i =0;i<11;i++){
       //System.out.println("  chromosome: " + Arrays.toString(old_population[i].chromosome) + " turns");
       
     //System.out.println(" father chromosome: " + Arrays.toString(mother.chromosome) );
     //System.out.println(" father chromosome: " + Double.valueOf(mother.chromosome[i]));
     //System.out.println(" mother chromosome: " + Double.valueOf(father.chromosome[i]));
     //}
     

     
     // Having some way of measuring the fitness, you should implement a proper
     // parent selection method here and create a set of new creatures.  You need
     // to create numCreatures of the new creatures.  If you'd like to have
     // some elitism, you can use old creatures in the next generation.  This
     // example code uses all the creatures from the old generation in the
     // new generation.
     
     // Return new population of creatures.
     return new_population;
  }
}