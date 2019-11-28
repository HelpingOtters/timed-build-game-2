Phase 3: Adding CardGameFramework and creating the game "Low Card"
We will start by just copying the CardGameFramework. Download the file at the bottom of the assignment.  Use it to generate the hands.   It makes sure that cards come in and out smoothly in a deck using the methods that you added to the Card, Hand, and Deck classes.

New for the Main:
Start with something like this:

      numPacksPerDeck = 1;
      numJokersPerPack = 2;
      numUnusedCardsPerPack = 0;
      unusedCardsPerPack = null;

      CardGameFramework LowCardGame = new CardGameFramework( 
            numPacksPerDeck, numJokersPerPack,  
            numUnusedCardsPerPack, unusedCardsPerPack, 
            NUM_PLAYERS, NUM_CARDS_PER_HAND);
Later, you can use method invocations like

   ...   LowCardGame.getHand(1).inspectCard(k)...
to access the human player's cards or the computer's cards for the game below.  So you instantiate a CardGameFramework object, deal the cards, and then read the player's hand, one-card-a-time, producing or updating a JLabel for each card.

The source code so far is exactly the same as Phase 2, except:

You instantiate a CardGameFramework object at the top of main().

You deal() from it (one statement).

In the section where you // CREATE LABELS ...,  instead of using randomCardGenerator() to pick an Icon, you use inspectCard() to do so.

Make sure that it produces the same output as Phase 2 before coding your game below.
  5. "Low-Card" Game

--You are now perfectly positioned to write a game.  You will need an action listener and some rules.  The simplest game would be "low-card" in which you and the computer each play a card, and the Low card takes both (which you place somewhere in a winnings[] array, not your hand).  You have to add JLabels like "You Win" or "Computer Wins".  You need to decide how to select a card from your hand like maybe making each card its own button. Or other ideas??  Also, how does the computer play?  Will you tell it to always try to win by playing the smallest available card that beats yours, and if it can't win, play its smallest card?  Maybe it should intentionally lose certain rounds in order to preserve cards.  You need to decide who plays first (maybe winner of last round?). 

--You need to figure out how to update your cards or the computer's cards to reflect one fewer cards every round so that hands get smaller.  This is the fun part!