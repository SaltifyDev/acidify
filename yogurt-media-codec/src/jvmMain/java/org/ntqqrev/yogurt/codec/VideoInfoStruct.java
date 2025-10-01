package org.ntqqrev.yogurt.codec;

import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class VideoInfoStruct extends Structure {
    public int width;      // offset 0
    public int height;     // offset 4
    public long duration;  // offset 8

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("width", "height", "duration");
    }

    public static class ByReference extends VideoInfoStruct implements Structure.ByReference {
    }

    public static class ByValue extends VideoInfoStruct implements Structure.ByValue {
    }
}