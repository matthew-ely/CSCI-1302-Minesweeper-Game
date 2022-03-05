package cs1302.game;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;

/**
 * This class does work involving building the minefield using the seed file
 * passed through the {@link cs1302.game.MinesweeperDriver} class, saving in game
 * commands to nagivate through the Minesweeper game itself, and printing ASCII art
 * welcome and game ending interfaces.
 */

public class MinesweeperGame {

    private static String[][] grid;
    private static int[] bombs;
    private static int rounds = 0;
    private static int rows;
    private static int cols;
    private static int coverFlags = 0;
    private static int bombCount = 0;
    private static Scanner stdIn;
    private static String seedPath;

    /**
     * Creates an object containing a copy of standard input commands used in game
     * as well as the seed to construct the game at the beginning.
     *
     * @param stdIn  the standard input
     * @param seedPath  the initial seed to build minefield
     */

    public MinesweeperGame(Scanner stdIn, String seedPath) {
        this.stdIn = stdIn;
        this.seedPath = seedPath;
    } // MinesweeperGame obj

    /**
     * Driver for attempting to build valid minefield, displaying welcome screen,
     * determining if player has won the game yet and prompting user for
     * their next move.
     *
     */

    public static void play() {
        try {
            convertSeed();
        } catch (FileNotFoundException fnfe) {
            System.err.println("Please input a valid seed file.");
            System.exit(0);
        } // catch

        buildGrid();
        printWelcome();

        while (!isWon()) {
            getRounds();
            printMineField();
            undoNoFog();
            try {
                promptUser();
            } catch (InputMismatchException imm) {
                System.err.print("Input Error: Command Not Recognized!");
            } catch (NoSuchElementException nse) {
                System.err.print("Input Error: Command Not Recognized!");
            } // catch

        } // while

        printWin();

    } // play

    /**
     * Reads the contents of the file and creates a valid minefield and
     * positions bombs based on contents of seed file.
     *
     * @throws FileNotFoundException  if file is not found
     */

    private static void convertSeed() throws FileNotFoundException {
        File configFile = new File(seedPath);
        Scanner s = new Scanner(configFile);
        rows = s.nextInt();
        cols = s.nextInt();
        bombCount = s.nextInt();
        if ((rows < 5) || (rows > 10) || (cols < 5) || (cols > 10)) {
            System.err.println(
                "Seed File Malformed Error: Cannot create a" +
                " mine field with that many rows and/or columns!");
            System.exit(0);
        } else if ((bombCount < 1) || (bombCount > (rows * cols) - 1)) {
            System.err.println(
                "Seed File Malformed Error: Cannot create a" +
                " mine field with that many mines!");
            System.exit(0);
        } // if
        bombs = new int[(2 * bombCount)];
        for (int i = 0; i < bombs.length; i ++) {
            bombs[i] = s.nextInt();
        } //for
    } // convertSeed

    /**
     * Builds the 2D array containing the rectangular grid and put a blank space
     * in each grid position.
     */

    private static void buildGrid() {
        grid = new String[rows][cols];
        for (int i = 0; i < grid.length; i ++) {
            for (int j = 0; j < grid[i].length; j ++) {
                grid[i][j] = "   ";
            } // inside for
        } // outside for
    } // buildGrid

    /**
     * Prints the contents of the minefield array as well as the
     * numbers on the x and y axis.
     */

    private static void printMineField() {
        String gridToString = "";
        for (int i = 0; i < grid.length; i ++) {
            if (i < 10) {
                gridToString += "\n " + i + " |";
            } else {
                gridToString += "\n" + i + " |";
            } // else
            for (int j = 0; j < grid[i].length; j ++) {
                gridToString += grid[i][j] + "|";
            } // inside for
        } // outside for
        gridToString += "\n ";
        for (int k = 0; k < grid[0].length; k ++) {
            gridToString += "   " + k;
        } // for
        System.out.println(gridToString);
    } // printGrid

    /**
     * Gathers the user input and modifies the array based on the command or displays
     * help or quit messages.
     *
     * @throws ArrayIndexOutOfBoundsException  when the inputted coordinates are not in array
     * @throws InputMismatchException  when a command is not recognized
     */

