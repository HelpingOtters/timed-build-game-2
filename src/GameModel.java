import java.util.Random;
import javax.swing.*;

/****************************************************************
 * GameModel
 * 
 * Description:  Creates the model for a card game  
 * Usage:        Manages the data of the card game
 *****************************************************************/

public class GameModel
{
   private CardGameFramework LowCardGame;
   
   // Variables to keep track of winnings
   private int computerWinningsCounter = 0;
   private int humanWinningsCounter = 0;
   private Card[] compWinnings;
   private Card[] humanWinnings;

   /** 
    * Constructor that takes arguments to create a new game
    * @param numPacks
    * @param numJokersPerPack
    * @param numUnusedCardsPerPack
    * @param unusedCardsPerPack
    * @param numPlayers
    * @param numCardsPerHand
    */
   public GameModel(int numPacks, int numJokersPerPack, 
      int numUnusedCardsPerPack, Card[] unusedCardsPerPack, int numPlayers, 
      int numCardsPerHand)
   {
      LowCardGame = new CardGameFramework(
         numPacks, numJokersPerPack, numUnusedCardsPerPack, unusedCardsPerPack,
         numPlayers, numCardsPerHand
         );
      compWinnings = new Card[LowCardGame.getNumCardsRemainingInDeck() * numPlayers];
      humanWinnings = new Card[LowCardGame.getNumCardsRemainingInDeck() * numPlayers];
   }

   /** 
    * Loads all the card icons to be used later and deals the deck
    */
   public void startNewGame()
   {
      GUICard.loadCardIcons();

      LowCardGame.deal();
   }

   /**
    * Retrieves all the icons associated with the player's current hand
    * @param playerIndex indicates which player
    * @return an array of icons, one for each card in the player's hand
    */
   public Icon[] loadHandIcons(int playerIndex)
   {
      int numHumanCards = getNumCardsInHand(playerIndex);
      Icon[] playerIcons = new Icon[numHumanCards];
      Card currCard;

      for (int card = 0; card < numHumanCards; card++)
      {
         currCard = LowCardGame.getHand(playerIndex).inspectCard(card);
         playerIcons[card] = GUICard.getIcon(currCard);
      }

      return playerIcons;
   }
   
   // Retrieves the back card icon
   public Icon getBackCardIcon()
   {
      return GUICard.getBackCardIcon();
   }
   
   /**
    * Determines the winner of the round and returns an int specifying who won
    * @param compCard
    * @param humanCard
    * @return 0 if computer won
    * @return 1 if human won
    * @return -1 if its a tie
    */
   public int determineRoundWinner(Card compCard, Card humanCard)
   {
      
      // Check who won this round 
      if(Card.valueAsInt(compCard) < Card.valueAsInt(humanCard))
      {
         // Computer won this round
         compWinnings[computerWinningsCounter] = compCard;
         compWinnings[computerWinningsCounter + 1] = humanCard;
         computerWinningsCounter += 2;
         return 0;
      }
      else if(Card.valueAsInt(compCard) > Card.valueAsInt(humanCard))
      {
         // Human won this round
         humanWinnings[humanWinningsCounter] = compCard;
         humanWinnings[humanWinningsCounter + 1] = humanCard;
         humanWinningsCounter += 2;
         return 1;
      }   
      else
      {
         // There was a tie
         return -1;
      }
   }
   
   /**
    * Checks if the game is over by checking if the player's hand is empty
    * @param playerIndex
    * @return boolean
    */
   public boolean isGameOver(int playerIndex)
   {
      if(LowCardGame.getHand(playerIndex).getNumCards() == 0)
      {
         return true;
      }
      return false;
   }
   
   /**
    * Retrieves the specified player's total score
    * @param playerIndex
    * @return the player's score
    */
   public int getPlayerScore(int playerIndex)
   {
      // Check who won 
      if(playerIndex == 0)
         return computerWinningsCounter; 
      
      else if(playerIndex == 1)
         return humanWinningsCounter;
  
      else
         return -1; // If input is incorrect
   }

   // Returns the number of cards in a players hand
   public int getNumCardsInHand(int playerIndex)
   {
      return LowCardGame.getHand(playerIndex).getNumCards();
   }
   
