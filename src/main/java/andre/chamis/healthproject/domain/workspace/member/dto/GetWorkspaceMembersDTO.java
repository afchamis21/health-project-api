package andre.chamis.healthproject.domain.workspace.member.dto;

import java.util.List;

public record GetWorkspaceMembersDTO(int lastPage, List<GetWorkspaceMemberDTO> members) {
}
