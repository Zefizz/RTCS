package pbjt;

public class Table {

  private Ingredient ingredient1 = Ingredient.NONE;
  private Ingredient ingredient2 = Ingredient.NONE;
  private boolean running = true;

  /**
   * the agent places two random ingredients on the table
   * and notifies the other chefs
   */
  public synchronized void supplyIngredients() {
	  //System.out.println("supplying");
	  
	  //wait for the table to be cleared
	  try {
		  while(ingredient1 != Ingredient.NONE && ingredient2 != Ingredient.NONE) {
			  //System.out.println("agent waits");
			  wait();
		  }
	  } catch (InterruptedException e) {
		  System.exit(1);
	  }
	  
	  //TODO randomize
	  ingredient1 = Ingredient.PENUT_BUTTER;
	  ingredient2 = Ingredient.JELLY;
	  
	  //wake the sleeping chefs
	  notifyAll();
  }

  /**
   * chef makes the sandwich with the ingredient he has or
   * waits if the required ingredients are not present
   * @param ingredient3
   */
  public synchronized void makeSandwich(Ingredient ingredient3) {

	  //System.out.println("making");
	  
	  //wait if there are not the required ingredients
	  try {
		  while(ingredient1 == Ingredient.NONE || ingredient2 == Ingredient.NONE ||
				ingredient1 == ingredient3 || ingredient2 == ingredient3) {
			  //System.out.println("chef waits");
			  wait();
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

  private synchronized void eat() {
	  System.out.println("nom nom, chef " + Thread.currentThread().getId() + " ate the sandwich.");
  }

}
