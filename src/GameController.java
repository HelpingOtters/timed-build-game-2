import javax.swing.*;
import java.awt.event.*;
import java.awt.Font;


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
          this(); // call to the default constructor
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
                doNothing(100); //100 millisecond pause 
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