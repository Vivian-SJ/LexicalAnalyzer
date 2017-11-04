package Lex;

import Common.Type;

import java.util.*;

/**
 * Created by vivian on 2017/11/1.
 */
public class Analyzer {
    //.l文件的位置
    private static final String L_PATH = "/Users/vivian/Desktop/lexicalAnalyzer/src/main/resources/re.l";

    //转换表文件的位置
    private static final String TABLE_PATH = "table.t";

    private static int stateId = 0;
    private static Stack<Character> operators = new Stack<Character>();
    private static Stack<NFA> nfas = new Stack<NFA>();
    private static Set<Character> inputSymbol = new HashSet<Character>();

    //关于DFA转换表的一些变量
    private static int column;
    private static int row;
    private static int[][] table;

    //为了优化方便，统一用id来代表DFA中的每个状态，需要的时候再通过id得到state
    private static Map<Integer, State> states = new HashMap<Integer, State>();

    // 记录最终状态
    // key ----- 最终状态号
    // value ----- 最终状态表征的token type
    private static HashMap<Integer, String> finalStates = new HashMap<Integer, String>();

    private static void start() {
        List<String> REs = IOHelper.readLFile(L_PATH);
//        for (String s : REs) {
//            System.out.println(s);
//        }
        inputSymbol.add('a');
        inputSymbol.add('b');
        inputSymbol.add('1');
        inputSymbol.add('2');
        inputSymbol.add('+');

        NFA nfa = mergeNFA(REs);
        System.out.println("nfa状态数：" + nfa.getNfaStates().size());
        DFA dfa = NFAToDFA(nfa);
        System.out.println("dfa状态数：" + dfa.getDfaStates().size());
        int[][] table = DFATable(dfa);
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[0].length; j++) {
                System.out.print(table[i][j] + " ");
            }
            System.out.println();
        }
    }

    private static NFA mergeNFA(List<String> REs) {
        Stack<NFA> tempNfas = new Stack<NFA>();
        for (int i = 0; i < REs.size(); i++) {
            NFA tempNFA = REToNFASingle(REs.get(i), Type.getType(i));
            tempNfas.push(tempNFA);
        }

        if (nfas.empty()) {
            while (!tempNfas.empty()) {
                NFA nfa = tempNfas.pop();
                nfas.push(nfa);
            }
        } else {
            System.out.println("ERROR!");
        }

        int count = nfas.size();
        for (int i = 0; i < count - 1; i++) {
            union();
        }
        NFA finalNFA = nfas.pop();

        return finalNFA;
    }

    private static NFA REToNFASingle(String re, String type) {
        re = addDotToRE(re);
//        System.out.println(re);

        nfas.clear();
        operators.clear();

        for (int i = 0; i < re.length(); i++) {
            if (isLetter(re.charAt(i)) || isNum(re.charAt(i)) || isOperator(re.charAt(i))) {
                createNFA(re.charAt(i));
            } else if (operators.empty()) {
                operators.push(re.charAt(i));
            } else if (re.charAt(i) == '(') {
                operators.push(re.charAt(i));
            } else if (re.charAt(i) == ')') {
                while (operators.peek() != '(') {
                    //执行括号内的运算
                    doOperation();
                }
                operators.pop();
            } else {
                //若栈顶运算符的优先级高于新出现的运算符的优先级，就执行运算
                while (!operators.empty() && Operator.getPrivilege(Operator.getOperator(operators.peek())) >= Operator.getPrivilege(Operator.getOperator(re.charAt(i)))) {
                    doOperation();
                }
                operators.push(re.charAt(i));
            }
        }

        while (!operators.isEmpty()) {
            doOperation();
        }

        NFA nfa = nfas.pop();

        //指定终态为接受态，并打上类型标记
        nfa.getNfaStates().getLast().setAcceptState(true);
        finalStates.put(nfa.getNfaStates().getLast().getStateId(), type);
        return nfa;
    }

    //用'.'标注出连接运算
    private static String addDotToRE(String re) {
        String newRE = "";

        for (int i = 0; i < re.length() - 1; i++) {
            if (isSymbol(re.charAt(i)) && isSymbol(re.charAt(i + 1))) {
                newRE = newRE + re.charAt(i) + '.';
            } else if (isSymbol(re.charAt(i)) && re.charAt(i + 1) == '(') {
                newRE = newRE + re.charAt(i) + '.';
            } else if (re.charAt(i) == ')' && isSymbol(re.charAt(i + 1))) {
                newRE = newRE + re.charAt(i) + '.';
            } else if (re.charAt(i) == ')' && re.charAt(i + 1) == '(') {
                newRE = newRE + re.charAt(i) + '.';
            } else if (re.charAt(i) == '*' && re.charAt(i + 1) == '(') {
                newRE = newRE + re.charAt(i) + '.';
            } else if (re.charAt(i) == '*' && isSymbol(re.charAt(i + 1))) {
                newRE = newRE + re.charAt(i) + '.';
            } else {
                newRE = newRE + re.charAt(i);
            }
        }

        newRE = newRE + re.charAt(re.length() - 1);
        return newRE;
    }

    //判断是不是字母
    private static boolean isLetter(Character c) {
        if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
            return true;
        } else {
            return false;
        }
    }

    //判断是不是数字
    private static boolean isNum(Character c) {
        if (c >= '0' && c <= '9') {
            return true;
        } else {
            return false;
        }
    }

    //判断是不是运算符（加减乘除）
    private static boolean isOperator(Character c) {
        if (c == '+') {
            return true;
        } else {
            return false;
        }
    }

    //letter, number, operator 都是symbol
    private static boolean isSymbol(Character c) {
        if (isOperator(c) || isNum(c) || isLetter(c)) {
            return true;
        } else {
            return false;
        }
    }

    //当遇到字母时，生成新的NFA状态并入栈
    private static void createNFA(char c) {
        State state1 = new State(stateId);
        stateId++;
        State state2 = new State(stateId);
        stateId++;

        state1.setNextState(c, state2);

        NFA tempNFA = new NFA();
        tempNFA.getNfaStates().add(state1);
        tempNFA.getNfaStates().add(state2);
        nfas.push(tempNFA);

    }

    //遇到运算符时，根据运算符做相应的处理
    private static void doOperation() {
        Operator operator = Operator.getOperator(operators.pop());
        switch (operator) {
            case UNION:
                union();
                break;
            case CONCATENATION:
                concatenation();
                break;
            case CLOSURE:
                closure();
                break;
            default:
                System.out.println("Unkown Symbol !");
                System.exit(1);
                break;
        }
    }

    //对UNION的处理
    private static void union() {
        //建立一个开始状态和结束状态
        State start = new State(stateId);
        stateId++;
        State end = new State(stateId);
        stateId++;

        //取出栈顶的两个NFA准备做运算
        NFA nfa2 = nfas.pop();
        State state2Start = nfa2.getNfaStates().getFirst();
        State state2End = nfa2.getNfaStates().getLast();
        NFA nfa1 = nfas.pop();
        State state1Start = nfa1.getNfaStates().getFirst();
        State state1End = nfa1.getNfaStates().getLast();

        //将开始状态、结束状态分别与已有的状态建立转换关系，e代表epsilon
        start.setNextState('e', state1Start);
        start.setNextState('e', state2Start);
        state1End.setNextState('e', end);
        state2End.setNextState('e', end);

        //把所有状态合并为一个新的NFA并压栈
        nfa1.getNfaStates().addFirst(start);
        nfa2.getNfaStates().addLast(end);
        for (State stateTemp : nfa2.getNfaStates()) {
            nfa1.getNfaStates().addLast(stateTemp);
        }

        nfas.push(nfa1);
    }

    //对CONCATENATION的处理
    private static void concatenation() {
        //取出栈顶的两个NFA准备做运算
        NFA nfa2 = nfas.pop();
        State state2Start = nfa2.getNfaStates().getFirst();
        NFA nfa1 = nfas.pop();
        State state1End = nfa1.getNfaStates().getLast();

        //将开始状态、结束状态分别与已有的状态建立转换关系，e代表epsilon
        state1End.setNextState('e', state2Start);

        //把所有状态合并为一个新的NFA并压栈
        for (State stateTemp : nfa2.getNfaStates()) {
            nfa1.getNfaStates().addLast(stateTemp);
        }

        nfas.push(nfa1);
    }

    //对CLOSURE的处理
    private static void closure() {
        //建立一个开始状态和结束状态
        State start = new State(stateId);
        stateId++;
        State end = new State(stateId);
        stateId++;

        //取出栈顶的NFA准备做运算
        NFA currentNFA = nfas.pop();
        State currentNFAStart = currentNFA.getNfaStates().getFirst();
        State currentNFAEnd = currentNFA.getNfaStates().getLast();

        //将开始状态、结束状态分别与已有的状态建立转换关系，e代表epsilon
        start.setNextState('e', end);
        start.setNextState('e', currentNFAStart);
        currentNFAEnd.setNextState('e', currentNFAStart);
        currentNFAEnd.setNextState('e', end);

        //把所有状态合并为一个新的NFA并压栈
        currentNFA.getNfaStates().addFirst(start);
        currentNFA.getNfaStates().addLast(end);

        nfas.push(currentNFA);
    }

    private static DFA NFAToDFA(NFA nfa) {
        //初始化
        DFA dfa = new DFA();
        stateId = 0;
        LinkedList<State> unHandledState = new LinkedList<State>();

        //DFA的第一个状态
        Set<State> firstStateSet = new HashSet<State>();
        State start = nfa.getNfaStates().getFirst();
        firstStateSet.add(start);
        firstStateSet = epsilonClosure(firstStateSet);
        State startDFA = new State(stateId, firstStateSet);
        stateId++;
        dfa.getDfaStates().add(startDFA);
        unHandledState.add(startDFA);

        //开始循环处理
        while (!unHandledState.isEmpty()) {
            State currentState = unHandledState.removeFirst();
            for (char c : inputSymbol) {
                Set<State> set = addTransition(c, currentState.getStatesForDFA());
                if (set.size() == 0) {
                    currentState.setNextState(c, new State(-10));
                    continue;
                }

                set = epsilonClosure(set);
                //用于判断状态是否重复的变量
                boolean exist = false;
                //用于记录可能出现的已存在的状态
                State repeatState = null;
                for (State s : dfa.getDfaStates()) {
                    //两个set完全相等
                    if (s.getStatesForDFA().containsAll(set) && set.containsAll(s.getStatesForDFA())) {
                        exist = true;
                        repeatState = s;
                        break;
                    }
                }

                if (!exist) {
                    State newState = new State(stateId, set);
                    stateId++;

                    //判断该状态里是否包含终态
                    for (State s : newState.getStatesForDFA()) {
                        if (s.isAcceptState()) {
                            newState.setAcceptState(true);
                            break;
                        }
                    }

                    dfa.getDfaStates().add(newState);
                    unHandledState.add(newState);
                    currentState.setNextState(c, newState);
                } else {
                    currentState.setNextState(c, repeatState);
                }
            }
        }


        return dfa;

    }

    //寻找某个NFA状态集（也就是DFA状态核）的epsilon闭包
    private static Set<State> epsilonClosure(Set<State> originState) {
        Stack<State> stack = new Stack<State>();
        for (State s : originState) {
            stack.push(s);
        }
        while (!stack.empty()) {
            State state = stack.pop();
            ArrayList<State> epsilonStates = state.getNextStatesBySymbol('e');
            for (State s : epsilonStates) {
                if (!originState.contains(s)) {
                    originState.add(s);
                    stack.push(s);
                }
            }
        }
        return originState;
    }

    //寻找某个状态通过某个symbol转换到的下一个状态
    private static Set<State> addTransition(char symbol, Set<State> originState) {
        Set<State> transState = new HashSet<State>();
        for (State s : originState) {
            ArrayList<State> followState = s.getNextStatesBySymbol(symbol);
            if (followState.size() != 0) {
                for (State s1 : followState) {
                    transState.add(s1);
                }
            }
        }
        return transState;
    }


    private static int[][] DFATable(DFA dfa) {
        //先把所有的状态都用id表示
        for (State s : dfa.getDfaStates()) {
            states.put(s.getStateId(), s);
        }

        row = states.size();
        column = inputSymbol.size()+1;
        table = new int[row][column];

        //制表
        Character[] symbols = new Character[inputSymbol.size()];
        inputSymbol.toArray(symbols);
        for (int i=0;i<symbols.length;i++) {
            System.out.print(symbols[i] + " ");
        }
        System.out.println();
        System.out.println();
        for (int i = 0; i < row; i++) {
            //每行的第一列是所有的DFA状态，依次排列
            table[i][0] = i;
            //后面的列是转换后的状态
            for (int j = 1; j < column; j++) {
                char symbol = symbols[j - 1];
                State originState = states.get(i);
                ArrayList<State> nextStates = originState.getNextStatesBySymbol(symbol);
                int transStateID = nextStates.get(0).getStateId();
                table[i][j] = transStateID;
            }
        }

        return table;
    }

    private static int[][] DFAToDFAO(DFA dfa) {
        //建立DFA转换表
        DFATable(dfa);

        //F表示终态，NF表示非终态
        Group F = new Group();
        Group NF = new Group();

        //把DFA分为初始的终态和非终态
        for (State s : dfa.getDfaStates()) {
            if (s.isAcceptState()) {
                F.getStates().add(s.getStateId());
            } else {
                NF.getStates().add(s.getStateId());
            }
        }

        List<Group> level = new ArrayList<Group>();
        level.add(F);
        level.add(NF);

        //递归进行分组
        List<Group> newLevel = level;
        do {
            level = newLevel;
            newLevel = new ArrayList<Group>();
            for (Group g : level) {
                if (g.isStrong()) {
                    newLevel.add(g);
                } else {
                    Set<Integer> left = new HashSet<Integer>();
                    Set<Integer> right = new HashSet<Integer>();
                    divideGroup(level, g, left, right);
                    boolean isStrong = checkStrong(left);
                    Group group = new Group(left, isStrong);
                    newLevel.add(group);

                    if (!right.isEmpty()) {
                        isStrong = checkStrong(right);
                        newLevel.add(new Group(right, isStrong));
                    }
                }
            }
        } while (newLevel.size() != level.size());

//        System.out.println(level.size());

        updateTable(level);

        return table;
    }

    private static void updateTable(List<Group> level) {
        Set<Integer> redundantIds = new HashSet<Integer>();
        Map<Integer, Integer> replace = new HashMap<Integer, Integer>();

        //将同一组里的状态统一用一个状态代替
        for (Group group : level) {
            if (group.getStates().size() > 1) {
                int first = -1;
                for (int id : group.getStates()) {
                    if (first == -1) {
                        first = id;
                        continue;
                    } else {
                        redundantIds.add(id);
                        replace.put(id, first);
                    }
                }
            }
        }

        int[][] newTable = new int[row - redundantIds.size()][column];
        int newRow = 0;
        int newColumn = 0;

        //先填充新表包含的状态，即转换表的第一列
        for (int i = 0; i < row; i++) {
            if (!redundantIds.contains(table[i][0])) {
                newTable[newRow][0] = table[i][0];
                newRow++;
            }
        }

        for (int i = 0; i < newRow; i++) {
            int currentId = newTable[i][0];
            int beforeRow = 0;
            for (int n = 0; n < row; n++) {
                if (table[n][0] == currentId) {
                    beforeRow = n;
                    break;
                }
            }

            for (int j = 1; j < column; j++) {
                int stateId = table[beforeRow][j];
                if (!redundantIds.contains(stateId)) {
                    newTable[i][j] = stateId;
                } else {
                    int replaceId = replace.get(stateId);
                    newTable[i][j] = replaceId;
                }
            }
        }

        table = newTable;
    }

    /**
     * 对给定分组层及层中的一个分组进行下一步分组
     *
     * @param level       给定分组层
     * @param originGroup 层中的一个分组
     * @param left        originGroup分出的第一个组
     * @param right       originGroup分出的第二个组
     */
    private static void divideGroup(List<Group> level, Group originGroup, Set<Integer> left, Set<Integer> right) {
        boolean[] match = new boolean[column - 1];
        //按第一个来分组，若与第一个状态等价则放在左边组，不等价则分为右边组
        int first = -1;
        for (int currentId : originGroup.getStates()) {
            if (first == -1) {
                first = currentId;
                left.add(currentId);
                continue;
            } else {
                for (int j = 1; j < column; j++) {
                    if (table[first][j] == table[currentId][j]) {
                        match[j - 1] = true;
                        continue;
                    }
                    for (Group up : level) {
                        if (up.getStates().contains(table[first][j]) && up.getStates().contains(table[currentId][j])) {
                            match[j - 1] = true;
                            break;
                        }
                    }
                }

                boolean equivalent = true;
                for (boolean b : match) {
                    if (b == false) {
                        equivalent = false;
                        break;
                    }
                }

                if (equivalent) {
                    left.add(currentId);
                } else {
                    right.add(currentId);
                }

            }
        }
    }

    private static boolean checkStrong(Set<Integer> group) {
        boolean isStrong = true;
        int first = -1;
        for (Integer id : group) {
            if (first == -1) {
                first = id;
                continue;
            }
            for (int j = 1; j < column; j++) {
                if (table[first][j] != table[id][j]) {
                    isStrong = false;
                    break;
                }
            }
            if (!isStrong) {
                break;
            }
        }
        return isStrong;
    }

    public static void main(String[] args) {
//        String s = Lex.Analyzer.addDotToRE("(a|b)*abb(a|b)*");
//        System.out.println(nfa.getNfaStates().size());

//        Analyzer.start();
        Analyzer.start();
//        NFA nfa = Analyzer.REToNFASingle("(1|2)*", "Int");
//        System.out.println(nfas.empty());
//        for (Lex.State s : nfa.getNfaStates()) {
//            s.print();
//            System.out.println();
//        }
//        System.out.println(s);

//        Set<Lex.State> originState = new HashSet<Lex.State>();
//        originState.add(nfa.getNfaStates().getFirst());
//        Set<Lex.State> states = Lex.Analyzer.epsilonClosure(originState);
//        Set<Lex.State> states1 = Lex.Analyzer.addTransition('a', states);
//        for (Lex.State s : states) {
//            System.out.print(s.getStates() + " ");
//        }
//        System.out.println();
//
//        for (Lex.State s : states1) {
//            System.out.print(s.getStates() + " ");
//        }

//        DFA dfa = Analyzer.NFAToDFA(nfa);
//        int[][] table = Lex.Analyzer.DFATable(dfa);

//        int[][] table = Analyzer.DFAToDFAO(dfa);

//        System.out.println(dfa.getDfaStates().size());
    }
}
