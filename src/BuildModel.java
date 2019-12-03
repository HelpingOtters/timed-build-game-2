import java.util.Random;
import javax.swing.*;

/****************************************************************
 * BuildModel
 * 
 * Description: Creates the model for a game called "BUILD" Usage: Manages the
 * data of the card game
 *****************************************************************/

public class BuildModel
{
   private static final int MAX_PLAYERS = 50;
   private static final int NUM_STACKS = 3; // only 3 stacks for the game

   private int numPlayers;
   private int numPacks; // # standard 52-card packs per deck

   private int numJokersPerPack; // if 2 per pack & 3 packs per deck, get 6
   private int numUnusedCardsPerPack; // # cards removed from each pack
   private int numCardsPerHand; // # cards to deal each player
   private Card[] unusedCardsPerPack; // an array holding the cards not used

   private Deck deck; // holds the initial full deck and gets

   // smaller (usually) during play
   private Hand[] hand; // one Hand for each player
   public Card[] stack = new Card[NUM_STACKS];

   // Variables to keep track of winnings
   private int computerScore = 0;
   private int humanScore = 0;
   private boolean deckExhausted = false;

   /**
    * Constructor that takes arguments to create a new game
    * 
    * @param numPacks              number of packs in the deck
    * @param numJokersPerPack      number of jokers added to the deck
    * @param numUnusedCardsPerPack number of unused cards removed from the deck
    * @param unusedCardsPerPack    list of unused cards
    * @param numPlayers            number of players
    * @param numCardsPerHand       number of cards to each player initially
    */
   public BuildModel(int numPacks, int numJokersPerPack,
      int numUnusedCardsPerPack, Card[] unusedCardsPerPack, int numPlayers,
      int numCardsPerHand)
   {

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
      if (numCardsPerHand < 1 || numCardsPerHand > numPacks * (52
         - numUnusedCardsPerPack) / numPlayers)
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
      initGame();

   }

   /**
    * constructor override/default for game
    */
   public BuildModel()
   {
      // only one pack, no jokers, no unused cards
      // only two players and each starts with seven cards
      this(1, 0, 0, null, 2, 7);
   }

   /**
    * Loads all the card icons to be used later and deals cards to each hand and
    * the stacks
    */
   public void startNewGame()
   {
      GUICard.loadCardIcons();

      dealToHand(); // deal cards to hands
      dealToStack(); // deal cards to the stacks
   }

   /**
    * start a new deck and reset hands. Shuffle the deck.
    */
   public void initGame()
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

