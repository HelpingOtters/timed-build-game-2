import javax.swing.*;
import java.awt.Font;
import java.awt.Component;
import java.awt.event.*;

/****************************************************************
 * GameController
 * 
 * Description: Manages both the GameModel and GameView objects Usage:
 * Communicates between the view and model classes
 *****************************************************************/

public class BuildController implements ActionListener {
   private BuildModel theModel;
   private BuildView theView;

   private int HUMAN = 1;
   private int COMPUTER = 0;

   private boolean humanPlayed = false; // human starts first
   private boolean compPlayed = true;
   private boolean humanTurn = true;
   private int humanCardIndex = -1; // Intalizing the location of the first human card

   /**
    * Constructor that starts a new game using a model and a view
    * 
    * @param model
    * @param view
    */
   public BuildController(BuildModel model, BuildView view) {
      theModel = model;
      theView = view;

      theModel.startNewGame();
      theView.createTable();

      theView.createComputerStatus();
      theView.createHumanButton(this);

      loadDeck();

      loadPlayerHands();
      loadStack();
      loadScore();
   }

   public void loadScore() {
      theView.createScoreLabels(theModel.getPlayerScore(COMPUTER), theModel.getPlayerScore(HUMAN));
   }

   /**
    * Loads the computer and human's current hands onto the table
    */
   public void loadPlayerHands() {
      // Get info from the GameModel class
      Icon[] playerIcons = theModel.loadHandIcons(HUMAN);
      Icon compIcon = theModel.getBackCardIcon();
      int numCompCards = theModel.getNumCardsInHand(COMPUTER);

      // Display info with the GameView class
      theView.createCompLabels(compIcon, numCompCards);
      theView.createHumanLabels(playerIcons, this);
   }

   /**
    * Loads the specific hand onto the table
    *@param handIndex
    */
   public void loadPlayerHands(int handIndex) {
      // Get info from the GameModel class
      Icon[] playerIcons = theModel.loadHandIcons(handIndex);

      // Display info with the GameView class
      theView.createHumanLabels(playerIcons, this);
   }

   public void loadStack() {
      // Get info from the GameModel class
      Icon[] stackIcons = theModel.loadStackIcons();

      // Display info with the GameView class
      theView.createStackButton(stackIcons, this);
   }
   /**
    * Dan's Test creates a deck of 56 back of card icons
    */
   public void loadDeck() 
   {
      for(int cards = 0, x = 56; cards < 56; cards++, x--)
      {
         theView.createDeckLabels(theModel.getBackCardIcon(),x); 
      }
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
      
      System.out.println("Action Command Index: " + cardIndex);
      System.out.println("Cards in Deck : " + theModel.getNumCardsRemainingInDeck());
   
      if (cardIndex == BuildView.BUTTON_INDEX)
      {
         // Human can't play
         humanNotPlay();
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
         computerPlay();
      }

   }
   
