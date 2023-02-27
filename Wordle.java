
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 *
 * @author ticus6313
 */
public class Wordle implements Runnable, ActionListener, ChangeListener {

    // Class Variables  
    //for the main window
    JFrame frame;
    JPanel mainPanel;
    JTextField[] letterBox;
    JLabel title;
    JButton howToPlay;
    JButton statistics;
    //for the instructions window
    JFrame instructions;
    JPanel mainInstructions;
    JButton instructionsClose;
    JLabel firstLabel;
    JLabel secondLabel;
    JLabel thirdLabel;
    JLabel example;
    JTextField[] exampleDecorationBox;
    JLabel greenHint;
    JLabel orangeHint;
    JLabel grayHint;
    //for the winning window 
    JFrame winningWindow;
    JPanel winning;
    JLabel winningLabel;
    JLabel guessesLabel;
    JButton winningExit;
    JButton winningRetry;
    //for the losing window
    JFrame losingWindow;
    JPanel losing;
    JLabel losingLabel;
    JButton losingExit;
    JButton losingRetry;
    //for the stats window
    JFrame statsWindow;
    JPanel stats;
    JLabel titleStats;
    JLabel winPercentage;
    JLabel guessDistribution;
    JButton statsClose;

    //create a custom font and colour
    Font bigger = new Font("Monospaced", Font.BOLD, 50);
    Color darkTheme = new Color(23, 23, 23);
    Font text = new Font("Trebuchet", Font.BOLD, 20);
    Color green = new Color(1, 154, 1);
    Color orange = new Color(255, 196, 37);

    Scanner input = new Scanner(System.in);
    //create an array list in order to store all of the strings in the dictionary word file
    //the array list allows it to be undefined, so if the actual text file updates so does the arraylist
    static ArrayList<String> dictionary = new ArrayList<>();

    //check if the letter is already green or not using a boolean array
    boolean[] isGreen = new boolean[5];

    String wordle = "";
    String inputWord = "";

    int randNum = 0;
    int boxNumber = 1;
    int numGuesses = 0;
    int counter = 0;
    int location = 0;
    int wordLength = 5;

