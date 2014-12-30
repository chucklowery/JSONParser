package p.lexer;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

public class LexerStream {

    InputStream stream;
    int position = 0;
    int line = 1;
    char rawCharacter;
    boolean hasMore = true;
    Integer lookahead = null;

    LexicalToken eos;
    CharBuffer buffer = new CharBuffer();
    char[] unicodeCharacterbuffer = new char[]{'\\', 'u', '\0', '\0', '\0', '\0'};

    public LexerStream(InputStream stream) {
        this.stream = stream;
    }

    public LexicalToken next() {
        int raw;
        while (true) {
            raw = read();
            switch (raw) {
                case -1:
                    LexicalToken token = new LexicalToken();
                    token.line = line;
                    token.position = position;

                    hasMore = false;
                    eos = token;
                    eos.type = TokenType.EOS;
                    return eos;
                case ' ':
                case '\t':
                    position++;
                    break;
                case '\n':
                    position = 0;
                    line++;
                    break;
                case '\r':
                    position = 0;
                    line++;
                    if ((raw = read()) != '\n') {
                        lookahead = raw;
                    }
                    break;
                default:
                    return parseToken(raw);
            }
        }
    }

    private LexicalToken parseToken(int raw) throws CompleteSuprise {
        LexicalToken token = new LexicalToken();
        token.line = line;
        token.position = position;

        rawCharacter = (char) raw;

        switch (rawCharacter) {
            case '{':
                return asToken(TokenType.OPEN_BRASE);
            case '}':
                return asToken(TokenType.CLOSE_BRASE);
            case '[':
                return asToken(TokenType.OPEN_BRACKET);
            case ']':
                return asToken(TokenType.CLOSE_BRACKET);
            case ':':
                return asToken(TokenType.SEMICOLON);
            case ',':
                return asToken(TokenType.COMMA);
            case '"':
                return lexStringToken();
            case 'n':
                return asLiteralToken("null", TokenType.NULL);
            case 't':
                return asLiteralToken("true", TokenType.TRUE);
            case 'f':
                return asLiteralToken("false", TokenType.FALSE);
            case '-':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case '0':
                return lexNumber(rawCharacter);
            default:
                throw new CompleteSuprise(position, line, new char[]{rawCharacter});

        }
    }

    private LexicalToken lexNumber(char rawCharacter) {
        buffer.reset();
        lookahead = (int)rawCharacter;

        lexNateralNumber();

        int c = read();
        switch (c) {
            case '.':
                buffer.append('.');
                lexUnnaturalNumber();
                break;
            case 'E':
            case 'e':
                buffer.append('E');
                lexPrecision();
            default:
                lookahead = c;
        }

        LexicalToken token = new LexicalToken();
        token.line = line;
        token.type = TokenType.NUMBER;
        token.value = new BigDecimal(new String(buffer.toArray()));
        position += buffer.length();
        return token;
    }

    private LexicalToken asLiteralToken(String value, TokenType type) {
        checkIf(value);
        return asToken(type, value.length());
    }

    private LexicalToken lexStringToken() {
        char[] chars = lexString();
        LexicalToken t = asToken(TokenType.STRING, chars.length);
        t.value = new String(chars);
        return t;
    }

    private LexicalToken asToken(TokenType type, int length) {
        LexicalToken token = new LexicalToken();
        token.line = line;
        token.position = position + length;
        token.value = null;
        token.type = type;
        return token;
    }

    private LexicalToken asToken(TokenType type) {
        return asToken(type, 1);
    }

    public void checkIf(String reserved) {
        for (int i = 1; i < reserved.length(); i++) {
            char letter = readChar();
            if (letter != reserved.charAt(i)) {
                throw new CompleteSuprise(position, line, reserved.toCharArray(), " expected *" + reserved + "* ");
            }
        }
    }

