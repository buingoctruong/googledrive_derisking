package indeed.googledrive_derisking.controlllers;

import static indeed.googledrive_derisking.utils.FileUtils.readDirectoryToList;

import java.io.File;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.bind.annotation.*;

import indeed.googledrive_derisking.service.FileOperationsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/google/drive/files")
@RequiredArgsConstructor
public class FileOperationsController {
    private final FileOperationsService fileOperationsService;
    @Operation(summary = "Get file metadata",
            description = "Returns metadata for the file, including its name, MIME type, ID, and more.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server Error: This may indicate that the service account lacks permission for the folder containing the file.")})
    @GetMapping("/metadata")
    public String getFileMetadata(@RequestParam("fileId") final String fileId) {
        return fileOperationsService.getFileMetadata(fileId);
    }

    @Operation(summary = "Download the file",
            description = "File will be written to build/resources/download-blt-files folder as it's used for resource at runtime")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully downloaded"),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server Error: This may indicate that the service account lacks permission for the folder containing the file.")})
    @GetMapping("/download")
    public void downloadFile(@RequestParam("fileId") final String fileId) {
        fileOperationsService.downloadFrom(fileId);
    }

    @Operation(summary = "Upload a single file",
            description = "A file with the corresponding extension in the resources/upload-blt-files folder will be uploaded.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully downloaded"),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server Error: This may indicate that the service account lacks permission for the folder where the file is being uploaded.")})
    @PostMapping("/single")
    public String uploadSingleFile(@RequestParam("folderId") final String folderId,
            @RequestParam("extension") final String extension) {
        final String dir = Optional
                .ofNullable(FileOperationsController.class.getClassLoader()
                        .getResource("upload-blt-files"))
                .orElseThrow(() -> new IllegalStateException("Failed to read the prepared files"))
                .getFile();
        final List<File> files = readDirectoryToList(dir);
        final File targetFile = files.stream()
                .filter(file -> FilenameUtils.getExtension(file.getName()).equals(extension))
                .findFirst().orElseThrow();
        return fileOperationsService.uploadSingleTo(targetFile, folderId);
    }

    @Operation(summary = "Upload multiple files in bulk.",
            description = "All files in the resources/upload-blt-files folder will be uploaded.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully downloaded"),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server Error: This may indicate that the service account lacks permission for the folder where all files are being uploaded.")})
    @PostMapping("/bulk")
    public void uploadBulkTo(@RequestParam("folderId") final String folderId) {
        final String dir = Optional
                .ofNullable(FileOperationsController.class.getClassLoader()
                        .getResource("upload-blt-files"))
                .orElseThrow(() -> new IllegalStateException("Failed to read the prepared files"))
                .getFile();
        final List<File> files = readDirectoryToList(dir);
        fileOperationsService.uploadBulkTo(files, folderId);
    }

    @Operation(summary = "Move a file to the trash",
            description = "The file will be moved to the trash. Only the file owner can move a file to the trash, and other users cannot view files in the owner's trash.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully downloaded"),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server Error: This may indicate that the service account The user does not have sufficient permissions for the file.")})
    @PutMapping("/trash")
    public void moveFileToTrash(@RequestParam("fileId") final String fileId) {
        fileOperationsService.moveFileToTrash(fileId);
    }

    @Operation(summary = "Move a file to the new folder",
            description = "The file can be moved to child folders (e.g., subdirectories like level1, level2, etc.) or separate folders (independent directories)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully downloaded"),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server Error: This may indicate that the service account lacks permission for the folder containing the file.")})
    @PutMapping("/move")
    public void moveFileToNewFolder(@RequestParam("newFolderId") final String newFolderId,
            @RequestParam("fileId") final String fileId) {
        fileOperationsService.moveFileToAnotherFolder(fileId, newFolderId);
    }
}