    private static void promptUser() throws InputMismatchException, NoSuchElementException {
        System.out.print("\nminesweeper-alpha:   ");
        int y;
        int x;
        String inputString = stdIn.nextLine();
        Scanner sIn = new Scanner(inputString);
        String input = sIn.next();

        if ((input.equals("help")) || (input.equals("h"))) {
            getHelp();
        } else if (input.equals("nofog")) {
            noFog();
        } else if ((input.equals("quit")) || (input.equals("q"))) {
            getQuit();
        } else {

            y = sIn.nextInt();
            x = sIn.nextInt();

            if (sIn.hasNext()) {
                input = "wrong";
            } // if

            try {
                if (!((y < rows) && (y >= 0))) {
                    System.err.println(
                        "Invalid Command: Index " + y +
                        " out of bounds for length " + rows);
                } else if (!((x < cols) && (x >= 0))) {
                    System.err.println(
                        "Invalid Command: Index " + x +
                        " out of bounds for length " + cols);
                } // if

                if ((input.equals("reveal")) || (input.equals("r"))) {
                    reveal(y, x);
                } else if ((input.equals("mark")) || (input.equals("m"))) {
                    mark(y, x);
                } else if ((input.equals("guess")) || (input.equals("g"))) {
                    guess(y, x);
                } else {
                    System.err.println("Input Error: Command not recognized!");
                } // else

            } catch (ArrayIndexOutOfBoundsException oob) {
                inputString = "";
                //} catch (NoSuchElementException nse) {
//                inputString = "";
                //System.err.println("Input Error: Command not recognized!");
            } // catch

        } // big else
    } // promptUser

    /**
     * Prints the list of usable commands when called.
     */

    private static void getHelp() {
        System.out.println(
            "\n Commands Available..." +
            "\n  - Reveal: r/reveal row col" +
            "\n  -   Mark: m/mark   row col" +
            "\n  -  Guess: g/guess  row col" +
            "\n  -   Help: h/help" +
            "\n  -   Quit: q/quit");
        rounds++;
    } // help

    /**
     * Prints an exit message and exits the game when called.
     */

    private static void getQuit() {
        System.out.println("\n Qutting the game...\n Bye!");
        System.exit(0);
    } // quit

    /**
     * Helper method for mineInProx, reveal, and mark methods to
     * determine if a mine is located in up to 8 squares surrounding it.
     * @param y  first coordinate
     * @param x  second coordinate
     * @return true  when a bomb is present in current square; false otherwise
     *
     */

    private static boolean presentMine(int y, int x) {
        try {
            for (int i = 0; i < (2 * bombCount); i += 2) {
                if ((y == bombs[i]) && (x == bombs[i + 1])) {
                    return true;
                } // if
            } // for
            return false;
        } catch (ArrayIndexOutOfBoundsException oob) {
            return false;
        } // catch
    } // presentMine

    /**
     * Scans the surrounding 8 squares to determine if a mine is present
     * and ticks a counter if there is a mine.
     * @param y  first coordinate
     * @param x  second coordinate
     * @return int value representing how many mines surround the square
     *
     */

    private static int mineInProx(int y, int x) {
        int counter = 0;
        for (int i = y - 1; i < y + 2; i ++) {
            for (int j = x - 1; j < x + 2; j ++) {
                if (presentMine(i, j)) {
                    counter ++;
                } // if
            } // inside for
        } // outside for
        return counter;
    } // mineInProx

    /**
     * When called, determines if a mine is located in desired square, if true, game is lost.
     * If false it replaces the grid position with an integer representing the amount of mines
     * located in surrounding squares.
     *
     * @param y  first coordinate
     * @param x second coordinate
     * @throws ArrayIndexOutOfBoundsException  when the inputted coordinates are not in grid
     */

    private static void reveal(int y, int x) throws ArrayIndexOutOfBoundsException {
        if (presentMine(y, x)) {
            printLoss();
        } //if
        grid[y][x] = " " + mineInProx(y, x) + " ";
        rounds ++;
    } // reveal

