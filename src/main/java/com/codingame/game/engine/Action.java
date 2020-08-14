package com.codingame.game.engine;


import java.util.HashSet;

public class Action
{
  public int x;
  public int y;
  public String message = "";
  public int color = -1;
  public HashSet<Integer> region = new HashSet<>();
  public boolean invalid = false;
  public String warning;
}
