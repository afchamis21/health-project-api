package andre.chamis.healthproject.domain.patient.repository;

import andre.chamis.healthproject.domain.patient.model.Patient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

/**
 * Repository class for managing patients.
 */
@Repository
@RequiredArgsConstructor
public class PatientRepository {
    private final PatientJpaRepository jpaRepository;

    /**
     * Saves a patient.
     *
     * @param patient the patient to save
     * @return the saved patient
     */
    public Patient save(Patient patient) {
        patient.setUpdateDt(Date.from(Instant.now()));
        return jpaRepository.save(patient);
    }

    /**
     * Checks if a patient exists with the given document.
     *
     * @param document the document to check
     * @return true if a patient exists with the given document, false otherwise
     */
    public boolean existsByDocument(String document) {
        return jpaRepository.existsByDocument(document);
    }

    /**
     * Finds a patient by their ID.
     *
     * @param patientId the ID of the patient
     * @return an Optional containing the patient, or empty if not found
     */
    public Optional<Patient> findById(Long patientId) {
        return jpaRepository.findById(patientId);
    }
}
