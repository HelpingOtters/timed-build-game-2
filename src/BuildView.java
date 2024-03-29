import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;

/****************************************************************
 * GameView
 * 
 * Description:  Displays the game UI components to the user  
 * Usage:        Updates the GUI of the game
 *****************************************************************/

public class BuildView 
{
   public static final int HUMAN_INDEX = 1;
   public static final int COMP_INDEX = 0;
   public static final int STACK_BASE_INDEX = 100;
   public static final int BUTTON_INDEX = 200;
   public static final int TIMER_BUTTON_INDEX = 50;

   private int numCardsPerHand;
   private int numStacks;

   private JPanel pnlComputerHand;
   private JPanel pnlHumanHand;
   private JPanel pnlPlayArea;

   // panels in the play area
   private JPanel stackPanel;
   private JPanel humanPanel;
   private JPanel computerPanel;
   private JPanel scorePanel;
   private JPanel theDeckPanel;

   private JLabel[] computerLabels;
   private JButton[] stackButtons; 
   private JButton[] humanCardButtons;
   private JFrame myCardTable;

   //Experimental by Dan
   private TimerLabel autoTimer; 
   private JButton timerButton;
   //Theme colors
   private Color pokerGreen = new Color(53,101,77);
   private Color gold = new Color(228,131,0);
   private Color ruby = new Color(88,7,37);

  /**
   * Recives the timer from the controller
   * @param autoTimer
   * @param threadCount
   */
   public void setTimerLabel(TimerLabel autoTimer)
   {
      this.autoTimer = autoTimer;
      //this.timerCount = timerCount;
      this.timerButton = autoTimer.toggleButton();
   }

   /**
    * Returns timer object
    * @return autoTimer
    */
   public TimerLabel getTimerLabel()
   {
      return autoTimer;
   }
  
   /**
    * Constructor that takes the number of cards per hand and number of players
    * @param numCardsPerHand
    * @param numPlayers
    */
   public BuildView(int numCardsPerHand, int numPlayers, int numStacks)
   {
      this.numCardsPerHand = numCardsPerHand;
      //this.numPlayers = numPlayers;
      this.numStacks = numStacks;
   }

   /**
    * Sets up the card table with panels for each player and a playing area
    */
   public void createTable()
   {
      computerLabels = new JLabel[numCardsPerHand];
      stackButtons  = new JButton[numStacks]; 
      humanCardButtons = new JButton[numCardsPerHand];

      // Establish main frame in which program will run
      myCardTable = new JFrame("Build Game");

      // Update the frame's display
      myCardTable.setLayout(new BorderLayout());  
      myCardTable.setSize(800, 600);
      myCardTable.setLocationRelativeTo(null);
      myCardTable.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      // field panels defined
      pnlComputerHand = new JPanel();
      pnlHumanHand = new JPanel();
      pnlPlayArea = new JPanel();

      // Set up layout for panels
      pnlPlayArea.setLayout(new BorderLayout());
      pnlHumanHand.setLayout(new FlowLayout(FlowLayout.CENTER));
      pnlComputerHand.setLayout(new FlowLayout(FlowLayout.CENTER));

      // define panels for the play area
      stackPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
      humanPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
      computerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
      scorePanel = new JPanel(new GridLayout(3, 1));
      theDeckPanel = new JPanel(new CardLayout());

      //forces the sizes to keep the panels even
      theDeckPanel.setPreferredSize(new Dimension(150,200));
      scorePanel.setPreferredSize(new Dimension(150,200));

      //set background color
      pnlPlayArea.setBackground(pokerGreen);
      pnlHumanHand.setBackground(pokerGreen);
      pnlComputerHand.setBackground(pokerGreen);
      stackPanel.setBackground(pokerGreen);
      computerPanel.setBackground(pokerGreen);
      humanPanel.setBackground(pokerGreen);
      theDeckPanel.setBackground(pokerGreen);

      // place panels on the play area
      pnlPlayArea.add(stackPanel, BorderLayout.CENTER);
      pnlPlayArea.add(computerPanel, BorderLayout.NORTH);
      pnlPlayArea.add(humanPanel, BorderLayout.SOUTH); 
      pnlPlayArea.add(scorePanel, BorderLayout.EAST);
      pnlPlayArea.add(theDeckPanel, BorderLayout.WEST);

      // place panels on grid
      myCardTable.add(pnlPlayArea, BorderLayout.CENTER);
      myCardTable.add(pnlComputerHand, BorderLayout.NORTH);
      myCardTable.add(pnlHumanHand, BorderLayout.SOUTH); 

      // labels the borders and sets the colors
      TitledBorder playAreaBorder = new TitledBorder("Community");
      playAreaBorder.setTitleColor(gold);
      pnlPlayArea.setBorder(playAreaBorder);

      TitledBorder compHandBorder = new TitledBorder("Computer");
      compHandBorder.setTitleColor(gold);
      pnlComputerHand.setBorder(compHandBorder);

      TitledBorder playerHandBorder = new TitledBorder("You");
      playerHandBorder.setTitleColor(gold);
      pnlHumanHand.setBorder(playerHandBorder);

      // show everything to the user
      myCardTable.setVisible(true);
   }

