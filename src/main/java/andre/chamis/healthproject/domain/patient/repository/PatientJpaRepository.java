package andre.chamis.healthproject.domain.patient.repository;

import andre.chamis.healthproject.domain.patient.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA repository for managing patients.
 */
@Repository
interface PatientJpaRepository extends JpaRepository<Patient, Long> {
}