    /**
     * When called, determines if a mine is located in desired square, if true, it is recorded
     * and regardless places an 'F' in the square to denote that there may be a mine there.
     *
     * @param y  first coordinate
     * @param x  second coordinate
     * @throws ArrayIndexOutOfBoundsException  when the inputted coordinates are not in grid
     */

    private static void mark(int y, int x) throws ArrayIndexOutOfBoundsException {
        if (presentMine(y, x)) {
            coverFlags ++;
        } // if
        grid[y][x] = " F ";
        rounds ++;
    } // mark

    /**
     * When called, replaces the contents of square on grid with a '?' character to
     * denote that there is possibly a mine there.
     *
     * @param y  first coordinate
     * @param x  second coordinate
     * @throws ArrayIndexOutOfBoundsException  when the inputted coordinates are not in grid
     */

    private static void guess(int y, int x) throws ArrayIndexOutOfBoundsException {
        grid[y][x] = " ? ";
        rounds ++;
    } // guess

    /**
     * A secret command not listed in help command that reveals the locations of all mines.
     * If a mine has already been flagged the 'F' will remain.
     */

    private static void noFog() {
        for (int i = 0; i < grid.length; i ++) {
            for (int j = 0; j < grid[i].length; j ++) {
                if (presentMine(i, j)) {
                    if (grid[i][j].equals(" F ")) {
                        grid[i][j] = "<F>";
                    } else {
                        grid[i][j] = "< >";
                    } //else
                } //if
            } // inside for
        } // outside for
        rounds ++;
    } // noFog

    /**
     * Called at the beginning of each round to reset the noFog commmand and hide locations
     * of mines on the grid.
     */

    private static void undoNoFog() {
        for (int i = 0; i < grid.length; i ++) {
            for (int j = 0; j < grid[i].length; j ++) {
                if (grid[i][j].equals("<F>")) {
                    grid[i][j] = " F ";
                } // if
                if (grid[i][j].equals("< >")) {
                    grid[i][j] = "   ";
                } // if
            } // inside for
        } // outside for
    } // undoNoFog

    /**
     * Helper method for isWon to determine if there are any blank or guessed
     * spaces the user has not chosen. It also determines if the player has
     * over marked the amount of mines.
     *
     * @return true  when there is still blank space, guessed space, or
     * there are falsely marked squares; false otherwise
     */

    private static boolean blankSpace() {
        boolean bool = false;
        int fCount = 0;
        for (int i = 0; i < grid.length; i ++) {
            for (int j = 0; j < grid[i].length; j ++) {
                if (grid[i][j].equals(" F ")) {
                    fCount ++;
                } // if

                if ((grid[i][j].equals("   ")) || (grid[i][j].equals(" ? "))) {
                    bool = true;
                } // if
            } // inside for
        } // outside for
        if (fCount > bombCount) {
            bool = true;
        } // if
        return bool;

    } // blankSpace

    /**
     * Called by the while loop in play method to determine if game has been won.
     * The game can only be won if player has uncovered all non-mine spaces and
     * accurately covered all of the mines.
     *
     * @return true  when conditions to win are met; false otherwise
     */

    private static boolean isWon() {
        boolean bool = false;

        if (!(blankSpace() || (coverFlags < bombCount))) {
            bool = true;

        } // if
        return bool;
    } // hasWonYet

    /**
     * Prints the ASCII art for the Minesweeper welcome screen.
     */

    private static void printWelcome() {
        System.out.println(
            "        _\r\n" +
            "  /\\/\\ (F)_ __   ___  _____      _____  ___ _ __   ___ _ __\r\n" +
            " /    \\| | '_ \\ / _ \\/ __\\ \\ /\\ / / _ \\/ _ \\ '_ \\ / _ \\ '__|\r\n" +
            "/ /\\/\\ \\ | | | |  __/\\__ \\\\ V  V /  __/  __/ |_) |  __/ |\r\n" +
            "\\/    \\/_|_| |_|\\___||___/ \\_/\\_/ \\___|\\___| .__/ \\___|_|\r\n" +
            "                             ALPHA EDITION |_| v2021.sp");

    } // getIntro

    /**
     * Prints how many rounds have been completed.
     */

    private static void getRounds() {
        System.out.println("\n Rounds Completed: " + rounds);
    } // getRounds

