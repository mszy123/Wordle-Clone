import java.awt.Color;
import java.util.Random;
import java.util.Scanner;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.awt.event.KeyEvent;
import java.io.*;

public class GameLogic{
   
      
   //Name of file containing all the possible "secret words"
   private static final String SECRET_WORDS_FILENAME = "secrets.txt";   
   
   //Name of file containing all the valid guess words
   private static final String VALID_GUESSES_FILENAME = "valids.txt";   
   
   //Use for generating random numbers!
   private static final Random rand = new Random();
   
   //Dimensions of the game grid in the game window
   public static final int MAX_ROWS = 6;
   public static final int MAX_COLS = 5;
   
   //Character codes for the enter and backspace key press
   public static final char ENTER_KEY = KeyEvent.VK_ENTER;
   public static final char BACKSPACE_KEY = KeyEvent.VK_BACK_SPACE;
   
   //The null character value (used to represent an "empty" value for a spot on the game grid)
   public static final char NULL_CHAR = 0;
   
   //Various Color Values
   private static final Color CORRECT_COLOR = new Color(53, 209, 42); //(Green)
   private static final Color WRONG_PLACE_COLOR = new Color(235, 216, 52); //(Yellow)
   private static final Color WRONG_COLOR = Color.DARK_GRAY; //(Dark Gray [obviously])
   private static final Color DEFAULT_KEY_COLOR = new Color(160, 163, 168); //(Light Gray)
   

   
   //A preset, hard-coded secret word to be use when the resepective debug is enabled
   private static final char[] DEBUG_PRESET_SECRET = {'L', 'O', 'G', 'I', 'C'};      
   
   
   //...Feel free to add more **FINAL** variables of your own!
      
   
   
   
   
   //******************   NON-FINAL GLOBAL VARIABLES   ******************
   //********  YOU CANNOT ADD ANY ADDITIONAL NON-FINAL GLOBALS!  ******** 
   
   
   //Array storing all valid guesses read out of the respective file
   private static String[] validGuesses;
   
   //The current row/col where the user left off typing
   private static int currentRow, currentCol;
      
   
   //*******************************************************************
   
   // converts string to char array
   public static char[] convertToCharList(String word){
      char[] charList = new char[word.length()];
      for(int i = 0; i < word.length();i++){
         charList[i] = word.charAt(i);
      }
      return charList;
   }
   
   
   // gets random word from secrets file and returns char list of word
   public static char[] getRandomWord(){
      try{
         File secrets = new File(SECRET_WORDS_FILENAME);
         Scanner fin = new Scanner(secrets);
         int numWords = Integer.parseInt(fin.next());
         int wordIndex = rand.nextInt(numWords)+1; //+1?
         String word = null;
         for (int i = 0; i < wordIndex;i++){ 
            word = fin.next();
         }
         fin.close();
         return convertToCharList(word);
      }
      catch(NumberFormatException nfe){
            System.out.println("First line of file is not a number");
            return null;
      }   
      catch (FileNotFoundException fnf){
         System.out.println("File not found");
         return null;
      }
   
   }

   // sets a list of all valid words
   public static boolean setValidWords(){
      try{
         File valids = new File(VALID_GUESSES_FILENAME);
         Scanner fin = new Scanner(valids);
         validGuesses = new String[Integer.parseInt(fin.nextLine())];
         for(int i = 0; i < validGuesses.length; i++){ 
            validGuesses[i] = fin.next();
         }
         fin.close();
         return true;
      }
      catch(FileNotFoundException fnf){
         System.out.println("File of valids not found");
         return false;
      }
      catch(NumberFormatException nfe){
         System.out.println("First line not number");
         return false;
      }
   }

   
   //This function gets called ONCE when the game is very first launched
   //before the user has the opportunity to do anything.
   //
   //Should perform any initialization that needs to happen at the start of the game,
   //and return the randomly chosen "secret word" as a char array
   //
   //If either of the valid guess or secret words files cannot be read, or are
   //missing the word count in the first line, this function returns null.
   public static char[] initializeGame(){
      if(!setValidWords()){
         return null;
      }
      if (JWordleLauncher.DEBUG_USE_PRESET_SECRET){
         return DEBUG_PRESET_SECRET; 
      }
      else{
         return getRandomWord();
      }
   }
   
   
   
   //Complete your warmup task (Section 3.1.1 part 2) here by calling the requisite
   //functions out of GameGUI.
   //This function gets called ONCE after the graphics window has been
   //initialized and initializeGame has been called.
   public static void warmup(){

     /* 
     GameGUI.setGridChar(0, 0, 'c');
     GameGUI.setGridColor(0, 0, CORRECT_COLOR);

     GameGUI.setGridChar(1, 3, 'o');
     GameGUI.setGridColor(1, 3, WRONG_COLOR);

     GameGUI.setGridChar(3, 4, 's');
     
     GameGUI.setGridChar(5, 4, 'c');
     GameGUI.setGridColor(5, 4, WRONG_PLACE_COLOR);
     */


   }
   