   // Returns the number of players in the current game
   public int getNumPlayers()
   {
      return LowCardGame.getNumPlayers();
   }
   
   // Sorts the specified player's hand
   public void sortHand(int playerIndex)
   {
      LowCardGame.getHand(playerIndex).sort();
   }
   
   // Plays the indicated card for the player
   public Card playCard(int playerIndex, int cardIndex)
   {
      return LowCardGame.playCard(playerIndex, cardIndex);
   }
   
   // Takes a card from the deck and gives it to the specified player
   public boolean takeCard(int playerIndex)
   {
      return LowCardGame.takeCard(playerIndex);
   }
   
   // Retrieves the Icon for the specified Card
   public Icon getCardIcon(Card card)
   {
      return GUICard.getIcon(card);
      
   }
}

/*-----------------------------------------------------
 * End Of GameModel class
 *-----------------------------------------------/



/****************************************
 * CardGameFrameWork class
 *****************************************/
class CardGameFramework 
{
   private static final int MAX_PLAYERS = 50;

   private int numPlayers;
   private int numPacks; // # standard 52-card packs per deck
                         // ignoring jokers or unused cards
   private int numJokersPerPack; // if 2 per pack & 3 packs per deck, get 6
   private int numUnusedCardsPerPack; // # cards removed from each pack
   private int numCardsPerHand; // # cards to deal each player
   private Deck deck; // holds the initial full deck and gets
                      // smaller (usually) during play
   private Hand[] hand; // one Hand for each player
   private Card[] unusedCardsPerPack; // an array holding the cards not used
                                      // in the game. e.g. pinochle does not
                                      // use cards 2-8 of any suit

   public CardGameFramework(int numPacks, int numJokersPerPack, int numUnusedCardsPerPack, Card[] unusedCardsPerPack,
         int numPlayers, int numCardsPerHand) {
      int k;

      // filter bad values
      if (numPacks < 1 || numPacks > 6)
         numPacks = 1;
      if (numJokersPerPack < 0 || numJokersPerPack > 4)
         numJokersPerPack = 0;
      if (numUnusedCardsPerPack < 0 || numUnusedCardsPerPack > 50) // > 1 card
         numUnusedCardsPerPack = 0;
      if (numPlayers < 1 || numPlayers > MAX_PLAYERS)
         numPlayers = 4;
      // one of many ways to assure at least one full deal to all players
      if (numCardsPerHand < 1 || numCardsPerHand > numPacks * (52 - numUnusedCardsPerPack) / numPlayers)
         numCardsPerHand = numPacks * (52 - numUnusedCardsPerPack) / numPlayers;

      // allocate
      this.unusedCardsPerPack = new Card[numUnusedCardsPerPack];
      this.hand = new Hand[numPlayers];
      for (k = 0; k < numPlayers; k++)
         this.hand[k] = new Hand();
      deck = new Deck(numPacks);

      // assign to members
      this.numPacks = numPacks;
      this.numJokersPerPack = numJokersPerPack;
      this.numUnusedCardsPerPack = numUnusedCardsPerPack;
      this.numPlayers = numPlayers;
      this.numCardsPerHand = numCardsPerHand;
      for (k = 0; k < numUnusedCardsPerPack; k++)
         this.unusedCardsPerPack[k] = unusedCardsPerPack[k];

      // prepare deck and shuffle
      newGame();
   }

   // constructor overload/default for game like bridge
   public CardGameFramework() 
   {
      this(1, 0, 0, null, 4, 13);
   }

   public Hand getHand(int k) 
   {
      // hands start from 0 like arrays

      // on error return automatic empty hand
      if (k < 0 || k >= numPlayers)
         return new Hand();

      return hand[k];
   }

   public Card getCardFromDeck() 
   {
      return deck.dealCard();
   }

   public int getNumCardsRemainingInDeck() 
   {
      return deck.getNumCards();
   }

   public void newGame() 
   {
      int k, j;

      // clear the hands
      for (k = 0; k < numPlayers; k++)
         hand[k].resetHand();

      // restock the deck
      deck.init(numPacks);

      // remove unused cards
      for (k = 0; k < numUnusedCardsPerPack; k++)
         deck.removeCard(unusedCardsPerPack[k]);

      // add jokers
      for (k = 0; k < numPacks; k++)
         for (j = 0; j < numJokersPerPack; j++)
            deck.addCard(new Card('X', Card.Suit.values()[j]));

      // shuffle the cards
      deck.shuffle();
   }

