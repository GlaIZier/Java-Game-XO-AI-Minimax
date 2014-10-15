package org.hexlet.gamexo.ai.minimaxway;

import org.hexlet.gamexo.ai.IBrainAI;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

    /* ***** ОПИСАНИЕ *******
            Программа строит дерево рекурсий на основе максиминной стратегии на глубину, задаваемую при вызове функции в
         параметре depth. Процедура осуществляет поиск в глубину. Она не отлажена, не протестирована, должна работать
         пока только для Х. Но зато должна работать для любого поля, на любую глубину. Еще она будет работать довольно
         медленно, сложность O(2^n), и использовать много памяти для стека вызовов.

         Идея: на каждой итерации проходимся по всему массиву в поисках пустой ячейки. При нахождении проверяем, какую
         фигуру надо поставить. Соответственно ставим ее. Если мы на самой вершине дерева, то мы запоминаем позицию,
         куда поставили Х, так как, возможно, это лучший ход. Оцениваем эвристику данного хода, записываем в deltaWeight.
         Далее идет рекурсивный вызов, при котором глубина уменьшается на 1, передается поле с установленной на нее
         фигурой (в первом случае Х), передается текущий вес ходов по пути от вершины до листа (на глубину depth),
         увеличенный на deltaWeight, и запомненный лучший ход.

         Когда добираемся до 0 глубины, оцениваем вес проделанного пути. Соответственно запоминаем максимальный вес и
         лучший ход. Здесь проиходит завершение рекурсивной функции, делается следующая итерация, поле и все веса
         возвращаются на шаг назад.
     */

public class Minimax implements IBrainAI {

   public static final char DEFAULT_CELL_VALUE = '_';

   public static final char VALUE_X = 'X';

   public static final char VALUE_O = 'O';

   public static final char VALUE_DRAW = 'D';

   public static final int ROW_COORD = 0;

   public static final int COL_COORD = 1;

   // Tests

   private static final String FILE_NAME = "minimax_test.log";

   private static final int TESTS_NUMBER = 25;

   private static final int DEFAULT_FIELD_SIZE = 3;

   // Instance fields

   private boolean isAIFigureX = true;

   private final int SEARCH_DEPTH = 5;

   private int bestStepRating = 0;

   private int[] bestStep = {-1, -1};

   private int worstCaseStepRating = 0;

//*************REMAKE Random dummy*********************************
/*
   private final int[] MOVE = new int[2];    // координаты следующих ходов
   private GetterLastEnemyMove getterLastEnemyMove;
   private char signBot; // за кого играет бот. Х либо О


   public int[] findMove(char[][] fieldMatrix) {

      signBot = GameField.getSignForNextMove();
      //        This is what you calculate
      MOVE[ROW_COORD] = (int) Math.floor(Math.random() * fieldMatrix.length);
      MOVE[COL_COORD] = (int) Math.floor(Math.random() * fieldMatrix.length);

      //        checkout for random - it isn't needed for real AI
      if (GameField.isCellValid(MOVE[ROW_COORD], MOVE[COL_COORD])) {
         int[] lastEnemyMove = getterLastEnemyMove.getLastEnemyMove(fieldMatrix);
         if (null != lastEnemyMove) {
            //do something
         }

         System.out.println("Spare::findMove MOVE[ROW_COORD] = " + MOVE[ROW_COORD] + " findMove MOVE[COL_COORD] = " + MOVE[COL_COORD] + " signBot = " + signBot);
         getterLastEnemyMove.setMyOwnMove(MOVE[ROW_COORD], MOVE[COL_COORD], signBot);
      }
      return MOVE;
   }

/*

//    **********End of random dummy******************************************


   /**
    * Constructs minimax AI instance with a given figure
    * @param isAIFigureX boolean - true if AI figure = X
    */
   public Minimax(boolean isAIFigureX) {
      this.isAIFigureX = isAIFigureX;
   }

   /**
    * Returns computer step using MaxMin strategy
    * @param fieldMatrix  char[][] - current field.
    * @return int[] with coords of computer step {row coordinate, column coordinate}.
    */

