package andre.chamis.healthproject.domain.health.patient.dto;

import andre.chamis.healthproject.domain.health.patient.model.Gender;
import andre.chamis.healthproject.domain.health.patient.model.Patient;

import java.util.Date;

public record GetPatientDTO(Long patientId,
                            String name,
                            String surname,
                            String document,
                            String contactPhone,
                            Gender gender,
                            Date dateOfBirth) {

    public static GetPatientDTO fromPatient(Patient patient) {
        return new GetPatientDTO(
                patient.getPatientId(),
                patient.getName(),
                patient.getSurname(),
                patient.getRg(),
                patient.getContactPhone(),
                patient.getGender(),
                patient.getDateOfBirth()
        );
    }
}
