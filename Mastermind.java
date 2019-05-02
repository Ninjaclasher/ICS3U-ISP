//Evan Zhang
//Ms Krasteva
//January 15, 2018
//This program is my ISP (Mastermind)

import java.awt.*;
import java.io.*;
import javax.swing.JOptionPane;
import hsa.Console;

//              Class Variables Dictionary
//Name                  Type            Use
//c                     Console         Main console object for GUI
//WIDTH                 final int       Width of the Console   
//HEIGHT                final int       Height of the Console
//ROW                   final int       Number of rows in the Console
//COLUMN                final int       Number of columns in the Console
//beadSize              final int       Size of a bead
//titleSize             final int       Size of the title
//SUCCESS               final int       Return value for when the file was successfully opened/saved
//INVALID_FILENAME      final int       Return value for when an invalid file name was passed as a parameter
//INVALID_FILEDATA      final int       Return value for when the data from the file is not a valid save file
//ARRAY_UNINIT          final int       Return value for when the array has not been initialized when trying to save
//WIN                   final int       Return value for when the user wins the game
//LOSE                  final int       Return value for when the user loses the game
//GIVE_UP               final int       Return value for when the user gives up while playing the game
//QUIT                  final int       Return value for when the user quits the game. This does not reveal the pattern and allows the user to save the game
//colour                final Color[]   An array of colours for the colours of the beads
//BACKGROUND            final Color     The colour of the main background of the Console
//pattern               int[]           The solution for the current game
//scores                int[]           Stores the highscores in memory
//colourCount           int[]           Stores the frequency of the colours in the pattern
//curColour             int[][]         Stores the user's pattern guesses
//highScoreNames        String[]        Stores the names of the users corresponding to each high score
//username              String          The name of the current user
//numColour             int             Stores the number of available colours in the current level
//numBeads              int             Stores the number of beads in the current level
//numGuesses            int             Stores the number of guesses in the current level
//horizontalSpacing     int             Stores the horizontal spacing between 2 beads when playing the game
//verticalSpacing       int             Stores the vertical spacing between 2 beads when playing the game
//numHighScores         int             Stores the number of high scores (max is 10)
//curRow                int             Stores the row of the game the user is currently on
public class Mastermind
{
    //Declares instance variables 
    Console c;
    final int WIDTH, HEIGHT, ROW, COLUMN;
    static final int beadSize = 50, titleSize = 50;
    static final int SUCCESS = 0, INVALID_FILENAME = 1, INVALID_FILEDATA = 2, ARRAY_UNINIT = 3, WIN = 0, LOSE = 1, GIVE_UP = 2, QUIT = 3;
    static final Color[] colour = {new Color(206, 27, 0), new Color(10, 0, 153), new Color(153, 0, 96), new Color(12, 153, 0), Color.PINK, new Color(163, 141, 0)};
    static final Color BACKGROUND = new Color (160, 254, 242);//new Color(255,230,71);
    int[] pattern, scores, colourCount = new int[colour.length];
    int[][] curColour;
    String[] highScoreNames;
    String username;
    int numColour, numBeads, numGuesses, horizontalSpacing, verticalSpacing, numHighScores, curRow;

    //Constructor Mastermind
    //Parameters: none
    //Initalizes a Mastermind game and sets all values to the default
    Mastermind()
    {
        //Initializes the instance variables
        c = new Console(37, 81, 16, "Mastermind - Evan Zhang");
        WIDTH = c.getWidth();
        HEIGHT = c.getHeight();
        ROW = c.getMaxRows();
        COLUMN = c.getMaxColumns();
        numColour = 4;
        numBeads = 4;
        numGuesses = 8;
        numHighScores = -1; 
        username = null;
        horizontalSpacing = (WIDTH-numBeads*beadSize)/(numBeads+2);
        verticalSpacing = (HEIGHT-titleSize*2-numGuesses*beadSize)/(numGuesses+4);
    }

    //Method delay
    //Parameters: int
    //Return type: void
    //Access level: private
    //Delays the program execution in the current thread for "milliseconds" milliseconds
    //          Method Variable Dictionary
    //Name          Type            Use
    //milliseconds  int             Number of milliseconds to delay program execution
    private static void delay(int milliseconds)
    {
        try { Thread.sleep(milliseconds); } catch (InterruptedException ie){}
    }

    //Method lowerBound
    //Parameters: int[], int
    //Return type: int
    //Access level: private
    //Returns the index of the first element of the array whose value is less than or equal to val
    //          Method Variable Dictionary
    //Name          Type            Use
    //arr           int[]           The array to check
    //val           int             The value to compare for
    private static int lowerBound(int[] arr, int val)
    {
        //Iterates through the array and finds the first index
        for (int x = 0; x < arr.length; x++)
            if (arr[x] <= val)
            return x;
        return arr.length;
    }

    //Method title
    //Parameters: none
    //Return type: void
    //Access level: private
    //Draws the title on the screen
    private void title()
    {
        //clears the screen
        c.clear();
        //fills the background with BACKGROUND
        c.setColor(BACKGROUND);
        c.setTextBackgroundColor(BACKGROUND);
        c.fillRect(0, 0, WIDTH, HEIGHT);
        //fills the header with cyan
        c.setColor(new Color(160, 252, 200));
        c.fillRect(0, 0, WIDTH, titleSize*2 - 15);
        //fills the header separator
        c.setColor(new Color(28,10,138));
        c.fillRect(0, titleSize*2-15, WIDTH, 7);
        //draws the title
        c.setColor(new Color(114, 63, 233));
        c.setFont(new Font("arial", Font.BOLD, titleSize*2));
        c.drawString("Mastermind", (int)(2.5*titleSize), titleSize+titleSize/2+5);
        //sets the cursor
        c.setCursor(titleSize/8,1);
    }

    //Method centrePrint
    //Parameters: String
    //Return type: void
    //Access level: private
    //Prints "output" centred on the screen
    //          Method Variable Dictionary
    //Name          Type            Use
    //output        String          The string to output to the Console 
    private void centrePrint(String output)
    {
        c.print("", COLUMN/2-output.length()/2);
        c.print(output);
    }

    //Method centrePrintln
    //Parameters: String
    //Return type: void
    //Access level: private
    //Prints "output" centred on the screen and appends a new line
    //          Method Variable Dictionary
    //Name          Type            Use
    //output        String          The string to output to the Console
    private void centrePrintln(String output)
    {
        centrePrint(output);
        c.println();
    }

    //Method pauseProgram
    //Parameters: none
    //Return type: void
    //Access level: private
    //Pauses the program until the user enters a character
    private void pauseProgram()
    {
        centrePrintln("Press any key to continue...");
        //gets a character
        c.getChar();
    }

