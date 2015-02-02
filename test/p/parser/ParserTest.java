/*
 * Copyright (C) 2015 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 */
package p.parser;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.junit.Ignore;
import org.junit.Test;
import p.lexer.LexerStream;

/**
 *
 * @author Chuck Lowery <chuck.lowery @ gopai.com>
 */
public class ParserTest {
    
    public ParserTest() {
    }

    @Test
    public void testPrint() {
        String test = "{\n" +
"    \"glossary\": {\n" +
"        \"title\": \"example glossary\",\n" +
"		\"GlossDiv\": {\n" +
"            \"title\": \"S\",\n" +
"			\"GlossList\": {\n" +
"                \"GlossEntry\": {\n" +
"                    \"ID\": \"SGML\",\n" +
"					\"SortAs\": \"SGML\",\n" +
"					\"GlossTerm\": \"Standard Generalized Markup Language\",\n" +
"					\"Acronym\": \"SGML\",\n" +
"					\"Abbrev\": \"ISO 8879:1986\",\n" +
"					\"GlossDef\": {\n" +
"                        \"para\": \"A meta-markup language, used to create markup languages such as DocBook.\",\n" +
"						\"GlossSeeAlso\": [\"GML\", \"XML\"]\n" +
"                    },\n" +
"					\"GlossSee\": \"markup\"\n" +
"                }\n" +
"            }\n" +
"        }\n" +
"    }\n" +
"}";
        
        ByteArrayInputStream stream = new ByteArrayInputStream(test.getBytes());
        
        Parser parserV2 = new Parser();
        
        PrintBuilder printBuilder = new PrintBuilder();
        
        parserV2.parse(new LexerStream(stream), printBuilder);
        
        System.out.println(printBuilder.toString());
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
        Map item3 = (Map)c.get(3);
        
        assertThat(item3.get("a"), is(BigDecimal.ONE));
    }
    
    
}
