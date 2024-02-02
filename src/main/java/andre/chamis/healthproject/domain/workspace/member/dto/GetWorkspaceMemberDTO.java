package andre.chamis.healthproject.domain.workspace.member.dto;

import andre.chamis.healthproject.domain.user.dto.GetUserDTO;

import java.util.Date;

public record GetWorkspaceMemberDTO(Long workspaceId, boolean isMemberActive, Date createDt, GetUserDTO user) {
}
