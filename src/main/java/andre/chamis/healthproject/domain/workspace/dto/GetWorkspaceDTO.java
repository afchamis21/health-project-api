package andre.chamis.healthproject.domain.workspace.dto;

import andre.chamis.healthproject.domain.workspace.model.Workspace;

import java.util.Date;

public record GetWorkspaceDTO(Long workspaceId, String name, Long ownerId, boolean isActive, Date createDt) {
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
