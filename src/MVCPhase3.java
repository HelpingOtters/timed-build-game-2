/************************************************************************
 * MVC Pattern Design for Low-card game
 * 
 * @author Lindsey Reynolds
 * @version 11/27/19
 * Description: Program that allows the user to play the game "Low-Card" 
 * with the computer. 
 * 
 ***********************************************************************/
public class MVCPhase3 
{
   public static void main(String[] args)
   {
      GameModel gameModel = new GameModel();
      GameView gameView = new GameView();

      GameController gameController = new GameController(gameModel, gameView);

   }
}
