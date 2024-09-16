package indeed.googledrive_derisking.utils;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableList;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FileUtils {
    public static List<File> readDirectoryToList(final String dir) {
        final File folder = new File(dir);
        if (StringUtils.isEmpty(dir) || !folder.exists()) {
            return List.of();
        }
        final Queue<File> fileQ = new LinkedList<>();
        fileQ.add(folder);
        final ImmutableList.Builder<File> fileList = ImmutableList.builder();
        while (!fileQ.isEmpty()) {
            final File file = fileQ.poll();
            if (file.isFile()) {
                fileList.add(file);
            } else if (file.isDirectory()) {
                fileQ.addAll(Arrays.asList(file.listFiles()));
            }
        }
        return fileList.build();
    }
}
