import java.util.LinkedList;

/**
 * Created by vivian on 2017/10/31.
 */
public class NFA {
    private LinkedList<State> nfaStates;

    public NFA() {
        this.nfaStates = new LinkedList<State>();
    }

    public LinkedList<State> getNfaStates() {
        return nfaStates;
    }

    public void setNfaStates(LinkedList<State> nfaStates) {
        this.nfaStates = nfaStates;
    }

}