    //Method splashScreen
    //Parameters: void
    //Return type: void
    //Access level: private
    //splash screen and initiliazes the high scores while the splash screen is displayed
    //          Method Variable Dictionary
    //Name          Type            Use
    //r             Thread          Stores the splashscreen
    private void splashScreen()
    {
        //clears the screen
        title();
        //clears the title
        c.setColor(new Color(160, 252, 200));
        c.setFont(new Font("arial", Font.BOLD, titleSize*2));
        c.drawString("Mastermind", (int)(2.5*titleSize), titleSize+titleSize/2+5);
        //animates the title sliding into the screen
        for (int x = -titleSize*13; x < (int)(2.5*titleSize); x += 8)
        {
            c.setColor(new Color(114, 63, 233));
            c.drawString("Mastermind", x, titleSize + titleSize/2 + 5);
            delay(35);
            c.setColor(new Color(160, 252, 200));
            c.drawString("Mastermind", x, titleSize + titleSize/2 + 5);
        }
        //draws the title
        title();
        delay(50);
        Thread r = new Thread()
        {
            public void run()
            {
                double yV = -5;
                boolean past = false;
                //animates the balls bouncing
                for (int xPos = curRow*50, yPos = 100, colourPos = (int)(Math.random()*6); !(yPos > HEIGHT && past); yV += 2, yPos += (int)yV)
                {
                    //if it "bounced", change the yV to negative so it bounces "upwards"
                    //if the y velocity is too low, the animation is over, and the beads "falls" off the screen
                    if (yPos >= HEIGHT-beadSize)
                    {
                        past = past || yV < 10;
                        if (!past)
                        {
                            yPos = HEIGHT-beadSize;
                            yV = -yV*0.9;
                        }
                    }
                    //draws the bead
                    synchronized (c)
                    {
                        c.setColor(colour[colourPos]);
                        c.fillOval(xPos, yPos, beadSize, beadSize);
                    }
                    //waits 20 milliseconds
                    delay(20);
                    //clears the bead
                    synchronized (c)
                    {
                        c.setColor(BACKGROUND);
                        c.fillOval(xPos, yPos, beadSize, beadSize);
                    }
                }
            }
        };
        //draws the beads bouncing
        for (curRow = 0; curRow < WIDTH/beadSize - 1; curRow++)
        {
            new Thread(r).start();
            delay(35);
        }
        try 
        {
            r.start();
            r.join();
        }
        catch (InterruptedException ie) {}
        curRow = 0;
        //waits 300 milliseconds
        delay(300);
        //opens the highscores
        switch(open(1, "Mastermind_Highscores.txt"))
        {
            case SUCCESS: break;
            case INVALID_FILENAME:
            case INVALID_FILEDATA:
                scores = null;
                highScoreNames = null;
                numHighScores = 0;
            break;
            default:
                System.err.println("Unknown Status Code. Aborting...");
                System.exit(1);
        }
    }

    //Method save
    //Parameters: int, String
    //Return type: int
    //Access level: private
    //saves the highscores/user game to file
    //          Method Variable Dictionary
    //Name          Type            Use
    //out           PrintWriter     Output object for writing to a file
    //encryptor     int             Randomly generated number to be used to encrypt the user game
    private int save(int choice, String filename)
    {
        try
        {
            //instantiates PrintWriter
            PrintWriter out = new PrintWriter(new FileWriter(filename.split("\n")[0]));       
            //whether to save the game or the high scores
            switch(choice)
            {
                //saves the game
                case 0:
                    //prints the information
                    out.println("----------MASTERMIND GAME----------");
                    out.println(username);
                    out.println(numBeads + " " + numColour + " " + numGuesses);
                    out.println("-------------GAME INFO-------------");
                    int encryptor = (int)(Math.random()*2147483646);
                    //encrypts the rest of the game
                    out.println(encryptor);
                    out.println(~(curRow+1));
                    //prints the current game state
                    out.println(encryptor^=Integer.parseInt(filename.split("\n")[1]));
                    for (int x = 0; x <= curRow; out.println(), x++)
                        for (int y = 0; y < numBeads; y++)
                            out.print((encryptor^=~curColour[x][y]) + " ");
                    for (int x = 0; x < numBeads; x++)
                    out.print((encryptor^=~pattern[x]) + " ");
                    //ends the game save output
                    out.println();
                    out.println("----------END OF GAME INFO---------");
                    out.close();
                break;
                //saves the high scores
                case 1:
                    //if there are no high scores
                    if (numHighScores == 0)
                        return ARRAY_UNINIT;
                    //prints the header
                    out.println("-------MASTERMIND HIGHSCORES-------");
                    out.println(numHighScores);
                    out.println("--------------SCORES---------------");
                    //prints the high scores
                    for (int x = 0; x < numHighScores; x++)
                        out.println(highScoreNames[x] + " " + scores[x]);
                    //prints the footer
                    out.println("---------END OF HIGH SCORES--------");
                    out.close();
                break;
            }
        }
        //if there are any errors
        catch (IOException ioe){ return INVALID_FILENAME;}
        catch (NumberFormatException nfe){ return ARRAY_UNINIT;}
        catch (ArrayIndexOutOfBoundsException aiobe){ return ARRAY_UNINIT;}
        catch (NullPointerException npe){ return ARRAY_UNINIT;}

        return SUCCESS;
    }

