import java.util.LinkedList;

/**
 * Created by vivian on 2017/10/31.
 */
public class NFA {
    public LinkedList<State> getNfaStates() {
        return nfaStates;
    }

    public void setNfaStates(LinkedList<State> nfaStates) {
        this.nfaStates = nfaStates;
    }

    private LinkedList<State> nfaStates;


}