    // Method to assemble our GUI
    public void run() {
        // Creats a JFrame that is 800 pixels by 600 pixels, and closes when you click on the X
        frame = new JFrame("WORDLE");
        // Makes the X button close the program
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // makes the windows 800 pixel wide by 600 pixels tall
        frame.setSize(800, 600);
        // shows the window
        frame.setVisible(true);

        //access the file through its link
        URL dictionaryFile;
        Scanner fileIn = null;
        try {
            dictionaryFile = new URL("https://raw.githubusercontent.com/charlesreid1/five-letter-words/master/sgb-words.txt");
            fileIn = new Scanner(dictionaryFile.openStream());
        } catch (MalformedURLException ex) {
            Logger.getLogger(Wordle.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Wordle.class.getName()).log(Level.SEVERE, null, ex);
        }

        //load in all of the words in the text file by going line by line and adding them to the arraylist
        while (fileIn.hasNext()) {
            String word = fileIn.nextLine();
            dictionary.add(word);
        }

        //get a random number in order for the computer to generate a random word
        randNum = (int) (Math.random() * (dictionary.size() - 1) + 1);
        wordle = dictionary.get(randNum).toUpperCase();

        InstructionsWindow();

        WinningWindow();

        LosingWindow();

        StatsWindow();

        //create the main panel
        mainPanel = new JPanel();
        mainPanel.setLayout(null);
        //set the backgound to be black
        mainPanel.setBackground(darkTheme);
        //add the main title
        title = new JLabel("WORDLE");
        title.setFont(new Font("Monospaced", Font.BOLD, 60));
        title.setForeground(Color.WHITE);
        title.setBounds(270, 75, 600, 50);
        mainPanel.add(title);
        //add the button that will display the instructions
        howToPlay = new JButton("?");
        howToPlay.addActionListener(this);
        howToPlay.setFont(new Font("Monospaced", Font.BOLD, 25));
        howToPlay.setForeground(Color.WHITE);
        howToPlay.setActionCommand("instructions");
        howToPlay.setBounds(20, 20, 50, 50);
        howToPlay.setBackground(darkTheme);
        mainPanel.add(howToPlay);
        //add the button that will display the statistics
        statistics = new JButton("%");
        statistics.addActionListener(this);
        statistics.setFont(new Font("Monospaced", Font.BOLD, 25));
        statistics.setForeground(Color.WHITE);
        statistics.setActionCommand("stats");
        statistics.setBounds(715, 20, 50, 50);
        statistics.setBackground(darkTheme);
        mainPanel.add(statistics);

        //create all of the letter boxes
        letterBox = new JTextField[30];
        //use a count variable in order to set the position for all of the boxes
        int count = 0;
        //set their positions
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                letterBox[count] = new JTextField();
                letterBox[count].setBounds(250 + 50 * j, 150 + 50 * i, 50, 50);
                count++;
            }
        }
        for (int i = 0; i < letterBox.length; i++) {
            //add the actions stuff
            letterBox[i].addActionListener(this);
            letterBox[i].setActionCommand("" + i);
            //set the font, color and the center the letter
            letterBox[i].setFont(bigger);
            letterBox[i].setForeground(Color.WHITE);
            letterBox[i].setBackground(darkTheme);
            letterBox[i].setHorizontalAlignment(JTextField.CENTER);
            //letterBox[i].setEnabled(false);
            //add to the panel
            mainPanel.add(letterBox[i]);
        }

        //check if the user is out of guesses
        if (numGuesses == 5) {
            losingWindow.setVisible(true);
        }

        System.out.println(wordle);
        //show the main panel
        frame.add(mainPanel);
    }

    public void CheckWord(String inputWord, String actualWordle) {
        inputWord = inputWord.toLowerCase();
        actualWordle = actualWordle.toLowerCase();

        //check if the letter is in the word and in the correct spot
        for (int i = 0; i < inputWord.length(); i++) {
            if (inputWord.charAt(i) == actualWordle.charAt(i)) {
                isGreen[i] = true;
                letterBox[i + (5 * numGuesses)].setBackground(green);
            }

        }
        //check if the letter is in the word, but not necessarily in the correct spot
        for (int i = 0; i < inputWord.length(); i++) {
            if (!isGreen[i] && actualWordle.contains(inputWord.charAt(i) + "")) {
                letterBox[i + (5 * numGuesses)].setBackground(orange);
            }

        }
        //otherwise set the square to be gray
        for (int i = 0; i < inputWord.length(); i++) {
            if (letterBox[i + (5 * numGuesses)].getBackground() == darkTheme) {
                letterBox[i + (5 * numGuesses)].setBackground(Color.GRAY);
            }

        }
    }

    public void CheckRow() {
        //check if the row is filled
        if (!letterBox[0 + (5 * numGuesses)].getText().equals("")
                && !letterBox[1 + (5 * numGuesses)].getText().equals("")
                && !letterBox[2 + (5 * numGuesses)].getText().equals("")
                && !letterBox[3 + (5 * numGuesses)].getText().equals("")
                && !letterBox[4 + (5 * numGuesses)].getText().equals("")) {

            //check if the inputed word matches with the wordle
            CheckWord(inputWord, wordle);

            if (isGreen[0] && isGreen[1] && isGreen[2] && isGreen[3] && isGreen[4]) {
                //winning screen
                winningWindow.setVisible(true);
            } else {
                //disable the row
                for (int i = 5 * numGuesses; i < letterBox.length - (5 * (5 - numGuesses)); i++) {
                    letterBox[i].setEnabled(false);
                    for (int j = 0; j < 5; j++) {
                        isGreen[j] = false;
                    }
                }
                //otherwise increase the guess count 
                numGuesses++;
                inputWord = "";
            }
        }
    }

    public void RestartGame() {
        //create a new word
        randNum = (int) (Math.random() * (dictionary.size() - 1) + 1);
        wordle = dictionary.get(randNum).toUpperCase();
        System.out.println(wordle);
        //reset all of the boxes to be empty
        for (int i = 0; i < letterBox.length; i++) {
            letterBox[i].setEnabled(true);
            letterBox[i].setText("");
            letterBox[i].setBackground(darkTheme);
        }
    }
    
    public void InstructionsWindow(){
        //set up the instructions window
        instructions = new JFrame("Instructions");
        instructions.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        // makes the windows 800 pixel wide by 600 pixels tall
        instructions.setSize(800, 600);
        // shows the window
        instructions.setVisible(true);

        mainInstructions = new JPanel();
        instructions.add(mainInstructions);
        mainInstructions.setLayout(null);

        //set the backgound to be black
        mainInstructions.setBackground(darkTheme);
        //create the text explaining the purpose of the game
        firstLabel = new JLabel("Guess the WORDLE in six tries.");
        firstLabel.setFont(text);
        firstLabel.setForeground(Color.WHITE);
        firstLabel.setBounds(250, 0, 400, 50);
        mainInstructions.add(firstLabel);
        //create the text explaining how to play
        secondLabel = new JLabel("Each word must be a valid five-letter word.");
        secondLabel.setFont(text);
        secondLabel.setForeground(Color.WHITE);
        secondLabel.setBounds(200, 30, 500, 50);
        mainInstructions.add(secondLabel);
        //create the text explaining how the hints work
        thirdLabel = new JLabel("After each guess, the colour of the tiles will change.");
        thirdLabel.setFont(text);
        thirdLabel.setForeground(Color.WHITE);
        thirdLabel.setBounds(150, 60, 500, 50);
        mainInstructions.add(thirdLabel);
        //example text
        example = new JLabel("example:");
        example.setFont(text);
        example.setForeground(Color.WHITE);
        example.setBounds(150, 90, 500, 50);
        mainInstructions.add(example);
        //create the button that would close the window
        instructionsClose = new JButton("CLOSE");
        instructionsClose.addActionListener(this);
        instructionsClose.setForeground(Color.WHITE);
        instructionsClose.setActionCommand("close");
        instructionsClose.setBounds(130, 500, 500, 30);
        instructionsClose.setBackground(darkTheme);
        mainInstructions.add(instructionsClose);
        //boxes to visually explain the how the hints work
        exampleDecorationBox = new JTextField[5];
        for (int i = 0; i < exampleDecorationBox.length; i++) {
            exampleDecorationBox[i] = new JTextField();
            exampleDecorationBox[i].addActionListener(this);
            exampleDecorationBox[i].setActionCommand("" + i);
            exampleDecorationBox[i].setFont(bigger);
            exampleDecorationBox[i].setForeground(Color.WHITE);
            exampleDecorationBox[i].setBackground(darkTheme);
            exampleDecorationBox[i].setHorizontalAlignment(JTextField.CENTER);
            exampleDecorationBox[i].setBounds(250 + 50 * i, 160, 50, 50);
            exampleDecorationBox[i].setEnabled(false);
            mainInstructions.add(exampleDecorationBox[i]);
        }
        //fill it in with an exmaple word and fill in the squares
        exampleDecorationBox[0].setText("A");
        exampleDecorationBox[0].setBackground(green);
        exampleDecorationBox[1].setText("C");
        exampleDecorationBox[2].setText("U");
        exampleDecorationBox[3].setText("T");
        exampleDecorationBox[3].setBackground(orange);
        exampleDecorationBox[4].setText("E");
        //add text to explain the green hint
        greenHint = new JLabel("The letter A is in the word and in the correct spot.");
        greenHint.setFont(text);
        greenHint.setForeground(Color.WHITE);
        greenHint.setBounds(100, 240, 500, 50);
        mainInstructions.add(greenHint);
        //add text to explain the orange hint
        greenHint = new JLabel("The letter T is in the word but in the incorrect spot.");
        greenHint.setFont(text);
        greenHint.setForeground(Color.WHITE);
        greenHint.setBounds(100, 270, 500, 50);
        mainInstructions.add(greenHint);
        //add text to explain the gray hint
        greenHint = new JLabel("The letters C, U, E are not in the word.");
        greenHint.setFont(text);
        greenHint.setForeground(Color.WHITE);
        greenHint.setBounds(100, 300, 500, 50);
        mainInstructions.add(greenHint);
    }
    
    public void WinningWindow(){
        //set up the winning window
        winningWindow = new JFrame("Winner");
        winningWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // makes the windows 600 pixel wide by 400 pixels tall
        winningWindow.setSize(600, 400);
        // shows the window
        winningWindow.setVisible(false);

        winning = new JPanel();
        winningWindow.add(winning);
        winning.setLayout(null);

        //set the backgound to be black
        winning.setBackground(darkTheme);
        //create the text explaining the purpose of the game
        winningLabel = new JLabel("You've won! The wordle was " + wordle);
        winningLabel.setFont(text);
        winningLabel.setForeground(Color.WHITE);
        winningLabel.setBounds(150, 0, 400, 30);
        winning.add(winningLabel);
        //create the text explaining how to play
        guessesLabel = new JLabel("It took you " + (numGuesses + 1) + " guesses to get the wordle.");
        guessesLabel.setFont(text);
        guessesLabel.setForeground(Color.WHITE);
        guessesLabel.setBounds(100, 90, 500, 30);
        winning.add(guessesLabel);
        //create a retry and exit button
        //**********retry button***********
        winningRetry = new JButton("RETRY");
        winningRetry.addActionListener(this);
        winningRetry.setForeground(Color.WHITE);
        winningRetry.setActionCommand("retry");
        winningRetry.setBounds(150, 300, 100, 30);
        winningRetry.setBackground(darkTheme);
        winning.add(winningRetry);
        //**********exit button***********
        winningExit = new JButton("EXIT");
        winningExit.addActionListener(this);
        winningExit.setForeground(Color.WHITE);
        winningExit.setActionCommand("exit");
        winningExit.setBounds(350, 300, 100, 30);
        winningExit.setBackground(darkTheme);
        winning.add(winningExit);
    }
    
    public void LosingWindow(){
        //set up the losing window
        losingWindow = new JFrame("Loser");
        losingWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // makes the windows 600 pixel wide by 400 pixels tall
        losingWindow.setSize(600, 400);
        // shows the window
        losingWindow.setVisible(false);

        losing = new JPanel();
        losingWindow.add(losing);
        losing.setLayout(null);

        //set the backgound to be black
        losing.setBackground(darkTheme);
        //create the text explaining the purpose of the game
        losingLabel = new JLabel("You've lost! The wordle was " + wordle);
        losingLabel.setFont(text);
        losingLabel.setForeground(Color.WHITE);
        losingLabel.setBounds(130, 50, 400, 30);
        losing.add(losingLabel);
        //create a retry and exit button
        //**********retry button***********
        losingRetry = new JButton("RETRY");
        losingRetry.addActionListener(this);
        losingRetry.setForeground(Color.WHITE);
        losingRetry.setActionCommand("retry");
        losingRetry.setBounds(150, 300, 100, 30);
        losingRetry.setBackground(darkTheme);
        losing.add(losingRetry);
        //**********exit button***********
        losingExit = new JButton("EXIT");
        losingExit.addActionListener(this);
        losingExit.setForeground(Color.WHITE);
        losingExit.setActionCommand("exit");
        losingExit.setBounds(350, 300, 100, 30);
        losingExit.setBackground(darkTheme);
        losing.add(losingExit);
    }
    
    public void StatsWindow(){
        //set up the statistics window
        statsWindow = new JFrame("Statistics");
        statsWindow.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        // makes the windows 600 pixel wide by 400 pixels tall
        statsWindow.setSize(600, 400);
        // shows the window
        statsWindow.setVisible(false);

        stats = new JPanel();
        statsWindow.add(stats);
        stats.setLayout(null);

        //set the backgound to be black
        stats.setBackground(darkTheme);
        //create the title for the statistics
        titleStats = new JLabel("STATISTICS");
        titleStats.setFont(text);
        titleStats.setForeground(Color.WHITE);
        titleStats.setBounds(150, 50, 400, 30);
        stats.add(titleStats);
        //create the text displaying the winning percentage
        winPercentage = new JLabel("WIN % ");
        winPercentage.setFont(text);
        winPercentage.setForeground(Color.WHITE);
        winPercentage.setBounds(100, 100, 400, 30);
        stats.add(winPercentage);
        //create the text displaying the guess distribution
        guessDistribution = new JLabel("GUESS DISTRIBUTION");
        guessDistribution.setFont(text);
        guessDistribution.setForeground(Color.WHITE);
        guessDistribution.setBounds(50, 150, 400, 30);
        stats.add(guessDistribution);
        //create the button that would close the window
        statsClose = new JButton("CLOSE");
        statsClose.addActionListener(this);
        statsClose.setForeground(Color.WHITE);
        statsClose.setActionCommand("close");
        statsClose.setBounds(50, 300, 500, 30);
        statsClose.setBackground(darkTheme);
        statsClose.setVisible(true);
        stats.add(statsClose);
    }

    // method called when a button is pressed
    public void actionPerformed(ActionEvent e) {
        // get the command from the action
        String command = e.getActionCommand();

        //when the close button is pressed, minimize the window
        if (command.equals("close")) {
            instructions.setVisible(false);
            statsWindow.setVisible(false);
            frame.addNotify();
            frame.requestFocus();
        }
        //when the how to play button is pressed the instructions window appears
        if (command.equals("instructions")) {
            instructions.setVisible(true);
        }
        //when the statistics button is pressed the window should appear
        if (command.equals("stats")) {
            statsWindow.setVisible(true);
        }
        //when the exit button is pressed, it stops the game
        if (command.equals("exit")) {
            System.exit(0);
        }
        //when the retry button is pressed it restarts the game 
        if (command.equals("retry")) {
            RestartGame();
            //minimize both of the windows
            winningWindow.setVisible(false);
            losingWindow.setVisible(false);
        }

        //have the focus move from one box to another after the enter key is pressed 
        inputWord = "";
        //get each individual letter from a row and create the word inputed
        for (int i = 5 * numGuesses; i < letterBox.length - (5 * (5 - numGuesses)); i++) {
            letterBox[i].setText(letterBox[i].getText().toUpperCase());
            inputWord += letterBox[i].getText();
        }
        
        CheckRow();

    }

    // Main method to start our program
    public static void main(String[] args) throws MalformedURLException, IOException {
        // Creates an instance of our program
        Wordle gui = new Wordle();
        // Lets the computer know to start it in the event thread
        SwingUtilities.invokeLater(gui);

    }

    

    @Override
    public void stateChanged(ChangeEvent ce) {

        JTextField spot = (JTextField) ce.getSource();

    }

}