package org.ntqqrev.yogurt.codec;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.ptr.IntByReference;

public interface CodecLibrary extends Library {
    CodecLibrary INSTANCE = Native.load("./lagrangecodec", CodecLibrary.class);

    int audio_to_pcm(Pointer data, int length, AudioCodecCallback callback, Pointer userData);

    int silk_decode(Pointer data, int length, AudioCodecCallback callback, Pointer userData);
    int silk_encode(Pointer data, int length, AudioCodecCallback callback, Pointer userData);

    int video_first_frame(Pointer data, int length, PointerByReference outFrame, IntByReference outSize);
    int video_get_size(Pointer data, int length, VideoInfoStruct info);
}