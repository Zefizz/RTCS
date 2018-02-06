
/*
 * Main is responsible for creating the three cheff
 * threads and agent thread, each with a reference to the same table.
 * main then starts all these threads
 */
public class Main {

  public static void main(String[] args) {

    Table table = new Table();

    (new Thread(new Agent(table))).run();

    (new Thread(new Chef(table, Ingredient.BREAD))).run();
    (new Thread(new Chef(table, Ingredient.PENUT_BUTTER))).run();
    (new Thread(new Chef(table, Ingredient.JELLY))).run();

  }

}
