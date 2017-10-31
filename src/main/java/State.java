import java.util.Map;
import java.util.Set;

/**
 * Created by vivian on 2017/10/31.
 *
 * 用于表示一个状态
 */
public class State {
    private int stateId;
    private Set<State> states;
    //状态A通过字母c转换为状态B，则表示为Set<c, A> = B
    private Map<Character, State> nextState;
    private boolean acceptState;

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

    public Map<Character, State> getNextState() {
        return nextState;
    }

    public void setNextState(Map<Character, State> nextState) {
        this.nextState = nextState;
    }

    public boolean isAcceptState() {
        return acceptState;
    }

    public void setAcceptState(boolean acceptState) {
        this.acceptState = acceptState;
    }
}
