package Lex;

import Common.Type;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vivian on 2017/11/4.
 */
public class IOHelper {
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
                    String[] keywords = re.substring(1).split(",");
                    for (String s : keywords) {
                        Type.addKeywords(s);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
