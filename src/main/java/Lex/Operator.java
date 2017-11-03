package Lex;

/**
 * Created by vivian on 2017/11/1.
 */
public enum Operator {
    UNION('|', 1),
    CONCATENATION('.', 0),
    CLOSURE('*', 2),
    LEFTBRACKET('(', -1);

    private char symbol;
    private int privilege;

    Operator(char symbol, int privilege) {
        this.symbol = symbol;
        this.privilege = privilege;
    }

    public static Operator getOperator(char c) {
        for (Operator o : Operator.values()) {
            if (o.symbol == c) {
                return o;
            }
        }
        return null;
    }

    public static int getPrivilege(Operator o) {
        if (o == null) {
            System.out.println("NULL OPERATOR!");
        }
        return o.getPrivilege();
    }

    public char getSymbol() {
        return symbol;
    }

    public void setSymbol(char symbol) {
        this.symbol = symbol;
    }

    public int getPrivilege() {
        return privilege;
    }

    public void setPrivilege(int privilege) {
        this.privilege = privilege;
    }
}
