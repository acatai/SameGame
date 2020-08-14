package com.codingame.game.engine;


import com.codingame.game.Player;
import com.codingame.gameengine.core.SoloGameManager;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Text;
import com.codingame.gameengine.module.entities.TextBasedEntity;
import com.codingame.gameengine.module.tooltip.TooltipModule;
import com.codingame.gameengine.module.entities.Sprite;

public class Viewer
{
  private int PLAYGROUND_OFFSET_X = 790;
  private int PLAYGROUND_OFFSET_Y = 60;
  private int BOARD_BG_MARGIN = 15;

  private int INFO_OFFSET_X = 140;
  private int INFO_CENTER_X = 395;
  private int COLORS_OFFSET_Y = 545;

  private double TILE_SCALE = 2.0;
  private int TILE_SIZE = (int)(32*TILE_SCALE);

  private GraphicEntityModule graphic;
  private SoloGameManager<Player> manager;
  private TooltipModule tooltip;

  private Sprite[][] board = new Sprite[Constants.COLUMNS][Constants.ROWS]; // [x][y]
  private Sprite target;
  private Text[] colorquantity = new Text[5];
  private Text message;
  private Text score;


  public Viewer(GraphicEntityModule graphic, SoloGameManager<Player> manager, TooltipModule tooltip)
  {
    this.graphic = graphic;
    this.manager = manager;
    this.tooltip = tooltip;
  }


  public void init(GameState state)
  {
    graphic.createSprite().setImage("background.png").setZIndex(-2);
    graphic.createSprite().setImage("boardbackground.png").setZIndex(-1)
            .setX(PLAYGROUND_OFFSET_X-BOARD_BG_MARGIN)
            .setY(PLAYGROUND_OFFSET_Y-BOARD_BG_MARGIN);
    graphic.createSprite().setImage("infobackground.png").setZIndex(-1)
            .setX(INFO_OFFSET_X)
            .setY(PLAYGROUND_OFFSET_Y-BOARD_BG_MARGIN);

    target = graphic.createSprite().setImage("target.png").setZIndex(1)
            .setScale(TILE_SCALE)
            .setAlpha(0);

    for (int y=Constants.ROWS-1; y >= 0 ; y--)
    {
      for (int x=0; x < Constants.COLUMNS; x++)
      {
        board[x][y] = graphic.createSprite().setImage(state.board[x][y]+".png")
                .setScale(TILE_SCALE)
                .setX(PLAYGROUND_OFFSET_X +x*TILE_SIZE)
                .setY(PLAYGROUND_OFFSET_Y +(Constants.ROWS-1-y)*TILE_SIZE);
        if (state.board[x][y]==-1)
          board[x][y].setAlpha(0);
        tooltip.setTooltipText(board[x][y], String.format("x = %d\ny = %d\ncolor = %d", x, y, state.board[x][y]));
      }
    }
    graphic.createSprite()
            .setImage(manager.getPlayer().getAvatarToken())
            .setX(INFO_CENTER_X)
            .setY(PLAYGROUND_OFFSET_Y+100)
            .setAnchor(0.5)
            .setBaseHeight(200)
            .setBaseWidth(200);
    graphic.createText(manager.getPlayer().getNicknameToken())
            .setX(INFO_CENTER_X)
            .setY(300)
            .setFontSize(50)
            .setAnchor(0.5)
            .setFillColor(0xffffff);
    graphic.createText("SCORE:")
            .setX(INFO_CENTER_X)
            .setY(405)
            .setFontSize(40)
            .setAnchor(0.5)
            .setFillColor(0xffffff);
    score = graphic.createText("0")
            .setX(INFO_CENTER_X)
            .setY(475)
            .setFontSize(80)
            .setAnchor(0.5)
            .setFillColor(0xffffff);

    for (int c=0; c < 5; c++)
    {
      graphic.createSprite().setImage(c+".png")
              .setScale(TILE_SCALE)
              .setX(310)
              .setY(COLORS_OFFSET_Y + c*TILE_SIZE);

      colorquantity[c] = graphic.createText(state.colorquantity[c]+"")
              .setX(470)
              .setY(COLORS_OFFSET_Y + 6 + c*TILE_SIZE)
              .setFontSize(50)
              .setAnchorX(1.0)
              .setFillColor(0xffffff);

      //tooltip.setTooltipText(colorsprite, "Remaining tiles of color " + c);
      //tooltip.setTooltipText(colorquantity[c], "color =" + c);
    }

    message = graphic.createText("")
            .setX(INFO_CENTER_X)
            .setY(965)
            .setFontSize(50)
            .setAnchor(0.5)
            .setTextAlign(TextBasedEntity.TextAlign.CENTER)
            .setFillColor(0xffffff);
  }

  public void update(GameState state, Action action)
  {
    target.setAlpha(1)
            .setX(PLAYGROUND_OFFSET_X +action.x*TILE_SIZE-6)
            .setY(PLAYGROUND_OFFSET_Y +(Constants.ROWS-1-action.y)*TILE_SIZE-6);

    message.setText(action.message);

    graphic.commitWorldState(0);

    score.setText(""+state.score);

    for (int c=0; c < 5; c++)
    {
      colorquantity[c].setText(""+state.colorquantity[c]);
    }

    if (action.invalid)
    {
      graphic.commitWorldState(1);
      return;
    }

    for (Integer xxyy: action.region)
    {
      board[xxyy/100][xxyy%100].setAlpha(0);
    }

    graphic.commitWorldState(1);

    for (int y=Constants.ROWS-1; y >= 0 ; y--)
    {
      for (int x=0; x < Constants.COLUMNS; x++)
      {
        //if (previousState.board[x][y]==state.board[x][y]) continue;

        tooltip.setTooltipText(board[x][y], String.format("x = %d\ny = %d\ncolor = %d", x, y, state.board[x][y]));

        if (state.board[x][y]==-1)
        {
          board[x][y].setAlpha(0);
        }
        else
        {
          board[x][y].setAlpha(1).setImage(state.board[x][y] + ".png");
        }
      }
    }
  }

  public void endgame()
  {
    graphic.commitWorldState(0);

    Sprite win = graphic.createSprite().setImage("endgame.png").setX(960).setY(670)
            .setScale(0.3).setZIndex(3).setAlpha(0).setAnchor(0.5);
    graphic.commitEntityState(0, win);
    win.setAlpha(1).setScale(1.5);
    score.setScale(1.7);
    graphic.commitEntityState(0.3, win, score);
    win.setScale(1);
    score.setScale(0.9);
    graphic.commitEntityState(0.4, win, score);
    win.setScale(1.1);
    score.setScale(1.0);
    graphic.commitEntityState(0.5, win, score);
  }
}
