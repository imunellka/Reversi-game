package reversi;

import java.util.ArrayList;

public class board {

    private final int[][] board;

    public int[][] getBoard() {
        return board;
    }

    public int getBoardXY(int x, int y) {
        if (x <= 7 && y <= 7 && x >= 0 && y >= 0)
            return board[x][y];
        else throw new IllegalArgumentException("Coordinates cannot be less than 0 or bigger than 7");

    }

    public void setBoardXY(int x, int y, int c) {
        if (x <= 7 && y <= 7 && x >= 0 && y >= 0)
            board[x][y] = c;
        else throw new IllegalArgumentException("Coordinates cannot be less than 0 or bigger than 7");
    }

    private int x;

    public int getX() {
        return x;
    }

    private int y;

    public int getY() {
        return y;
    }

    private int bestScore = 0;

    public int getBestScore() {
        return bestScore;
    }

    public board(int[][] board) {
        this.board = board;
    }

    /**
     * @param x      координата
     * @param y      координата
     * @param player кто делает ход
     * @return Делает ход, если возможно и возвращает результат true false
     */
    public boolean makeMove(int x, int y, int player) {
        boolean flag = false, inverse, wasRival;
        if (board[x][y] == 1 || board[x][y] == 2)
            return false;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                inverse = false;
                wasRival = false;
                int k = 1;
                if (i == 0 && j == 0) continue;
                while (x + j * k >= 0 && x + j * k <= 7 && y + i * k >= 0 && y + i * k <= 7) {
                    if (board[x + j * k][y + i * k] == 0 || board[x + j * k][y + i * k] == 3 ||
                            (board[x + j * k][y + i * k] == player && !wasRival))
                        break;
                    if (board[x + j * k][y + i * k] == player && wasRival) {
                        inverse = true;
                        break;
                    } else if (board[x + j * k][y + i * k] == player % 2 + 1) {
                        wasRival = true;
                        k++;
                    }
                }
                if (inverse) {
                    board[x][y] = player;
                    for (int l = 1; l <= k; l++)
                        board[x + j * l][y + i * l] = player;
                    flag = true;
                }
            }
        }
        this.x = x;
        this.y = y;
        return flag;
    }

    /**
     * Подсчет количества заполненных ячеек
     *
     * @return количество заполненных ячеек
     */
    public int finalAccount() {
        return account(1) + account(2);
    }

    /**
     * Подсчет разницы
     *
     * @param player игрок
     * @return разница в очках
     */
    public double countDiff(int player) {
        return accountWithCoeff(player) - accountWithCoeff(player % 2 + 1);
    }

    /**
     * Счет игрока с коэффициентами для функции оптимизации
     *
     * @param player игрок
     * @return Счет игрока
     */
    public double accountWithCoeff(int player) {
        double count = 0;
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                if (board[j][i] == player) {
                    if ((i == 0 && j == 0) || (i == 0 && j == 7) || (i == 7 && j == 0) || (i == 7 && j == 7)) {
                        count += 2 + 0.8;
                    } else if (i == 0 || i == 7 || j == 0 || j == 7) {
                        count += 2 + 0.4;
                    } else count++;
                }
        return count;
    }

    /**
     * Счет игрока
     *
     * @param player игрок
     * @return Счет игрока
     */
    public int account(int player) {
        int count = 0;
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                if (board[j][i] == player) count++;
        if (player == 2) {
            bestScore = Math.max(bestScore, count);
        }
        return count;
    }

    /**
     * Возврат досок с всевозможными ходами
     *
     * @param player игрок
     * @return досок с всевозможными ходами
     */
    public ArrayList<board> getMoves(int player) {
        ArrayList<board> boards = new ArrayList<>();
        board b = new board(copy(board));
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (b.makeMove(j, i, player)) {
                    boards.add(b);
                    b = new board(copy(board));
                }
            }
        }
        return boards;
    }

    /**
     * Это не говнокод - это просто копирование полей для учета всевозможных вариантов
     *
     * @param board - игровая доска
     * @return копия
     */
    public static int[][] copy(int[][] board) {
        int[][] copy = new int[8][];
        for (int i = 0; i < 8; i++) {
            copy[i] = board[i].clone();
        }
        return copy;
    }

    /**
     * Это не говнокод - это просто копирование полей для учета всевозможных вариантов
     *
     * @param board - игровая доска
     * @return копия
     */
    public static board copy(board board) {
        int[][] copy = new int[8][];
        for (int i = 0; i < 8; i++) {
            copy[i] = board.board[i].clone();
        }
        return new board(copy);
    }
}