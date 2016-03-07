package p.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class DefaultBuilder implements Builder {

    Stack<Object> structures = new Stack();
    String mapKey;

    public DefaultBuilder() {
        structures.add(new ArrayList(1));
    }

    @Override
    public void builderMap() {
        HashMap map = new HashMap();
        buildNode(map);
    }

    @Override
    public void buildMapKey(String key) {
        mapKey = key;
    }

    @Override
    public void buildMapValue(Object value) {
        ((Map) structures.peek()).put(mapKey, value);
        mapKey = null;
    }

    @Override
    public void finishMap() {
        structures.pop();
    }

    @Override
    public void buildArray() {
        ArrayList list = new ArrayList();
        buildNode(list);
    }

    @Override
    public void buildArrayValue(Object value) {
        ((List) structures.peek()).add(value);
    }

    @Override
    public void finishArray() {
        structures.pop();
    }

    private void buildNode(Object list) {
        if (mapKey == null) {
            buildArrayValue(list);
        } else {
            buildMapValue(list);
        }
        structures.push(list);
    }

    public <T extends Object> T getStrucuture() {
        return (T) ((List) structures.pop()).get(0);
    }
}
