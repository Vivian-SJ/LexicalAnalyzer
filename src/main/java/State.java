import java.util.*;

/**
 * Created by vivian on 2017/10/31.
 * <p>
 * 用于表示一个状态
 */
public class State {
    private int stateId;
    private Set<State> states;
    //状态A通过字母c转换为状态B，则表示为Set<c, A> = B
    private Map<Character, ArrayList<State>> nextState;
    private boolean acceptState;

    public State(int id) {
        this.stateId = id;
        this.nextState = new HashMap<Character, ArrayList<State>>();
        this.acceptState = false;
    }

    public int getStateId() {
        return stateId;
    }

    public void setStateId(int stateId) {
        this.stateId = stateId;
    }

    public Set<State> getStates() {
        return states;
    }

    public void setStates(Set<State> states) {
        this.states = states;
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
}