   public void createComputerStatus()
   {
      computerPanel.setVisible(false);
      JLabel status = new JLabel("Computer Status", JLabel.CENTER);
      status.setForeground(gold);
      computerPanel.add(status);
      computerPanel.setVisible(true);
   }

   public void createHumanButton(ActionListener buttonListener)
   {
      humanPanel.setVisible(false);
      JButton button = new JButton("I can't play");
      button.setPreferredSize(new Dimension(135,30));
      //button.setBackground(ruby);
      //button.setForeground(Color.WHITE);
      button.setActionCommand(Integer.toString(BUTTON_INDEX));
      button.addActionListener(buttonListener);
      humanPanel.add(button);

      timerButton.addActionListener(buttonListener);
      timerButton.setText("Start/Stop Timer");
      timerButton.setPreferredSize(new Dimension(135,30));
      //timerButton.setBackground(ruby);
      //timerButton.setForeground(Color.white);
      timerButton.setActionCommand(Integer.toString(TIMER_BUTTON_INDEX));
      humanPanel.add(timerButton);

      humanPanel.setVisible(true);
   }

   /**
    * Creates and displays the card labels on the computer panel
    * @param icon
    * @param numCompCards
    */
   public void createCompLabels(Icon icon, int numCompCards)
   {
      // Clear any old data 
      pnlComputerHand.removeAll();
      pnlComputerHand.setVisible(false);

      // Create the labels
      for (int card = 0; card < numCompCards; card++)
      {
         // Create back labels for all the computer's cards 
         computerLabels[card] = new JLabel(icon);

         // add computer's card labels to the table
         pnlComputerHand.add(computerLabels[card]);
      }
      myCardTable.setVisible(true);
      pnlComputerHand.setVisible(true);
   }

   public void updateCompStatus(String status)
   {
      computerPanel.setVisible(false);
      computerPanel.removeAll();
      JLabel label = new JLabel(status, JLabel.CENTER);
      label.setForeground(gold);
      computerPanel.add(label);
      computerPanel.setVisible(true);
   }

   /**
    * Creates and displays the card buttons on the human panel
    * @param cardIcons
    * @param buttonListener
    */
   public void createHumanLabels(Icon[] cardIcons, ActionListener buttonListener)
   {
      // Clear any old data 
      pnlHumanHand.removeAll();
      pnlHumanHand.setVisible(false);

      // Create the buttons for each card
      for (int index = 0; index < cardIcons.length; index++)
      {
         // Create buttons for each of the human cards
         humanCardButtons[index] = new JButton(cardIcons[index]);
         humanCardButtons[index].setPreferredSize(new Dimension(73,97));
         humanCardButtons[index].setBorder(BorderFactory.createLineBorder(Color.black));
         humanCardButtons[index].setActionCommand(Integer.toString(index));
         humanCardButtons[index].addActionListener(buttonListener);

         // add human's card buttons to the table
         pnlHumanHand.add(humanCardButtons[index]);
      }
      myCardTable.setVisible(true);
      pnlHumanHand.setVisible(true);
   }

