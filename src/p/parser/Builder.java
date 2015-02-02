/*
 * Copyright (C) 2015 Payment Alliance International. All Rights Reserved.
 * 
 * This software is the proprietary information of Payment Alliance International.
 * Use is subject to license terms.
 */
package p.parser;

public interface Builder {

    public void builderMap();

    public void buildMapKey(String key);

    public void buildMapValue(Object value);

    public void finishMap();

    public void buildArray();

    public void buildArrayValue(Object value);

    public void finishArray();
}