    //Method open
    //Parameters: int, String
    //Return type: int
    //Access level: private
    //Opens the high scores or a user game
    //          Method Variable Dictionary
    //Name          Type            Use
    //status        int             The status code to return
    //in            BufferedReader  The input stream to read from
    //tmp           String[]        The temporary String input
    //tmpOption     int             Stores the temporary options
    //encryptor     int             The encryptor key used to encrypt the file
    //time          int             Stores the encrypted time
    private int open(int choice, String filename)
    {
        int status = SUCCESS;
        try
        {
            //instantiates variables
            BufferedReader in = new BufferedReader(new FileReader(filename));
            String[] tmp;
            switch(choice)
            {
                //loads the game
                case 0: 
                    //if header is invalid
                    if (!in.readLine().equals("----------MASTERMIND GAME----------"))
                        return INVALID_FILEDATA;
                    //gets input
                    username = in.readLine();
                    tmp = in.readLine().split(" ");
                    //if three options are not provided
                    if (tmp.length != 3)
                        return INVALID_FILEDATA;
                    int tmpOption;
                    //checks if the options are in the valid range
                    for (int x = 0; x < 3; x++)
                    {
                        tmpOption = Integer.parseInt(tmp[x]);
                        if ((x < 2 && tmpOption > 3 && tmpOption < 7) || (x == 3 && tmpOption > 0 && tmpOption < 9))
                            continue;
                        return INVALID_FILEDATA;
                    }
                    //gets input
                    numBeads = Integer.parseInt(tmp[0]);
                    numColour = Integer.parseInt(tmp[1]);
                    numGuesses = Integer.parseInt(tmp[2]);
                    curColour = new int[numGuesses][numBeads];
                    pattern = new int[numBeads];
                    //if the separator is invalid
                    if (!in.readLine().equals("-------------GAME INFO-------------"))
                        return INVALID_FILEDATA;
                    //gets input
                    int encryptor = Integer.parseInt(in.readLine());
                    curRow = ~Integer.parseInt(in.readLine()) - 1;
                    int time = Integer.parseInt(in.readLine());
                    status = time ^ encryptor;
                    encryptor = time;
                    //if the user quit within the first 16 milliseconds of the game
                    if (status <= ARRAY_UNINIT)
                        status = 16;
                    //gets the beads that the user entered
                    for (int x = 0; x <= curRow; x++)
                    {
                        tmp = in.readLine().split(" ");
                        for (int y = 0; y < numBeads; y++)
                        {
                            curColour[x][y] = ~(encryptor^Integer.parseInt(tmp[y]));
                            if (curColour[x][y] >= numColour || curColour[x][y] < 0)
                                return INVALID_FILEDATA;
                            encryptor = Integer.parseInt(tmp[y]);
                        }
                    }
                    //gets the pattern
                    tmp = in.readLine().split(" ");
                    for (int x = 0; x < numBeads; x++)
                    {
                        pattern[x] = ~(encryptor^Integer.parseInt(tmp[x]));
                        if (pattern[x] >= numColour || pattern[x] < 0)
                            return INVALID_FILEDATA;
                        colourCount[pattern[x]]++;
                        encryptor = Integer.parseInt(tmp[x]);
                    }
                    //if the footer does not math
                    if (!in.readLine().equals("----------END OF GAME INFO---------"))
                        return INVALID_FILEDATA;
                    //recalculate the spacing between the beads
                    horizontalSpacing = (WIDTH-numBeads*beadSize)/(numBeads+2);
                    verticalSpacing = (HEIGHT-titleSize*2-numGuesses*beadSize)/(numGuesses+4);
                break;
                //load high scores
                case 1:
                    //if header is invalid
                    if (!in.readLine().equals("-------MASTERMIND HIGHSCORES-------"))
                        return INVALID_FILEDATA;
                    //get the number of high scores
                    numHighScores = Integer.parseInt(in.readLine());
                    //if there are not high scores
                    if (numHighScores == 0)
                        return INVALID_FILEDATA;
                    //if the separator is invalid
                    if (!in.readLine().equals("--------------SCORES---------------"))
                        return INVALID_FILEDATA;
                    //gets the high scores
                    highScoreNames = new String[numHighScores];
                    scores = new int[numHighScores];
                    for (int x = 0; x < numHighScores; x++)
                    {
                        tmp = in.readLine().split(" ");
                        highScoreNames[x] = "";
                        for (int y = 0; y < tmp.length-1; y++)
                            highScoreNames[x] += tmp[y] + " ";
                        scores[x] = Integer.parseInt(tmp[tmp.length-1]);
                        if (x != 0 && (scores[x] > scores[x-1] || (scores[x] == scores[x-1] && highScoreNames[x-1].compareToIgnoreCase(highScoreNames[x]) > 0)))
                            return INVALID_FILEDATA;
                    }
                    //if the footer does not match
                    if (!in.readLine().equals("---------END OF HIGH SCORES--------"))
                        return INVALID_FILEDATA;
                break;
                default:
                    System.err.println("Unknown Option. Aborting...");
                    System.exit(1);
                break;
            }
            in.close();
        }
        //returns any errors detected
        catch (IOException ioe) { return INVALID_FILENAME; }
        catch (NumberFormatException nfe) { return INVALID_FILEDATA; }
        catch (ArrayIndexOutOfBoundsException aiobe) {return INVALID_FILEDATA; }
        catch (NullPointerException npe) { return INVALID_FILEDATA; }
        return status;
    }

    //Method clearCursor
    //Parameters: int, int
    //Return type: void
    //Access level: private
    //Clears the rest of the console starting at position (row, column)
    private void clearCursor (int row, int column)
    {
        c.setCursor (row, column);
        c.println ("", COLUMN-column-1 + (ROW-row-1)*COLUMN);
        c.setCursor (row, column);
    }

    //Method drawBead
    //Parameters: int, int, int
    //Return type: void
    //Access level: private
    //Draws a bead at the specified row and column with colour[color]
    private void drawBead(int color, int x, int y)
    {
        c.setColor(colour[color]);
        c.fillOval(horizontalSpacing*(x+1) + x*beadSize, titleSize*2 + verticalSpacing*y + y*beadSize, beadSize, beadSize);
    }

    //Method drawThickCircle
    //Parameters: int, int, int, int, int
    //Return type: void
    //Access level: private
    //Draws a thick circle at postion (x, y) using c.drawOval()
    private void drawThickCircle(int x, int y, int diameterX, int diameterY, int thickness)
    {
        //iterates from 0 to thickness
        for (int i = 0; i < thickness; i++)
        {
            c.drawOval(x-i/2, y-i/2, diameterX+i, diameterY+i);
            c.drawOval(x-i/2-1, y-i/2-1, diameterX+i+1, diameterY+i+1);
            c.drawOval(x-i/2, y-i/2-1, diameterX+i, diameterY+i+1);
            c.drawOval(x-i/2+1, y-i/2+1, diameterX+i-1, diameterY+i-1);
        }
    }

    //Method displaySolution
    //Parameters: none
    //Return type: void
    //Access level: private
    //Displays the solution in the middle of the Console
    private void displaySolution()
    {
        //draws the box around the pattern
        c.setColor(new Color(18,42,94));
        c.fillRect((WIDTH + ((numBeads%2) == 0 ? horizontalSpacing : -beadSize))/2 + (-numBeads/2)*(beadSize+horizontalSpacing) - horizontalSpacing/2 - 10, 320, numBeads * (beadSize+horizontalSpacing) + 20, beadSize+40);
        c.setColor(new Color(37,99,244));
        c.fillRect((WIDTH + ((numBeads%2) == 0 ? horizontalSpacing : -beadSize))/2 + (-numBeads/2)*(beadSize+horizontalSpacing) - horizontalSpacing/2, 330, numBeads * (beadSize+horizontalSpacing), beadSize+20);
        //draws the beads
        for (int x = -numBeads/2; x < numBeads/2 + (numBeads%2); x++)
        {
            c.setColor(colour[pattern[x+numBeads/2]]);
            c.fillOval((WIDTH + ((numBeads%2) == 0 ? horizontalSpacing : -beadSize))/2 + x*(beadSize+horizontalSpacing), 340, beadSize, beadSize);
        }
    }

