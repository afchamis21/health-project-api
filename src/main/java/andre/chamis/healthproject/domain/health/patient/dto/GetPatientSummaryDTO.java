package andre.chamis.healthproject.domain.health.patient.dto;

import andre.chamis.healthproject.domain.health.patient.model.Patient;

import java.util.Date;

/**
 * Represents a data transfer object (DTO) for retrieving summary information of a patient.
 */
public record GetPatientSummaryDTO(
        Long patientId,
        String name,
        Long ownerId,
        boolean isActive,
        Date createDt
) {

    public static GetPatientSummaryDTO fromPatient(Patient patient) {
        return new GetPatientSummaryDTO(
                patient.getPatientId(),
                patient.getName() + " " + patient.getSurname(),
                patient.getOwnerId(),
                patient.isActive(),
                patient.getCreateDt());
    }
}
