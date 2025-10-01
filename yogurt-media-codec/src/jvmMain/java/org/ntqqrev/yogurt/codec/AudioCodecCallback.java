package org.ntqqrev.yogurt.codec;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

public interface AudioCodecCallback extends Callback {
    void invoke(Pointer userData, Pointer byteArray, int length);
}