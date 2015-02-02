package p.lexer;

public class Terminal {

    int position;
    int line;
    TerminalType type;
    Object value;

    public int getPosition() {
        return position;
    }

    public int getLine() {
        return line;
    }

    public TerminalType getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public String toString() {
        return line + ":" + position + " " + type + " " + (value == null ? type.getCommonValue() : value.toString());
    }

}
