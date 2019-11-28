import java.awt.*;
import java.util.Random;

import javax.swing.*;
import java.awt.event.*;

/**
 * Communicates between the view and model classes 
 */
public class GameController implements ActionListener
{
   private GameModel theModel;
   private GameView theView;

   private final int NUM_CARDS_PER_HAND = 7;
   private final int NUM_PLAYERS = 2;
   private int numPacksPerDeck = 1;
   private int numJokersPerPack = 4;
   private int numUnusedCardsPerPack = 0;
   private Card[] unusedCardsPerPack = null;

   private int HUMAN_INDEX = 1;
   private int COMP_INDEX = 0;

   public GameController(GameModel model, GameView view)
   {
      System.out.println("In game controller");
      theModel = model;
      theView = view;

      
      theModel.startNewGame(numPacksPerDeck, numJokersPerPack, 
         numUnusedCardsPerPack, unusedCardsPerPack, NUM_PLAYERS, NUM_CARDS_PER_HAND);
      
      theView.setUpTable(NUM_CARDS_PER_HAND, NUM_PLAYERS);

      setPlayerPanels();
   }

   public void setPlayerPanels()
   {
      // Get info from the GameModel class
      Icon[] playerIcons = theModel.getPlayerIcons();
      Icon compIcon = theModel.getCompIcon();
      int numCompCards = theModel.getNumCardsInHand(COMP_INDEX);

      // Display info with the GameView class
      theView.setUpCompLabels(compIcon, numCompCards);
      theView.setUpPlayerLabels(playerIcons, this);
   }

   /**
   * Action event that is fired every time the user clicks a card button
   *@param ActionEvent
   */
   @Override
   public void actionPerformed(ActionEvent e) 
   {
      // Get the card that the user played
      String cardPlayed = e.getActionCommand();
      int cardNum = Integer.parseInt(cardPlayed);
     
      
      // Sort the computer's hand, so we can easily access the lowest card  
      theModel.sort(COMP_INDEX);
      //LowCardGame.getHand(COMP_INDEX).sort();
      
      /*
      putCardsOnTable(cardNum);

      determineWinner(cardsInPlay[COMP_INDEX], cardsInPlay[HUMAN_INDEX]);

      updateButtons();
      

      // Check if the game is over 
      if(LowCardGame.getHand(HUMAN_INDEX).getNumCards() == 0)
         endGame();
      */
   }

   /**
    * Gets the playing card from each player and adds it to the card table.
    * @param int the index of the card being played
    */
   private void putCardsOnTable(int cardNum)
   {
      // Each player plays a card
      cardsInPlay[COMP_INDEX] = LowCardGame.playCard(COMP_INDEX, 0);
      cardsInPlay[HUMAN_INDEX] = LowCardGame.playCard(HUMAN_INDEX, cardNum);
      
      // Get each player's card icons to be displayed in the playing area
      Icon playerIcon = GUICard.getIcon(cardsInPlay[HUMAN_INDEX]);
      Icon compIcon = GUICard.getIcon(cardsInPlay[COMP_INDEX]);

      // Create each player's label to put on the playing area
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
      myCardTable.pnlPlayArea.removeAll();
      cardsPanel.removeAll();
      

      // Add new cards to play area
      cardsPanel.add(playedCardLabels[HUMAN_INDEX]);
      cardsPanel.add(playedCardLabels[COMP_INDEX]);
      cardsPanel.setBackground(backgroundColor);
      myCardTable.pnlPlayArea.add(cardsPanel, BorderLayout.NORTH);
      
   }

