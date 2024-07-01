package andre.chamis.healthproject.domain.health.patient.repository;

import andre.chamis.healthproject.domain.health.patient.dto.GetPatientSummaryDTO;
import andre.chamis.healthproject.domain.health.patient.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA repository for managing patients.
 */
@Repository
interface PatientJpaRepository extends JpaRepository<Patient, Long> {
    @Query(value = """
                    SELECT DISTINCT new andre.chamis.healthproject.domain.health.patient.dto.GetPatientSummaryDTO(
                        p.patientId,
                        CONCAT(p.name, COALESCE(' ' || p.surname, '')),
                        p.ownerId,
                        p.active,
                        p.createDt
                    )
                    FROM Patient p
                    JOIN Collaborator c ON p.patientId = c.patientId
                    WHERE p.patientId = :patientId
                    AND (
                        (p.ownerId = :userId AND c.userId = :userId) OR
                        (c.userId = :userId AND c.active = true)
                    )
            """)
    Optional<GetPatientSummaryDTO> getPatientSummaryByIdIfOwnerOrCollaboratorAndActive(Long patientId, Long userId);

    boolean existsByPatientIdAndOwnerId(Long patientId, Long ownerId);
}
