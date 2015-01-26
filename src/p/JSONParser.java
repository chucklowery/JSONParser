package p;

import java.io.InputStream;
import p.lexer.LexerStream;
import p.lexer.LexicalToken;
import p.parser.CommonBuilder;
import p.parser.Parser;

public class JSONParser {

    public Object parse(InputStream stream) {
        LexerStream lexer = new LexerStream(stream);
        CommonBuilder builder = new CommonBuilder();
        Parser parser = new Parser(builder);

        LexicalToken token;
        while ((token = lexer.next()) != null) {
            parser.accept(token);
        }

        if (!parser.isFinished()) {
            throw new UnexpectedEndOfStream();
        }

        return builder.getProduct();
    }
}

class UnexpectedEndOfStream extends IllegalStateException {

}
