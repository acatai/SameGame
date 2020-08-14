package com.codingame.game.engine;

public class Constants
{
  // CG Optimization puzzle constraints:
  // - Max 30s runtime for each of the validators
  // - Max 50 validators

  public static int COLUMNS = 15;
  public static int ROWS = 15;

  public static int ACTIONLIMIT = 115;
  public static int TIMELIMIT_INIT = 20*1000;
  public static int TIMELIMIT_TURN = 50;

  public static int MAX_MESSAGE_LENGTH = 15;

  public static int FRAMEDURATION=500;
}
