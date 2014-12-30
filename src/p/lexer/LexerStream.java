package p.lexer;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.math.BigDecimal;

public class LexerStream {

    PushbackInputStream stream;
    int position = 0;
    int line = 1;
    char rawCharacter;
    boolean hasMore = true;

    LexicalToken eos;
    CharBuffer buffer;
    StringLexer stringLexer;

    public LexerStream(InputStream stream) {
        this.stream = new PushbackInputStream(stream, 1);
        buffer = new CharBuffer();
        stringLexer = new StringLexer(this.stream, buffer);
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
                        uncheckPushBack(raw);
                    }
                    break;
                default:
                    return parseToken(raw);
            }
        }
    }

    private void uncheckPushBack(int raw) {
        try {
            stream.unread(raw);
        } catch (IOException ioe) {
            throw new IllegalStateException(ioe);
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
        uncheckPushBack((int) rawCharacter);

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
                uncheckPushBack(c);
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
        char[] chars = stringLexer.lexString(position, line);
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

    private char readChar() {
        int raw = read();
        checkNotEndOfStream(raw);
        return (char) raw;
    }

    private int read() {
        try {
            return stream.read();
        } catch (IOException io) {
            throw new UnexpectedEndOfStream();
        }
    }

    private void checkNotEndOfStream(int raw) {
        if (raw == -1) {
            throw new UnexpectedEndOfStream();
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
            uncheckPushBack(c);
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
                    uncheckPushBack(readChar);
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
