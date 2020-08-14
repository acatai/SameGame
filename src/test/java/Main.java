import com.codingame.game.Player;
import com.codingame.gameengine.runner.SoloGameRunner;

public class Main
{
  public static void main(String[] args)
  {
    SoloGameRunner gameRunner = new SoloGameRunner();

    //gameRunner.setAgent(PlayerStub.class);
    gameRunner.setAgent(PlayerRandom.class);
    //gameRunner.setAgent(PlayerError.class);
    //gameRunner.setAgent(PlayerFlatMC.class);

    //gameRunner.setTestCase("test1.json");
    gameRunner.setTestCase("test13.json");
    //gameRunner.setTestCase("test3.json");

    //gameRunner.setTestCase("test10.json");

    gameRunner.start();
  }
}
