package Common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by vivian on 2017/11/4.
 * <p>
 * 该类用于记录.l文件中规定的所有正则字符串的名称
 */
public class Type {
    private static Set<String> keywords = new HashSet<String>();

    private static ArrayList<String> typeList = new ArrayList<String>();

    //添加正则表达式名
    public static void addType(String name) {
        typeList.add(name);
    }

    //通过数字编号获得type名称
    public static String getType(int i) {
        switch (i) {
            case -1:
                return "ID";
            case -2:
                return "INTEGER";
            case -3:
                return "OPERATOR";
            case -10:
                return "ERROR";
            default:
                System.out.println("WRONG RE !");
                break;
        }
        return "";
    }

    //添加关键字
    public static void addKeywords(String keyword) {
        keywords.add(keyword);
    }

    //判断字符串是否是keyword
    public static boolean isKeyword(String text) {
        return keywords.contains(text);
    }

    public static int getTypeNum() {
        return typeList.size();
    }
}
