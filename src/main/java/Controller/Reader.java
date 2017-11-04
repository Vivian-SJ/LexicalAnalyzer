package Controller;

import Common.Type;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by vivian on 2017/11/4.
 */
public class Reader {
    public static int[][] readTable(String fileName){
        File file = new File(fileName);
        int row = 0;
        int column = 0;
        int[][] table = null;

        try {
            Scanner scanner = new Scanner(file);
            row = scanner.nextInt();
            column = scanner.nextInt();
            table = new int[row][column];

            for (int i = 0; i < row; i++) {
                for (int j = 0; j < column; j++) {
                    table[i][j] = scanner.nextInt();
                }
            }

            while (scanner.hasNext()){
                String next = scanner.next();
                if(next.equals("@")){
                    break;
                }
//                Type.addType(next);
            }

            while (scanner.hasNext()){
                Type.addKeywords(scanner.next());
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return table;
    }
}
