import javax.swing.*;
import java.awt.event.*;

/****************************************************************
 * GameController
 * 
 * Description:  Manages both the GameModel and GameView objects   
 * Usage:        Communicates between the view and model classes
 *****************************************************************/

public class GameController implements ActionListener
{
   private GameModel theModel;
   private GameView theView;

   private int HUMAN_INDEX = 1;
   private int COMP_INDEX = 0;
   private Card[] cardsInPlay; //holds the two cards that are being played

   /**
    * Constructor that starts a new game using a model and a view
    * @param model
    * @param view
    */
   public GameController(GameModel model, GameView view)
   {
      theModel = model;
      theView = view;

      theModel.startNewGame();
      theView.createTable();
      
      cardsInPlay = new Card[theModel.getNumPlayers()];

      loadPlayerHands();
   }
   
   /**
    * Loads the computer and human's current hands onto the table
    */
   public void loadPlayerHands()
   {
      // Get info from the GameModel class
      Icon[] playerIcons = theModel.loadHandIcons(HUMAN_INDEX);
      Icon compIcon = theModel.getBackCardIcon();
      int numCompCards = theModel.getNumCardsInHand(COMP_INDEX);

      // Display info with the GameView class
      theView.createCompLabels(compIcon, numCompCards);
      theView.createHumanLabels(playerIcons, this);
   }

   /**
   * Action event that is fired every time the user clicks a card button
   *@param ActionEvent
   */
   @Override
   public void actionPerformed(ActionEvent e) 
   {
      // Get the card that the user clicked
      String cardPlayed = e.getActionCommand();
      int cardIndex = Integer.parseInt(cardPlayed);
     
      
      // Sort the computer's hand, so we can easily access the lowest card  
      theModel.sortHand(COMP_INDEX);
            
      playChosenCards(cardIndex);

      // 0 means computer won, 1 means human won, -1 means a tie
      int winner = theModel.determineRoundWinner(
         cardsInPlay[COMP_INDEX], cardsInPlay[HUMAN_INDEX]);
      
      theView.displayRoundWinner(winner);

      loadPlayerHands();

      // Check if the game is over 
      if(theModel.isGameOver(HUMAN_INDEX))
         endGame();
   }

   /**
    * Gets the playing card from each player and adds it to the card table.
    * @param int the index of the card being played
    */
   private void playChosenCards(int cardIndex)
   {
      // Each player plays a card
      cardsInPlay[HUMAN_INDEX] = theModel.playCard(HUMAN_INDEX, cardIndex);
      cardsInPlay[COMP_INDEX] = theModel.playCard(COMP_INDEX, 0);
      
      // Get each player's card icons to be displayed in the playing area
      Icon playerIcon = theModel.getCardIcon(cardsInPlay[HUMAN_INDEX]);
      Icon compIcon = theModel.getCardIcon(cardsInPlay[COMP_INDEX]);

      // Create each player's label and put them on the playing area
      theView.displayPlayingCards(playerIcon, compIcon);
      
   }

   /**
    * Gets the final scores for each player and displays the
    * the appropriate message.
    */
   private void endGame()
   {      
      int humanScore = theModel.getPlayerScore(HUMAN_INDEX);
      int compScore = theModel.getPlayerScore(COMP_INDEX);
      
      theView.displayLowCardWinner(compScore, humanScore);

   }
 
}