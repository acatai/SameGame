import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;


public class PlayerError
{
  private static Random RNG = new Random();

  public static void main(String[] args) throws InterruptedException
  {
    Scanner scanner = new Scanner(System.in);

    for (int y =0; y < 15; y++)
    {
      String row = scanner.nextLine();
      System.err.println(">"+row+"<");
    }

    int len = 1+RNG.nextInt(200);
    ArrayList<String> actions = new ArrayList<>();
    for (int i=0; i < 10; i++)
    {
      if (RNG.nextDouble() < 0.05) // Empty Action
      {
        actions.add(" ");
        continue;
      }
      if (RNG.nextDouble() < 0.05) // Bad formatted action
      {
        actions.add(" 2 bad 2 B good");
        continue;
      }
      if (RNG.nextDouble() < 0.02) // IndexOutOfBoundsException
      {
        ArrayList<Integer> x = new ArrayList<>();
        x.get(12);
        continue;
      }
      if (RNG.nextDouble() < 0.02) // Timeout
      {
        Thread.sleep(999000);
        continue;
      }

      actions.add(String.format("%d %d", RNG.nextInt(17), RNG.nextInt(17)));
    }

    // SINGLE TURN VERSION
    //System.out.println(String.join(";", actions));

    // MULTIPLE TURNS VERSION
    System.out.println(actions.get(0));
    for(int turn=1; turn < 666; turn++)
    {
      for (int y =0; y < 15; y++)
      {
        String row = scanner.nextLine();
        System.err.println(">"+row+"<");
      }
      System.out.println(actions.get(turn));
    }
  }
}
