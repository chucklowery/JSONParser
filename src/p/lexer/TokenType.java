package p.lexer;

public enum TokenType {

    EOS(null),
    OPEN_BRASE('{'),
    CLOSE_BRASE('}'),
    OPEN_BRACKET('['),
    CLOSE_BRACKET(']'),
    SEMICOLON(':'),
    COMMA(','),
    NULL(null),
    TRUE(Boolean.TRUE),
    FALSE(Boolean.FALSE),
    STRING(null),
    NUMBER(null);

    Object cachedValue;

    TokenType(char token) {
        this(new char[]{token});
    }

    TokenType(Object cachedValue) {
        this.cachedValue = cachedValue;
    }

    public Object getCommonValue() {
        return cachedValue;
    }

}
