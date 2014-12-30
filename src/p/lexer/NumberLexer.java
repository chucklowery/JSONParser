package p.lexer;

import java.io.PushbackInputStream;
import java.math.BigDecimal;
import static p.lexer.StaticHelpers.read;
import static p.lexer.StaticHelpers.readChar;
import static p.lexer.StaticHelpers.uncheckPushBack;

class NumberLexer {

    PushbackInputStream stream;
    CharBuffer buffer;

    int position;
    int line;

    NumberLexer(PushbackInputStream stream) {
        this.stream = stream;
        buffer = new CharBuffer();
    }

    LexicalToken lexNumber(int position, int line) {
        this.line = line;
        this.position = position;
        buffer.reset();

        lexNateralNumber();

        int c = read(stream);
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
                uncheckPushBack(stream, c);
        }

        LexicalToken token = new LexicalToken();
        token.line = line;
        token.type = TokenType.NUMBER;
        token.value = new BigDecimal(new String(buffer.toArray()));
        position += buffer.length();
        return token;
    }

    private void lexNateralNumber() {
        char c = readChar(stream);

        if (c == '0') {
            buffer.append(c);
            return;
        } else if (c == '-') {
            buffer.append(c);
        } else {
            uncheckPushBack(stream, c);
        }

        lexUnnaturalNumber();
    }

    private void lexUnnaturalNumber() {
        boolean hasMore = true;
        do {
            int readChar = read(stream);
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
                    uncheckPushBack(stream, readChar);
                    hasMore = false;
            }
        } while (hasMore);
    }

    private void lexPrecision() {
    }
}
