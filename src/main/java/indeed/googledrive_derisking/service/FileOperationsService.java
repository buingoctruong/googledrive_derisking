package indeed.googledrive_derisking.service;

import java.io.File;
import java.util.List;

public interface FileOperationsService {
    String getFileMetadata(final String fileId);

    String uploadSingleTo(final File file, final String folderId);

    void uploadBulkTo(final List<File> files, final String folderId);

    void downloadFrom(final String fileId);

    void moveFileToAnotherFolder(final String fileId, final String newFolderId);

    void moveFileToTrash(final String fileId);
}
