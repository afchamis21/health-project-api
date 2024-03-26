package andre.chamis.healthproject.domain.workspace.dto;

import andre.chamis.healthproject.domain.workspace.model.Workspace;

import java.util.Date;

/**
 * Represents a data transfer object (DTO) for retrieving workspace information.
 */
public record GetWorkspaceDTO(Long workspaceId, String name, Long ownerId, boolean isActive, Date createDt) {

    /**
     * Constructs a GetWorkspaceDTO object from a Workspace entity.
     *
     * @param workspace The Workspace entity to extract data from.
     * @return A GetWorkspaceDTO object populated with data from the Workspace entity.
     */
    public static GetWorkspaceDTO fromWorkspace(Workspace workspace) {
        return new GetWorkspaceDTO(
                workspace.getWorkspaceId(),
                workspace.getWorkspaceName(),
                workspace.getOwnerId(),
                workspace.isActive(),
                workspace.getCreateDt()
        );
    }
}
