package andre.chamis.healthproject.domain.patient.repository;

import andre.chamis.healthproject.domain.patient.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA repository for managing patients.
 */
@Repository
interface PatientJpaRepository extends JpaRepository<Patient, Long> {

    /**
     * Checks if a patient exists with the given document.
     *
     * @param document the document to check
     * @return true if a patient exists with the given document, false otherwise
     */
    boolean existsByDocument(String document);
}
