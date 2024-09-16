package indeed.googledrive_derisking.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import indeed.googledrive_derisking.exceptions.FileOperationException;
import indeed.googledrive_derisking.service.FileOperationsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileOperationsServiceImpl implements FileOperationsService {
    private final Drive googleDrive;
    private final ObjectMapper objectMapper;
    @Override
    public String getFileMetadata(final String fileId) {
        try {
            final File file = googleDrive.files().get(fileId).execute();
            return objectMapper.writeValueAsString(file);
        } catch (final IOException e) {
            throw new FileOperationException(
                    "Failed to get metadata of the file [%s].".formatted(fileId), e);
        }
    }

    // TODO: Should test with uploading a large file, the performance seems not too good
    @Override
    public String uploadSingleTo(final java.io.File file, final String folderId) {
        try {
            final File fileMetadata = new File();
            fileMetadata.setName(file.getName());
            fileMetadata.setParents(List.of(folderId));
            final FileContent mediaContent = new FileContent(Files.probeContentType(file.toPath()),
                    file);
            final File targetFile = googleDrive.files().create(fileMetadata, mediaContent)
                    .setFields("id").execute();
            log.info("File {} has been uploaded", targetFile.getId());
            return targetFile.getId();
        } catch (final IOException e) {
            throw new FileOperationException("Failed to upload the file [%s] to folder [%s]."
                    .formatted(file.getName(), folderId), e);
        }
    }

    // TODO: Should use CompletableFuture
    @Override
    public void uploadBulkTo(final List<java.io.File> files, final String folderId) {
        files.forEach(file -> uploadSingleTo(file, folderId));
    }

    // TODO: Should find other solutions, the performance seems not too good
    @Override
    public void downloadFrom(final String fileId) {
        try (final OutputStream outputStream = new ByteArrayOutputStream()) {
            final Drive.Files.Get getObject = googleDrive.files().get(fileId);
            final File fileMetaData = getObject.execute();
            getObject.executeMediaAndDownloadTo(outputStream);
            final ByteArrayOutputStream byteArrayOutputStream = (ByteArrayOutputStream) outputStream;
            final String resourceDir = Optional.ofNullable(FileOperationsServiceImpl.class
                    .getClassLoader().getResource("download-blt-files")).orElseThrow().getFile();
            final java.io.File resourceFolder = new java.io.File(resourceDir);
            final java.io.File downloadFile = new java.io.File(resourceFolder,
                    fileMetaData.getName());
            final FileOutputStream fileOutputStream = new FileOutputStream(downloadFile);
            byteArrayOutputStream.writeTo(fileOutputStream);
            log.info("Download file {} successfully", fileId);
        } catch (final IOException e) {
            throw new FileOperationException("Failed to download the file [%s].".formatted(fileId),
                    e);
        }
    }

    /**
     * The file can be moved to:
     * 1. Child folders (e.g., subdirectories like level1, level2, etc.)
     * 2. Separate folders (independent directories)
     * <p>
     * Need to share permissions with the account associated with the credentials - use client-email
     */
    @Override
    public void moveFileToAnotherFolder(final String fileId, final String newFolderId) {
        try {
            // Retrieve the existing parents to remove
            File file = googleDrive.files().get(fileId).setFields("parents").execute();
            final StringBuilder previousParents = new StringBuilder();
            file.getParents().forEach(parent -> {
                previousParents.append(parent);
                previousParents.append(',');
            });
            // Move the file to the new folder
            file = googleDrive.files().update(fileId, null).setAddParents(newFolderId)
                    .setRemoveParents(previousParents.toString()).setFields("id, parents")
                    .execute();
            log.info("The file {} has been moved to the new parents {}", fileId, file.getParents());
        } catch (final IOException e) {
            throw new FileOperationException("Unable to move file [%s] to the new folder [%s]."
                    .formatted(fileId, newFolderId), e);
        }
    }

    @Override
    public void moveFileToTrash(final String fileId) {
        try {
            final File newContent = new File();
            newContent.setTrashed(true);
            googleDrive.files().update(fileId, newContent).execute();
            log.info("File {} has been moved to the trash", fileId);
        } catch (final IOException e) {
            throw new FileOperationException(
                    "Unable to move file [%s] to the trash.".formatted(fileId), e);
        }
    }
}