   public boolean deal() 
   {
      // returns false if not enough cards, but deals what it can
      int k, j;
      boolean enoughCards;

      // clear all hands
      for (j = 0; j < numPlayers; j++)
         hand[j].resetHand();

      enoughCards = true;
      for (k = 0; k < numCardsPerHand && enoughCards; k++) 
      {
         for (j = 0; j < numPlayers; j++)
            if (deck.getNumCards() > 0)
               hand[j].takeCard(deck.dealCard());
            else {
               enoughCards = false;
               break;
            }
      }

      return enoughCards;
   }

   void sortHands() {
      int k;

      for (k = 0; k < numPlayers; k++)
         hand[k].sort();
   }

   Card playCard(int playerIndex, int cardIndex) 
   {
      // returns bad card if either argument is bad
      if (playerIndex < 0 || playerIndex > numPlayers - 1 || cardIndex < 0 || cardIndex > numCardsPerHand - 1) 
      {
         // Creates a card that does not work
         return new Card('M', Card.Suit.SPADES);
      }

      // return the card played
      return hand[playerIndex].playCard(cardIndex);

   }

   boolean takeCard(int playerIndex) 
   {
      // returns false if either argument is bad
      if (playerIndex < 0 || playerIndex > numPlayers - 1)
         return false;

      // Are there enough Cards?
      if (deck.getNumCards() <= 0)
         return false;

      return hand[playerIndex].takeCard(deck.dealCard());
   }

   int getNumCardsPerHand()
   {
      return numCardsPerHand;
   }

   int getNumPlayers()
   {
      return numPlayers;
   }

}
/*-----------------------------------------------------
 * End Of CardGameFramework
 *-----------------------------------------------/



 /****************************************************************
 * GUI Card
 * 
 * description:  creates a new GUICard class 
 * usage:        manages the reading and building of the card
 *               image Icons.
 *****************************************************************/
class GUICard 
{
   // members to facilitate GUICard class
   private static Icon[][] iconCards = new ImageIcon[14][4];
   private static Icon iconBack;
   static boolean iconsLoaded = false;

   // identifies card icon filenames
   private static char[] cardSuits = { 'C', 'D', 'H', 'S' };

   /**
    * generates the image icon array from files
    */
   static void loadCardIcons() 
   {
      if (iconsLoaded)
         return;

      // generates file names of icon and adds to Icon's array
      String filename = "";
      for (int i = 0; i < Card.valuRanks.length; ++i) 
      {
         for (int j = 0; j < cardSuits.length; ++j) 
         {
            filename = "src/images/" + Card.valuRanks[i] + cardSuits[j] + ".gif";
            iconCards[i][j] = new ImageIcon(filename);
         }
      }
      // fills the back of card
      iconBack = new ImageIcon("src/images/BK.gif");
      iconsLoaded = true;
   }

   // returns back icon for back of card
   public static Icon getBackCardIcon() 
   {
      loadCardIcons();
      return iconBack;
   }

   // returns specific icon
   public static Icon getIcon(Card card) 
   {
      loadCardIcons();
      return iconCards[valueAsInt(card)][suitAsInt(card)];
   }

   // index of card value returned
   private static int valueAsInt(Card card) 
   {
      int i = 0;
      while (card.getValue() != Card.valuRanks[i])
         ++i;

      return i;
   }

   // returns index of card suit
   private static int suitAsInt(Card card) 
   {
      int i;
      Card.Suit suit = card.getSuit();
      Card.Suit[] allSuits = Card.Suit.values();

      for (i = 0; i < allSuits.length; ++i)
         if (suit == allSuits[i])
            return i;

      // invalid suit
      return -1;
   }

}

/*-----------------------------------------------------
 * End Of GUI Card
 *-----------------------------------------------/



/****************************************************************
 * Card
 * imported Card class 
 ***************************************************************/
class Card 
{
   public enum Suit 
   {
      CLUBS, DIAMONDS, HEARTS, SPADES
   };

   // card values
   private char value;
   private Suit suit;
   public static char[] valuRanks = { 'A', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'X' };

