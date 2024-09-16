package indeed.googledrive_derisking.service;

import com.google.api.services.drive.model.File;

public interface FolderOperationsService {
    File createTo(final String parentFolderId, final String lionTicket);

    void removeFrom(final String folderId);

    void moveTo(final String folderId, final String newParentFolderId);
}