   private void humanNotPlay()
   {
      humanPlayed = false;
      humanTurn = false;      // human turn is over
      theModel.addScore(HUMAN);  // increment the score
      // display human score since it is changed
      theView.createScoreLabels(theModel.getPlayerScore(COMPUTER), 
         theModel.getPlayerScore(HUMAN));
      
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
   
   private void selectCard(int cardIndex)
   {
      if(cardIndex >= 0 && cardIndex < theModel.getNumCardsInHand(HUMAN))
      {
         if (humanCardIndex >= 0)
         {
            theView.unhighlightCard(humanCardIndex);
         }
         theView.highlightCard(cardIndex);
         humanCardIndex = cardIndex;
      }
   }
 
   
   private void computerPlay()
   {
      Card[] stack = theModel.getStack();
      Hand computer = theModel.getHand(COMPUTER);
      boolean found = false;
      for(int i = 0; i < stack.length && !found; i++)
      {
         Card stackCard = stack[i];
         int stackValue = Card.valueAsInt(stackCard);
         for(int j = 0; j < computer.getNumCards() && !found; j++)
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
               
               if (theModel.isGameOver())
               {
                  endGame();
               }
               else
               {
                  // game continues
                  Icon compLabel = GUICard.getBackCardIcon(); // back side of the card
                  // Display info with the GameView class
                  theView.createCompLabels(compLabel, theModel.getHand(COMPUTER).getNumCards());

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
         humanTurn = true;
         theModel.addScore(COMPUTER);  // increment the score
         // display human score since it is changed
         theView.createScoreLabels(theModel.getPlayerScore(COMPUTER), 
            theModel.getPlayerScore(HUMAN));
         theView.updateCompStatus("Computer Can't Play");
         if (!humanPlayed)
         {
            // human did not play as well, need to reload the stacks
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
      else
      {
         theView.updateCompStatus("Computer Played");
      }
   }

   private void humanPlay(int stackIndex)
   {
      if(humanCardIndex >= 0)
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
            // do nothing now, may be a warning message dialog to tell how to play
         }
      }
      
   }
   


   /**
    * Gets the final scores for each player and displays the
    * the appropriate message.
    */
   private void endGame()
   {      
      int humanScore = theModel.getPlayerScore(HUMAN);
      int compScore = theModel.getPlayerScore(COMPUTER);
      //***********add a clear deck method here? - Dan ***********************************************/
      theView.clearDeckLabels();
      theView.displayWinner(compScore, humanScore);
      
      System.exit(0);

   }

   /**
    * Timer class 
    */

    @SuppressWarnings("serial")
    public static class Timer extends JLabel implements ActionListener
    {
       private JButton timerButton = new JButton();
       private Counter threadCount = new Counter();
 
       /**
        * default constructor
        */
       public Timer()
       {
          timerButton.addActionListener(this);
          this.setHorizontalAlignment(SwingConstants.CENTER);
          setFont(new Font("Adobe Caslon", Font.BOLD, 25));
       }
 
       /**
        * constuctor allows creation of start of time 
        */
       public Timer(boolean startTimerNow)
       {
          this(); //call to the default constructor
          if (startTimerNow)
          {
             threadCount.start();
          }
       }
 
       /**
        * @return a JButton 
        * start and stop the timer
        */
       public JButton toggleButton()
       {
          return timerButton;
       }
 
       /**
        * resets timer to 0s
        */
       public boolean resetTimer()
       {
          this.threadCount.resetSec(0);
          return true;
       }
 
       /**
        * action listener to start and stop the timer. "pause" functionality
        */
       @Override
       public void actionPerformed(ActionEvent e)
       {
          if (threadCount.isAlive())
          {
             threadCount.stopThread();
             threadCount = new Counter(threadCount.secElapsed());
          } else
          {
             threadCount.start();
          }
       }
 
       /**
        * Counter class: multi-thread of the timer 
        */
       public class Counter extends Thread
       {
          private int sec = 0;
          private boolean threading = true;
 
          /**
           * default constructor 
           * calls the constructor of the Thread class
           */
          public Counter()
          {
             super();
          }
 
          /**
           * constructor allows the caller to initialize the thread
           * with a start time. "pause" illusion
           */
          public Counter(int timeStartValue)
          {
             // prevents incrementation 
             this.sec = timeStartValue - 1;
          }
 
          /**
           * updates timer
           */
          public void run()
          {
             while (threading)
             {
                //timer restarts to 0 after 99min
                if (this.sec < 6000)
                {
                   this.sec += 1;
                } else
                {
                   this.sec = 0;
                }
                // JLabel text
                setText(timeFormat(sec));
                doNothing(1000); //1000 millisecond pause 
             }
          }
 
          /**
           * Added to allow Timer class to reset timer to zero.
           */
          public boolean resetSec(int sec)
          {
             this.sec = sec;
             return true;
          }
 
          /**
           * terminates run() loop
           */
          public boolean stopThread()
          {
             this.threading = false;
             return true;
          }
 
          /**
           * return time elapsed 
           */
          public int secElapsed()
          {
             return this.sec;
          }
 
          /**
           * returns string in mm:ss format 
           */
          private String timeFormat(int totalSeconds)
          {
             int minutes = totalSeconds / 60;
             int sec = totalSeconds - (minutes * 60);
             String formattedTime = String.format("%02d", minutes) + ":"
                            + String.format("%02d", sec);
             return formattedTime;
          }
 
          /**
           * private helping method pauses thread to allow multi-threading 
           */
          private void doNothing(int milliseconds)
          {
             try
             {
                Thread.sleep(milliseconds);
             } catch (InterruptedException e)
             {
                System.out.println("Unexpected interrupt");
                System.exit(0);
             }
          }
       }
    }
 
}