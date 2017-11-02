import java.util.LinkedList;

/**
 * Created by vivian on 2017/10/31.
 */
public class DFA {
    private LinkedList<State> dfaStates;

    public DFA() {
        this.dfaStates = new LinkedList<State>();
    }

    public LinkedList<State> getDfaStates() {
        return dfaStates;
    }

    public void setDfaStates(LinkedList<State> dfaStates) {
        this.dfaStates = dfaStates;
    }
}
