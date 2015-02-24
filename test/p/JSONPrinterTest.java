package p;

import java.util.ArrayList;
import java.util.HashMap;
import org.junit.Test;

public class JSONPrinterTest {

    @Test
    public void testPrint() {
        HashMap<String, Object> map = new HashMap<>();
        HashMap<String, Object> map2 = new HashMap<>();
        ArrayList<String> strings = new ArrayList<>();
        strings.add("1");
        strings.add("2");
        strings.add("3");

        map.put("a", 1);
        map.put("b", "2");
        map.put("c", map2);
        map.put("d", strings);

        JSONPrinter printer = new JSONPrinter();
        System.out.println(printer.print(map));

    }

}