   // Returns true if char guessCh is in the char list of mySecretWord
   public static boolean inWord(char guessCh, char[] mySecretWord, int[] alphabet){
      for (char secretCh : mySecretWord){
         if (guessCh == secretCh){
            if(alphabet[guessCh-'A'] != 0){
            alphabet[guessCh - 'A']--;
            return true;
            }
         }
      }
      return false;
   }

   // checks if current guess is valid word by going through each word in valid word file
   // and retrning true if it is found
   public static boolean isValidWord(char[] word){
      if (JWordleLauncher.DEBUG_ALL_GUESSES_VALID){
         return true;
      }
      for(int i = 0; i < validGuesses.length; i++){  
         char[] charList = convertToCharList(validGuesses[i]);
         if(Arrays.equals(charList, word)){
            return true;
         }
      }
      return false;
   }

   //gets the guess on the current row and returns it as an array of chars
   public static char[] getGuess(){
      char[] guessedWord = new char[MAX_COLS];
      for (int i = 0; i<MAX_COLS; i++){
         char currentChar = GameGUI.getGridChar(currentRow, i);
         guessedWord[i] = currentChar;
      }
      return guessedWord;
   }


   //returns true if whole word correct
   public static boolean greenPass(int[] alphabet, char[] guessedWord, char[] mySecretWord){
      int count = 0;
      for (int i = 0; i<MAX_COLS; i++){
         char currentChar = guessedWord[i];

         if (currentChar == mySecretWord[i]){ //correct letter + spot
            count++;
            GameGUI.setGridColor(currentRow, i, CORRECT_COLOR);
            GameGUI.setKeyColor(currentChar, CORRECT_COLOR);
            alphabet[currentChar - 'A']--;
         }
      }
      return (count == MAX_COLS);
   }

   // checks word to see if characters are in the word, then changes color appropriately
   public static void yellowGreyPass(int[] alphabet, char[] guessedWord, char[] mySecretWord){
      for(int i = 0; i < MAX_COLS; i++){
         if (GameGUI.getGridColor(currentRow, i)!= CORRECT_COLOR){
            
            char currentChar = guessedWord[i];
            if (inWord(currentChar, mySecretWord, alphabet)){ //correct letter + not spot
               if (GameGUI.getKeyColor(currentChar) != CORRECT_COLOR){
                  GameGUI.setKeyColor(currentChar, WRONG_PLACE_COLOR);
               }
               GameGUI.setGridColor(currentRow, i, WRONG_PLACE_COLOR);
            }
            else{ //nothing correct
               GameGUI.setGridColor(currentRow, i, WRONG_COLOR);
               if (GameGUI.getKeyColor(currentChar) != CORRECT_COLOR){
                  GameGUI.setKeyColor(currentChar, WRONG_COLOR);
               }
            } 
         }
      }
   }
    


   // when enter key is pressed, checks each char to see if it is in correct place, in word or not using helper function
   // at end checks if whole word is correct
   // returns true if whole word correct
   public static boolean checkCorrectness(char[] guessedWord){
      char[] mySecretWord = GameGUI.getSecretWordArr();
      int[] alphabet = new int[26];

      for (char ch : mySecretWord){
         alphabet[ch-'A']++;
      }


      if(greenPass(alphabet, guessedWord, mySecretWord)){
         return true;
      }
      yellowGreyPass(alphabet, guessedWord, mySecretWord);
         
      
      if (currentRow != MAX_ROWS){
         currentRow++;
      }
      currentCol = 0;
      
      return false;
   }
      
   
   public static void enterPressed(){
      if (currentCol == MAX_COLS){
         char[] myGuess = getGuess();
         if (isValidWord(myGuess)){
            if (checkCorrectness(myGuess)){
               GameGUI.gameOver(true);
            }
            else if(currentRow == MAX_ROWS){
               GameGUI.gameOver(false);
            }
         }
         else{
            GameGUI.wiggle(currentRow);
         }
      }
      else{
         GameGUI.wiggle(currentRow);
      }
   }


   public static void backspacePressed(){
      if (currentCol != 0){
         currentCol--;
         GameGUI.setGridChar(currentRow, currentCol, NULL_CHAR);
      }
      else if (currentCol == 0){
         GameGUI.wiggle(currentRow);
      }
   }


   //This function gets called everytime the user types a valid key on the
   //keyboard (alphabetic character, enter, or backspace) or clicks one of the
   //keys on the graphical keyboard interface.
   //
   //The key pressed is passed in as a char value.
   public static void reactToKey(char key){ 
      //backspace
      if (key == BACKSPACE_KEY){
         backspacePressed();
      }
      
      //alphabetic character
      if (currentCol<(MAX_ROWS-1) && key != BACKSPACE_KEY && key != ENTER_KEY){
         GameGUI.setGridChar(currentRow, currentCol, key);
         currentCol++;
      }

      //enter
      if (key == ENTER_KEY){
         enterPressed();
      }

       System.out.println("reactToKey(...) called! key (int value) = '" + ((int)key) + "'");
   }
   
   
   
   
   
}
