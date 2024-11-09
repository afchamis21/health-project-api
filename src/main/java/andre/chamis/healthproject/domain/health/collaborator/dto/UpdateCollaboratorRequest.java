package andre.chamis.healthproject.domain.health.collaborator.dto;

public record UpdateCollaboratorRequest(Long patientId, Long userId, String description) {
}
