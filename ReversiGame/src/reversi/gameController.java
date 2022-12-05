package reversi;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

public class gameController {
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_RESET = "\u001B[0m";

    private static boolean opponent;

    private int[][] gameBoard;
    private static final Stack<board> stackOfBoard = new Stack();
    private board b;

    /**
     * Запуск приложения
     */
    public static void menu() {
        System.out.println("Welcome to Reversi Game. It's a start menu");
        System.out.println("Please select your opponent: (1 - computer, 2 - player vs player )");
        Scanner sc = new Scanner(System.in);
        int x;
        try {
            x = sc.nextInt();
        } catch (Exception e) {
            throw new IllegalArgumentException("Input must be integer");
        }
        opponent = (x == 1);
        if (opponent) {
            System.out.println("Please select the game mode: (1-Beginner, 2- Professional)");
            try {
                x = sc.nextInt();
            } catch (Exception e) {
                throw new IllegalArgumentException("Input must be integer");
            }
            reversi.setMaxDepth(x);
        }
        gameController reversi = new gameController();
        reversi.startGame();
    }

    public gameController() {
        initialState();
        b = new board(gameBoard);
        stackOfBoard.add(board.copy(b));
    }

    /**
     * Начальная инициализация
     */
    private void initialState() {
        gameBoard = new int[8][8];
        gameBoard[3][3] = 1;
        gameBoard[3][4] = 2;
        gameBoard[4][3] = 2;
        gameBoard[4][4] = 1;
    }

    /**
     *
     */
    private void startGame() {
        int x, y;
        Scanner sc = new Scanner(System.in);
        while (b.finalAccount() != 64 && (b.getMoves(1).size() != 0 || b.getMoves(2).size() != 0)) {
            if (b.getMoves(2).size() != 0) {
                suggestMoves(2);
                System.out.println("Red's Move. Select a field to move from '□'");
                System.out.println("Write in format 'x y' or write -1 to cancel last step");
                try {
                    x = sc.nextInt();
                } catch (Exception e) {
                    throw new IllegalArgumentException("Input must be integer");
                }
                if (x == -1) {
                    b = cancel();
                    continue;
                }
                try {
                    y = sc.nextInt();
                } catch (Exception e) {
                    throw new IllegalArgumentException("Input must be integer");
                }
                move(x, y, 2);
            } else {
                System.out.println("Oops, you have no moves");
            }
            if (!opponent) {
                suggestMoves(1);
                System.out.println("Green's moves. Select a field to move from '□'");
                System.out.println("Write in format 'x y'");
                try {
                    x = sc.nextInt();
                } catch (Exception e) {
                    throw new IllegalArgumentException("Input must be integer");
                }
                try {
                    y = sc.nextInt();
                } catch (Exception e) {
                    throw new IllegalArgumentException("Input must be integer");
                }
                move(x, y, 1);
            }
        }
        endGame();
    }


    /**
     * Окончание игры
     */
    private void endGame() {
        System.out.println("----------------------");
        System.out.println("The Game is over");
        System.out.printf("Total score (red/green): %d, %d \n", b.account(2), b.account(1));
        System.out.printf("Best score over all game: %d\n", b.getBestScore());
    }

    /**
     * Отмена хода
     *
     * @return Доску
     */
    public board cancel() {
        if (stackOfBoard.size() <= 1) {
            System.out.println("You have nothing to cancel");
            return b;
        } else {
            return stackOfBoard.pop();
        }
    }

    /**
     * Печать доски
     */
    void printBoard() {
        System.out.println(" \t0\t1\t2\t3\t4\t5\t6\t7");
        for (int x = 0; x < 8; x++) {
            System.out.printf("%d\t", x);
            for (int y = 0; y < 8; y++) {
                setFeature(b.getBoardXY(x, y));
            }
            System.out.println();
        }
        System.out.println("-\t-\t-\t-\t-\t-\t-\t-\t-");
        System.out.printf("Score (red/green): %d, %d \n", b.account(2), b.account(1));
    }

    /**
     * Печать символов - очень красивая
     *
     * @param c флаг символа
     */
    private void setFeature(int c) {
        switch (c) {
            case 0:
                System.out.print("x\t");
                break;
            case 1:
                System.out.printf(ANSI_GREEN + "○\t" + ANSI_RESET);
                break;
            case 2:
                System.out.printf(ANSI_RED + "●\t" + ANSI_RESET);
                break;
            case 3:
                System.out.printf(ANSI_YELLOW + "□\t" + ANSI_RESET);
                break;
        }
    }

    /**
     * Печать всевозможных шагов
     */
    private void suggestMoves(int player) {
        ArrayList<board> moves = b.getMoves(player);
        for (board i : moves) {
            if (b.getBoardXY(i.getX(), i.getY()) == 0)
                b.setBoardXY(i.getX(), i.getY(), 3);
        }
        printBoard();
    }

    /**
     * Убираем возможные шаги, чтобы не было путаницы
     */
    private void clearSuggestion() {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if (b.getBoardXY(x, y) == 3)
                    b.setBoardXY(x, y, 0);
            }
        }
    }

    /**
     * Ход игрока
     *
     * @param x координата
     * @param y координата
     */
    private void move(int x, int y, int player) {
        if (b.getBoardXY(x, y) != 3) {
            clearSuggestion();
            System.out.println("Don't fool me!");
            return;
        }
        if (player == 2) {
            stackOfBoard.add(board.copy(b));
        }
        if (!b.makeMove(x, y, player)) {
            clearSuggestion();
            return;
        }
        clearSuggestion();
        if (opponent) stepOfComputer();
    }

    /**
     * Ход компьютера
     */
    private void stepOfComputer() {
        printBoard();
        int md = reversi.getMaxDepth();
        int[][] copyBoard = board.copy(b.getBoard());
        for (int i = 1; i <= md; i++) {
            reversi.setMaxDepth(i);
            reversi.brainShtorm(1, 0, -1000, 1000, new board(copyBoard));
            b = reversi.getBestBoard();
        }
    }
}