   /**
    * Checks the final score of the game and displays the
    * the approriate message.
    */
   private void endGame()
   {
      String compWinner = "Computers win! " + computerWinningsCounter + " vs " + humanWinningsCounter;
      String humanWinner = "Humans win! " + humanWinningsCounter + " vs " + computerWinningsCounter;
      String tie = humanWinningsCounter + " vs " + computerWinningsCounter;
      //computer wins scenario
      if(computerWinningsCounter > humanWinningsCounter)
      {
         myCardTable.pnlPlayArea.removeAll();
         cardsPanel.removeAll();
         JOptionPane.showMessageDialog(null,new JLabel(compWinner,JLabel.CENTER),"01010111 01001001 01001110", JOptionPane.PLAIN_MESSAGE);  
         System.exit(0);       
      }
      //human wins scenario
      else if (humanWinningsCounter > computerWinningsCounter)
      {
         myCardTable.pnlPlayArea.removeAll();
         cardsPanel.removeAll();
         JOptionPane.showMessageDialog(null,new JLabel(humanWinner,JLabel.CENTER),"HUMANS RULE! ROBOTS DROOL!", JOptionPane.PLAIN_MESSAGE); 
         System.exit(0);       
      }
      //tie scenario. If we are only playing cards in hand this should never happen
      else
      {
         //JOptionPane.showMessageDialog(null,new ImageIcon("gifs/TIE.gif"),"IT'S A TIE", JOptionPane.PLAIN_MESSAGE);
         myCardTable.pnlPlayArea.removeAll();
         cardsPanel.removeAll();
         JOptionPane.showMessageDialog(null,new JLabel(tie,JLabel.CENTER),"IT'S A TIE", JOptionPane.PLAIN_MESSAGE);
         System.exit(0); 
      }

   }
   
   /**
    * Determines the winner of the round and displays
    * the appropriate message.
    * @param compCard
    * @param humanCard
    */
   private void determineWinner(Card compCard, Card humanCard)
   {
      JLabel winnerMessage = new JLabel("", JLabel.CENTER);
      
      // Check who won this round 
      if(Card.valueAsInt(compCard) < Card.valueAsInt(humanCard))
      {
         // Computer wins this round
         compWinnings[computerWinningsCounter] = compCard;
         compWinnings[computerWinningsCounter] = humanCard;
         computerWinningsCounter += 2;
         winnerMessage.setText("Resistance is futile");
         winnerMessage.setForeground(textColor);
      }
      else if(Card.valueAsInt(compCard) > Card.valueAsInt(humanCard))
      {
         // Human wins this round
         humanWinnings[humanWinningsCounter] = compCard;
         humanWinnings[humanWinningsCounter] = humanCard;
         humanWinningsCounter += 2;
         winnerMessage.setText("It's a Human thing— you wouldn't understand...");
         winnerMessage.setForeground(textColor);
      }   
      else
      {
         // If there is a tie
         winnerMessage.setText("It's a tie!");
         winnerMessage.setForeground(textColor);
      }

      myCardTable.pnlPlayArea.add(winnerMessage, BorderLayout.CENTER);
   }

   /**
    * Updates the card buttons for the player panel.
    */
   private void updateButtons()
   {
      myCardTable.pnlHumanHand.removeAll();
      myCardTable.pnlComputerHand.removeAll();
      myCardTable.pnlComputerHand.setVisible(false);
      myCardTable.pnlHumanHand.setVisible(false);

      // Create the labels
      for (int card = 0; card < LowCardGame.getHand(HUMAN_INDEX).getNumCards(); card++)
      {
      
         //back labels made for playing cards 
         computerLabels[card] = new JLabel(GUICard.getBackCardIcon());
         
         //labels for each card in the user's hand
         tempIcon = GUICard.getIcon(LowCardGame.getHand(HUMAN_INDEX).inspectCard(card));

         cardButtons[card] = new JButton(tempIcon);
         cardButtons[card].setPreferredSize(new Dimension(73,97));
         cardButtons[card].setActionCommand(Integer.toString(card));
         cardButtons[card].addActionListener(this);

         // index label added to human panel
         myCardTable.pnlHumanHand.add(cardButtons[card]);

         // index label added to computer panel
         myCardTable.pnlComputerHand.add(computerLabels[card]);
      }
      myCardTable.setVisible(true);
      myCardTable.pnlComputerHand.setVisible(true);
      myCardTable.pnlHumanHand.setVisible(true);
   }

 
}