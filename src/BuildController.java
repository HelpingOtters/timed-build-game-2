import javax.swing.*;
import java.awt.event.*;

/****************************************************************
 * BuildController
 * 
 * Description: Manages both the BuildModel and BuildView objects Usage:
 * Communicates between the view and model classes
 *****************************************************************/

public class BuildController implements ActionListener
{
   private BuildModel theModel;
   private BuildView theView;

   private int HUMAN = 1; // Index for human hand
   private int COMPUTER = 0; // Index for computer hand

   private boolean humanPlayed = false; // human starts first
   private boolean compPlayed = true; // computer still can play
   private boolean humanTurn = true; // false for computer turn
   private int humanCardIndex = -1; // no card is selected in human hand

   private TimerLabel timerLabel;
   private Timer timerCount;

   /**
    * Constructor that starts a new game using a model and a view
    * 
    * @param model
    * @param view
    */
   public BuildController(BuildModel model, BuildView view)
   {
      theModel = model;
      theView = view;

      theModel.startNewGame();
      theView.createTable();

      theView.createComputerStatus();
      loadTimer();
      theView.createHumanButton(this);

      loadBackOfDeckImage(); // create the deck
      loadPlayerHands(); // display the computer and human hand
      loadStack(); // display the stacks in playing area
      loadScore(); // display score and timer
   }

   /**
    * Display the scores
    */
   public void loadScore()
   {
      theView.createScoreLabels(theModel.getPlayerScore(COMPUTER), theModel
         .getPlayerScore(HUMAN));
   }

   /**
    * Load and display the computer and human's current hands onto the table
    */
   public void loadPlayerHands()
   {
      // Get info from the GameModel class
      Icon[] playerIcons = theModel.loadHandIcons(HUMAN);
      Icon compIcon = theModel.getBackCardIcon();
      int numCompCards = theModel.getNumCardsInHand(COMPUTER);

      // Display info with the GameView class
      theView.createCompLabels(compIcon, numCompCards);
      theView.createHumanLabels(playerIcons, this);
   }

   /**
    * Load and display the specific hand onto the table
    * 
    * @param handIndex the index of the specific hand
    */
   public void loadPlayerHands(int handIndex)
   {
      // Get info from the GameModel class
      Icon[] playerIcons = theModel.loadHandIcons(handIndex);

      // Display info with the GameView class
      theView.createHumanLabels(playerIcons, this);
   }

   /**
    * Load and display the stack onto the table
    */
   public void loadStack()
   {
      // Get info from the GameModel class
      Icon[] stackIcons = theModel.loadStackIcons();

      // Display info with the GameView class
      theView.createStackButton(stackIcons, this);
   }

   /**
   /* Load and display the deck onto the table Dan's Test creates a deck of 56
   * back of card icons
   */
   public void loadBackOfDeckImage() 
   {
      theView.createDeckLabels(theModel.getBackCardIcon()); 
   }

   /**
    * loads the timer on the right side of the table
    */
   public void loadTimer()
   {
      timerCount = new Timer();
      timerLabel = new TimerLabel(true, timerCount);

      theView.setTimerLabel(timerLabel);
      timerCount.setJLabel(theView.getTimerLabel());
   }

   /**
    * Action event that is fired every time the user clicks a card button
    * 
    * @param ActionEvent
    */
   @Override
   public void actionPerformed(ActionEvent e)
   {
      // Get the card that the user clicked
      String cardPlayed = e.getActionCommand();
      int cardIndex = Integer.parseInt(cardPlayed);

      if (cardIndex == BuildView.BUTTON_INDEX)
      {
         // Human can't play
         humanNotPlay();
      }
      else if (cardIndex == BuildView.TIMER_BUTTON_INDEX)
      {
         // timer is toggled
         if (timerCount.isAlive())
         {
            timerCount.stopThread();
            timerCount = new Timer(timerCount.secElapsed());
            timerCount.setJLabel(theView.getTimerLabel());
         }
         else
         {
            timerCount.start();
         }
      }
      else if (cardIndex >= BuildView.STACK_BASE_INDEX)
      {
         // a stack is chosen
         humanPlay(cardIndex - BuildView.STACK_BASE_INDEX);
      }
      else
      {
         // a card is chosen
         selectCard(cardIndex);
      }

      if (!humanTurn)
      {
         // it is computer turn to play
         computerPlay();
      }
   }

   /**
    * This is to check if the human did not play and then checks if the computer
    * did not play as well and adds to their scores by 1 for not playing
    */
   private void humanNotPlay()
   {
      humanPlayed = false; // human can't play
      humanTurn = false; // human turn is over
      theModel.addScore(HUMAN); // increment the score
      // display human score since it is changed
      theView.createScoreLabels(theModel.getPlayerScore(COMPUTER), theModel
         .getPlayerScore(HUMAN));

      if (!compPlayed)
      {
         // computer did not play as well, need to reload the stacks
         theModel.dealToStack();
         if (theModel.isGameOver())
         {
            // not enough cards, game ends
            endGame();
         }
         loadStack();
         // new cards on stack, reset the flags for human and computer
         humanPlayed = true;
         compPlayed = true;
      }
   }