    //Method displayBoard
    //Parameters: int
    //Return type: int[]
    //Access level: private
    //Runs the main Mastermind game and returns the game state
    //              Method Variable Dictionary
    //Name              Type            Use
    //SELECT_COLOUR     Color           Stores the Color used to select a bead
    //HIGHLIGHT_COLOUR   Color           Stores the Color used to highlight the row that the user is currently on
    private int[] displayBoard(int existingScore)
    {
        //initializes selecting colour and highlighting colour
        final Color SELECT_COLOUR = Color.BLACK, HIGHLIGHT_COLOUR = new Color(65, 244, 229);
        title();
        //draws the box surrounding the number of correct beads
        c.setColor(new Color(18,42,94));
        c.fillRect(horizontalSpacing*(numBeads+1) + numBeads*beadSize - beadSize/4 - 6, titleSize*2-6, beadSize + (numBeads > 4 ? (beadSize/2 - 10) : 0) + 22, numGuesses * (beadSize+verticalSpacing) - verticalSpacing + 12);                
        c.setColor(new Color(37,99,244));
        c.fillRect(horizontalSpacing*(numBeads+1) + numBeads*beadSize - beadSize/4, titleSize*2, beadSize + (numBeads > 4 ? (beadSize/2 - 10) : 0) + 10, numGuesses * (beadSize+verticalSpacing) - verticalSpacing);
        c.setCursor(ROW-4, 1);
        //displays the hints at the bottom of the screen
        centrePrintln("HINT");
        centrePrintln("Use the W and S keys to change colours.");
        centrePrintln("Use the A and D keys to select a different bead.");
        centrePrintln("Press Enter to confirm your guess.");        
        long beginTime = System.currentTimeMillis();
        loopStart:
        //for each guess
        for (int curRow = 0; curRow < numGuesses; curRow++)
        {   
            //selected the current row
            c.setColor(new Color(10,48,138));
            c.fillRoundRect((WIDTH + ((numBeads%2) == 0 ? horizontalSpacing : -beadSize))/2 + (-numBeads/2)*(beadSize+horizontalSpacing) - horizontalSpacing - 2, titleSize*2 + verticalSpacing*curRow + curRow*beadSize - 7, numBeads * (beadSize+horizontalSpacing) + 4, beadSize+14, 20, 20);            
            c.setColor(HIGHLIGHT_COLOUR);
            c.fillRoundRect((WIDTH + ((numBeads%2) == 0 ? horizontalSpacing : -beadSize))/2 + (-numBeads/2)*(beadSize+horizontalSpacing) - horizontalSpacing, titleSize*2 + verticalSpacing*curRow + curRow*beadSize - 5, numBeads * (beadSize+horizontalSpacing), beadSize+10, 20, 20);
            //draws the default beads
            for (int x = 0; x < numBeads; x++)
                drawBead(curColour[curRow][x], x, curRow);
            char input = (curRow < this.curRow ? '\n' : '\0');
            int curBead = 0, numCorrectPos = 0, numCorrectColour = 0;
            //while the user is changing the colour of the beads
            while (input != '\n')
            {
                //select the current beads
                c.setColor(SELECT_COLOUR);
                drawThickCircle(horizontalSpacing*(curBead+1) + curBead*beadSize, titleSize*2 + verticalSpacing*curRow + curRow*beadSize, beadSize-1, beadSize-1, 10);
                //waits for user input
                input = Character.toLowerCase(c.getChar());
                //deselect the current bead
                c.setColor(HIGHLIGHT_COLOUR);
                drawThickCircle(horizontalSpacing*(curBead+1) + curBead*beadSize, titleSize*2 + verticalSpacing*curRow + curRow*beadSize, beadSize-1, beadSize-1, 10);
                switch(input)
                {
                    case 'd':
                        //move to the next bead
                        curBead = Math.min(curBead+1, numBeads-1);
                    break;
                    case 'a':
                        //move to the previous beads
                        curBead = Math.max(curBead-1, 0);
                    break;
                    case 'w':
                    case 's':
                        //get the next/previous colour of the current bead
                        curColour[curRow][curBead]+=(input == 'w' ? 1 : -1);
                        curColour[curRow][curBead]+=numColour;
                        curColour[curRow][curBead]%=numColour;
                        drawBead(curColour[curRow][curBead], curBead, curRow);
                    break;
                    case 'c':
                        //"cheat" option
                        title();
                        c.setCursor(14, 1);
                        centrePrintln("The pattern you are solving for is: ");
                        c.println("\n\n\n\n");
                        displaySolution();
                        pauseProgram();
                        beginTime -= 216000;
                        this.curRow = curRow;    
                        curRow = -1;
                        title();
                        //draws the board
                        c.setColor(new Color(18,42,94));
                        c.fillRect(horizontalSpacing*(numBeads+1) + numBeads*beadSize - beadSize/4 - 6, titleSize*2-6, beadSize + (numBeads > 4 ? (beadSize/2 - 10) : 0) + 22, numGuesses * (beadSize+verticalSpacing) - verticalSpacing + 12);                
                        c.setColor(new Color(37,99,244));
                        c.fillRect(horizontalSpacing*(numBeads+1) + numBeads*beadSize - beadSize/4, titleSize*2, beadSize + (numBeads > 4 ? (beadSize/2 - 10) : 0) + 10, numGuesses * (beadSize+verticalSpacing) - verticalSpacing);
                        c.setCursor(ROW-4, 1);
                        //redisplays the hints
                        centrePrintln("HINT");
                        centrePrintln("Use the W and S keys to change colours.");
                        centrePrintln("Use the A and D keys to select a different bead.");
                        centrePrintln("Press Enter to confirm your guess.");                    
                        continue loopStart;
                    case 'g':
                        //the user gives up
                        displaySolution();
                    return new int[] {GIVE_UP, -1};
                    case 'q':
                        //the user quits but may want to resume the game
                        this.curRow = curRow;
                    return new int[] {QUIT, existingScore + (int)(System.currentTimeMillis()-beginTime)};
                }
            }
            //count the colour frequency in the current quess and the number of beads in the current positions
            int[] tmpCount = new int[colour.length];
            for (int x = 0; x < numBeads; x++)
            {
                if (curColour[curRow][x] == pattern[x])
                    numCorrectPos++;
                tmpCount[curColour[curRow][x]]++;
            }
            //get how many beads are the current colour
            for (int x = 0; x < colour.length; x++)
                numCorrectColour += Math.min(tmpCount[x], colourCount[x]);
            //if the beads are all correct
            if (numCorrectPos == numBeads)
            {
                //calculate the score 
                int thisScore = (int)(2.0/(System.currentTimeMillis()-beginTime+existingScore) * 120000 * Math.pow(numBeads, 1.4) * Math.pow(numColour, 1.2) * (8.0/Math.pow(curRow+1, 1.0/3.0)));
                //if there are already highscores
                if (numHighScores > 0)
                {
                    //get where to insert the score
                    int idx = lowerBound(scores, thisScore);
                    while (idx < numHighScores && scores[idx] == thisScore && highScoreNames[idx].compareToIgnoreCase(username) < 0)
                        idx++;
                    //if the index to insert is less than 10
                    if (idx < 10)
                    {
                        numHighScores = Math.min(numHighScores+1, 10);
                        //copies the scores
                        String[] tmpNames = highScoreNames;
                        int[] tmpScores = scores;
                        highScoreNames = new String[numHighScores];
                        scores = new int[numHighScores];
                        //reassigns the scores
                        for (int x = 0; x < idx; x++)
                        {
                            highScoreNames[x] = tmpNames[x];
                            scores[x] = tmpScores[x];
                        }
                        //copy the current score
                        highScoreNames[idx] = username;
                        scores[idx] = thisScore;
                        //copy the rest of the scores
                        for (int x = idx+1; x < numHighScores; x++)
                        {
                            highScoreNames[x] = tmpNames[x-1];
                            scores[x] = tmpScores[x-1];
                        }
                    }
                }
                //no highscores
                else
                {
                    //insert this highscore
                    scores = new int[1];
                    highScoreNames = new String[1];
                    scores[0] = thisScore;
                    highScoreNames[0] = username;
                    numHighScores = 1;
                }
                //save the highscores to file
                save(1, "Mastermind_Highscores.txt");
                //the user wins
                return new int[] {WIN, thisScore};
            }
            for (int x = 0, posX = 0, posY = 0; x < numCorrectColour; x++)
            {
                //draws the number of correct beads on the screen
                c.setColor(Color.BLACK);
                c.drawOval(horizontalSpacing*(numBeads+1) + numBeads*beadSize + posX*(beadSize/2)-5, 
                titleSize*2 + verticalSpacing*curRow + beadSize*curRow + posY*(beadSize/2) + 5, beadSize/2-6, beadSize/2-6);
                if (x < numCorrectPos)
                    c.fillOval(horizontalSpacing*(numBeads+1) + numBeads*beadSize + posX*(beadSize/2)-5, 
                        titleSize*2 + verticalSpacing*curRow + beadSize*curRow + posY*(beadSize/2) + 5, 
                        beadSize/2-5, beadSize/2-5);
                posY++;
                if (posY >= 2)
                    posX++;
                posY %= 2;
            }
            //set colour of background highlight
            c.setColor((numCorrectColour != 0 ? new Color(196, 255, 96) : new Color(255, 116, 107)));
            //deselect the current row
            c.fillRoundRect((WIDTH + ((numBeads%2) == 0 ? horizontalSpacing : -beadSize))/2 + (-numBeads/2)*(beadSize+horizontalSpacing) - horizontalSpacing, titleSize*2 + verticalSpacing*curRow + curRow*beadSize - 5, numBeads * (beadSize+horizontalSpacing), beadSize+10, 20, 20);
            //redraws the beads for the current row
            for (int x = 0; x < numBeads; x++)
                drawBead(curColour[curRow][x], x, curRow);
        }
        //if the user did not guess the pattern
        return new int[] {LOSE, -1};
    }

