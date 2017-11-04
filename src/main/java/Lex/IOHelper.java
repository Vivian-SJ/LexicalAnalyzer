package Lex;

import Common.Type;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vivian on 2017/11/4.
 */
public class IOHelper {
    private static String[] keywords;
    /**
     * 读取.l文件
     *
     * @param fileName .l文件路径
     * @return 后缀正则表达式列表
     */
    public static List<String> readLFile(String fileName) {
        ArrayList<String> result = new ArrayList<String>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
            String re = "";
            while ((re = reader.readLine()) != null) {
                if (re.startsWith("$")) {
                    String[] express = re.substring(1).split("=");
                    Type.addType(express[0].trim());
                    result.add(express[1].trim());
                }
                if (re.startsWith("%")) {
                    keywords = re.substring(1).split(",");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 创建.t表文件
     *
     * @param table  表数组
     * @param row    表的行数
     * @param column 表的列数
     * @param path   .t表文件的路径
     */
    public static void buildTableFile(int[][] table, int row, int column, char[] symbolLine, String path) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(new File(path));
            for (int i=0;i<symbolLine.length;i++) {
                writer.write(symbolLine[i] + " ");
            }
            writer.write("\n");
            writer.write(row + " " + column + "\n");
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < column; j++) {
                    writer.write(table[i][j] + " ");
                }
                writer.write("\n");
            }

            for (int i = 0; i < Type.getTypeNum(); i++) {
                writer.write(Type.getType(-i-1) + " ");
            }
            writer.write('\n');
            writer.write("@ ");
            for (String keyword : keywords) {
                writer.write(keyword.trim() + " ");
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
