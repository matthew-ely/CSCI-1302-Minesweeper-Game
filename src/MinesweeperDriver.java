package cs1302.game;

import cs1302.game.MinesweeperGame;

import java.util.Scanner;

/**
 * Entry point for Minesweeper. A file name is passed as the seed for the minefield
 * detailed in the {@link cs1302.game.MinesweeperGame} class. An object is created
 * and all valid commands are ran through the {@link cs1302.game.MinesweeperGame}
 * class.
 *
 * @param args  the command-line argument to get seed file
 */

public class MinesweeperDriver {

    public static void main(String[] args) {

        Scanner stdIn = new Scanner(System.in);
        String seedPath = args[0];
        MinesweeperGame msG = new MinesweeperGame(stdIn, seedPath);

        msG.play();

    } // main

} // MineSweeperDriver
