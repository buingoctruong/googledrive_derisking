package indeed.googledrive_derisking.controlllers;

import org.springframework.web.bind.annotation.*;

import indeed.googledrive_derisking.service.FolderOperationsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/google/drive/folder")
@RequiredArgsConstructor
public class FolderOperationsController {
    private final FolderOperationsService folderOperationsService;
    @Operation(summary = "Create a folder for the given ticket",
            description = "The folder will be created by the service account, which will also grant access permissions to the specified user or group.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server Error: This may indicate that the service account lacks permission for the folder where the new folder is being created.")})
    @PostMapping
    public void createLionTicketFolder(@RequestParam("parentFolderId") final String parentFolderId,
            @RequestParam("lionTicket") final String lionTicket) {
        folderOperationsService.createTo(parentFolderId, lionTicket);
    }

    @Operation(summary = "Move a folder to the trash",
            description = "The folder and its contents will be moved to the trash. Only the folder owner can move a file to the trash, and other users cannot view files in the owner's trash.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server Error: This may indicate that the service account does not have sufficient permissions for the folder.")})
    @PutMapping("/trash")
    public void moveFileToTrash(@RequestParam("folderId") final String folderId) {
        folderOperationsService.removeFrom(folderId);
    }

    @Operation(summary = "Moves the folder to a new location.",
            description = "The folder and its contents will be moved to a new location, cannot move a folder to a descendant folder of itself.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server Error: This may indicate that the service account does not have sufficient permissions for the folders.")})
    @PutMapping("/move")
    public void moveFileToNewFolder(@RequestParam("newFolderId") final String newFolderId,
            @RequestParam("folderId") final String folderId) {
        folderOperationsService.moveTo(folderId, newFolderId);
    }
}
