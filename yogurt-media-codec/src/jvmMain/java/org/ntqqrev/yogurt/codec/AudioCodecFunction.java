package org.ntqqrev.yogurt.codec;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

public interface AudioCodecFunction extends Callback {
    int invoke(Pointer data, int length, AudioCodecCallback callback, Pointer userData);
}