#Phase 2: Encapsulating Layount and Icons into CardTable and GUICard Classes
The second part creates a separate CardTable class that extends JFrame. This class will control the positioning of the panels and cards of the GUI. We also create a new GUICard class that manages the reading and building of the card image Icons. As a result, some of the machinery and statics that we debugged in the first phase of the main will be moved into one or the other of these two new classes.

CardTable Class (subclassed from JFrame)
Include five members:

   static int MAX_CARDS_PER_HAND = 56;
   static int MAX_PLAYERS = 2;  // for now, we only allow 2 person games
   
   private int numCardsPerHand;
   private int numPlayers;

   public JPanel pnlComputerHand, pnlHumanHand, pnlPlayArea;
 

These members are needed is to establish the grid layout for the JPanels, the organization of which depends on how many cards and players will be displayed. We'll provide an accessor, but no mutator (other than the constructor) for these members.  Here are the public instance methods:

CardTable(String title, int numCardsPerHand, int numPlayers) - The constructor filters input, adds any panels to the JFrame, and establishes layouts according to the general description below.
Accessors for the two instance members.
Note: We will use three Public JPanels, one for each hand (player-bottom and computer-top) and a middle "playing" JPanel.  The client (below) will generate the human's cards at random and will be visible in the bottom JPanel, while the computer's cards will be chosen (again, by the client) to be all back-of-card images in the top JPanel.  The middle JPanel will display cards that are "played" by the computer and human during the conflict.  Let's assume that each player plays one card per round, so for a 2-person game (computer + human) there will be exactly two cards played in the central region per round of battle.  My client chose a joker for the two central cards, just so we would have something to see in the playing region. 

#GUICard Class
This class is the benefactor of most of the GUI machinery we tested in Phase 1. It will read the image files and store them in a static Icon array. Rather than a 1-D array of Phase 1, this will be a 2-D array to facilitate addressing the value and suit of a Card in order get its Icon. While simple in principle (just read the Icons and store them in an array for client use), the details are subtle. We have to be able to convert from chars and suits to ints, and back again, in order to find the Icon for any given Card object. The overview of the class data and methods, shown below, will suggest the right approach and should take the mystery out of this class.

#Include three members:

 private static Icon[][] iconCards = new ImageIcon[14][4]; // 14 = A thru K + joker
 private static Icon iconBack;
 static boolean iconsLoaded = false;
The 52 + 4 jokers Icons will be read and stored into the iconCards[][] array.  The card-back image in the iconBack member.  None of these data need to be stored more than once, so this is a class without instance data.  This class is used is to produce an image icon when the client needs one. 

To begin, we need a method that generates the image Icon array from files:

static void loadCardIcons() - the code for this was fundamentally done in Phase 1.  The difference here is that we are storing the Icons in a 2-D array.  Don't require the client to call this method.  Think about where you would need to call it and how can you avoid having the method reload the icons after it has already loaded them once.  Hint:  Call this method any time you might need an Icon, but make sure that it loads the entire array the first time it is called, and does nothing any later time.
The primary public method offered by this class:

static public Icon getIcon(Card card) - This method takes a Card object from the client, and returns the Icon for that card.  It would be used when the client needs to instantiate or change a JLabel. It can return something like:
return iconCards[valueAsInt(card)][suitAsInt(card)];
There is another method that returns the card-back image:

static public Icon getBackCardIcon() - this one is even simpler than getIcon().
The above three methods comprise the essential part of the GUICard class.  Everything else is support for these three, so you can work off my implied suggestions, or you can build the class from scratch as you wish.  Just make sure you are efficient.

#Code From Prior Assignments
We are going to use the classes from Module 3 (Card, Hand, and Deck) but we need to add a couple of things.

#Card class

Adjust for the joker. (Even though there are 4 card icons, think of them as one type, X )
We need a way to know which card is Lower when we compare them for the game later.  Create the following array and method(s):

public static char[] valuRanks - put the order of the card values in here with the smallest first, include 'X' for a joker
static void arraySort(Card[], int arraySize) - will sort the incoming array of cards using a bubble sort routine.  You can break this up into smaller methods if it gets over 20 lines or so.
Hand class

