package p.lexer;

import java.io.IOException;
import java.io.InputStream;

class StaticHelpers {

    static char readChar(InputStream stream) {
        int raw = read(stream);
        checkNotEndOfStream(raw);
        return (char) raw;
    }

    static int read(InputStream stream) {
        try {
            return stream.read();
        } catch (IOException io) {
            throw new UnexpectedEndOfStream();
        }
    }

    static void checkNotEndOfStream(int raw) {
        if (raw == -1) {
            throw new UnexpectedEndOfStream();
        }
    }
}
