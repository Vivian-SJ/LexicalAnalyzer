package Controller;

import java.util.List;

/**
 * Created by vivian on 2017/11/5.
 */
public class Main {
    public static void main(String[] args) {
        Controller controller = new Controller();
        List<Token> tokens = controller.generateToken();
        controller.print(tokens);
    }
}
