package pbjt;

import java.util.Random;

public class Table {

  private Ingredient ingredient1 = Ingredient.NONE;
  private Ingredient ingredient2 = Ingredient.NONE;
  private static Random random   = new Random();
  private boolean running        = true;

  /**
   * the agent places two random ingredients on the table
   * and notifies the other chefs
   */
  public synchronized void supplyIngredients() {
	  //wait for the table to be cleared
	  try {
		  while (ingredient1 != Ingredient.NONE && ingredient2 != Ingredient.NONE) {
		  	  if (!running) {
			 	   return;
		  	  } else {
				    wait();
		 	  }
		  }
	  } catch (InterruptedException e) {
		  System.exit(1);
	  }
	  
	  ingredient1 = randomIngredient();
	  ingredient2 = randomIngredient();
	  
	  //wake the sleeping chefs
	  notifyAll();
  }

  /**
   * chef makes the sandwich with the ingredient he has or
   * waits if the required ingredients are not present
   * @param ingredient3
   */
  public synchronized void makeSandwich(Ingredient ingredient3) {
	  //wait if there are not the required ingredients
	  try {
		  while (ingredient1 == Ingredient.NONE || ingredient2 == Ingredient.NONE ||
				ingredient1 == ingredient3 || ingredient2 == ingredient3) {
		  	  if (!running) {
			 	   return;
		  	  } else {
				    wait();
		 	  }
		  }
	  } catch (InterruptedException e) {
		  System.exit(1);
	  }
	  
	  //make the sandwich and clear the table
	  ingredient1 = ingredient2 = Ingredient.NONE;
	  
	  //eat the sandwich and notify others
	  eat();
	  notifyAll();
  }
  
  /**
   * set running to false to stop chef threads
   */
  public synchronized void stop() {
	  running = false;
	  notifyAll();
  }
  
  /**
   * check if the table is available to make another sandwich
   * @return running
   */
  public synchronized boolean running() {
	  return running;
  }

  /**
   * @return a random ingredient
   */
  private Ingredient randomIngredient() {
  	  int next = random.nextInt();
	  switch (next%3) {
		  case 0: return Ingredient.BREAD;
		  case 1: return Ingredient.PENUT_BUTTER;
		  case 2: default: return Ingredient.JELLY;
	  }
  }

  private void eat() {
	  System.out.println("nom nom, chef " + Thread.currentThread().getId() + " ate the sandwich.");
  }

}
