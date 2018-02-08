package pbjt;

public class Agent implements Runnable {

  private Table table;

  public Agent(Table table) {
    this.table = table;
  }

  public void run() {
	  for (int i=0; i<2000000; ++i) {
		  table.supplyIngredients();
	  }
	  table.stop();
  }
}

