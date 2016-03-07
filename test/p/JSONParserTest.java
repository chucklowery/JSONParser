package p;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;


public class JSONParserTest {

    JSONParser parser;

    @Test(expected = Exception.class)
    public void givenIllegalFirstCharacter_expectExpection() {
        parse("abcd");
    }

    @Test
    public void givenOpenCloseBrases_expectEmptyMap() {
        Object result = parse("{}");

        assertThat(result, instanceOf(Map.class));
        Map map = (Map) result;
        assertThat(map.size(), is(0));
    }

    @Test
    public void givenOpenCloseBracket_expectEmptyList() {
        Object result = parse("[]");

        assertThat(result, instanceOf(List.class));
        List list = (List) result;
        assertThat(list.size(), is(0));
    }

    @Test
    public void givenEmptyArrayOfArrays_expectEmptyListOfList() {
        Object result = parse("[[]]");

        assertThat(result, instanceOf(List.class));
        List list = (List) result;
        assertThat(list.size(), is(1));
    }

    @Test
    public void givenEmptyArrayOfArraysSingleValue_expectEmptyListOfList() {
        Object result = parse("[[null]]");

        assertThat(result, instanceOf(List.class));
        List list = (List) result;
        assertThat(list.size(), is(1));
    }

    @Test
    public void givenArrayWithNull_expectAListContainingNull() {
        Object result = parse("[null]");

        assertThat(result, instanceOf(List.class));
        List<Object> list = (List) result;
        assertThat(list.size(), is(1));
        assertThat(list, hasItem((String) null));
    }

    @Test
    public void givenAnArrayOfStrings_expectAListOfStrings() {
        Object result = parse("[\"a\", \"b\", \"c\"]");

        assertThat(result, instanceOf(List.class));
        List list = (List) result;
        assertThat(list.size(), is(3));
    }

    @Test(expected = Exception.class)
    public void givenAlmostNull() {
        Object result = parse("[na]");

        assertThat(result, instanceOf(List.class));
    }

    @Test
    public void givenArrayWithTrue_expectAListContainingTrue() {
        Object result = parse("[true]");

        assertThat(result, instanceOf(List.class));
        List<Object> list = (List) result;
        assertThat(list.size(), is(1));
        assertThat(list, hasItem(Boolean.TRUE));
    }

    @Test
    public void givenArrayWithFalse_expectAListContainingFalse() {
        Object result = parse("[false]");

        assertThat(result, instanceOf(List.class));
        List<Object> list = (List) result;
        assertThat(list.size(), is(1));
        assertThat(list, hasItem(Boolean.FALSE));
    }

    @Test
    public void givenArrayOfReserved_expectListOfReserved() {
        Object result = parse("[null, null, true, false]");

        assertThat(result, instanceOf(List.class));
        List<Object> list = (List) result;
        assertThat(list.size(), is(4));
        assertThat(list, hasItems(null, null, Boolean.TRUE, Boolean.FALSE));
    }

    @Test
    public void givenArrayOfArrayOfNull_expectListOfListOfNull() {
        Object result = parse("[[null], [null], [null]]");
        ArrayList RESULT_LIST = new ArrayList();
        RESULT_LIST.add(null);

        assertThat(result, instanceOf(List.class));
        List<Object> list = (List) result;
        assertThat(list.size(), is(3));
        assertThat(list, hasItems(RESULT_LIST, RESULT_LIST, RESULT_LIST));
    }

    @Test
    public void givenDeepArray() {
        Object result = parse("[[[null]], [null], [null]]");
        ArrayList RESULT_LIST_1 = new ArrayList();
        ArrayList RESULT_LIST = new ArrayList();
        RESULT_LIST.add(null);
        RESULT_LIST_1.add(RESULT_LIST);

        assertThat(result, instanceOf(List.class));
        List<Object> list = (List) result;
        assertThat(list.size(), is(3));
        assertThat(list, hasItems(RESULT_LIST_1, RESULT_LIST, RESULT_LIST));
    }

    private Object parse(String stream) {
        return parser.parse(toStream(stream));
    }

    private ByteArrayInputStream toStream(String stream) {
        return new ByteArrayInputStream(stream.getBytes());
    }

    @Before
    public void setup() {
        parser = new JSONParser();
    }

}
