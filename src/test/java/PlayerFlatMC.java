import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.*;

class GameState
{
  public int[][] board = new int[15][15]; // [x][y]
  public int[] colorquantity = new int[5];
  public int score;
  public boolean terminal;

  public GameState()
  {

  }

  public GameState(Scanner scanner)
  {

    for (int revy=0; revy < 15 ; revy++)
    {
      String[] row = scanner.nextLine().trim().split(" ");

      for (int x=0; x < 15; x++)
      {
        int c = Integer.parseInt(row[x].trim());
        board[x][15-revy-1] = c;
        if (c != -1)
          colorquantity[c]++;
      }
    }

  }

  public GameState copy()
  {
    GameState s = new GameState();
    s.board = Arrays.stream(board).map(int[]::clone).toArray(int[][]::new);
    s.colorquantity = Arrays.stream(colorquantity).toArray();
    s.score = score;
    s.terminal = terminal;
    return s;
  }

  public void apply(int x, int y, int color, HashSet<Integer> region)
  {
    for (Integer xxyy: region)
    {
      board[xxyy/100][xxyy%100] = -1;
    }

    score += (region.size()-2)*(region.size()-2);
    colorquantity[color] -= region.size();

    normalizeVertically();
    normalizeHorizontaly();

    terminal = checkTerminal();
    if (board[0][0]==-1)
      score += 1000;
  }

  private HashSet<Integer> computeRegion(int x, int y)
  {
    int color = board[x][y];
    HashSet<Integer> region = new HashSet<>();
    HashSet<Integer> visited = new HashSet<>();
    Queue<Integer> open = new LinkedList<>();

    open.add(x*100+y);
    visited.add(x*100+y);

    while (!open.isEmpty())
    {
      int xxyy = open.remove();
      if (region.contains(xxyy))
        continue;

      region.add(xxyy);

      int left = ((xxyy/100)-1)*100 + xxyy%100;
      if (xxyy/100>0 && board[left/100][left%100]==color && !visited.contains(left))
        open.add(left);
      visited.add(left);

      int right = ((xxyy/100)+1)*100 + xxyy%100;
      if (xxyy/100<15-1 && board[right/100][right%100]==color && !visited.contains(right))
        open.add(right);
      visited.add(right);

      int down = xxyy-1;
      if (xxyy%100>0 && board[down/100][down%100]==color && !visited.contains(down))
        open.add(down);
      visited.add(down);

      int up = xxyy+1;
      if (xxyy%100<15-1 && board[up/100][up%100]==color && !visited.contains(up))
        open.add(up);
      visited.add(up);
    }

    return region;
  }

  private void normalizeVertically()
  {
    for (int x=0; x < 15; x++)
    {
      for (int y=0; y < 15; y++)
      {
        if (board[x][y] != -1)
          continue;

        int gapEnd = y+1;
        while (gapEnd < 15 && board[x][gapEnd]==-1)
          gapEnd++;

        if (gapEnd==15)
          break; // column checked
        board[x][y] = board[x][gapEnd];
        board[x][gapEnd] = -1;
      }
    }
  }

  private void normalizeHorizontaly()
  {
    // todo
    for (int x=0; x < 15; x++)
    {
      if (board[x][0] != -1)
        continue;
      //score += 10*(colorquantity[0]+colorquantity[1]+colorquantity[2]+colorquantity[3]+colorquantity[4]);
      int gapEnd = x+1;

      while (gapEnd < 15 && board[gapEnd][0]==-1)
        gapEnd++;

      if (gapEnd==15)
        return; // all columns checked

      for (int y=0; y < 15; y++)
      {
        board[x][y] = board[gapEnd][y];
        board[gapEnd][y] = -1;
      }
    }
  }

