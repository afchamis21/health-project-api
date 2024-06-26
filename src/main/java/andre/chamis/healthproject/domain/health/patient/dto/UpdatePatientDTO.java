package andre.chamis.healthproject.domain.health.patient.dto;

import andre.chamis.healthproject.domain.health.patient.model.Gender;

import java.util.Date;

public record UpdatePatientDTO(String name,
                               String surname,
                               String document,
                               Gender gender,
                               String contactPhone,
                               Date dateOfBirth
) {
}