   /**
    * A card is selected from a hand
    * 
    * @param cardIndex
    * 
    */
   private void selectCard(int cardIndex)
   {
      if (cardIndex >= 0 && cardIndex < theModel.getNumCardsInHand(HUMAN))
      {
         if (humanCardIndex >= 0)
         {
            // remove the highlight of previous selected card
            theView.unhighlightCard(humanCardIndex);
         }
         // highlight the selected card
         theView.highlightCard(cardIndex);
         humanCardIndex = cardIndex;
      }
   }

   /**
    * The computer takes its turn and makes a move
    */
   private void computerPlay()
   {
      Card[] stack = theModel.getStack();
      Hand computer = theModel.getHand(COMPUTER);
      boolean found = false;
      for (int i = 0; i < stack.length && !found; i++)
      {
         Card stackCard = stack[i];
         int stackValue = Card.valueAsInt(stackCard);
         for (int j = 0; j < computer.getNumCards() && !found; j++)
         {
            Card computerCard = computer.inspectCard(j);
            int computerValue = Card.valueAsInt(computerCard);
            if (Math.abs(stackValue - computerValue) == 1)
            {
               // found a computer card can place on the stack
               found = true;

               // replace the stack card with the computer card
               theModel.setStackCard(i, computerCard);
               Icon stackIcon = GUICard.getIcon(computerCard);
               theView.changeStackIcon(i, stackIcon);

               // play the computer card then take a card from deck
               theModel.playCard(COMPUTER, j);
               theModel.takeCard(COMPUTER);

               // check if game is over
               if (theModel.isGameOver())
               {
                  endGame();
               }
               else
               {
                  // game continues
                  Icon compLabel = GUICard.getBackCardIcon(); // back side of
                                                              // the card
                  // Display info with the GameView class
                  theView.createCompLabels(compLabel, theModel.getHand(COMPUTER)
                     .getNumCards());

                  // computer turn is done
                  compPlayed = true;
                  humanTurn = true;
               }
            }
         }
      }
      if (!found)
      {
         // Computer can't play
         compPlayed = false;
         humanTurn = true; // computer turn is over
         theModel.addScore(COMPUTER); // increment the score
         // display human score since it is changed
         theView.createScoreLabels(theModel.getPlayerScore(COMPUTER), theModel
            .getPlayerScore(HUMAN));
         theView.updateCompStatus("Computer Can't Play");

         if (!humanPlayed)
         {
            // human did not play as well, need to reload the stacks
            theModel.dealToStack();

            // check if the game is over first
            if (theModel.isGameOver())
            {
               // not enough cards, game ends
               endGame();
            }
            loadStack(); // new stack cards
            // new cards on stack, reset the flags for human and computer
            humanPlayed = true;
            compPlayed = true;

         }

      }
      else
      {
         // show computer has played
         theView.updateCompStatus("Computer Played");
      }
   }

   /**
    * the human takes the turn and makes its move on the specific stack
    * 
    * @param stackIndex
    * 
    */
   private void humanPlay(int stackIndex)
   {
      if (humanCardIndex >= 0)
      {
         // card is selected to place on the stack
         Card[] stack = theModel.getStack();
         Hand hand = theModel.getHand(HUMAN);
         Card stackCard = stack[stackIndex];
         int stackValue = Card.valueAsInt(stackCard);
         Card humanCard = hand.inspectCard(humanCardIndex);
         int humanValue = Card.valueAsInt(humanCard);
         if (Math.abs(stackValue - humanValue) == 1)
         {
            // the human card can place on the stack

            // replace the stack card with human card
            theModel.setStackCard(stackIndex, humanCard);
            Icon stackIcon = GUICard.getIcon(humanCard);
            theView.changeStackIcon(stackIndex, stackIcon);

            // play the human card then take a card from deck
            theModel.playCard(HUMAN, humanCardIndex);
            theModel.takeCard(HUMAN);

            if (theModel.isGameOver())
            {
               endGame();
            }
            else
            {
               // game continues
               Icon[] playerIcons = theModel.loadHandIcons(HUMAN);
               // Display info with the GameView class
               theView.createHumanLabels(playerIcons, this);

               // human turn is done
               humanCardIndex = -1; // clear the selected card
               humanPlayed = true;
               humanTurn = false;
            }
         }
         else
         {
            // do nothing now, may be a warning message dialog to tell how to
            // play
         }
      }
   }

   /**
    * Gets the final scores for each player and displays the the appropriate
    * message.
    */
   private void endGame()
   {
      int humanScore = theModel.getPlayerScore(HUMAN);
      int compScore = theModel.getPlayerScore(COMPUTER);
      // ***********add a clear deck method here? - Dan
      // ***********************************************/
      theView.clearDeckLabels();
      theView.displayWinner(compScore, humanScore);

      System.exit(0);

   }

}