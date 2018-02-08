package pbjt;

public class Chef implements Runnable {
  
  private Table table;
  private Ingredient ingredient;

  public Chef(Table table, Ingredient ingredient) {
    this.table = table;
    this.ingredient = ingredient;
  }

  public void run() {
	  while(table.running()) {
		  table.makeSandwich(ingredient);
	  }
  }

}
