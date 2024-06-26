package andre.chamis.healthproject.domain.health.collaborator.repository;

import andre.chamis.healthproject.domain.health.collaborator.model.Collaborator;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface CollaboratorJpaRepository extends JpaRepository<Collaborator, Long> {

    boolean existsByPatientIdAndUserIdAndActive(Long PatientId, Long userId, boolean active);

    long deleteAllByPatientId(Long PatientId);

    Optional<Collaborator> findByPatientIdAndUserId(Long PatientId, Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE Collaborator wm SET wm.active = :active WHERE wm.patientId = :patientId AND wm.userId = :userId ")
    void updateCollaboratorByPatientIdAndUserIdSetActive(Long patientId, Long userId, boolean active);

    boolean existsByPatientIdAndUserId(Long patientId, Long userId);
}
