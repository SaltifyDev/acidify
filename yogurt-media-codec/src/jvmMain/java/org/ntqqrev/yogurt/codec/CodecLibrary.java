package org.ntqqrev.yogurt.codec;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.ptr.IntByReference;

import java.nio.file.Path;

public interface CodecLibrary extends Library {
    static String getLibraryPath() {
        String osName = System.getProperty("os.name").toLowerCase();
        String arch = System.getProperty("os.arch").toLowerCase();
        if (osName.contains("win")) {
            if (arch.contains("64")) {
                return "./lib/windows-x64/lagrangecodec";
            } else {
                return "./lib/windows-x86/lagrangecodec";
            }
        } else if (osName.contains("mac")) {
            if (arch.contains("aarch64") || arch.contains("arm64")) {
                return "./lib/macos-arm64/liblagrangecodec.dylib";
            } else {
                return "./lib/macos-x64/liblagrangecodec.dylib";
            }
        } else if (osName.contains("nux") || osName.contains("nix")) {
            if (arch.contains("aarch64") || arch.contains("arm64")) {
                return "./lib/linux-arm64/liblagrangecodec.so";
            } else if (arch.contains("x86_64") || arch.contains("amd64")) {
                return "./lib/linux-x64/liblagrangecodec.so";
            }
        }
        throw new UnsupportedOperationException("Unsupported OS or architecture: " + osName + " " + arch);
    }

    static String getLibraryAbsolutePath() {
        return Path.of(getLibraryPath()).toAbsolutePath().toString();
    }

    CodecLibrary INSTANCE = Native.load(getLibraryAbsolutePath(), CodecLibrary.class);

    int audio_to_pcm(Pointer data, int length, AudioCodecCallback callback, Pointer userData);

    int silk_decode(Pointer data, int length, AudioCodecCallback callback, Pointer userData);
    int silk_encode(Pointer data, int length, AudioCodecCallback callback, Pointer userData);

    int video_first_frame(Pointer data, int length, PointerByReference outFrame, IntByReference outSize);
    int video_get_size(Pointer data, int length, VideoInfoStruct info);
}