import java.util.*;

/**
 * Created by vivian on 2017/11/1.
 */
public class Analyzer {
    private static int stateId = 0;
    private static Stack<Character> operators = new Stack<Character>();
    private static Stack<NFA> nfas = new Stack<NFA>();
    private static Set<Character> inputSymbol = new HashSet<Character>();

    private static NFA REToNFA(String re) {
        re = addDotToRE(re);

        nfas.clear();
        operators.clear();

        for (int i = 0; i < re.length(); i++) {
            if (isLetter(re.charAt(i))) {
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
//                Operator o = Operator.getOperator('|');
//                int ii = Operator.getPrivilege(o);
                while (!operators.empty() && Operator.getPrivilege(Operator.getOperator(operators.peek())) >= Operator.getPrivilege(Operator.getOperator(re.charAt(i)))) {
                    doOperation();
                }
                operators.push(re.charAt(i));
            }
        }

        while (!operators.isEmpty()) {	doOperation(); }

        NFA nfa = nfas.pop();

        //指定终态为接受态
        nfa.getNfaStates().getLast().setAcceptState(true);

        return nfa;
    }

    //用'.'标注出连接运算
    private static String addDotToRE(String re) {
        String newRE = "";

        for (int i = 0; i < re.length() - 1; i++) {
            if (isLetter(re.charAt(i)) && isLetter(re.charAt(i + 1))) {
                newRE = newRE + re.charAt(i) + '.';
            } else if (isLetter(re.charAt(i)) && re.charAt(i + 1) == '(') {
                newRE = newRE + re.charAt(i) + '.';
            } else if (re.charAt(i) == ')' && isLetter(re.charAt(i + 1))) {
                newRE = newRE + re.charAt(i) + '.';
            } else if (re.charAt(i) == ')' && re.charAt(i + 1) == '(') {
                newRE = newRE + re.charAt(i) + '.';
            } else if (re.charAt(i) == '*' && re.charAt(i + 1) == '(') {
                newRE = newRE + re.charAt(i) + '.';
            } else if (re.charAt(i) == '*' && isLetter(re.charAt(i + 1))) {
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
        inputSymbol.add('a');
        inputSymbol.add('b');

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

    private static Set<State> addTransition (char symbol, Set<State> originState) {
        Set<State> transState = new HashSet<State>();
        for (State s : originState) {
            ArrayList<State> followState = s.getNextStatesBySymbol(symbol);
            if (followState.size()!=0) {
                for (State s1 : followState) {
                    transState.add(s1);
                }
            }
        }
        return transState;
    }

    private static void DFAToDFAO(DFA dfa) {
        //F表示终态，NF表示非终态
        Group F = new Group();
        Group NF = new Group();

        //为了优化方便，先统一用id来代表DFA中的每个状态，需要的时候再通过id得到state
        //此处也把DFA分为初始的终态和非终态
        Map<Integer, State> states = new HashMap<Integer, State>();
        for (State s : dfa.getDfaStates()) {
            states.put(s.getStateId(), s);
            for (State s1 : s.getStatesForDFA()) {
                if (s1.isAcceptState()) {
                    F.getStateId().add(s1.getStateId());
                } else {
                    NF.getStateId().add(s1.getStateId());
                }
            }
        }

        List<Group> level = new ArrayList<Group>();
        level.add(F);
        level.add(NF);

    }
    public static void main(String[] args) {
//        String s = Analyzer.addDotToRE("(a|b)*abb(a|b)*");
//        System.out.println(nfa.getNfaStates().size());

        NFA nfa = Analyzer.REToNFA("(a|b)*a");
//        for (State s : nfa.getNfaStates()) {
//            s.print();
//            System.out.println();
//        }
//        System.out.println(s);

//        Set<State> originState = new HashSet<State>();
//        originState.add(nfa.getNfaStates().getFirst());
//        Set<State> states = Analyzer.epsilonClosure(originState);
//        Set<State> states1 = Analyzer.addTransition('a', states);
//        for (State s : states) {
//            System.out.print(s.getStateId() + " ");
//        }
//        System.out.println();
//
//        for (State s : states1) {
//            System.out.print(s.getStateId() + " ");
//        }

        DFA dfa = Analyzer.NFAToDFA(nfa);
        System.out.println(dfa.getDfaStates().size());
    }
}
