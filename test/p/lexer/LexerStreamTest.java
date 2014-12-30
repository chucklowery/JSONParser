package p.lexer;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import org.hamcrest.core.Is;
import static org.hamcrest.core.Is.is;
import org.junit.Assert;
import static org.junit.Assert.assertThat;
import org.junit.Test;

public class LexerStreamTest {

    @Test
    public void givenEmptyStream_expectEOS() {
        String tokens = "";

        LexerStream stream = toLexicalStream(tokens);
        assertThat(stream.next().getType(), is(TokenType.EOS));
        assertThat(stream.next().getType(), is(TokenType.EOS));
        assertThat(stream.next().getType(), is(TokenType.EOS));
    }

    @Test
    public void givenEmptStringWithSpaces_expectEOS() {
        LexicalToken token = toLexicalStream("   ").next();
        assertThat(token.getType(), is(TokenType.EOS));
        assertThat(token.position, is(3));

        token = toLexicalStream("\t").next();
        assertThat(token.getType(), is(TokenType.EOS));
        assertThat(token.position, is(1));

        token = toLexicalStream("\r\r\r\n").next();
        assertThat(token.getType(), is(TokenType.EOS));
        assertThat(token.line, is(4));
    }

    @Test
    public void givenWord_expectString() {
        String tokens = "\"abcd\"";

        LexerStream stream = toLexicalStream(tokens);
        LexicalToken token = stream.next();

        assertThat(token.getType(), is(TokenType.STRING));
        assertThat(token.getValue(), is("abcd"));
    }
    
    @Test
    public void givenNateralNumberZero_expectNumber() {
        String tokens = "0";

        LexerStream stream = toLexicalStream(tokens);
        LexicalToken token = stream.next();

        assertThat(token.getType(), is(TokenType.NUMBER));
        assertThat(token.getValue(), is(BigDecimal.ZERO));
    }
    
    @Test
    public void givenNateralNumber_expectNumber() {
        String tokens = "-123456789";

        LexerStream stream = toLexicalStream(tokens);
        LexicalToken token = stream.next();

        assertThat(token.getType(), is(TokenType.NUMBER));
        assertThat(token.getValue(), is(new BigDecimal(tokens)));
    }
    
    @Test
    public void givenRealNumber_expectNumber() {
        String tokens = "-123456789.123456789";

        LexerStream stream = toLexicalStream(tokens);
        LexicalToken token = stream.next();

        assertThat(token.getType(), is(TokenType.NUMBER));
        assertThat(token.getValue(), is(new BigDecimal(tokens)));
    }


    @Test
    public void givenWordWithEscapedCharacters_expectStringWithValueCharacters() {
        String tokens
                = "\""
                + "\\\"" // (Quotation Mark)
                + "\\\\" // (Reverse Solidus)
                + "\\/" // (Solidus)
                + "\\b" // (Backspace)
                + "\\f" // (formfeed)
                + "\\n" // (newline)
                + "\\r" // (carriage return)
                + "\\t" // (horizontal table)
                + "\\uABCD" // character as hex
                + "\"";

        LexerStream stream = toLexicalStream(tokens);
        LexicalToken token = stream.next();

        assertThat(token.getType(), is(TokenType.STRING));
        assertThat(token.getValue(), is("\"\\/\b\f\n\r\t\uABCD"));
    }
    
    @Test
    public void givenPunctuation_expectPunuationTokens() {
        String source = "{}[]:,";
        LexerStream stream = toLexicalStream(source);
        
        assertThat(stream.next().getType(), is(TokenType.OPEN_BRASE));
        assertThat(stream.next().getType(), is(TokenType.CLOSE_BRASE));
        assertThat(stream.next().getType(), is(TokenType.OPEN_BRACKET));
        assertThat(stream.next().getType(), is(TokenType.CLOSE_BRACKET));
        assertThat(stream.next().getType(), is(TokenType.SEMICOLON));
        assertThat(stream.next().getType(), is(TokenType.COMMA));
        
        
    }

    private LexerStream toLexicalStream(String tokens) {
        LexerStream stream = new LexerStream(toStream(tokens));
        return stream;
    }

    private ByteArrayInputStream toStream(String stream) {
        return new ByteArrayInputStream(stream.getBytes());
    }

}
