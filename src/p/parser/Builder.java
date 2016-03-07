package p.parser;

public interface Builder {

    void builderMap();

    void buildMapKey(String key);

    void buildMapValue(Object value);

    void finishMap();

    void buildArray();

    void buildArrayValue(Object value);

    void finishArray();
}