   public int[] findMove(char[][] fieldMatrix) {
      // clean from old steps, if we've called this method earlier
      bestStepRating = 0;
      bestStep[ROW_COORD] = -1;
      bestStep[COL_COORD] = -1;

      searchSimpleSolutions(fieldMatrix);
      if (Math.abs(bestStepRating) > 0) return bestStep;
      minimaxRecursiveSearch(fieldMatrix, SEARCH_DEPTH, bestStepRating, bestStep);
      // if minimax hasn't found best step
      if ( (bestStepRating == 0) ||
         // or if minimax has chosen engaged  cell (It is error, but program must not fail)
         (fieldMatrix[ bestStep[ROW_COORD] ][ bestStep[COL_COORD] ] != DEFAULT_CELL_VALUE) )  {
            randomStep(fieldMatrix);
      }
      return bestStep;
   }

   /**
    * Search for situations winning in one step or loosing in one step
    * @param field - Game field
    */
   private void searchSimpleSolutions(char[][] field) {
      char aiFigure = VALUE_O;
      char rivalFigure = VALUE_X;
      if (isAIFigureX) {
         aiFigure = VALUE_X;
         rivalFigure = VALUE_O;
      }
      int[] aiWinnerStep = {-1, -1};
      int[] rivalWinnerStep = {-1, -1};

      for (int row = 0; row < field.length; row++) {
         for (int col = 0; col < field[row].length; col++) {
            if (field[row][col] == DEFAULT_CELL_VALUE) {
               int[] stepTaken = {row, col};
               field[row][col] = aiFigure;
               if (Game.winner(field, stepTaken) == aiFigure) aiWinnerStep = stepTaken;
               field[row][col] = rivalFigure;
               if (Game.winner(field, stepTaken) == rivalFigure) rivalWinnerStep = stepTaken;
               field[row][col] = DEFAULT_CELL_VALUE;
            }
         }
      }

      if (aiWinnerStep[ROW_COORD] != -1) {
         bestStep = aiWinnerStep;
         field[ aiWinnerStep[ROW_COORD]][aiWinnerStep[COL_COORD] ] =  aiFigure;
         bestStepRating = Heuristic.stepRaiting(field, isAIFigureX, aiWinnerStep, 1);
         field[ aiWinnerStep[ROW_COORD]][aiWinnerStep[COL_COORD] ] = DEFAULT_CELL_VALUE;
      }
      else if (rivalWinnerStep[ROW_COORD] != -1) {
         bestStep = rivalWinnerStep;
         // place ai figure so rival won't win
         field[ rivalWinnerStep[ROW_COORD]][rivalWinnerStep[COL_COORD] ] =  aiFigure;
         bestStepRating = Heuristic.stepRaiting(field, isAIFigureX, rivalWinnerStep, 1);
         field[ rivalWinnerStep[ROW_COORD]][rivalWinnerStep[COL_COORD] ] = DEFAULT_CELL_VALUE;
      }
   }

   /**
    *
    * Algorithm
    * With recursion create tree of possible fields and estimate them using Heuristic class (heuristic coefficient)
    * Choose the best step based on the future best possible field
    *
    * @param field - Game field
    * @param depth - Current depth of search
    * @param rating - Current rating of the step
    * @param bestStep - Current best step
    */
   private void minimaxRecursiveSearch(char[][] field, int depth, int rating, int[] bestStep) {
      if (depth != 0) {
         boolean defaultCellWasFound = false;
         for (int row = 0; row < field.length; row++) {
            for (int col = 0; col < field[row].length; col++) {
               if (field[row][col] == DEFAULT_CELL_VALUE) {
                  defaultCellWasFound = true;
                  int[] step = {row, col};
                  if ( (SEARCH_DEPTH - depth) % 2 == 0) {
                     // AI figure's step
                     if (isAIFigureX) field[row][col] = VALUE_X;
                     else field[row][col] = VALUE_O;
                     // if it is first step then remember it
                     if (SEARCH_DEPTH == depth) bestStep = step;
                  }
                  else {
                     // NOT AI figure's step
                     if (isAIFigureX) field[row][col] = VALUE_O;
                     else field[row][col] = VALUE_X;
                  }
                  int stepRating = Heuristic.stepRaiting(field, isAIFigureX, step, SEARCH_DEPTH - depth + 1);
                  //
                  if (Math.abs(stepRating) > 1) {
                     rememberBestStep(rating + stepRating, bestStep);
                     field[row][col] = DEFAULT_CELL_VALUE;
                     return;
                  }
                  minimaxRecursiveSearch(field, depth - 1, rating + stepRating, bestStep);
                  // reverse field changes
                  field[row][col] = DEFAULT_CELL_VALUE;
               }
            }
         }
         if (!defaultCellWasFound) {
            rememberBestStep(rating, bestStep);
         }
      }
      rememberBestStep(rating, bestStep);
   }

