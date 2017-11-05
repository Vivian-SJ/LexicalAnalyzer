package Controller;

import Common.Type;

import java.io.*;
import java.util.Scanner;

/**
 * Created by vivian on 2017/11/4.
 */
public class Reader {
    public static char[] readSymbolLine(String fileName) {
        char[] symbolLine = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
            String s = reader.readLine().replace(" ", "");
            s = s.substring(0,s.length()-1);
            symbolLine = s.toCharArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return symbolLine;
    }

    public static int[][] readTable(String fileName){
        File file = new File(fileName);
        int row = 0;
        int column = 0;
        int[][] table = null;

        try {
            Scanner scanner = new Scanner(file);
            scanner.nextLine();
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

    public static String readInput(String fileName) {
        String encoding = "UTF-8";
        File file = new File(fileName);
        Long fileLength = file.length();
        byte[] fileContent = new byte[fileLength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(fileContent);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            return new String(fileContent, encoding);
        } catch (UnsupportedEncodingException e) {
            System.err.println("The OS does not support " + encoding);
            e.printStackTrace();
            return "";
        }
    }
}
