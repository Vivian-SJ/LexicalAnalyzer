import java.util.*;

/**
 * Created by vivian on 2017/10/31.
 * <p>
 * 用于表示一个状态
 */
public class State {
    private int stateId;
    private Set<State> statesForDFA;
    //状态A通过字母c转换为状态B，则表示为Set<c, A> = B
    private Map<Character, ArrayList<State>> nextState;
    private boolean acceptState;

    //用于构造NFA
    public State(int id) {
        this.stateId = id;
        this.nextState = new HashMap<Character, ArrayList<State>>();
        this.acceptState = false;
    }

    //用于构造DFA
    public State(int id, Set<State> states) {
        this.stateId = id;
        this.statesForDFA = states;
        this.nextState = new HashMap<Character, ArrayList<State>>();
        this.acceptState = false;
    }

    public int getStateId() {
        return stateId;
    }

    public void setStateId(int stateId) {
        this.stateId = stateId;
    }

    public Set<State> getStatesForDFA() {
        return statesForDFA;
    }

    public void setStatesForDFA(Set<State> statesForDFA) {
        this.statesForDFA = statesForDFA;
    }

    public Map<Character, ArrayList<State>> getNextState() {
        return nextState;
    }

    public void setNextState(char key, State nextState) {
        ArrayList<State> states;
        if (this.getNextState().get(key) == null) {
            states = new ArrayList<State>();
        } else {
            states = this.getNextState().get(key);
        }
        states.add(nextState);
        this.nextState.put(key, states);
    }

    public boolean isAcceptState() {
        return acceptState;
    }

    public void setAcceptState(boolean acceptState) {
        this.acceptState = acceptState;
    }

    //输出一个state以及与它有连接的后续state，这个方法是测试时使用的
    public void print() {
        System.out.print(this.getStateId() + " ");
        Map<Character, ArrayList<State>> characterArrayListMap = this.getNextState();
        for (Map.Entry<Character, ArrayList<State>> entry : characterArrayListMap.entrySet()) {
            System.out.print("key: " + entry.getKey() + " ");
            ArrayList<State> states = entry.getValue();
            for (State s : states) {
                System.out.print(s.getStateId() + " ");
            }
        }
    }

    public ArrayList<State> getNextStatesBySymbol(char symbol) {
        if (this.getNextState().get(symbol) != null) {
            return this.getNextState().get(symbol);
        } else {
            return new ArrayList<State>();
        }
    }
}