   // Checks for illegal card data
   private boolean errorFlag;

   /**
    * Constructor
    * 
    * @param value
    * @param suit
    */
   public Card(char value, Suit suit) 
   {
      set(value, suit);
   }

   /**
    * Overloaded constructor
    */
   public Card() 
   {
      this('A', Suit.SPADES);
   }

   /**
    * Copy Constructor
    * 
    * @param otherCard
    */
   public Card(Card otherCard) 
   {
      if (otherCard == null) 
      {
         System.out.println("Fatal Error!");
         System.exit(0);
      }
      set(otherCard.value, otherCard.suit);
   }

   // Accessors
   public Suit getSuit() 
   {
      return suit;
   }

   public char getValue() 
   {
      return value;
   }

   public boolean getErrorFlag() 
   {
      return errorFlag;
   }

   // Mutators
   public boolean set(char value, Suit suit) 
   {
      if (isValid(value, suit)) 
      {
         this.value = value;
         this.suit = suit;
         errorFlag = false;
         return true;
      }
      errorFlag = true;
      return false;
   }

   // Valid card data returned
   public String toString() 
   {
      if (errorFlag)
         return "[Card Not Valid]";
      return value + " of " + suit;

   }

   // Returns true if all field members are identical, false otherwise
   public boolean equals(Card card) 
   {
      if (card == null)
         return false;

      // comparing member values
      if (this.value == card.value) 
      {
         if (this.suit == card.suit)
            return true;
      }
      return false;
   }

   // Determine validity for the value
   private boolean isValid(char value, Suit suit) 
   {
      // checks if value is in the field of valid values
      for (int i = 0; i < valuRanks.length; ++i) 
      {
         if (value == valuRanks[i])
            return true;
      }
      // invalid value
      return false;
   }

   public static void arraySort(Card[] cArray, int arraySize) 
   {
      for (int i = 0; i < arraySize - 1; i++) 
      {
         for (int j = 0; j < arraySize - i - 1; j++) 
         {
            if (cardAsInt(cArray[j]) > cardAsInt(cArray[j + 1])) 
            {
               Card temp = cArray[j];
               cArray[j] = cArray[j + 1];
               cArray[j + 1] = temp;
            }
         }
      }
   }

   /**
    * It returns an integer according to the value
    * 
    * @param card
    * @return -1 if the value is not valid
    */
   public static int valueAsInt(Card card) 
   {
      for (int i = 0; i < valuRanks.length; i++) 
      {
         if (valuRanks[i] == card.getValue()) 
         {
            return i;
         }
      }
      // Not matching any values in the ranks
      return -1;
   }

   /**
    * It returns the suit position as an integer
    * 
    * @param card
    * @return an integer
    */
   public static int suitAsInt(Card card) 
   {
      return card.getSuit().ordinal();
   }

   /**
    * It returns a specific integer for a card according to the value and the suit
    * 
    * @param card
    * @return an integer for the card
    */
   public static int cardAsInt(Card card) 
   {
      int total = Card.valueAsInt(card) * 4 + Card.suitAsInt(card);
      return total;
   }
}

/*-----------------------------------------------------
 * End Of Card
 *----------------------------------------------/


 /****************************************************************
 * Hand 
 * imported Hand class 
 ***************************************************************/
class Hand 
{
   // a hand can only have 52 cards maximum
   public static final int MAX_CARDS = CardTable.MAX_CARDS_PER_HAND;

   private Card[] myCards;
   private int numCards;

   // Constructor
   public Hand() 
   {
      myCards = new Card[MAX_CARDS];
      numCards = 0;
   }

   /**
    * resets hand
    */
   public void resetHand() 
   {
      numCards = 0;
   }

   /**
    * Adds a card to the next available position
    * 
    * @param card
    * @return boolean
    */
   public boolean takeCard(Card card) 
   {
      if (numCards < MAX_CARDS && card != null && !card.getErrorFlag()) 
      {
         Card tempCard = new Card(card);
         myCards[numCards] = tempCard;
         numCards++;
         return true;
      }
      return false;
   }

   // stringifies the hand
   public String toString() 
   {
      String myHand = "";
      if (numCards > 0) 
      {

         myHand += myCards[0];
         for (int i = 1; i < numCards; i++) 
         {
            myHand += " , " + myCards[i];
         }

      }
      return "Hand = " + "(" + myHand + ")";
   }

