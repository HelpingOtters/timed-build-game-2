import javax.swing.*;
import java.awt.*;

/************************************************************
 * Phase 1
 * @author Lindsey Reynolds
 * @version November 26, 2019
 * Description: Class to retrieve card icons from .gif files and 
 * display them. 
 *    
 ************************************************************/

public class Phase1
{  
   static final int NUM_CARD_IMAGES = 57; // 52 + 4 jokers + 1 back-of-card image
   static Icon[] icon = new ImageIcon[NUM_CARD_IMAGES];
      
   static void loadCardIcons()
   {
      String[] values = 
      {"A", "2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K", "X"};
      String[] suits = {"C", "D","H", "S"};
      String imageName = "";
      String fileString = "";
      int arrayIndex = 0;

      // loop through number of values, 0 - 13, which will be converted to card values
      for(int valueIndex = 0; valueIndex < values.length; valueIndex++)
      {
         // loop through number of suits, 0 - 3, which will be converted to card suits
         for(int suitIndex = 0; suitIndex < suits.length; suitIndex++, arrayIndex++){
            imageName = values[valueIndex] + suits[suitIndex];               
            fileString = "images/" + imageName + ".gif";
            icon[arrayIndex] = new ImageIcon(fileString);
         }
      }
      // add in the blank card icon
      icon[NUM_CARD_IMAGES - 1] = new ImageIcon("images/BK.gif");
   }
   
   // a simple main to throw all the JLabels out there for the world to see
   public static void main(String[] args)
   {
      int k;
      
      // prepare the image icon array
      loadCardIcons();
      
      // establish main frame in which program will run
      JFrame frmMyWindow = new JFrame("Card Room");
      frmMyWindow.setSize(1150, 650);
      frmMyWindow.setLocationRelativeTo(null);
      frmMyWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
      // set up layout which will control placement of buttons, etc.
      FlowLayout layout = new FlowLayout(FlowLayout.CENTER, 5, 20);   
      frmMyWindow.setLayout(layout);
      
      // prepare the image label array
      JLabel[] labels = new JLabel[NUM_CARD_IMAGES];
      for (k = 0; k < NUM_CARD_IMAGES; k++)
         labels[k] = new JLabel(icon[k]);
      
      // place your 3 controls into frame
      for (k = 0; k < NUM_CARD_IMAGES; k++)
         frmMyWindow.add(labels[k]);

      // show everything to the user
      frmMyWindow.setVisible(true);
      
   }
}