   private void rememberBestStep(int rating, int[] bestStep) {
      if ( ( (isAIFigureX) && (rating > this.bestStepRating) ) ||
      ( (!isAIFigureX) && (rating < this.bestStepRating) ) ) {
         bestStepRating = rating;
         this.bestStep = bestStep;
      }
   }

   /**
    * Random algorithm for choosing step
    * @param field - Game field
    */
   private void randomStep(char[][] field) {
      Random random = new Random();
      int[] stepTaken = new int[2];
      while (true) {
         stepTaken[ROW_COORD] = random.nextInt(field.length);
         stepTaken[COL_COORD] = random.nextInt(field.length);
         if (field[ stepTaken[COL_COORD] ][ stepTaken[COL_COORD] ] == DEFAULT_CELL_VALUE) {
            bestStep = stepTaken;
            return;
         }
      }
   }



   @Test
   public static void main(String[] args) {
      System.out.println("Input test mode: \n1. Random testing to log file.\n2. User testing");
      Scanner scan = new Scanner(System.in);
      int inputNum;
      try {
         inputNum = scan.nextInt();
      }
      catch (InputMismatchException e) {
         System.out.println("Wrong number!");
         return;
      }
      try {
         if (inputNum == 1) {
            autoLogTesting();
         } else if (inputNum == 2) {
            userTesting();
         }
         else {
            System.out.println("Wrong number!");
            return;
         }
      }
      catch (IOException e) {
         e.printStackTrace();
         return;
      }
      catch (Exception e) {
         e.printStackTrace();
         return;
      }
   }

   private static void autoLogTesting() throws IOException {
      File file = new File(FILE_NAME);
      file.createNewFile();
      FileWriter fw = new FileWriter(file.getAbsoluteFile());
      BufferedWriter bw = new BufferedWriter(fw);

      try {
         for (int testNum = 0; testNum < TESTS_NUMBER; testNum++) {
            autoTest(bw, testNum);
         }
      }
      finally {
         bw.close();
      }
      System.out.println("Log test was done successfully!");
   }