   // Accessor
   public int getNumCards() 
   {
      return numCards;
   }

   // Accessor
   public Card inspectCard(int k) 
   {

      if (k >= 0 && k < numCards) // assume valid k starts from 0
      {
         Card aCard = new Card(myCards[k]); // prevent privacy leaks
         return aCard;
      }

      // return a dummy invalid card
      Card badCard = new Card(' ', Card.Suit.SPADES);
      return badCard;

   }

   /**
    * Sort the cards in the hand in ascending order
    */
   public void sort() 
   {
      Card.arraySort(myCards, numCards);
   }

   /**
    * Return a card on the top of the hand
    * 
    * @return a good card if there is any
    */
   public Card playCard() 
   {
      if (numCards > 0) 
      {
         numCards--;
         return myCards[numCards];
      }

      // No more cards, return a card that does not work
      return new Card('M', Card.Suit.SPADES);

   }

   /**
    * Remove a card from the given location and slide down cards behind down one
    * spot
    * 
    * @param cardIndex the location where the card is removed from the hand
    * @return the card at the given location or a bad card if location is invalid
    */
   public Card playCard(int cardIndex) 
   {
      if (cardIndex < 0 || cardIndex >= numCards) // out of bound error
      {
         // Creates a card that does not work
         return new Card('M', Card.Suit.SPADES);
      }
      // Decreases numCards.
      Card card = myCards[cardIndex];
      numCards--;

      // Slide down cards that followed down one spot
      for (int i = cardIndex; i < numCards; i++) 
      {
         myCards[i] = myCards[i + 1];
      }

      myCards[numCards] = null;

      return card;
   }

}
/*-----------------------------------------------------
 * End Of Hand
 *----------------------------------------------/


 /****************************************************************
 * Deck import
 * imported Deck class
 ***************************************************************/

class Deck 
{
   // playing card pack values
   public static final int MAX_CARDS = 312; // 6 packs x 52 cards
   public static final int ONE_PACK = 52; // standard 52 cards
   public static boolean beenHereBefore = false;

   // creates a new object with one pack of cards
   private static Card[] masterPack = new Card[ONE_PACK];

   private Card[] cards; // cards in the deck
   private int topCard; // keep track of number of cards in the deck
   private int numPacks; // keep track of number of packs in the deck

   /**
    * Constructor that takes in a number of packs as an argument and then creates a
    * deck of cards with that many packs of cards (56 x numPacks)
    * 
    * @param numPacks
    */
   public Deck(int numPacks) 
   {
      // maximum packs is 6
      if ((numPacks * ONE_PACK) > MAX_CARDS)
         numPacks = 6;

      this.numPacks = numPacks;

      allocateMasterPack();

      // create the cards array with (52+4) x numPacks cards
      // standard is 52 cards but possible add 4 jokers per pack
      cards = new Card[numPacks * (ONE_PACK + 4)];

      // populate the cards in the deck
      init(numPacks);
   }

   /*
    * Overloaded no argument constructor that creates a pack of cards using just
    * one deck
    */
   public Deck() 
   {
      this.numPacks = 1;

      allocateMasterPack();

      // Create the cards array using one pack of cards
      cards = new Card[ONE_PACK + 4]; // add 4 joker spaces

      // Initialize the last index of the array to be the top card of the deck
      topCard = ONE_PACK; // no jokers that will be added if needed

      // Loop through the cards array, populating it with Cards
      for (int i = 0; i < topCard; i++) 
      {
         // Create a new Card Object, copying it from the masterPack
         cards[i] = new Card(masterPack[i]);
      }
   }

   /**
    * Method to re-populate cards[] with 52 x numPacks cards.
    * 
    * @param numPacks
    */
   public void init(int numPacks) 
   {

      // Initialize the last index of the array to be the top card of the deck
      topCard = numPacks * ONE_PACK; // no jokers that can be added when needed

      this.numPacks = numPacks;

      // Populate the card array with Card objects, copying values from
      // masterPack
      for (int masterCounter = 0, i = 0; i < topCard; i++, masterCounter++) 
      {
         // Create a new Card Object, copying it from the masterPack
         cards[i] = new Card(masterPack[masterCounter]);

         // If the cards array is more than one pack, reset the index of
         // masterPack
         // in order to loop through it again
         if (masterCounter == ONE_PACK - 1)
            masterCounter = -1;
      }
   }