    /**
     * Calculates the players score when determined after game has been won.
     *
     * @return {@code String} containing user score.
     */

    private static String getScore() { //round to 2 places
        double dScore;
        if (rounds > 0) {
            dScore = (100.00 * rows * cols) / rounds;
        } else {
            dScore = 0;
        } // else
        return String.format("%.2f", dScore);
    } // getScore

    /**
     * Prints ASCII art when player loses the game.
     */

    private static void printLoss() {
        System.out.println(
            "\n Oh no... You revealed a mine!\r\n" +
            "  __ _  __ _ _ __ ___   ___    _____   _____ _ __\r\n" +
            " / _` |/ _` | '_ ` _ \\ / _ \\  / _ \\ \\ / / _ \\ '__|\r\n" +
            "| (_| | (_| | | | | | |  __/ | (_) \\ V /  __/ |\r\n" +
            " \\__, |\\__,_|_| |_| |_|\\___|  \\___/ \\_/ \\___|_|\r\n" +
            " |___/\r\n");
        System.exit(0);
    } // printLoss

    /**
     * Prints ASCII art when player wins the game as well as the score.
     */

    private static void printWin() {
        System.out.println(
            "\n ░░░░░░░░░▄░░░░░░░░░░░░░░▄░░░░ \"So Doge\"\r\n" +
            " ░░░░░░░░▌▒█░░░░░░░░░░░▄▀▒▌░░░\r\n" +
            " ░░░░░░░░▌▒▒█░░░░░░░░▄▀▒▒▒▐░░░ \"Such Score\"\r\n" +
            " ░░░░░░░▐▄▀▒▒▀▀▀▀▄▄▄▀▒▒▒▒▒▐░░░\r\n" +
            " ░░░░░▄▄▀▒░▒▒▒▒▒▒▒▒▒█▒▒▄█▒▐░░░ \"Much Minesweeping\"\r\n" +
            " ░░░▄▀▒▒▒░░░▒▒▒░░░▒▒▒▀██▀▒▌░░░\r\n" +
            " ░░▐▒▒▒▄▄▒▒▒▒░░░▒▒▒▒▒▒▒▀▄▒▒▌░░ \"Wow\"\r\n" +
            " ░░▌░░▌█▀▒▒▒▒▒▄▀█▄▒▒▒▒▒▒▒█▒▐░░\r\n" +
            " ░▐░░░▒▒▒▒▒▒▒▒▌██▀▒▒░░░▒▒▒▀▄▌░\r\n" +
            " ░▌░▒▄██▄▒▒▒▒▒▒▒▒▒░░░░░░▒▒▒▒▌░\r\n" +
            " ▀▒▀▐▄█▄█▌▄░▀▒▒░░░░░░░░░░▒▒▒▐░\r\n" +
            " ▐▒▒▐▀▐▀▒░▄▄▒▄▒▒▒▒▒▒░▒░▒░▒▒▒▒▌\r\n" +
            " ▐▒▒▒▀▀▄▄▒▒▒▄▒▒▒▒▒▒▒▒░▒░▒░▒▒▐░\r\n" +
            " ░▌▒▒▒▒▒▒▀▀▀▒▒▒▒▒▒░▒░▒░▒░▒▒▒▌░\r\n" +
            " ░▐▒▒▒▒▒▒▒▒▒▒▒▒▒▒░▒░▒░▒▒▄▒▒▐░░\r\n" +
            " ░░▀▄▒▒▒▒▒▒▒▒▒▒▒░▒░▒░▒▄▒▒▒▒▌░░\r\n" +
            " ░░░░▀▄▒▒▒▒▒▒▒▒▒▒▄▄▄▀▒▒▒▒▄▀░░░ CONGRATULATIONS!\r\n" +
            " ░░░░░░▀▄▄▄▄▄▄▀▀▀▒▒▒▒▒▄▄▀░░░░░ YOU HAVE WON!\r\n" +
            " ░░░░░░░░░▒▒▒▒▒▒▒▒▒▒▀▀░░░░░░░░ SCORE: " + getScore());
        System.exit(0);
    } // printWin

} // MinesweeperGame
