package com.codingame.game;

import java.util.List;

import com.codingame.game.engine.*;
import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.SoloGameManager;
import com.codingame.gameengine.module.tooltip.TooltipModule;
import com.codingame.gameengine.module.entities.*;
import com.google.inject.Inject;

public class Referee extends AbstractReferee
{
  @Inject private SoloGameManager<Player> manager;
  @Inject private GraphicEntityModule graphic;
  @Inject private TooltipModule tooltip;

  private GameState gameState;
  private Viewer viewer;
  private Player player;

  //private String[] playerActions; // ONE TURN VERSION
  private String playerAction;

  @Override
  public void init()
  {
    gameState = new GameState(manager);
    viewer = new Viewer(graphic, manager, tooltip);
    player = manager.getPlayer();

    manager.setFirstTurnMaxTime(Constants.TIMELIMIT_INIT);
    manager.setTurnMaxTime(Constants.TIMELIMIT_TURN);
    manager.setMaxTurns(Constants.ACTIONLIMIT);
    manager.setFrameDuration(Constants.FRAMEDURATION);

    viewer.init(gameState);
  }

  @Override
  public void gameTurn(int turn)
  {
    //if (turn==1) // ONE TURN VERSION
    //{
    //  //FirstTurn();
    //  if (!executePlayer())
    //    return;
    //}


    //if (gameState.terminal || turn-1 == playerActions.length || turn == Constants.ACTIONLIMIT) // ONE TURN VERSION
    if (gameState.terminal || turn == Constants.ACTIONLIMIT)
    {
      viewer.endgame();
      manager.winGame("Endgame. Obtained a score of " + gameState.score+".");
      return;
    }

    if (!executePlayer()) // MULTIPLE TURNS VERSION
      return;

    //System.out.println(gameState.legals().stream().map(Object::toString).collect(Collectors.joining("\n")));

    Action action;
    try
    {
      //action = gameState.validate(playerActions[turn - 1]); // ONE TURN VERSION
      action = gameState.validate(playerAction);
    }
    catch (ActionErrorException e)
    {
      manager.loseGame("[ERROR] "+e.getMessage());
      return;
    }

    manager.addToGameSummary(String.format("Performed action x=%d, y=%d (color %d, region size %d).",
            action.x, action.y, action.color, action.region.size()));

    if (action.invalid)  // MULTIPLE TURNS VERSION
    {
      manager.loseGame("[ERROR] " + action.warning);
      return;
    }

    if (!action.invalid)
      gameState.apply(action);

    if (action.invalid)
      manager.addToGameSummary("[WARNING] " + action.warning);

    viewer.update(gameState, action);
  }

  @Override
  public void onEnd()
  {
    manager.putMetadata("Score", ""+gameState.score);
  }

  private boolean executePlayer()
  {
    try
    {
      //for (int y=0; y < Constants.ROWS; y++)
      //  manager.getPlayer().sendInputLine(manager.getTestCaseInput().get(y));
      String[] input = gameState.toPlayerInputString();
      for (int y=0; y < Constants.ROWS; y++)
        manager.getPlayer().sendInputLine(input[y]);
      manager.getPlayer().execute();

      List<String> outputs = manager.getPlayer().getOutputs();
      String output = outputs.get(0);
      if (output != null)
      {
        //playerActions = output.trim().replaceAll(";$", "").split(";");
        playerAction = output.trim();
        if (playerAction.length()==0)
        {
          manager.loseGame("[ERROR] Empty action provided.");
          return false;
        }
      }
      else
      {
        manager.loseGame("[ERROR] No action provided.");
        return false;
      }
    }
    catch (TimeoutException e)
    {
      manager.loseGame("[ERROR] Timeout!");
      return false;
    }
    catch (Exception e)
    {
      manager.loseGame("[ERROR]");
      return false;
    }

    return true;
  }
}