   public void createStackButton(Icon[] cardIcons, ActionListener buttonListener)
   {
      // Clear any old data 
      stackPanel.removeAll();
      stackPanel.setVisible(false);

      // Create the buttons for each card
      for (int index = 0; index < cardIcons.length; index++)
      {
         // Create buttons for each of the stack buttons
         stackButtons[index] = new JButton(cardIcons[index]);
         stackButtons[index].setPreferredSize(new Dimension(73,97));
         stackButtons[index].setActionCommand(Integer.toString(index + STACK_BASE_INDEX));
         stackButtons[index].addActionListener(buttonListener);

         stackPanel.add(stackButtons[index]);
      }
      myCardTable.setVisible(true);
      stackPanel.setVisible(true);
   }

   /**
    * Creates the Deck Labels
    *@param deckImage
    */
   public void createDeckLabels(Icon deckImage)
   {
      theDeckPanel.add(new JLabel(deckImage));
      theDeckPanel.setVisible(true);
   }
   /**
    * clears the deck panel
    */
   public void clearDeckLabels()
   {
      theDeckPanel.removeAll();
      theDeckPanel.setVisible(false);
   }

   public void createScoreLabels(int compScore, int humScore)
   {
      scorePanel.removeAll();
      scorePanel.setVisible(false);
      scorePanel.setBackground(pokerGreen);

      autoTimer.setForeground(Color.WHITE);
      //autoTimer.setText(threadCount.run());
      scorePanel.add(autoTimer);

      String text = "   Computer Score: " + compScore + "     ";
      JLabel compLabel = new JLabel(text, JLabel.CENTER);
      compLabel.setForeground(gold);
      scorePanel.add(compLabel);

      String text1 = "Your Score: " + humScore;
      JLabel humLabel = new JLabel(text1, JLabel.CENTER);
      humLabel.setForeground(gold);
      scorePanel.add(humLabel);

      scorePanel.setVisible(true);
   }

   public void changeStackIcon(int stackIndex, Icon stackIcon)
   {
      stackPanel.setVisible(false);
      stackButtons[stackIndex].setIcon(stackIcon);
      //stackPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
      stackPanel.setVisible(true);
   }

   public void highlightCard(int cardIndex)
   {
      humanCardButtons[cardIndex].setBorder(BorderFactory.createMatteBorder(
         1, 5, 5, 1, Color.orange));
   }

   public void unhighlightCard(int cardIndex)
   {
      humanCardButtons[cardIndex].setBorder(BorderFactory.createLineBorder(Color.black));
   }

   /**
    * Creates a pop up window to display who won the whole game
    * @param compScore
    * @param humanScore
    */
   public void displayWinner(int compScore, int humanScore)
   {
      String compWinner = "Computers win! " + compScore + " vs " + humanScore;
      String humanWinner = "Humans win! " + humanScore + " vs " + compScore;
      String tie = humanScore + " vs " + compScore;
      //computer wins scenario
      if(compScore < humanScore)
      {
         // the low score wins
         JOptionPane.showMessageDialog(myCardTable,new JLabel(
            compWinner,JLabel.CENTER),"01010111 01001001 01001110", 
            JOptionPane.PLAIN_MESSAGE);  
      }
      //human wins scenario
      else if (humanScore < compScore)
      {
         JOptionPane.showMessageDialog(myCardTable,new JLabel(
            humanWinner,JLabel.CENTER),"Humans Rule and Robots Drool!", 
            JOptionPane.PLAIN_MESSAGE); 
      }
      //tie scenario
      else
      {
         JOptionPane.showMessageDialog(myCardTable,new JLabel(
            tie,JLabel.CENTER),"IT'S A TIE", JOptionPane.PLAIN_MESSAGE);
      }
   }

   // accessors
   public int getNumCardsPerHand() 
   {
      return numCardsPerHand;
   }

}

@SuppressWarnings("serial")
class TimerLabel extends JLabel 
{
   Timer timerCount;
   private JButton timerButton = new JButton();

   /**
    * default constructor
    */
   public TimerLabel()
   {
      //timerButton.addActionListener(this);
      this.setHorizontalAlignment(SwingConstants.CENTER);
      setFont(new Font("Adobe Caslon", Font.BOLD, 25));
   }

   /**
    * constuctor allows creation of start of time 
    */
   public TimerLabel(boolean startTimerNow, Timer timerCount)
   {
      this(); //call to the default constructor
     
      if (startTimerNow)
      {
         timerCount.start();
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
      this.timerCount.resetSec(0);
      return true;
   }
}