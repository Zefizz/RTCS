package pbjt;

/*
 * Main is responsible for creating the three chef
 * threads and agent thread, each with a reference to the same table.
 * main then starts all these threads
 */
public class Pbjt {

  public static void main(String[] args) {

    Table table = new Table();

    Thread agent = new Thread(new Agent(table));

    Thread c1 = new Thread(new Chef(table, Ingredient.BREAD));
    Thread c2 = new Thread(new Chef(table, Ingredient.PENUT_BUTTER));
    Thread c3 = new Thread(new Chef(table, Ingredient.JELLY));

    agent.start();
    c1.start();
    c2.start();
    c3.start();
  }

}
