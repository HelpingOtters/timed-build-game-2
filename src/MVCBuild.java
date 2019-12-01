/************************************************************************
 * MVC Pattern Design for Low-card game
 * 
 * @author Lindsey Reynolds
 * @version 11/27/19
 * Description: Program that allows the user to play the game "Low-Card" 
 * with the computer. 
 * 
 ***********************************************************************/

public class MVCBuild 
{
   public static void main(String[] args)
   {
      final int NUM_CARDS_PER_HAND = 7;
      final int NUM_PLAYERS = 2;
      final int NUM_STACKS = 3;
      int numPacksPerDeck = 1;
      int numJokersPerPack = 4;
      int numUnusedCardsPerPack = 0;
      Card[] unusedCardsPerPack = null;
      
      // Create the model
      BuildModel gameModel = new BuildModel(numPacksPerDeck, numJokersPerPack, 
         numUnusedCardsPerPack, unusedCardsPerPack, NUM_PLAYERS, NUM_CARDS_PER_HAND);
      
      // Create the view
      BuildView gameView = new BuildView(NUM_CARDS_PER_HAND, NUM_PLAYERS, NUM_STACKS);

      // Create the controller 
      BuildController gameController = new BuildController(gameModel, gameView);
   }
}