   /**
    * deal the specified number of cards to each hand
    * 
    * @return true if successful, false if not enough cards
    */
   public boolean dealToHand()
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
            else
            {
               enoughCards = false;
               break;
            }
      }

      return enoughCards;
   }

   /**
    * Deal cards to each stack
    * 
    * @return true if successful, false if not enough cards
    */
   public boolean dealToStack()
   {
      for (int i = 0; i < NUM_STACKS; i++)
      {
         if (deck.getNumCards() > 0)
         {
            stack[i] = deck.dealCard();
         }
         else
         {
            // set the exhausted flag since no more cards in the deck
            deckExhausted = true;
            return false;
         }

      }
      return true;
   }

   /**
    * Retrieves all the icons associated with the player's current hand
    * 
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
         currCard = getHand(playerIndex).inspectCard(card);
         playerIcons[card] = GUICard.getIcon(currCard);
      }

      return playerIcons;
   }

   public Icon[] loadStackIcons()
   {
      Icon[] stackIcons = new Icon[NUM_STACKS];

      for (int i = 0; i < NUM_STACKS; i++)
      {
         stackIcons[i] = GUICard.getIcon(stack[i]);
      }

      return stackIcons;
   }

   /**
    * Retrieves the back card icon
    * 
    * @return the icon of the back of the card
    */
   public Icon getBackCardIcon()
   {
      return GUICard.getBackCardIcon();
   }

   /**
    * Play the indicated card for the specified player
    * 
    * @param playerIndex the index to which player
    * @param cardIndex   the index of the card in player's hand
    * @return the card from the player's as indicated
    */
   public Card playCard(int playerIndex, int cardIndex)
   {
      // returns bad card if either argument is bad
      if (playerIndex < 0 || playerIndex > numPlayers - 1 || cardIndex < 0
         || cardIndex > numCardsPerHand - 1)
      {
         // Creates a card that does not work
         return new Card('M', Card.Suit.SPADES);
      }

      // return the card played
      return hand[playerIndex].playCard(cardIndex);

   }

   /**
    * deal a card from the deck to the specified player
    * 
    * @param playerIndex the specified player
    * @return true if successful, false if deck is empty
    */
   public boolean takeCard(int playerIndex)
   {
      // returns false if either argument is bad
      if (playerIndex < 0 || playerIndex > numPlayers - 1)
         return false;

      // Are there enough Cards?
      if (deck.getNumCards() <= 0)
      {
         // deck is empty, set the flag
         deckExhausted = true;
         return false;
      }
      // deck is not empty, deal a card to player
      return hand[playerIndex].takeCard(deck.dealCard());
   }

   /**
    * Checks if the game is over by checking if the flag is set
    * 
    * @return true if the game is over
    */
   public boolean isGameOver()
   {
      if (deckExhausted)
      {
         return true;
      }
      return false;
   }

   /**
    * Increment the score counter of the specified player
    * 
    * @param playerIndex the specified player
    */
   public void addScore(int playerIndex)
   {
      if (playerIndex == 0)
         computerScore++; // increment the computer score

      else if (playerIndex == 1)
         humanScore++; // increment the human score

   }

   /**
    * Retrieves the specified player's total score
    * 
    * @param playerIndex the specified player
    * @return the player's score
    */
   public int getPlayerScore(int playerIndex)
   {
      // Check who won
      if (playerIndex == 0)
         return computerScore; // score of computer

      else if (playerIndex == 1)
         return humanScore; // score of the human

      else
         return -1; // If input is incorrect
   }

   /**
    * Sort the hands of each player
    */
   public void sortHands()
   {
      int k;

      for (k = 0; k < numPlayers; k++)
         hand[k].sort();
   }

   /**
    * Sorts the specified player's hand
    * 
    * @param playerIndex the specified player
    */
   public void sortHand(int playerIndex)
   {
      getHand(playerIndex).sort();
   }

   /**
    * Retrieves the Icon for the specified Card
    * 
    * @param card the specified card
    * @return the icon of the specified card
    */
   public Icon getCardIcon(Card card)
   {
      return GUICard.getIcon(card);

   }

   /**
    * Returns the number of cards in a players hand
    * 
    * @param playerIndex the specified player
    * @return the number of cards
    */
   public int getNumCardsInHand(int playerIndex)
   {
      return getHand(playerIndex).getNumCards();
   }

   /**
    * Return the hand of the specified player
    * 
    * @param k the specified player
    * @return the hand of the player
    */
   public Hand getHand(int k)
   {
      // hands start from 0 like arrays

      // on error return automatic empty hand
      if (k < 0 || k >= numPlayers)
         return new Hand();

      return hand[k];
   }

   /**
    * Get a card from the deck
    * 
    * @return the card if deck is not empty
    */
   public Card getCardFromDeck()
   {
      if (deck.getNumCards() == 0)
      {
         // set the flag to indicate deck is empty
         deckExhausted = true;
      }

      return deck.dealCard();
   }

   /**
    * Return the number of cards left in the deck
    */
   public int getNumCardsRemainingInDeck()
   {
      return deck.getNumCards();
   }

   /**
    * Return the number of cards of a hand
    */
   public int getNumCardsPerHand()
   {
      return numCardsPerHand;
   }

   /**
    * Return the number of players
    */
   public int getNumPlayers()
   {
      return numPlayers;
   }

   /**
    * Return the stack cards array
    */
   public Card[] getStack()
   {
      return stack;
   }

   /**
    * Replace the card of specified stack with the given card
    * 
    * @return true if successful
    */
   public boolean setStackCard(int stackIndex, Card card)
   {
      if (stackIndex < NUM_STACKS && stackIndex >= 0)
      {
         // replace the card of the specified stack
         stack[stackIndex] = card;
         return true;
      }
      return false;
   }

}

/*-----------------------------------------------------
 * End Of BuildModel class
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
   private static char[] cardSuits =
   { 'C', 'D', 'H', 'S' };

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
            filename = "src/images/" + Card.valuRanks[i] + cardSuits[j]
               + ".gif";
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
   public static char[] valuRanks =
   { 'A', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'X' };

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
    * It returns a specific integer for a card according to the value and the
    * suit
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
   public static final int MAX_CARDS = 52;

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
    * @return the card at the given location or a bad card if location is
    *         invalid
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
    * Constructor that takes in a number of packs as an argument and then
    * creates a deck of cards with that many packs of cards (56 x numPacks)
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

      if (ctr < numPacks)
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

/**
 * Counter class: multi-thread of the timer
 */
class Counter extends Thread
{
   private int sec = 0;
   private boolean threading = true;
   JLabel timerLabel;

   /**
    * default constructor calls the constructor of the Thread class
    */
   public Counter()
   {
      super();
   }

   /**
    * constructor allows the caller to initialize the thread with a start time.
    * "pause" illusion
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
         // timer restarts to 0 after 99min
         if (this.sec < 6000)
         {
            this.sec += 1;
         }
         else
         {
            this.sec = 0;
         }
         // JLabel text
         timerLabel.setText(timeFormat(sec));

         doNothing(1000); // 1000 millisecond pause

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

   public void setJLabel(JLabel timerLabel)
   {
      this.timerLabel = timerLabel;
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
   public String timeFormat(int totalSeconds)
   {
      int minutes = totalSeconds / 60;
      int sec = totalSeconds - (minutes * 60);
      String formattedTime = String.format("%02d", minutes) + ":" + String
         .format("%02d", sec);
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