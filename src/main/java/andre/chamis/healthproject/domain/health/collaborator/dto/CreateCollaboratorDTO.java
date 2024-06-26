package andre.chamis.healthproject.domain.health.collaborator.dto;

public record CreateCollaboratorDTO(
        String email,
        Long patientId
) {
}