  private boolean checkTerminal()
  {
    if (board[0][0]==-1)
      return true;

    HashSet<Integer> closed = new HashSet<>();
    Queue<Integer> open = new LinkedList<>();
    open.add(0);

    while (!open.isEmpty())
    {
      int xxyy = open.remove();

      if (closed.contains(xxyy))
        continue;
      closed.add(xxyy);

      int color = board[xxyy/100][xxyy%100];
      if (color==-1)
        continue;

      int left = ((xxyy/100)-1)*100 + xxyy%100;
      if (xxyy/100>0)
      {
        if (board[left/100][left%100]==color)
          return false;
        if(!closed.contains(left))
          open.add(left);
      }

      int right = ((xxyy/100)+1)*100 + xxyy%100;
      if (xxyy/100<15-1)
      {
        if (board[right/100][right%100]==color)
          return false;
        if(!closed.contains(right))
          open.add(right);
      }

      int down = xxyy-1;
      if (xxyy%100>0)
      {
        if (board[down/100][down%100]==color)
          return false;
        if(!closed.contains(down))
          open.add(down);
      }

      int up = xxyy+1;
      if (xxyy%100<15-1)
      {
        if (board[up/100][up%100]==color)
          return false;
        if(!closed.contains(up))
          open.add(up);
      }
    }

    return true;
  }

  public ArrayList<HashSet<Integer>> legals()
  {
    ArrayList<HashSet<Integer>> sectors = new ArrayList<>();
    HashSet<Integer> closed = new HashSet<>();

    for (int x=0; x < 15; x++)
    {
      for (int y=0; y < 15; y++)
      {
        if (board[x][y]==-1 || closed.contains(x*100+y))
          continue;
        HashSet<Integer> region = computeRegion(x, y);
        if (region.size()<2)
          continue;
        sectors.add(region);
        for (int xxyy : region)
          closed.add(xxyy);
      }
    }
    return sectors;
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    for (int y=15-1; y >= 0 ; y--)
    {
      for (int x=0; x < 10; x++)
      {
        if (board[x][y]==-1)
          sb.append('.');
        else
          sb.append(board[x][y]);
      }
      sb.append('\n');
    }
    return sb.toString().trim();
  }
}

class FlatMC
{
  private Random RNG;
  private GameState root;

  ArrayList<HashSet<Integer>> bestregions = new ArrayList<>();
  ArrayList<Integer> bestscores = new ArrayList<>();
  int bestscore = -1;

  public FlatMC(GameState root, Random RNG)
  {
    this.root = root;
    this.RNG = RNG;
  }

  public String run(long timelimit_ms)
  {
    long timelimit = 1000000 * timelimit_ms;
    long time = System.nanoTime();
    int nsims = 0;

    while (System.nanoTime() - time < timelimit)
    {
      playout();
      nsims++;
    }

    ArrayList<String> output = new ArrayList<>();

    for (int i=0; i < bestregions.size(); i++)
    {
      int action = bestregions.get(i).iterator().next();
      //String msg = String.format("%d/%d\\nx=%d y=%d (|r|=%d)", bestscores.get(i), bestscore, action/100, action%100, bestregions.get(i).size());
      //String msg = String.format("%d/%d\\n%d sims", bestscores.get(i), bestscore, nsims);
      String msg = String.format("x=%d y=%d\\n|r|=%d", action/100, action%100, bestregions.get(i).size());
      output.add(String.format("%d %d %s", action/100, action%100 , msg));
    }

    return String.join(";", output);
  }

  private void playout()
  {
    GameState state = root.copy();

    ArrayList<HashSet<Integer>> actionregions = new ArrayList<>();
    ArrayList<Integer> scores = new ArrayList<>();

    while (!state.terminal)
    {
      ArrayList<HashSet<Integer>> legals = state.legals();
      HashSet<Integer> actionregion = legals.get(RNG.nextInt(legals.size()));

      int action = actionregion.iterator().next();
      state.apply(action/100, action%100, state.board[action/100][action%100], actionregion);

      actionregions.add(actionregion);
      scores.add(state.score);
    }

    if (scores.get(scores.size()-1) > bestscore)
    {
      bestscore = scores.get(scores.size()-1);
      bestregions = actionregions;
      bestscores = scores;
    }
  }
}

public class PlayerFlatMC
{
  public static void main(String[] args)
  {
    Random RNG = new Random();

    GameState root = new GameState(new Scanner(System.in));
    System.err.println(root);

    FlatMC flatMC = new FlatMC(root, RNG);
    String output =  flatMC.run(19600);

    // SINGLE TURN VERSION
    //System.out.println(output);

    // MULTIPLE TURNS VERSION
    System.err.println(output);
    System.out.println(output.split(";")[0]);
    for(int turn=1; turn < 666; turn++)
    {
      root = new GameState(new Scanner(System.in));
      System.err.println(output.split(";").length);
      System.err.println(root);
      System.out.println(output.split(";")[turn]);
    }
  }
}