#Add a sort method:

void sort() - it will sort the hand by calling the arraySort() method in the Card class.
In order for playCard() to work in the cardGameFramework class, add the following to the Hand class.  This will remove the card at a location and slide all of the cards down one spot in the myCards array.  (We will use this next week)

   public Card playCard(int cardIndex)
   {
      if ( numCards == 0 ) //error
      {
         //Creates a card that does not work
         return new Card('M', Card.Suit.spades);
      }
      //Decreases numCards.
      Card card = myCards[cardIndex];
      
      numCards--;
      for(int i = cardIndex; i < numCards; i++)
      {
         myCards[i] = myCards[i+1];
      }
      
      myCards[numCards] = null;
      
      return card;
    }

#Deck class

Adjust for the joker in the MasterPack.
Add methods for adding and removing cards from the deck as well as a sort method. (these will be using in the CardGameFramework given in Phase 3)

boolean addCard(Card card) - make sure that there are not too many instances of the card in the deck if you add it.  Return false if there will be too many.  It should put the card on the top of the deck.
bollean removeCard(Card card) - you are looking to remove a specific card from the deck.  Put the current top card into its place.  Be sure the card you need is actually still in the deck, if not return false.
void sort() - put all of the cards in the deck back into the right order according to their values.  Is there another method somewhere that already does this that you could refer to?
int getNumCards() - return the number of cards remaining in the deck.
Main class

You will also need a method that will give you a random new card.  It is a main class method.  

static Card randomCardGenerator() - returns a new random card for the main to use in its tests.
  

#Client for Phase 2
The main class needs to define the specific JLabel arrays that will go into each of CardTable's JPanels. You will need NUM_CARDS_PER_HAND JLabels for the player and the computer (each), even though the computer only uses one Icon (back-of-card). We also want two Icon JLabels for the central JPanel (these are the two cards played by computer and human, each turn). But we also need to some text below each of the two center icons to we know who played which card ( "Computer" or "You", so, we'll really need four labels in this central play JPanel : two for card images and two for text "Computer" and "You". Since we want the text directly below the icon, one way to do this is to make your central playing panel a 2x2 Grid Layout, where the top two positions will be images and the bottom two will be text that describe the images. Hint: to center text in a label, use

   myLabel = new JLabel( "My Text", JLabel.CENTER );
The net result should be cards we can see (our hand) in the lower JPanel , cards that we can't see -- except for the card backs -- in the upper JPanel (the computer's hand) and a central playing region which would represent two cards, one each played by the user and the computer. These two cards depend on what game we are playing, the rules, and the goal.  Based on these two cards played, either we or the computer win that round and then we go on to the next round. For this phase, we don't worry about strategy or rules or winning -- we just want to see two cards in the central JPanel so we know they are correctly positioned for later program development.  Here's a partial picture of a basic solution:


Your job is to simply produce this output using the classes and methods suggested.  Here is an idea for a main() that you can use to get started:

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;


public class Assig5
{
   static int NUM_CARDS_PER_HAND = 7;
   static int  NUM_PLAYERS = 2;
   static JLabel[] computerLabels = new JLabel[NUM_CARDS_PER_HAND];
   static JLabel[] humanLabels = new JLabel[NUM_CARDS_PER_HAND];  
   static JLabel[] playedCardLabels  = new JLabel[NUM_PLAYERS]; 
   static JLabel[] playLabelText  = new JLabel[NUM_PLAYERS]; 
   
   public static void main(String[] args)
   {
      int k;
      Icon tempIcon;
      
      // establish main frame in which program will run
      CardTable myCardTable 
         = new CardTable("CardTable", NUM_CARDS_PER_HAND, NUM_PLAYERS);
      myCardTable.setSize(800, 600);
      myCardTable.setLocationRelativeTo(null);
      myCardTable.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      // show everything to the user
      myCardTable.setVisible(true);

      // CREATE LABELS ----------------------------------------------------
      code goes here ...
  
      // ADD LABELS TO PANELS -----------------------------------------
      code goes here ...
      
      // and two random cards in the play region (simulating a computer/hum ply)
      code goes here ...

      // show everything to the user
      myCardTable.setVisible(true);
   }