import java.util.Set;

/**
 * Created by vivian on 2017/11/2.
 *
 * 用于DFA->DFAO的分组类
 */
public class Group {
    private Set<Integer> stateId;

    public Set<Integer> getStateId() {
        return stateId;
    }

    public void setStateId(Set<Integer> stateId) {
        this.stateId = stateId;
    }
}
