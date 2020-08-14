import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;


public class PlayerRandom
{
  private static Random RNG = new Random(36);

  public static void main(String[] args)
  {
    Scanner in = new Scanner(System.in);

    // game loop
    while (true)
    {
      int[][] board = new int[15][15];

      for (int y = 0; y < 15; y++)
      {
        for (int x = 0; x < 15; x++)
        {
          int color = in.nextInt(); // Color of the tile
          board[x][y] = color;
        }
      }

      int tries = 0;
      while (true)
      {
        tries++;
        int x = RNG.nextInt(15);
        int y = RNG.nextInt(15);

        if (board[x][y] == -1) continue;
        boolean ok = false;
        if (x > 0 && board[x][y] == board[x - 1][y]) ok = true;
        if (x < 14 && board[x][y] == board[x + 1][y]) ok = true;
        if (y > 0 && board[x][y] == board[x][y - 1]) ok = true;
        if (y < 14 && board[x][y] == board[x][y + 1]) ok = true;
        if (!ok) continue;
        // Write an answer using System.out.println()
        // To debug: System.err.println("Debug messages...");

        System.out.println(String.format("%d %d %d tries", x, 14-y, tries));
        break;
      }
    }
  }
}
