package com.codingame.game;
import com.codingame.gameengine.core.AbstractSoloPlayer;

public class Player extends AbstractSoloPlayer
{
    public int expectedOutputLines = 1;

    public void endGame() { expectedOutputLines = 0; }

    @Override
    public int getExpectedOutputLines() {
        return expectedOutputLines;
    }
}