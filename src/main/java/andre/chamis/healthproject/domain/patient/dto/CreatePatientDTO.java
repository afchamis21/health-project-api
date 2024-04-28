package andre.chamis.healthproject.domain.patient.dto;

import andre.chamis.healthproject.domain.patient.model.Gender;

import java.util.Date;

public record CreatePatientDTO(String name,
                               String surname,
                               String document,
                               Gender gender,
                               String contactPhone,
                               Date dateOfBirth) {
}
