package org.ntqqrev.yogurt.codec;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.ptr.IntByReference;

public interface CodecLibrary extends Library {
    static String getLibraryPath() {
        String libraryName = "lagrangecodec";
        String osName = System.getProperty("os.name").toLowerCase();
        String arch = System.getProperty("os.arch").toLowerCase();
        if (osName.contains("win")) {
            if (arch.contains("64")) {
                return "./lib/windows-x64/" + libraryName;
            } else {
                return "./lib/windows-x86/" + libraryName;
            }
        } else if (osName.contains("mac")) {
            if (arch.contains("aarch64") || arch.contains("arm64")) {
                return "./lib/macos-arm64/" + libraryName;
            } else {
                return "./lib/macos-x64/" + libraryName;
            }
        } else if (osName.contains("nux") || osName.contains("nix")) {
            if (arch.contains("aarch64") || arch.contains("arm64")) {
                return "./lib/linux-arm64/" + libraryName;
            } else if (arch.contains("x86_64") || arch.contains("amd64")) {
                return "./lib/linux-x64/" + libraryName;
            }
        }
        throw new UnsupportedOperationException("Unsupported OS or architecture: " + osName + " " + arch);
    }

    CodecLibrary INSTANCE = Native.load(getLibraryPath(), CodecLibrary.class);

    int audio_to_pcm(Pointer data, int length, AudioCodecCallback callback, Pointer userData);

    int silk_decode(Pointer data, int length, AudioCodecCallback callback, Pointer userData);
    int silk_encode(Pointer data, int length, AudioCodecCallback callback, Pointer userData);

    int video_first_frame(Pointer data, int length, PointerByReference outFrame, IntByReference outSize);
    int video_get_size(Pointer data, int length, VideoInfoStruct info);
}