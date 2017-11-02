import java.util.Stack;

/**
 * Created by vivian on 2017/11/1.
 */
public class REToDFAO {
    private static int stateId = 0;
    private static Stack<Character> operators = new Stack<Character>();
    private static Stack<NFA> nfas = new Stack<NFA>();
    private static Stack<DFA> dfas = new Stack<DFA>();

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

        //制定终态为接受态
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

    public static void main(String[] args) {
//        String s = REToDFAO.addDotToRE("(a|b)*abb(a|b)*");
        NFA nfa = REToDFAO.REToNFA("(a|b)*a");
//        System.out.println(nfa.getNfaStates().size());
        for (State s : nfa.getNfaStates()) {
            s.print();
            System.out.println();
        }
//        System.out.println(s);
    }
}
