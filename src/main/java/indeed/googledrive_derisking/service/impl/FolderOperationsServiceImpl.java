package indeed.googledrive_derisking.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;

import indeed.googledrive_derisking.exceptions.FolderOperationException;
import indeed.googledrive_derisking.service.FolderOperationsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FolderOperationsServiceImpl implements FolderOperationsService {
    private final Drive googleDrive;
    @Override
    public File createTo(final String parentFolderId, final String lionTicket) {
        // File's metadata.
        final File fileMetadata = new File();
        fileMetadata.setName(lionTicket);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        fileMetadata.setParents(List.of(parentFolderId));
        try {
            File file = googleDrive.files().create(fileMetadata).setFields("id, parents").execute();
            log.info("Folder for lion ticket {} has been created with ID: {}", lionTicket,
                    file.getId());
            // The created folder is accessible only by the service account.
            // Access permissions need to be granted to users or groups.
            grantUserPermission(file);
            return file;
        } catch (IOException e) {
            throw new FolderOperationException(
                    "Unable to create folder in parent folder [%s] for ticket [%s]."
                            .formatted(parentFolderId, lionTicket),
                    e);
        }
    }

    @Override
    public void removeFrom(final String folderId) {
        try {
            final File newContent = new File();
            newContent.setTrashed(true);
            googleDrive.files().update(folderId, newContent).execute();
            log.info("Folder {} has been moved to the trash", folderId);
        } catch (final IOException e) {
            throw new FolderOperationException(
                    "Unable to move folder [%s] to the trash.".formatted(folderId), e);
        }
    }

    /**
     * Moves the folder and its contents to a new location.
     * <p>
     * Need to share permissions with the account associated with the credentials - use client-email
     */
    @Override
    public void moveTo(final String folderId, final String newParentFolderId) {
        try {
            // Retrieve the existing parents to remove
            File file = googleDrive.files().get(folderId).setFields("parents").execute();
            final StringBuilder previousParents = new StringBuilder();
            file.getParents().forEach(parent -> {
                previousParents.append(parent);
                previousParents.append(',');
            });
            // Move the folder to the new parent folder
            file = googleDrive.files().update(folderId, null).setAddParents(newParentFolderId)
                    .setRemoveParents(previousParents.toString()).setFields("id, parents")
                    .execute();
            log.info("The folder {} has been moved to the new parents {}", folderId,
                    file.getParents());
        } catch (final IOException e) {
            throw new FolderOperationException("Unable to move file [%s] to the new folder [%s]."
                    .formatted(folderId, newParentFolderId), e);
        }
    }

    private void grantUserPermission(final File file) throws IOException {
        final Permission permission = new Permission();
        permission.setType("user");
        permission.setRole("writer");
        permission.setEmailAddress("buingoctruong1508@gmail.com");
        googleDrive.permissions().create(file.getId(), permission).execute();
    }

    private void grantGroupPermission(final File file) throws IOException {
        final Permission permission = new Permission();
        permission.setType("group");
        permission.setRole("writer");
        permission.setEmailAddress("group@gmail.com");
        googleDrive.permissions().create(file.getId(), permission).execute();
    }
}
