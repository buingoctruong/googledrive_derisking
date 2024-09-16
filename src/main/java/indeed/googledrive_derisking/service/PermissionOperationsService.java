package indeed.googledrive_derisking.service;

import com.google.api.services.drive.model.File;

public interface PermissionOperationsService {
    void grantUserPermission(final File file, final String userEmail);

    void grantGroupPermission(final File file, final String groupEmail);

    void revokeAccountPermission(final File file, final String email);
}
