package Controller;

/**
 * Created by vivian on 2017/11/4.
 */
public class Token {
    private String content;
    private String type;

    public Token(String content, String type) {
        this.content = content;
        this.type = type;
    }

    @Override
    public String toString() {
        return "<" + type + ", " + content + ">";
    }
}
