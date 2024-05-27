package andre.chamis.healthproject.domain.collaborator.dto;

import andre.chamis.healthproject.domain.user.dto.GetUserDTO;

import java.util.Date;

public record GetCollaboratorDTO(Long patientId, boolean isCollaboratorActive, Date createDt, GetUserDTO user) {
}
