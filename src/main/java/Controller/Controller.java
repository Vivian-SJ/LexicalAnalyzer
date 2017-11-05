package Controller;

import Common.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vivian on 2017/11/4.
 */
public class Controller {
    // 转换表文件位置
    private static final String TABLE_PATH = "/Users/vivian/Desktop/lexicalAnalyzer/src/main/resources/table.t";

    // 输入文件位置
    private static final String INPUT_PATH = "/Users/vivian/Desktop/lexicalAnalyzer/src/main/resources/input.txt";

    // 转换表
    private int[][] table;

    //找到某一个转换符对应的列的编号
    private Map<Character, Integer> columnNum = new HashMap<Character, Integer>();

    public Controller() {
        char[] symbolLine = Reader.readSymbolLine(TABLE_PATH);
        for (int i = 1; i < symbolLine.length; i++) {
            columnNum.put(symbolLine[i], i);
        }
        table = Reader.readTable(TABLE_PATH);
    }

    //生成token序列
    public List<Token> generateToken() {
        String input = Reader.readInput(INPUT_PATH);
//        System.out.println(input);
        int pointer = 0;
        int state = 0;
        int nextState = 0;
        int first = -1;

        // for error handling
//        int line = 1;
//        int loc = 1;
        //

        List<Token> result = new ArrayList<Token>();
        String current = "";

        while (pointer < input.length() - 1) {
            char c = input.charAt(pointer);
            current = current + c;
//            BasicType basicType = BasicType.fromCharToType(c);
            if (first == -1) {
                state = table[0][columnNum.get(c)];
                first = 0;
            } else {
                state = findNextState(state, c);
            }

//            state = table[state][columnNum.get(c)];

            //System.out.println(c);

            //如果进入终态或出现错误
            if (state < 0) {
                if (state == -10) {
                    System.out.println("ERROR!");
                    return null;
                }

                //要读取最长串
                pointer++;
                nextState = findNextState(state, input.charAt(pointer));
                if (nextState != -10 && state == nextState) {
                    continue;
                } else {
                    String finalType = Type.getType(state);
                    addToken(result, current, finalType);
                    current = "";
                    first = -1;
                }


//                if (basicType == BasicType.DELIMITER) {
//                    state = 0;
//                    if (!finalType.equals("ERROR")) {
//                        addToken(result, current, finalType);
//                        current = "";
//                    }
//                    pointer++;
//                    continue;
//                }


//                pointer++;
            }
            // 继续在DFA中运行
            else {
//                current = current + c;
                pointer++;
//                loc++;
            }

//            if (c == '\n') {
//                line++;
//                loc = 1;
//            }
        }

        //处理结尾的一个字符
        char c = input.charAt(input.length() - 1);
        if (first == -1) {
            state = table[0][columnNum.get(c)];
            first = 0;
        } else {
            state = findNextState(state, c);
        }
//        state = findNextState(state, c);
        if (state == -10) {
            System.out.println("ERROR!");
            return null;
        }
        current = current + c;
        String finalType = Type.getType(state);
        addToken(result, current, finalType);
//        state = table[state][BasicType.EMPTY.ordinal()];
//        String finalType = Type.intToType(state);
//        if (finalType.equals("ERROR")) {
//            errorHandling(line, loc);
//            //System.out.println("!");
//            return null;
//        }
//        addToken(result, current, finalType);
        //

        return result;
    }

    private int findNextState(int currentState, char c) {
        int state = -10;
        for (int i = 0; i < table.length; i++) {
            if (table[i][0] == currentState) {
                if (table[i][columnNum.get(c)] != -10) {
                    state = table[i][columnNum.get(c)];
                    break;
                }
            }
        }
        return state;
    }

    /**
     * 向结果集中添加一个token
     *
     * @param result    结果集
     * @param now       当前字符串
     * @param finalType 判定的类型
     */
    private void addToken(List<Token> result, String now, String finalType) {
        if (Type.isKeyword(now)) {
            result.add(new Token(now, "KEYWORD"));
        } else {
            result.add(new Token(now, finalType));
        }
    }

    public void print(List<Token> tokens) {
        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}
