import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.event.*;

/****************************************************************
 * GameView
 * 
 * Description:  Displays the game UI components to the user  
 * Usage:        Updates the GUI of the game
 *****************************************************************/

public class GameView 
{
   private int HUMAN_INDEX = 1;
   private int COMP_INDEX = 0;
   private int numCardsPerHand;
   private int numPlayers;
   
   private static JPanel pnlComputerHand;
   private static JPanel pnlHumanHand;
   private static JPanel pnlPlayArea;
   private static JPanel cardsPanel = new JPanel(new GridLayout());
   private static JLabel[] computerLabels;
   private static JLabel[] playedCardLabels; 
   private static JButton[] cardButtons;
   private static JFrame myCardTable;

   private static Color backgroundColor = new Color(53,101,77);
   private static Color textColor = new Color(228,131,0);

   /**
    * Constructor that takes the number of cards per hand and number of players
    * @param numCardsPerHand
    * @param numPlayers
    */
   public GameView(int numCardsPerHand, int numPlayers)
   {
      this.numCardsPerHand = numCardsPerHand;
      this.numPlayers = numPlayers;
   }

   /**
    * Sets up the card table with panels for each player and a playing area
    */
   public void createTable()
   {
      computerLabels = new JLabel[numCardsPerHand];
      playedCardLabels  = new JLabel[numPlayers]; 
      cardButtons = new JButton[numCardsPerHand];

      // Establish main frame in which program will run
      myCardTable = new JFrame("Card Table");

      // Update the frame's display
      myCardTable.setLayout(new BorderLayout());  
      myCardTable.setSize(800, 600);
      myCardTable.setLocationRelativeTo(null);
      myCardTable.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      // field panels defined
      pnlComputerHand = new JPanel(new GridLayout(1, numCardsPerHand));
      pnlHumanHand = new JPanel(new GridLayout(1, numCardsPerHand));
      pnlPlayArea = new JPanel(new GridLayout(2, numPlayers));
      
      // Set up the play area layout
      pnlPlayArea.setLayout(new BorderLayout());
      pnlHumanHand.setLayout(new FlowLayout(FlowLayout.CENTER));
      pnlComputerHand.setLayout(new FlowLayout(FlowLayout.CENTER));
      //set background color
      pnlPlayArea.setBackground(backgroundColor);
      pnlHumanHand.setBackground(backgroundColor);
      pnlComputerHand.setBackground(backgroundColor);

      // place panels on grid
      myCardTable.add(pnlPlayArea, BorderLayout.CENTER);
      myCardTable.add(pnlComputerHand, BorderLayout.NORTH);
      myCardTable.add(pnlHumanHand, BorderLayout.SOUTH); 

      // labels the borders and sets the colors
      TitledBorder playAreaBorder = new TitledBorder("Community");
      playAreaBorder.setTitleColor(new Color(228,132,0));
      pnlPlayArea.setBorder(playAreaBorder);

      TitledBorder compHandBorder = new TitledBorder("Computer");
      compHandBorder.setTitleColor(new Color(228,132,0));
      pnlComputerHand.setBorder(compHandBorder);

      TitledBorder playerHandBorder = new TitledBorder("You");
      playerHandBorder.setTitleColor(new Color(228,132,0));
      pnlHumanHand.setBorder(playerHandBorder);
      
      // show everything to the user
      myCardTable.setVisible(true);

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
         cardButtons[index] = new JButton(cardIcons[index]);
         cardButtons[index].setPreferredSize(new Dimension(73,97));
         cardButtons[index].setActionCommand(Integer.toString(index));
         cardButtons[index].addActionListener(buttonListener);
         
         // add human's card buttons to the table
         pnlHumanHand.add(cardButtons[index]);
      }
     myCardTable.setVisible(true);
     pnlHumanHand.setVisible(true);
   }
   
   /**
    * Creates and displays the cards being played onto the playing area
    * @param playerIcon
    * @param compIcon
    */
   public void displayPlayingCards(Icon playerIcon, Icon compIcon)
   {
      playedCardLabels[HUMAN_INDEX] = new JLabel("You", JLabel.CENTER);  
      playedCardLabels[HUMAN_INDEX].setForeground(textColor); 
      playedCardLabels[COMP_INDEX] = new JLabel("Computer", JLabel.CENTER);
      playedCardLabels[COMP_INDEX].setForeground(textColor);

      playedCardLabels[HUMAN_INDEX].setIcon(playerIcon);   
      playedCardLabels[COMP_INDEX].setIcon(compIcon);

      // Make sure text is centered
      playedCardLabels[HUMAN_INDEX].setHorizontalTextPosition(JLabel.CENTER);
      playedCardLabels[HUMAN_INDEX].setVerticalTextPosition(JLabel.BOTTOM);
      playedCardLabels[COMP_INDEX].setHorizontalTextPosition(JLabel.CENTER);
      playedCardLabels[COMP_INDEX].setVerticalTextPosition(JLabel.BOTTOM);

      // Remove old info from play area 
      pnlPlayArea.removeAll();
      cardsPanel.removeAll();
      
      // Add new cards to play area
      cardsPanel.add(playedCardLabels[HUMAN_INDEX]);
      cardsPanel.add(playedCardLabels[COMP_INDEX]);
      cardsPanel.setBackground(backgroundColor);
      pnlPlayArea.add(cardsPanel, BorderLayout.NORTH);
      
      myCardTable.setVisible(true);
   }
   
   /**
    * Displays who won the current round onto the playing area
    * @param winner 0 means computer, 1 means human, -1 means tie
    */
   public void displayRoundWinner(int winner)
   {
      JLabel winnerMessage = new JLabel("", JLabel.CENTER);
      
      // Check who won this round 
      if(winner == 0) // if the computer won
      {
         winnerMessage.setText("Resistance is futile");
         winnerMessage.setForeground(textColor);
      }
      else if(winner == 1) // if the human won
      {
         winnerMessage.setText("It's a Human thing— you wouldn't understand...");
         winnerMessage.setForeground(textColor);
      }   
      else
      {
         // If there is a tie
         winnerMessage.setText("It's a tie!");
         winnerMessage.setForeground(textColor);
      }

      pnlPlayArea.add(winnerMessage, BorderLayout.CENTER);
      myCardTable.setVisible(true);
      
   }
   
   /**
    * Creates a pop up window to display who won the whole game
    * @param compScore
    * @param humanScore
    */
   public void displayLowCardWinner(int compScore, int humanScore)
   {

      String compWinner = "Computers win! " + compScore + " vs " + humanScore;
      String humanWinner = "Humans win! " + humanScore + " vs " + compScore;
      String tie = humanScore + " vs " + compScore;
      //computer wins scenario
      if(compScore > humanScore)
      {
         pnlPlayArea.removeAll();
         cardsPanel.removeAll();
         JOptionPane.showMessageDialog(null,new JLabel(
            compWinner,JLabel.CENTER),"01010111 01001001 01001110", 
            JOptionPane.PLAIN_MESSAGE);  
         System.exit(0);       
      }
      //human wins scenario
      else if (humanScore > compScore)
      {
         pnlPlayArea.removeAll();
         cardsPanel.removeAll();
         JOptionPane.showMessageDialog(null,new JLabel(
            humanWinner,JLabel.CENTER),"HUMANS RULE! ROBOTS DROOL!", 
            JOptionPane.PLAIN_MESSAGE); 
         System.exit(0);       
      }
      //tie scenario
      else
      {
         pnlPlayArea.removeAll();
         cardsPanel.removeAll();
         JOptionPane.showMessageDialog(null,new JLabel(
            tie,JLabel.CENTER),"IT'S A TIE", JOptionPane.PLAIN_MESSAGE);
         System.exit(0); 
      }
   }
}