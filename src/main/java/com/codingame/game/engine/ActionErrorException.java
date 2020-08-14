package com.codingame.game.engine;

public class ActionErrorException extends Exception
{
  private static final long serialVersionUID = -8185589153224401565L;

  public ActionErrorException(String message)
  {
    super(message);
  }
}