    private char[] lexString() {
        buffer.reset();

        while (true) {
            rawCharacter = readChar();
            switch (rawCharacter) {
                case '\\':
                    rawCharacter = findEscaped(readChar());
                    buffer.append(rawCharacter);
                    break;
                case '"':
                    return buffer.toArray();
                default:
                    buffer.append(rawCharacter);
            }
        }
    }

    private char findEscaped(char c) {
        switch (c) {
            case '"':
                return '"';
            case '\\':
                return '\\';
            case '/':
                return '/';
            case 'b':
                return '\b';
            case 'f':
                return '\f';
            case 'n':
                return '\n';
            case 'r':
                return '\r';
            case 't':
                return '\t';
            case 'u':
                return readUnicode();
            default:
                throw new CompleteSuprise(position + buffer.length(), line, new char[]{'\\', 'c'});
        }
    }

    private char readChar() {
        int raw = read();
        checkNotEndOfStream(raw);
        return (char) raw;
    }

    private int read() {

        if (lookahead == null) {
            try {
                return stream.read();
            } catch (IOException io) {
                throw new UnexpectedEndOfStream();
            }
        } else {
            int t = lookahead;
            lookahead = null;
            return t;
        }
    }

    private void checkNotEndOfStream(int raw) {
        if (raw == -1) {
            throw new UnexpectedEndOfStream();
        }
    }

    private char readUnicode() {
        unicodeCharacterbuffer[2] = readChar();
        unicodeCharacterbuffer[3] = readChar();
        unicodeCharacterbuffer[4] = readChar();
        unicodeCharacterbuffer[5] = readChar();

        char hex1 = (char) toHexValue(unicodeCharacterbuffer[2]);
        char hex2 = (char) toHexValue(unicodeCharacterbuffer[3]);
        char hex3 = (char) toHexValue(unicodeCharacterbuffer[4]);
        char hex4 = (char) toHexValue(unicodeCharacterbuffer[5]);

        if (checkOutOfBounds(hex1)) {
            throw new CompleteSuprise(position + 2, line, unicodeCharacterbuffer);
        }
        if (checkOutOfBounds(hex2)) {
            throw new CompleteSuprise(position + 3, line, unicodeCharacterbuffer);
        }
        if (checkOutOfBounds(hex3)) {
            throw new CompleteSuprise(position + 4, line, unicodeCharacterbuffer);
        }
        if (checkOutOfBounds(hex4)) {
            throw new CompleteSuprise(position + 5, line, unicodeCharacterbuffer);
        }

        return (char) ((hex1 << 12) + (hex2 << 8) + (hex3 << 4) + hex4);
    }

    private static boolean checkOutOfBounds(char c) {
        return (c > 16 || c < 0);
    }

    private static int toHexValue(char c) {
        if (c >= 'a') {
            return 10 + c - 'a';
        } else if (c >= 'A') {
            return 10 + c - 'A';
        } else {
            return c - '0';
        }
    }

    private void lexNateralNumber() {
        char c = readChar();

        if (c == '0') {
            buffer.append(c);
            return;
        } else if (c == '-') {
            buffer.append(c);
        } else {
            lookahead = (int) c;
        }

        lexUnnaturalNumber();
    }

    private void lexUnnaturalNumber() {
        boolean hasMore = true;
        do {
            int readChar = read();
            switch (readChar) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    buffer.append((char) readChar);
                    break;
                default:
                    lookahead = readChar;
                    hasMore = false;
            }
        } while (hasMore);
    }

    private void lexPrecision() {
    }
}

class UnexpectedEndOfStream extends IllegalStateException {

}

class CompleteSuprise extends IllegalStateException {

    public CompleteSuprise(int position, int line, char[] tokens) {
        this(position, line, tokens, "");
    }

    public CompleteSuprise(int position, int line, char[] tokens, String message) {
        super(line + ":" + position + " This came as a complete suprise to me: " + new String(tokens) + " " + message);
    }
}
