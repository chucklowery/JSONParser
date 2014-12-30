package p.lexer;

public class LexicalToken {

    int position;
    int line;
    TokenType type;
    Object value;

    public int getPosition() {
        return position;
    }

    public int getLine() {
        return line;
    }

    public TokenType getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public String toString() {
        return line + ":" + position + " " + type + " " + (value == null ? type.getCommonValue() : value.toString());
    }

}