    //Method mainMenu
    //Parameters: none
    //Return type: int
    //Access level: public
    //Gets the user's choice in main menu
    //          Method Variable Dictionary
    //Name              Type            Use
    //choices           String[]        The available choice names
    //row               int             Stores the current row
    //curChoice         int             Stores the current option selected
    //input             char            Stores the user's key presses
    public int mainMenu()
    {
        //clears the screen
        title();
        String[] choices = {"New Game", "Load Game", "Instructions", "High Scores", "Settings", "Exit Game"};
        int row = c.getRow(), curChoice = 0;
        char input = '\0';
        //prompts the user
        c.println("\n");
        centrePrintln("Please select a choice:");
        c.setCursor(ROW-4, 1);
        centrePrintln("HINT");
        centrePrintln("Use the W and S keys to select a choice.");
        centrePrintln("Press Enter to confirm your choice.");
        //highlights the first option
        c.setCursor(row, 1);
        c.setTextBackgroundColor(Color.WHITE);        
        centrePrintln(choices[0]);
        c.setTextBackgroundColor(BACKGROUND);
        for (int x = 1; x < choices.length; x++)
            centrePrintln(choices[x]);
        //while the user is choosing what to do
        while (input != '\n')
        {
            //gets what the user would like to do
            input = Character.toLowerCase(c.getChar());
            //sets the cursor at the current position
            c.setCursor((row+curChoice), 1);
            //unhighlight the current selected choice
            c.setTextBackgroundColor(BACKGROUND);
            centrePrintln(choices[curChoice]);
            //moves which choice is highlighted depending on the user's key press
            switch(input)
            {
                case 'w':
                    curChoice = Math.max(curChoice-1, 0);
                break;
                case 's':
                    curChoice = Math.min(curChoice+1, choices.length-1);
                break;
            }
            //highlights the current choice
            c.setCursor((row+curChoice), 1);
            c.setTextBackgroundColor(Color.WHITE);
            centrePrintln(choices[curChoice]);            
        }
        c.setTextBackgroundColor(BACKGROUND);
        return curChoice+1;
    }