   private static void autoTest(BufferedWriter bw, int testNum) throws IOException {
      char[][] field = new char[DEFAULT_FIELD_SIZE][DEFAULT_FIELD_SIZE];
      clearField(field);

      bw.write("////////////// TEST NUMBER " + testNum + " //////////////");
      bw.newLine();
      boolean isMinimaxStep = true;
      char minimaxFigure = VALUE_X;
      Minimax minimax = new Minimax(true);
      if (testNum % 2 == 0) {
         isMinimaxStep = false;
         minimaxFigure = VALUE_O;
         minimax = new Minimax(false);
      }

      int stepNum = 1;
      char winner = DEFAULT_CELL_VALUE;
      int[] stepTaken = new int[2];
      while ( (winner == DEFAULT_CELL_VALUE) && (stepNum <= field.length * field.length) ) {
         bw.write("Step  " + stepNum + ")");
         bw.newLine();

         if (isMinimaxStep) {
            stepTaken[ROW_COORD] = minimax.findMove(field)[ROW_COORD];
            stepTaken[COL_COORD] = minimax.findMove(field)[COL_COORD];
            if (minimaxFigure == VALUE_X) {
               field[ stepTaken[ROW_COORD] ][ stepTaken[COL_COORD] ] = VALUE_X;
            }
            else {
               field[ stepTaken[ROW_COORD] ][ stepTaken[COL_COORD] ] = VALUE_O;
            }
            bw.write("Minimax goes to [ " + stepTaken[ROW_COORD] + " ] [ " + stepTaken[COL_COORD] + " ]");
            bw.newLine();
            isMinimaxStep = false;
         }
         else {
            while (true) {
               Random random = new Random();
               stepTaken[ROW_COORD] = random.nextInt(field.length);
               stepTaken[COL_COORD] = random.nextInt(field.length);
               if (field[ stepTaken[ROW_COORD] ][ stepTaken[COL_COORD] ] == DEFAULT_CELL_VALUE) {
                  if (minimaxFigure == VALUE_X) {
                     field[ stepTaken[ROW_COORD] ][ stepTaken[COL_COORD] ] = VALUE_O;
                  }
                  else {
                     field[ stepTaken[ROW_COORD] ][ stepTaken[COL_COORD] ] = VALUE_X;
                  }
                  bw.write("Random goes to [ " + stepTaken[ROW_COORD] + " ] [ " + stepTaken[COL_COORD] + " ]");
                  bw.newLine();
                  isMinimaxStep = true;
                  break;
               }
            }
         }
         fieldToFile(field, bw);
         bw.newLine();
         stepNum++;
         winner = Game.winner(field, stepTaken);
      }

      bw.write(winner + " wins!");
      bw.newLine();
      bw.newLine();
      bw.newLine();
   }

   private static void fieldToFile(char[][] field, BufferedWriter bw) throws IOException {
      for (int row = 0; row < field.length; row++) {
         for (int col = 0; col < field.length; col++) {
            bw.write("[ " + field[row][col] + " ] ");
         }
         bw.newLine();
      }
      bw.newLine();
   }

   private static void userTesting() {
      char[][] field = {
              {VALUE_O, DEFAULT_CELL_VALUE, DEFAULT_CELL_VALUE},
              {DEFAULT_CELL_VALUE, DEFAULT_CELL_VALUE, DEFAULT_CELL_VALUE},
              {VALUE_X, DEFAULT_CELL_VALUE, VALUE_X},
      };
      Minimax minimax = new Minimax(true);
      int[] minimaxStep = minimax.findMove(field);
      System.out.println(minimaxStep[ROW_COORD] + " " + minimaxStep[COL_COORD]);
      printField(field);
      clearField(field);

      System.out.println("Enter field size: ");
      Scanner scan = new Scanner(System.in);
      int fieldSize = scan.nextInt();
      field = new char[fieldSize][fieldSize];
      minimax = new Minimax(false);
      clearField(field);
      printField(field);

      int cnt = 1;
      int[] stepTaken = new int[2];
      while (cnt <= (fieldSize * fieldSize)) {
         if (cnt % 2 == 0) {
            stepTaken = minimax.findMove(field);
            System.out.println("Computer has stepped to: " + "{" +
                    stepTaken[ROW_COORD] + "," + stepTaken[COL_COORD] + "}");
            field[stepTaken[0]][stepTaken[1]] = 'O';
         }
         else {
            try {
               System.out.print("Enter row: ");
               stepTaken[0] = scan.nextInt();
               System.out.print("Enter col: ");
               stepTaken[1] = scan.nextInt();
            }
            catch (InputMismatchException e) {
               return;
            }
            field[stepTaken[0]][stepTaken[1]] = 'X';
         }
         printField(field);
         cnt++;
      }
   }

   private static void  clearField(char[][] field) {
      for (int row = 0; row < field.length; row++) {
         for (int col = 0; col < field[row].length; col++) {
            field[row][col] = DEFAULT_CELL_VALUE;
         }
      }
   }

   private static void printField(char[][] field) {
      for (int row = 0; row < field.length; row++) {
         for (int col = 0; col < field.length; col++) {
            System.out.print("[" + field[row][col] + "]");
         }
         System.out.println();
      }
   }

}