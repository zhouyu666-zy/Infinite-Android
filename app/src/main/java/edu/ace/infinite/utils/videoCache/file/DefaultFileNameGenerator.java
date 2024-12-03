package edu.ace.infinite.utils.videoCache.file;

import android.net.Uri;

/**
 * Implementation of {@link com.danikula.videocache.file.FileNameGenerator} that uses MD5 of url as file name
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class DefaultFileNameGenerator implements FileNameGenerator {
    @Override
    public String generate(String ID) {
        ID = Uri.encode(ID);
        return ID;
    }
}
