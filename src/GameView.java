import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.event.*;

/** 
 * Displays the game to the user
 */
public class GameView 
{
   private int numCardsPerHand;
   private int numPlayers;


   private int HUMAN_INDEX = 1;
   private int COMP_INDEX = 0;
   static int computerWinningsCounter = 0;
   static int humanWinningsCounter = 0;
   
   static JPanel cardsPanel = new JPanel(new GridLayout());
   static JLabel[] computerLabels;
   static JLabel[] playedCardLabels; 
   static JLabel[] playLabelText;
   static JButton[] cardButtons;

   //static Card[] cardsInPlay = new Card[NUM_PLAYERS];
   static Card[] compWinnings = new Card[57]; //fix size and instantiate in main
   static Card[] humanWinnings = new Card[57]; //fix size instantiate in main
   
   static Icon tempIcon;
   static CardTable myCardTable;

   static Color backgroundColor = new Color(53,101,77);
   static Color textColor = new Color(228,131,0);

   public GameView()
   {
   }

   public void setUpTable(int numCardsPerHand, int numPlayers)
   {
      computerLabels = new JLabel[numCardsPerHand];
      playedCardLabels  = new JLabel[numPlayers]; 
      playLabelText  = new JLabel[numPlayers];
      cardButtons = new JButton[numCardsPerHand];


      // establish main frame in which program will run
      myCardTable 
      = new CardTable("Card Table", numCardsPerHand, numPlayers);
      myCardTable.setSize(800, 600);
      myCardTable.setLocationRelativeTo(null);
      myCardTable.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      // Set up the play area layout
      myCardTable.pnlPlayArea.setLayout(new BorderLayout());
      myCardTable.pnlHumanHand.setLayout(new FlowLayout(FlowLayout.CENTER));
      myCardTable.pnlComputerHand.setLayout(new FlowLayout(FlowLayout.CENTER));
      //set background color
      myCardTable.pnlPlayArea.setBackground(backgroundColor);
      myCardTable.pnlHumanHand.setBackground(backgroundColor);
      myCardTable.pnlComputerHand.setBackground(backgroundColor);

      // show everything to the user
      myCardTable.setVisible(true);

   }

   /*
   theView.setUpCompLabels(theModel.getCompIcon());
      
   theView.setUpPlayerLabels(playerIcons, this);
   */

   public void setUpCompLabels(Icon icon, int numCompCards)
   {
      // Clear any old data 
      myCardTable.pnlComputerHand.removeAll();

      // Create the labels
      for (int card = 0; card < numCompCards; card++)
      {
         // Create back labels for all the computer's cards 
         computerLabels[card] = new JLabel(icon);
         
         // add computer's card labels to the table
         myCardTable.pnlComputerHand.add(computerLabels[card]);
      }
     myCardTable.setVisible(true);
   }

   public void setUpPlayerLabels(Icon[] cardIcons, ActionListener buttonListener)
   {
      // Clear any old data 
      myCardTable.pnlHumanHand.removeAll();

      // Create the buttons for each card
      for (int index = 0; index < cardIcons.length; index++)
      {
         // Create buttons for each of the human cards
         cardButtons[index] = new JButton(cardIcons[index]);
         cardButtons[index].setPreferredSize(new Dimension(73,97));
         cardButtons[index].setActionCommand(Integer.toString(index));
         cardButtons[index].addActionListener(buttonListener);
         
         // add human's card buttons to the table
         myCardTable.pnlHumanHand.add(cardButtons[index]);
      }
     myCardTable.setVisible(true);

   }

}

/*********************************************************************
 * CardTable
 * 
 * description: creates CardTable class that extends JFrame usage: controls the
 * positioning of the panels and cards of the GUI
 **********************************************************************/

class CardTable extends JFrame 
{
   private static final long serialVersionUID = 1L;
   // members establish the grid layout for the JPanels
   static int MAX_CARDS_PER_HAND = 56;
   static int MAX_PLAYERS = 2;
   private int numCardsPerHand;
   private int numPlayers;
   public JPanel pnlComputerHand, pnlHumanHand, pnlPlayArea;

   /**
    * constructor filters input, adds any panels to the Jframe and establishes
    * layouts accordingly.
    * 
    * @param title
    * @param numCardsPerHand
    * @param numPlayers
    */
   public CardTable(String title, int numCardsPerHand, int numPlayers) 
   {
      // displays title on window
      super(title);

      // lays out the border
      setLayout(new BorderLayout());      

      // values that will be used
      this.numCardsPerHand = numCardsPerHand;
      this.numPlayers = numPlayers;

      // field panels defined
      pnlComputerHand = new JPanel(new GridLayout(1, numCardsPerHand));
      pnlHumanHand = new JPanel(new GridLayout(1, numCardsPerHand));
      pnlPlayArea = new JPanel(new GridLayout(2, numPlayers));

      // place panels on grid
      add(pnlPlayArea, BorderLayout.CENTER);
      add(pnlComputerHand, BorderLayout.NORTH);
      add(pnlHumanHand, BorderLayout.SOUTH);      

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

   }

   // accessors
   public int getNumCardsPerHand() 
   {
      return numCardsPerHand;
   }

   public int getNumPlayers() 
   {
      return numPlayers;
   }
}