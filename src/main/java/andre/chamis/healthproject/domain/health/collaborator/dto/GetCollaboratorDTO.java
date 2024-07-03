package andre.chamis.healthproject.domain.health.collaborator.dto;

import andre.chamis.healthproject.domain.health.collaborator.model.Collaborator;
import andre.chamis.healthproject.domain.user.dto.GetUserSummaryDTO;
import andre.chamis.healthproject.domain.user.model.User;

import java.util.Date;

public record GetCollaboratorDTO(Long patientId, boolean isCollaboratorActive, Date createDt, GetUserSummaryDTO user) {
    public GetCollaboratorDTO(Long patientId, Collaborator collaborator, User user) {
        this(patientId, collaborator.isActive(), collaborator.getCreateDt(), GetUserSummaryDTO.fromUser(user));
    }
}
