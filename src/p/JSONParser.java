package p;

import java.io.InputStream;
import p.lexer.LexerStream;
import p.parser.DefaultBuilder;
import p.parser.ParserV2;

public class JSONParser {

    public Object parse(InputStream stream) {
        LexerStream lexer = new LexerStream(stream);
        DefaultBuilder builder = new DefaultBuilder();
        ParserV2 parser = new ParserV2();

        parser.parse(lexer, builder);

        return builder.getStrucuture();
    }
}
