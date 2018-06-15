import cosc343.assig2.World;
import cosc343.assig2.Creature;
import java.util.*;

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
  private final int _numTurns = 100;
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
       float score;
       if(c.isDead()) {
         score = (float)c.getEnergy()+1;
       } else {
         score = 50 + 1 + (float)c.getEnergy()+1;
       }
       if(c.isDead()) {
         fitness[i] = (float)c.timeOfDeath() + score;
       } else {
         fitness[i] = (float)_numTurns + score;
       }
     }
     
     
     float sum = 0f;
     for(int i=0;i<numCreatures;i++) {
       sum += fitness[i];
     }
     avgFitness = sum/(float) numCreatures;
     
     //tournament selection
     Random random = new Random();
     int sampleNumber = numCreatures/5;
     MyCreature parent1[]= new MyCreature[sampleNumber];  
     MyCreature parent2[]= new MyCreature[sampleNumber];
     //randomly choose 1/5 population from previous population
     for (int i = 0; i < sampleNumber; i++) {
       int theOne=random.nextInt(numCreatures);
       parent1[i]=old_population[random.nextInt(numCreatures)];
     }
     for (int i = 0; i < sampleNumber; i++) {
       parent2[i]=old_population[random.nextInt(numCreatures)];
     }
     
     MyCreature father = new MyCreature();
     MyCreature mother = new MyCreature();
     float bestFit=0;
     //selecting father
     for (int i = 0; i < (parent1.length-1); i++) {
       if(fitness[i]>bestFit){
         father=parent1[i];
         bestFit=fitness[i];
       } 
     }
     //selecting mother
     for (int i = 0; i < (parent2.length-1); i++) {
       if(fitness[i]>bestFit){
         mother=parent2[i];
         bestFit=fitness[i];
       } 
     }
     
     //creating new generation with one crosspoint 
     MyCreature child = new MyCreature();
     int crossPoint = random.nextInt()*5;
     for (int i=0;i<5;++i)
     {
       if (i<crossPoint){
         child.chromosome[i]=father[i];
       }else{
         child.chromosome[i]=mother[i];
       }
     }
     return child;
  
     
  
     // Right now the information is used to print some stats...but you should
     // use this information to access creatures fitness.  It's up to you how
     // you define your fitness function.  You should add a print out or
     // some visual display of average fitness over generations.
     avgLifeTime /= (float) numCreatures;
     System.out.println("Simulation stats:");
     System.out.println("  Survivors    : " + nSurvivors + " out of " + numCreatures);
     System.out.println("  Avg life time: " + avgLifeTime + " turns");
     System.out.println("  Avg fitness score: " + avgFitness);

     
     // Having some way of measuring the fitness, you should implement a proper
     // parent selection method here and create a set of new creatures.  You need
     // to create numCreatures of the new creatures.  If you'd like to have
     // some elitism, you can use old creatures in the next generation.  This
     // example code uses all the creatures from the old generation in the
     // new generation.
     for(int i=0;i<numCreatures; i++) {
        new_population[i] = old_population[i];
     }
     new_population.add(child);
     
     // Return new population of cratures.
     return new_population;
  }
  
}