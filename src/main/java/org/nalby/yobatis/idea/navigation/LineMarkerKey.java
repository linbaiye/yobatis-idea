package org.nalby.yobatis.idea.navigation;

import com.intellij.openapi.util.Key;

public class LineMarkerKey extends Key<String> {

    private final static LineMarkerKey KEY = new LineMarkerKey();

    private LineMarkerKey() {
        super("yobatisLineMarkerKey");
    }

    public static LineMarkerKey get() {
        return KEY;
    }
}
