package andre.chamis.healthproject.domain.workspace.dto;

import andre.chamis.healthproject.domain.workspace.model.Workspace;

import java.util.Date;

/**
 * Represents a data transfer object (DTO) for retrieving workspace information.
 */
public record GetWorkspaceDTO(Long workspaceId, String name, Long ownerId, Long patientId, boolean isActive,
                              Date createDt) {

    /**
     * Constructs a GetWorkspaceDTO object from a Workspace entity.
     *
     * @param workspace The Workspace entity to extract data from.
     * @param name      The name of the Workspace
     */
    public GetWorkspaceDTO(Workspace workspace, String name) {
        this(
                workspace.getWorkspaceId(),
                name,
                workspace.getOwnerId(),
                workspace.getPatientId(),
                workspace.isActive(),
                workspace.getCreateDt()
        );
    }
}
