package com.codingame.game.engine;


import com.codingame.game.Player;
import com.codingame.gameengine.core.SoloGameManager;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Text;

import java.util.*;
import java.util.regex.Pattern;

public class GameState
{
  private SoloGameManager<Player> manager;
  private GraphicEntityModule graphic;

  public int[][] board = new int[Constants.COLUMNS][Constants.ROWS]; // [x][y]
  public int[] colorquantity = new int[5];
  public int score;
  public boolean terminal;

  public GameState(SoloGameManager<Player> manager)
  {
    this.manager = manager;

    for (int revy=0; revy < Constants.ROWS ; revy++)
    {
      String[] row = manager.getTestCaseInput().get(revy).trim().split(" ");
      for (int x=0; x < Constants.COLUMNS; x++)
      {
        int c = Integer.parseInt(row[x].trim());
        board[x][Constants.ROWS-revy-1] = c;
        if (c != -1)
          colorquantity[c]++;
      }
    }
    //System.out.println(this.toString());
  }

  public GameState copy()
  {
    GameState s = new GameState(manager);
    s.graphic = graphic;
    s.board = Arrays.stream(board).map(int[]::clone).toArray(int[][]::new);
    s.colorquantity = Arrays.stream(colorquantity).toArray();
    s.score = score;
    s.terminal = terminal;
    return s;
  }

  public Action validate(String actionstring) throws ActionErrorException
  {
    String[] playeraction = actionstring.split("\\s+", 3);
    Action a = new Action();

    try
    {
      a.x = Integer.parseInt(playeraction[0]);
      a.y = Integer.parseInt(playeraction[1]);
    } catch (Exception e)
    {
      throw new ActionErrorException(String.format("Action not properly formatted. Should be: 'x y [message];'"));
    }

    if (!(a.x>=0 && a.x<Constants.COLUMNS&&a.y>=0&&a.y<Constants.ROWS))
    {
      throw new ActionErrorException(String.format("Selected target out of bounds: x=%d, y=%d", a.x, a.y));
    }

    if (playeraction.length>2)
    {
      a.message = parseMessage(playeraction[2]);
    }

    a.color = board[a.x][a.y];
    if (a.color==-1)
    {
      a.invalid = true;
      a.warning = "Selected empty tile.";
      return a;
    }

    a.region = computeRegion(a.x, a.y);
    if (a.region.size()<2)
    {
      a.invalid = true;
      a.warning = "Selected region should have size greater than 1.";
    }

    return a;
  }

  private String parseMessage(String playermsg)
  {
    String[] msg = playermsg.split(Pattern.quote("\\n"));
    String x = msg[0].substring(0, Math.min(msg[0].length(), Constants.MAX_MESSAGE_LENGTH));
    if (msg[0].length() > Constants.MAX_MESSAGE_LENGTH)
    {
      if (msg[0].length() > 2*Constants.MAX_MESSAGE_LENGTH)
        x += "\n"+msg[0].substring(Constants.MAX_MESSAGE_LENGTH, 2*Constants.MAX_MESSAGE_LENGTH-1)+"...";
      else
        x += "\n"+msg[0].substring(Constants.MAX_MESSAGE_LENGTH, Math.min(msg[0].length(), 2*Constants.MAX_MESSAGE_LENGTH));

      return x;
    }

    if (msg.length>1)
    {
      if (msg[1].length() > Constants.MAX_MESSAGE_LENGTH)
        x += "\n"+msg[1].substring(0, Constants.MAX_MESSAGE_LENGTH-1)+"...";
      else
        x += "\n"+msg[1].substring(0, Math.min(msg[1].length(), Constants.MAX_MESSAGE_LENGTH));
    }

    return x;
  }

  public void apply(Action action)
  {
    for (Integer xxyy: action.region)
    {
      board[xxyy/100][xxyy%100] = -1;
    }

    score += (action.region.size()-2)*(action.region.size()-2);
    colorquantity[action.color] -= action.region.size();

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
      if (xxyy/100<Constants.COLUMNS-1 && board[right/100][right%100]==color && !visited.contains(right))
        open.add(right);
      visited.add(right);

      int down = xxyy-1;
      if (xxyy%100>0 && board[down/100][down%100]==color && !visited.contains(down))
        open.add(down);
      visited.add(down);

      int up = xxyy+1;
      if (xxyy%100<Constants.ROWS-1 && board[up/100][up%100]==color && !visited.contains(up))
        open.add(up);
      visited.add(up);
    }

    return region;
  }

  private void normalizeVertically()
  {
    for (int x=0; x < Constants.COLUMNS; x++)
    {
      for (int y=0; y < Constants.ROWS; y++)
      {
        if (board[x][y] != -1)
          continue;

        int gapEnd = y+1;
        while (gapEnd < Constants.ROWS && board[x][gapEnd]==-1)
          gapEnd++;

        if (gapEnd==Constants.ROWS)
          break; // column checked
        board[x][y] = board[x][gapEnd];
        board[x][gapEnd] = -1;
      }
    }
  }

  private void normalizeHorizontaly()
  {
    // todo
    for (int x=0; x < Constants.COLUMNS; x++)
    {
      if (board[x][0] != -1)
        continue;

      int gapEnd = x+1;

      while (gapEnd < Constants.COLUMNS && board[gapEnd][0]==-1)
        gapEnd++;

      if (gapEnd==Constants.COLUMNS)
        return; // all columns checked

      for (int y=0; y < Constants.ROWS; y++)
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
      if (xxyy/100<Constants.COLUMNS-1)
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
      if (xxyy%100<Constants.ROWS-1)
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

    for (int x=0; x < Constants.COLUMNS; x++)
    {
      for (int y=0; y < Constants.ROWS; y++)
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
    for (int y=Constants.ROWS-1; y >= 0 ; y--)
    {
      for (int x=0; x < Constants.COLUMNS; x++)
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

  public String[] toPlayerInputString()
  {
    String[] playerinput = new String[Constants.ROWS];
    for (int y=Constants.ROWS-1; y >= 0 ; y--)
    {
      String row = "";
      for (int x=0; x < Constants.COLUMNS; x++)
      {
        row += board[x][y] + " ";
      }
      playerinput[Constants.ROWS-y-1] = row.trim();
    }
    return playerinput;
  }
}
