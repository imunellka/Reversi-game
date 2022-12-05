

package reversi;

import java.util.ArrayList;

public class reversi {

    private static int maxDepth = 2;

    public static int getMaxDepth() {
        return maxDepth;
    }

    public static void setMaxDepth(int x) {
        maxDepth = Math.max(1, Math.min(x, 2));
    }

    private static board bestBoard;

    public static board getBestBoard() {
        return bestBoard;
    }

    /**
     * Оптимальный выбор стратегии
     *
     * @param player      игрок
     * @param depth       глубина рекурсии
     * @param countAI     подсчет компьютера
     * @param countPlayer подсчет игрока
     * @param b           доска
     * @return Счет
     */
    public static double brainShtorm(int player, int depth, double countAI, double countPlayer, board b) {

        if (depth > maxDepth) return b.countDiff(1);
        ArrayList<board> moves = b.getMoves(player);
        if (moves.size() == 0) return player == 1 ? 100 : -100;
        if (player == 1) {
            int ind = 0;
            for (int i = 0; i < moves.size(); i++) {
                double score = brainShtorm(2, depth + 1, countAI, countPlayer, moves.get(i));
                if (score > countAI) {
                    countAI = score;
                    ind = i;
                }
                if (countAI >= countPlayer) break;
            }
            if (depth == 0) bestBoard = moves.get(ind);
            return countAI;
        } else {
            for (board i : moves) {
                double score = brainShtorm(1, depth + 1, countAI, countPlayer, i);
                if (score < countPlayer) countPlayer = score;
                if (countAI >= countPlayer) break;
            }
            return countPlayer;
        }
    }
}
