package p.lexer;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

public class LexerStreamTest {

    @Test
    public void givenEmptyStream_expectEOS() {
        String tokens = "";

        LexerStream stream = toLexicalStream(tokens);
        assertThat(stream.next().getType(), is(TerminalType.EOS));
        assertThat(stream.next().getType(), is(TerminalType.EOS));
        assertThat(stream.next().getType(), is(TerminalType.EOS));
    }

    @Test
    public void givenEmptStringWithSpaces_expectEOS() {
        Terminal token = toLexicalStream("   ").next();
        assertThat(token.getType(), is(TerminalType.EOS));
        assertThat(token.position, is(4));

        token = toLexicalStream("\t").next();
        assertThat(token.getType(), is(TerminalType.EOS));
        assertThat(token.position, is(2));

        token = toLexicalStream("\r\r\r\n").next();
        assertThat(token.getType(), is(TerminalType.EOS));
        assertThat(token.line, is(4));
    }

    @Test
    public void givenWordDoubleQuoted_expectString() {
        String tokens = "\"abcd\"";

        LexerStream stream = toLexicalStream(tokens);
        assertNextTokenValue(stream, TerminalType.STRING_LITERAL, "abcd");
    }

    @Test
    public void givenWordSingleQuoted_expectString() {
        String tokens = "'abcd'";

        LexerStream stream = toLexicalStream(tokens);
        assertNextTokenValue(stream, TerminalType.STRING_LITERAL, "abcd");
    }

    @Test(expected = UnexpectedEndOfStream.class)
    public void givenWordWithInvalid_terminal_seperators_expectString() {
        String tokens = "'abcd\"";

        LexerStream stream = toLexicalStream(tokens);
        assertNextTokenValue(stream, TerminalType.STRING_LITERAL, "abcd");
    }


    @Test
    public void givenNateralNumberZero_expectNumber() {
        String tokens = "0";

        LexerStream stream = toLexicalStream(tokens);
        assertNextTokenValue(stream, TerminalType.VALUE_LITERAL, BigDecimal.ZERO);
    }

    @Test
    public void givenCommonValueTypes_expectProperValue() {
        String tokens = "null true false";
        LexerStream stream = toLexicalStream(tokens);
        assertNextTokenValue(stream, TerminalType.VALUE_LITERAL, null);
        assertNextTokenValue(stream, TerminalType.VALUE_LITERAL, Boolean.TRUE);
        assertNextTokenValue(stream, TerminalType.VALUE_LITERAL, Boolean.FALSE);
    }

    @Test
    public void givenNateralNumber_expectNumber() {
        String tokens = "-123456789";

        LexerStream stream = toLexicalStream(tokens);
        assertNextTokenValue(stream, TerminalType.VALUE_LITERAL, new BigDecimal(tokens));
    }

    @Test
    public void givenRealNumber_expectNumber() {
        String tokens = "-123456789.123456789";

        LexerStream stream = toLexicalStream(tokens);
        assertNextTokenValue(stream, TerminalType.VALUE_LITERAL, new BigDecimal(tokens));
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
        assertNextTokenValue(stream, TerminalType.STRING_LITERAL, "\"\\/\b\f\n\r\t\uABCD");
    }

    @Test
    public void givenPunctuation_expectPunuationTokens() {
        String source = "{}[]:,";
        LexerStream stream = toLexicalStream(source);

        assertThat(stream.next().getType(), is(TerminalType.OPEN_BRACE));
        assertThat(stream.next().getType(), is(TerminalType.CLOSE_BRACE));
        assertThat(stream.next().getType(), is(TerminalType.OPEN_BRACKET));
        assertThat(stream.next().getType(), is(TerminalType.CLOSE_BRACKET));
        assertThat(stream.next().getType(), is(TerminalType.SEMICOLON));
        assertThat(stream.next().getType(), is(TerminalType.COMMA));
    }

    
    private LexerStream toLexicalStream(String tokens) {
        LexerStream stream = new LexerStream(toStream(tokens));
        return stream;
    }

    private ByteArrayInputStream toStream(String stream) {
        return new ByteArrayInputStream(stream.getBytes());
    }

    private void assertNextTokenValue(LexerStream stream, TerminalType type, Object value) {
        Terminal token = stream.next();
        assertThat(token.getType(), is(type));
        assertThat(token.getValue(), is(value));
    }
}
