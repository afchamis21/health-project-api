package andre.chamis.healthproject.domain.workspace.dto;

import java.util.List;

public record GetWorkspacesDTO(int lastPage, List<GetWorkspaceDTO> workspaces) {
}
