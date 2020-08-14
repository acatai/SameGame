import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;


public class PlayerRandom_deprecated
{
  private static Random RNG = new Random(36);

  public static void main(String[] args)
  {
    Scanner scanner = new Scanner(System.in);

    for (int y =0; y < 15; y++)
    {
      String row = scanner.nextLine();
      System.err.println(">"+row+"<");
    }

    int len = 1+RNG.nextInt(200);
    ArrayList<String> actions = new ArrayList<>();
    for (int i=0; i < 500; i++)
    {
      String msg = "";
      if (RNG.nextDouble() > 0.5)
      {
        if (i%4==0)
          msg = String.format("Action: %d/%d", i+1, len);
        if (i%4==1)
          msg = String.format("Action\\n%d/%d\\nwith\\nmsg", i+1, len);
        if (i%4==2)
          msg = String.format("1234567890123456789012345678901234567890", i+1, len);
        if (i%4==3)
          msg = String.format("abc\\nabcdefghijklmnopqrstuvwxyz", i+1, len);
      }
      actions.add(String.format("%d %d %s", RNG.nextInt(15), RNG.nextInt(15), msg));
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
