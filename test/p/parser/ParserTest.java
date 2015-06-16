/*
 * Copyright (C) 2015 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 */
package p.parser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.junit.Ignore;
import org.junit.Test;
import p.JSONPrinter;
import p.lexer.LexerStream;

/**
 *
 * @author Chuck Lowery <chuck.lowery @ gopai.com>
 */
public class ParserTest {

    public ParserTest() {
    }

    @Test
    public void testExample1() throws IOException {
        InputStream stream = getClass().getResourceAsStream("example1.json");
        byte[] bytes = readIntoByteArray(stream);
        ByteArrayInputStream source = new ByteArrayInputStream(bytes);
        
        
        Parser parserV2 = new Parser();

        JSONPrinter printer = new JSONPrinter();
        DefaultBuilder builder = new DefaultBuilder();

        parserV2.parse(new LexerStream(source), builder);
        String required = new String(bytes);
        String result = printer.print(builder.getStrucuture()).toString();
        assertThat(result, is( required.replace("\r\n", "\n")));
    }
    
    @Test
    @Ignore
    public void testExample2() throws IOException {
        InputStream stream = getClass().getResourceAsStream("example2.json");
        byte[] bytes = readIntoByteArray(stream);
        ByteArrayInputStream source = new ByteArrayInputStream(bytes);
        
        
        Parser parserV2 = new Parser();

        JSONPrinter printer = new JSONPrinter();
        DefaultBuilder builder = new DefaultBuilder();

        parserV2.parse(new LexerStream(source), builder);
        String required = new String(bytes);
        String result = printer.print(builder.getStrucuture()).toString();

        assertThat(result, is( required.replaceAll("[\r\n]+", "\n")));
    }

    @Test
    public void testBuild() {
        ByteArrayInputStream stream = new ByteArrayInputStream("{ \"a\" : 123, \"b\" : 455, \"c\" : [1,2,3, { \"a\" : 1}] }".getBytes());

        Parser parserV2 = new Parser();

        DefaultBuilder builder = new DefaultBuilder();

        parserV2.parse(new LexerStream(stream), builder);

        Map root = builder.getStrucuture();
        List c = (List) root.get("c");
        assertThat(c.get(0), is(BigDecimal.ONE));
        Map item3 = (Map) c.get(3);

        assertThat(item3.get("a"), is(BigDecimal.ONE));
    }

    
    public static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int index;
        while ((index = in.read(buffer)) > 0) {
            out.write(buffer, 0, index);
        }
    }

    public static byte[] readIntoByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        copy(in, out);
        return out.toByteArray();
    }
}
