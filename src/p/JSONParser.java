package p;

import java.io.InputStream;
import p.lexer.LexerStream;
import p.parser.DefaultBuilder;
import p.parser.Parser;

public class JSONParser {

    public Object parse(InputStream stream) {
        LexerStream lexer = new LexerStream(stream);
        DefaultBuilder builder = new DefaultBuilder();
        Parser parser = new Parser();

        parser.parse(lexer, builder);

        return builder.getStrucuture();
    }
}