    //Method newGame
    //Parameters: none
    //Return type: void
    //Access level: public
    //Creates a new game for the user to play
    //          Method Variable Dictionary
    //Name              Type            Use
    //row               int             stores the current row
    //returnVal         int[]           return values after playing a game (win, lose, give up, etc) 
    //saveRow           int             the current row when prompting to save the game
    //saveColumn        int             the current column when prompting to save the game
    //choice            int             gets whether the user wants to save the game or not
    //filename          int             stores the filename of where the user wants to store the game
    //curRow            int             stores the current row when getting the filename
    //curColumn         int             stores the current column when getting the filename
    public void newGame()
    {
        //clears the screen and prompts the user
        title();
        c.println("\n\n");
        centrePrintln("You are about to start a new game of Mastermind!");
        c.println();
        centrePrintln("Please enter your username (Enter 'q' to return to main menu)");
        int row = c.getRow();
        username = "";
        //gets the username of the user
        do
        {
            for (char input = c.getChar(); input != '\n'; input = c.getChar())
            {
                clearCursor(row, 1);
                if (input == 8)
                {
                    if (username.length() > 0)
                        username = username.substring(0, username.length()-1);
                }
                else if (username.length() < 50)
                    username += input;
                c.setCursor(row, COLUMN/2-username.length()/2);
                c.print(username);
            }
        }
        while (username.length() == 0);
        //if the user wants to quit
        if (username.equals("q") || username.equals("Q"))
            return;
        title();
        //resets everything
        pattern = new int[numBeads];
        curColour = new int[numGuesses][numBeads];
        curRow = 0;
        for (int x = 0; x < numBeads; x++)
        {
            pattern[x] = (int)(Math.random()*numColour);
            colourCount[pattern[x]]++;
        }
        //runs the game
        int[] returnVal = displayBoard(0);
        title();
        c.println("\n\n");
        switch(returnVal[0])
        {
            //if the user won
            case WIN:
                centrePrintln("You win with a score of " + returnVal[1] + "!");
                c.println("\n\n\n");
                centrePrintln("You solved for the pattern:");
            break;
            //if the user lost
            case LOSE:
                centrePrintln("You did not solve the pattern.");
                centrePrintln("Better luck next time!");
                c.println("\n\n");
                centrePrintln("The solution was:");
            break;
            //if the user gave up
            case GIVE_UP:
                centrePrintln("You gave up!");
                if (numBeads == 4 && numColour == 4 && numGuesses == 8)
                    centrePrintln("Try again! Practice makes perfect!");
                else
                    centrePrintln("Try again with an easier setting!");
                c.println("\n\n");
                centrePrintln("The correct solution was:");
            break;
            //if the user quit
            case QUIT:
                //prompts the user
                centrePrintln("You quit the game!");
                c.println("\n\n\n");
                centrePrintln("Would you like to save the current game?");
                centrePrintln("Press 'y' for yes and anything else for no.");
                int saveRow = c.getRow(), saveColumn = c.getColumn();
                String filename = "";
                //gets if the suer wants to save the game
                char choice = Character.toLowerCase(c.getChar());
                while (choice == 'y')
                {
                    centrePrintln("You would like to save your current game.");
                    centrePrintln("Please enter the final game to save the game to (Enter 'q' to go back).");
                    int curRow = c.getRow(), curColumn = c.getColumn();
                    //gets the filename and tries to save
                    while (true)
                    {
                        filename = "";
                        clearCursor(curRow, curColumn);
                        //gets the filename
                        for (char input = c.getChar(); input != '\n'; input = c.getChar())
                        {
                            clearCursor(curRow, 1);
                            //if backspace is pressed
                            if (input == 8)
                            { 
                                if (filename.length() > 0)
                                    filename = filename.substring(0, filename.length()-1);
                            }
                            //else if the filename is less than 50 characters
                            else if (filename.length() < 50)
                                filename += input;
                            c.setCursor(curRow, COLUMN/2-filename.length()/2);
                            c.print(filename);
                        }
                        //if the user wants to go back
                        if (filename.equals("q") || filename.equals("Q"))
                            break;
                        //tries to save the game
                        switch(save(0, filename + "\n" + returnVal[1]))
                        {
                            //if successfully saved
                            case SUCCESS:
                                title();
                                c.println("\n\n\n\n");
                                centrePrintln("File Successfully Saved!");
                                c.println("\n\n\n\n\n");
                                curColour = null;
                                pattern = null;
                                username = null;
                                colourCount = new int[colour.length];
                                pauseProgram();
                            return;
                            //if it cannot be saved
                            case ARRAY_UNINIT:
                                JOptionPane.showMessageDialog(null, "The game has not been played yet.", "No Game", JOptionPane.ERROR_MESSAGE);
                            return;
                            //if cannot be saved to the specified filename
                            case INVALID_FILENAME:
                                JOptionPane.showMessageDialog(null, "The filename entered cannot be saved to. Please try again.", "Invalid Filename", JOptionPane.ERROR_MESSAGE);
                            break;
                            //if the game is corrupt
                            case INVALID_FILEDATA: 
                                curColour = null;
                                pattern = null;
                                username = null;
                                colourCount = new int[colour.length];
                                JOptionPane.showMessageDialog(null, "The game is corrupt, and cannot be saved.", "Corrupt Game", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    } 
                    //clears the cursor
                    clearCursor(saveRow, saveColumn);
                    choice = Character.toLowerCase(c.getChar());
                }
            break;
        }
        //displays the solution unless the user wants to continue at a later time
        if (returnVal[0] != QUIT)
        {
            c.println("\n\n\n\n");
            displaySolution();
        }
        //resets everything
        curColour = null;
        pattern = null;
        username = null;
        colourCount = new int[colour.length];
        pauseProgram();
    }

    //Method loadGame
    //Parameters: none
    //Return type: void
    //Access level: public
    //Loads an existing game for the user to play
    //          Method Variable Dictionary
    //Name              Type            Use
    //row               int             stores the current row
    //status            int             return status when trying to open a file
    //returnVal         int[]           return values after playing a game (win, lose, give up, etc) 
    public void loadGame()
    {
        //clears the screen
        title();
        c.println("\n\n");
        centrePrintln("You are about to load an existing game!");
        centrePrintln("Please enter the name of an existing game. Enter 'q' to return to main menu.");
        int row = c.getRow();
        String filename = "";
        //while the user tries to load a game
        while (true)
        {
            //while the user is entering the filename and they do not press the enter key
            for (char input = c.getChar(); input != '\n'; input = c.getChar())
            {
                //clears the cursor
                clearCursor(row, 1);
                //if the user is pressing the backspace, try to remove a character from the string
                if (input == 8)
                {
                    if (filename.length() > 0)
                    filename = filename.substring(0, filename.length()-1);
                }
                //else if the user is pressing a character and the current filename is not greater than 50 characters
                else if (filename.length() < 50)
                    filename += input;
                //reprints the new string
                c.setCursor(row, COLUMN/2-filename.length()/2);
                c.print(filename);
            }
            //if the user would like to exit
            if (filename.equals("q") || filename.equals("Q"))
                return;
            int status = open(0, filename);
            //if the file cannot be opened, then reset everything
            if (status == INVALID_FILEDATA || status == INVALID_FILENAME || status == ARRAY_UNINIT)
            {
                curColour = null;
                pattern = null;
                username = null;
                colourCount = new int[colour.length];
            }
            switch(status)
            {
                //prints the corresponding error messages
                case INVALID_FILEDATA:
                    JOptionPane.showMessageDialog(null, "The file cannot be loaded because the data has been modified by an external source. Please try again.", "Corrupt Game", JOptionPane.ERROR_MESSAGE);
                break;
                case INVALID_FILENAME:
                    JOptionPane.showMessageDialog(null, "The filename entered is invalid. Please try again", "Invalid Filename", JOptionPane.ERROR_MESSAGE);
                break;
                case ARRAY_UNINIT:
                    JOptionPane.showMessageDialog(null, "The file cannot be loaded. Please try again", "Invalid Mastermind Game", JOptionPane.ERROR_MESSAGE);
                break;
                //if the file was successfully loaded
                default:
                    //clears the screen and waits for the user
                    title();
                    centrePrintln("File Successfully Loaded!");
                    pauseProgram();
                    //run the game
                    int[] returnVal = displayBoard(0);
                    title();
                    c.println("\n\n");
                    switch(returnVal[0])
                    {
                        //if the user wins
                        case WIN:
                            centrePrintln("You win with a score of " + returnVal[1] + "!");
                            c.println("\n\n\n");
                            centrePrintln("You solved for the pattern:");
                        break;
                        //if the user loses
                        case LOSE:
                            centrePrintln("You did not solve the pattern.");
                            centrePrintln("Better luck next time!");
                            c.println("\n\n");
                            centrePrintln("The solution was:");
                        break;
                        //if the user gave up
                        case GIVE_UP:
                            centrePrintln("You gave up!");
                            if (numBeads == 4 && numColour == 4 && numGuesses == 8)
                                centrePrintln("Try again! Practice makes perfect!");
                            else
                                centrePrintln("Try again with an easier setting!");
                            c.println("\n\n");
                            centrePrintln("The correct solution was:");
                        break;
                        //if the user quits but wants to continue later
                        case QUIT:
                            centrePrintln("You quit the game!");
                            c.println("\n\n\n");
                            centrePrintln("Would you like to save the current game?");
                            centrePrintln("Press 'y' to save to the current filename and anything else for no.");
                            //if the user wants to save
                            if (Character.toLowerCase(c.getChar()) == 'y')
                            {
                                switch(save(0, filename + " " + returnVal[1]))
                                {
                                    //saved successfully
                                    case SUCCESS:
                                        title();
                                        c.println("\n\n\n\n");
                                        centrePrintln("File Successfully Saved!");
                                        c.println("\n\n\n\n\n");
                                    break;
                                    //this should never happen, but if the game has not been run
                                    case ARRAY_UNINIT:
                                        curColour = null;
                                        pattern = null;
                                        username = null;
                                        colourCount = new int[colour.length];
                                        JOptionPane.showMessageDialog(null, "The game has not been played yet.", "No Game", JOptionPane.ERROR_MESSAGE);
                                    return;
                                    //this should never happen, but if the game cannot be saved to the filename (e.g. the file became write-protected)
                                    case INVALID_FILENAME:
                                        JOptionPane.showMessageDialog(null, "The filename entered cannot be saved to. Please try again.", "Invalid Filename", JOptionPane.ERROR_MESSAGE);
                                    break;
                                    //this should never happen, but if the game is corrupt for some reason
                                    case INVALID_FILEDATA: 
                                        curColour = null;
                                        pattern = null;
                                        username = null;
                                        colourCount = new int[colour.length];
                                        JOptionPane.showMessageDialog(null, "The game is corrupt, and cannot be saved.", "Corrupt Game", JOptionPane.ERROR_MESSAGE);
                                    return;
                                }
                            }
                        break;
                    }
                    //displays the solution unless the user wants to continue later
                    if (returnVal[0] != QUIT)
                    {
                        c.println("\n\n\n\n");
                        displaySolution();
                    }
                    //resets everything
                    curColour = null;
                    pattern = null;
                    username = null;
                    colourCount = new int[colour.length];
                    pauseProgram();
                return;
            }
        }
    }

    //Method instructions
    //Parameters: none
    //Return type: void
    //Access level: public
    //Prints the instructions to the user
    public void instructions()
    {
        //clears the screen
        title();
        c.println();
        //Displays the instructions
        centrePrintln("Mastermind is a game where you try to guess a pattern of colours.");
        c.println();
        centrePrintln("This is a pattern of colours");
        c.println("\n\n\n\n");
        centrePrintln("You can use the W-A-S-D keys to select a bead");
        centrePrintln("or change the colour of the selected bead.");
        c.println();
        centrePrintln("Once you are done, press Enter to confirm.");
        centrePrintln("Up to " + numBeads + " beads will show up next to the pattern.");
        c.println();        
        centrePrintln("   means that a correctly coloured bead is in the correct position.");
        c.println();
        centrePrintln("    means that a correctly coloured bead is in the wrong position.  ");
        c.println("\n");
        //Draws a sample game
        c.setColor(new Color(18,42,94));
        c.fillRect((WIDTH + ((numBeads%2) == 0 ? horizontalSpacing : -beadSize))/2 + (-numBeads/2)*(beadSize+horizontalSpacing) - horizontalSpacing/2 - 10, 210, numBeads * (beadSize+horizontalSpacing) + 20, beadSize+40);
        c.setColor(new Color(37,99,244));
        c.fillRect((WIDTH + ((numBeads%2) == 0 ? horizontalSpacing : -beadSize))/2 + (-numBeads/2)*(beadSize+horizontalSpacing) - horizontalSpacing/2, 220, numBeads * (beadSize+horizontalSpacing), beadSize+20);
        c.setColor(Color.BLACK);
        c.fillOval(70,443,beadSize/2-6, beadSize/2-6);
        c.drawOval(70,488,beadSize/2-6, beadSize/2-6);
        for (int x = -numBeads/2; x < numBeads/2 + (numBeads%2); x++)
        {
            c.setColor(colour[0]);
            c.fillOval((WIDTH + ((numBeads%2) == 0 ? horizontalSpacing : -beadSize))/2 + x*(beadSize+horizontalSpacing), 230, beadSize, beadSize);
        }
        //Key mappings
        centrePrintln("At any point in the game, press Q to quit the game.");
        centrePrintln("You will be prompted to save the game.");
        centrePrintln("You can also press G to give up if you cannot solve the current puzzle.");
        centrePrintln("Additionally, pressing C will reveal the pattern.");
        centrePrintln("However, there will be a 1-hour penalty each time you reveal the pattern.");
        c.println();
        centrePrintln("You have " + numGuesses + " attempts to guess the pattern. Good luck!");
        c.println("\n");
        centrePrintln("This concludes the instructions for Mastermind.");
        //Waits for the user to press a key
        pauseProgram();
    }

    //Method highscores
    //Parameters: none
    //Return type: void
    //Access level: public
    //Displays the high scores to the user
    //          Method Variable Dictionary
    //Name              Type            Use
    //row               int             Stores the current row of the cursor
    //column            int             Stores the current column of the cursor
    //input             char            Stores the user character input 
    public void highscores()
    {
        //draws the title
        title();
        c.println();
        //if there are no high scores
        if (numHighScores == 0)
        {
            centrePrintln("There are currently no high scores. Play a game to display your score!\n");
            pauseProgram();
        }
        else
        {
            //prints the high scores with alternating colours
            centrePrintln("The current highscores are\n");
            for (int x = 0; x < numHighScores; x++)
            {
                c.setTextColor((x%2) == 0 ? Color.MAGENTA : Color.BLUE);
                c.print("", 3);
                c.print((x+1) + ".", 3);
                c.print(highScoreNames[x], 63);
                c.println(scores[x], 7);
            }
            c.setTextColor(Color.BLACK);
            c.println("\n\n");
            centrePrintln("Press 'c' to clear the highscores or any other key to return to main menu");
            int row = c.getRow(), column = c.getColumn();
            //allows the user to clear the high scores
            while (true)
            {
                clearCursor(row, column);
                char input = Character.toLowerCase(c.getChar());
                if (input == 'c')
                {
                    centrePrintln("Are you sure you want to clear the highscores?");
                    centrePrintln("Press 'y' for yes and anything else for no.");
                    if (Character.toLowerCase(c.getChar()) == 'y')
                    {
                        numHighScores = 0;
                        highScoreNames = null;
                        scores = null;
                        save(1, "Mastermind_Highscores.txt");
                        highscores();
                        return;
                    }
                }
                else
                    return;
            }
        }
    }

    //Method settings
    //Parameters: none
    //Return type: void
    //Access level: public
    //Allows the user to change the settings in the game using sliding bars
    //          Method Variable Dictionary
    //Name              Type            Use
    //tmpVals           int[]           Stores the three variables tied so that changing between them is easier
    //maxNum            int[]           The maximum possible value for each category
    //minNum            int[]           The minimum possible value for eahc category
    //lens              int[]           The position on the screen to display the three sliding bars
    //rows              int[]           The position on the screen to display the text
    //slidingBarLength  final int       The length of the sliding bar to display
    //curOption         int             The current option the user has highlighted
    //char              int             Stores the input character the user entered
    public void settings()
    {
        //displays titles
        title();
        //instantiates variables
        int[] tmpVals = {numColour, numBeads, numGuesses}, maxNum = {6, 6, 8}, minNum = {4, 4, 1}, lens = {175, 285, 395}, rows = new int[3];
        final int slidingBarLength = 300;
        char input = '\0';
        int curOption = 0;
        //prints the names for each setting
        c.println();
        centrePrintln("Number of Colours");
        rows[0] = c.getRow()+1;
        c.print("\n\n\n\n");
        centrePrintln("Number of Beads");
        rows[1] = c.getRow()+1;
        c.print("\n\n\n\n");
        centrePrintln("Number of Guesses");
        rows[2] = c.getRow()+1;
        c.print("\n\n\n\n");
        //draws the sliding bars
        c.setColor(Color.BLACK);
        for (int x = 0; x < tmpVals.length; x++)
        {
            c.setCursor(rows[x], COLUMN/2+COLUMN/4);
            c.print(tmpVals[x]);
            c.fillRect((WIDTH-slidingBarLength-20)/2, lens[x]+14, slidingBarLength+20, 2);
            c.fillRect((WIDTH-slidingBarLength-20)/2 + (int)((tmpVals[x]-minNum[x])/(double)(maxNum[x]-minNum[x])*slidingBarLength), lens[x], 20, 30);
        }
        //prints the hint at the bottom of the screen
        c.setCursor(ROW-3, 1);
        centrePrintln("Press Enter to return to the main menu.");
        centrePrintln("Use the W-A-S-D keys to adjust the settings.");
        //while the user does not wish to return to the main menu
        while (input != '\n')
        {
            //prints the current value at the current setting
            c.setCursor(rows[curOption], COLUMN/2 + COLUMN/4);
            c.print(tmpVals[curOption]);
            //draws the current position of the bar
            c.fillRect((WIDTH-slidingBarLength-20)/2, lens[curOption]+10, slidingBarLength+20, 10);
            //gets a user input
            input = Character.toLowerCase(c.getChar());
            //clears the bar at the current position and unhighlights the current bar
            c.setColor(BACKGROUND);
            c.fillRect((WIDTH-slidingBarLength-20)/2, lens[curOption]+10, slidingBarLength+20, 10);
            c.fillRect((WIDTH-slidingBarLength-20)/2 + (int)((tmpVals[curOption]-minNum[curOption])/(double)(maxNum[curOption]-minNum[curOption])*slidingBarLength), lens[curOption], 20, 30);
            c.setColor(Color.BLACK);
            //switch statement that changes the currently highlighed bar or the setting at the current bar
            switch(input)
            {
                case 'd':
                case 'a':
                    tmpVals[curOption] = (input == 'd') ? Math.min(tmpVals[curOption]+1, maxNum[curOption]) : Math.max(tmpVals[curOption]-1, minNum[curOption]);
                break;
                case 'w':
                case 's':
                    c.fillRect((WIDTH-slidingBarLength-20)/2 + (int)((tmpVals[curOption]-minNum[curOption])/(double)(maxNum[curOption]-minNum[curOption])*slidingBarLength), lens[curOption], 20, 30);
                    c.fillRect((WIDTH-slidingBarLength-20)/2, lens[curOption]+14, slidingBarLength+20, 2);
                    curOption = (input == 'w') ? Math.max(curOption-1, 0) : Math.min(curOption+1, tmpVals.length-1);
                break;
            }
            //highlights the current selected bar
            c.fillRect((WIDTH-slidingBarLength-20)/2 + (int)((tmpVals[curOption]-minNum[curOption])/(double)(maxNum[curOption]-minNum[curOption])*slidingBarLength), lens[curOption], 20, 30);
        }
        //splits the tmpVals into their corresponding variables
        numColour = tmpVals[0];
        numBeads = tmpVals[1];
        numGuesses = tmpVals[2];
        //recalculates the horizontal spacing and the vertical spacing between beads
        horizontalSpacing = (WIDTH-numBeads*beadSize)/(numBeads+2);
        verticalSpacing = (HEIGHT-titleSize*2-numGuesses*beadSize)/(numGuesses+4);
    }
    
    //Method goodbye
    //Parameters: none
    //Return type: void
    //Access level: public
    //Says goodbye to the user
    public void goodbye()
    {
        title();
        c.println("\n\n");
        centrePrintln("You are about to exit the game.");
        c.println();
        centrePrintln("Thank you for playing Mastermind, programmed and tested by Evan Zhang.");
        centrePrintln("This program would not have been possible without the help of Ms. Krasteva.");
        c.println("\n\n\n\n\n\n\n\n\n\n\n\n\n");
        pauseProgram();
        c.close();
        System.exit(0);
    }

    //Method main
    //Parameters: argv(array of strings)
    //Return type: void
    //Runs the entire program
    //      Method variable Dictionary
    //Name        Type               Use
    //argv        array of Strings   Stores any user parameters passed from command line
    //m           Mastermind         Variable to run the enter game
    public static void main (String [] argv)
    {
        //declares a Mastermidn instance
        Mastermind m = new Mastermind();
        //displays splash screen
        m.splashScreen();
        //runs indefinitely until the user chooses the exit
        for (int choice = m.mainMenu(); choice != 6; choice = m.mainMenu())
        {
            //runs the choice accordingly
            switch (choice)
            {
                case 1:
                    m.newGame();
                break;
                case 2: 
                    m.loadGame();
                break;
                case 3: 
                    m.instructions();
                break;
                case 4:
                    m.highscores();
                break;
                case 5:
                    m.settings();
                break;
            }
        }
        //says goodbye to the user
        m.goodbye();
    }
}
