package Lex;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by vivian on 2017/11/2.
 *
 * 用于DFA->DFAO的分组类
 */
public class Group {
    private Set<Integer> states;

    //强关联与否
    private boolean isStrong;

    public Group() {
        this.states = new HashSet<Integer>();
        this.isStrong = false;
    }

    public Group(Set<Integer> states, boolean isStrong) {
        this.states = states;
        this.isStrong = isStrong;
    }

    public Set<Integer> getStates() {
        return states;
    }

    public void setStates(Set<Integer> states) {
        this.states = states;
    }

    public boolean isStrong() {
        return isStrong;
    }

    public void setStrong(boolean strong) {
        isStrong = strong;
    }
}
