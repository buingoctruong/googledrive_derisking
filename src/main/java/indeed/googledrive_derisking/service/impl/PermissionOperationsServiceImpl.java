package indeed.googledrive_derisking.service.impl;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.drive.model.PermissionList;

import indeed.googledrive_derisking.exceptions.PermissionOperationsException;
import indeed.googledrive_derisking.service.PermissionOperationsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionOperationsServiceImpl implements PermissionOperationsService {
    private final Drive googleDrive;
    @Override
    public void grantUserPermission(final File file, final String userEmail) {
        try {
            final Permission permission = new Permission();
            permission.setType("user");
            permission.setRole("writer");
            permission.setEmailAddress(userEmail);
            googleDrive.permissions().create(file.getId(), permission).execute();
        } catch (final IOException e) {
            throw new PermissionOperationsException(
                    "Failed to grant permission to user [%s] for file [%s]".formatted(userEmail,
                            file.getId()),
                    e);
        }
    }

    @Override
    public void grantGroupPermission(final File file, final String groupEmail) {
        try {
            final Permission permission = new Permission();
            permission.setType("group");
            permission.setRole("writer");
            permission.setEmailAddress(groupEmail);
            googleDrive.permissions().create(file.getId(), permission).execute();
        } catch (final IOException e) {
            throw new PermissionOperationsException(
                    "Failed to grant permission to group [%s] for file [%s]".formatted(groupEmail,
                            file.getId()),
                    e);
        }
    }

    @Override
    public void revokeAccountPermission(final File file, final String email) {
        try {
            // List all permissions for the file/folder
            final PermissionList permissions = googleDrive.permissions().list(file.getId())
                    .setFields("permissions(id, emailAddress)").execute();
            final String permissionIdToRemove = permissions.getPermissions().stream()
                    .filter(it -> email.equals(it.getEmailAddress())).findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            "Permission for user [%s] not found".formatted(email)))
                    .getId();
            googleDrive.permissions().delete(file.getId(), permissionIdToRemove).execute();
        } catch (final IOException e) {
            throw new PermissionOperationsException(
                    "Failed to revoke permission to email [%s] for file [%s]".formatted(email,
                            file.getId()),
                    e);
        }
    }
}