   /**
    * Shuffles the deck of Cards
    */
   public void shuffle() 
   {
      Random shuffle = new Random();
      Card tempCard;
      int randCard;

      // loops through the entire deck
      for (int x = 0; x < topCard; x++) 
      {
         // Picks a random card from the deck
         randCard = shuffle.nextInt(ONE_PACK);
         // assigns the random card to a placeholder
         tempCard = cards[randCard];
         // assigns the random card to the next card in the deck
         cards[randCard] = cards[x];
         // assigns the next card in the deck to the card in
         // the place holder
         cards[x] = tempCard;
      }
   }

   /**
    * Deals a card by taking the top of the deck and makes sure there are still
    * cards available.
    * 
    * @return the top Card from the deck.
    */
   public Card dealCard() 
   {
      Card dealCard;

      // checks if there are cards in the deck
      if (topCard > 0) 
      {
         // assigns the top card to the dealCard variable
         dealCard = inspectCard(topCard - 1);

         // removes the topcard from the deck
         cards[topCard - 1] = null;

         // decreases card count
         topCard--;
         return dealCard;
      }
      // returns null if no more cards
      return null;
   }

   /**
    * Returns the number of cards in a deck.
    * 
    * @return the number of cards in the deck
    */
   public int getTopCard() 
   {
      return topCard;
   }

   /**
    * Accessor for an individual card. Returns a card or returns a card with an
    * error flag.
    * 
    * @return the card at index k
    * @return a card with with an error flag
    */
   public Card inspectCard(int k)
   {
      Card returnCard;

      // If k is out of bounds, return a card with an error flag
      if (k < 0 || k >= topCard)
      {
         // Create an invalid card with errorFlag = true
         returnCard = new Card('E', Card.Suit.CLUBS);
      }
      else
      {
         // Otherwise return the card at k index
         returnCard = cards[k];
      }
      return returnCard;
   }

   /**
    * This method creates new cards and fills the masterPack.
    */
   private static void allocateMasterPack()
   {
      // Check if this method has already been run. Return if it has.
      if (beenHereBefore)
         return;

      char[] value =
      { 'A', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K' };

      Card.Suit[] suits =
      { Card.Suit.CLUBS, Card.Suit.DIAMONDS, Card.Suit.HEARTS,
         Card.Suit.SPADES };

      int curIndex = 0;

      // Loop through the value array
      for (int x = 0; x < value.length; x++)
      {
         // Loop through the suits array
         for (int y = 0; y < suits.length; y++)
         {
            // Create a new Card object with the correct suit and value
            masterPack[curIndex] = new Card(value[x], suits[y]);
            curIndex++;
         }
      }

      beenHereBefore = true;
   }

   /**
    * Adds the card to the top of the deck if there aren't too many instances
    * 
    * @param card the card to be added
    * @return false if the card is already there or no rooms, otherwise return
    *         true
    */
   public boolean addCard(Card card)
   {
      if (cards.length == topCard)
      {
         return false; // no room for the card
      }
      
      int ctr = 0; // counter for the instances of the card
      for (int i = 0; i < topCard; i++)
      {
         if (cards[i].equals(card))
         {
            ctr++;
         }
      }

      if(ctr < numPacks)
      {
         // number of instances not exceeding the numPacks
         cards[topCard] = card; // add the card to the deck
         topCard++;
         return true;
      }
      return false;
   }

   /**
    * Remove one instance of a specific card from the deck
    * 
    * @param card the specific card
    * @return true if success, otherwise false
    */
   public boolean removeCard(Card card)
   {

      // remove only one instance of the card
      for (int i = 0; i < topCard; i++)
      {
         if (cards[i].equals(card))
         {
            cards[i] = cards[topCard - 1];
            cards[topCard - 1] = null;
            topCard--;
            return true;
         }
      }
      return false;
   }

   public void sort()
   {
      Card.arraySort(cards, topCard);
   }

   public int getNumCards()
   {
      return topCard;
   }
}

/*-----------------------------------------------------
 * End Of Deck
 *----------------------------------------------*/
