package andre.chamis.healthproject.domain.health.collaborator.repository;

import andre.chamis.healthproject.domain.health.collaborator.dto.GetCollaboratorDTO;
import andre.chamis.healthproject.domain.health.collaborator.model.Collaborator;
import andre.chamis.healthproject.domain.user.dto.GetUsernameAndIdDTO;
import andre.chamis.healthproject.infra.request.request.PaginationInfo;
import andre.chamis.healthproject.infra.request.response.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CollaboratorRepository {
    private final CollaboratorDAO collaboratorDAO;
    private final CollaboratorJpaRepository jpaRepository;

    public Collaborator save(Collaborator collaborator) {
        return jpaRepository.save(collaborator);
    }

    public boolean existsByPatientIdAndUserIdAndIsActive(Long patientId, Long userId) {
        return jpaRepository.existsByPatientIdAndUserIdAndActive(patientId, userId, true);
    }

    public PaginatedResponse<GetCollaboratorDTO> getAllCollaboratorsByPatientId(Long patientId, PaginationInfo paginationInfo) {
        return collaboratorDAO.getAllCollaboratorsByPatientId(patientId, paginationInfo);
    }

    public void updateCollaboratorSetActive(Long patientId, Long userId, boolean active) {
        jpaRepository.updateCollaboratorByPatientIdAndUserIdSetActive(patientId, userId, active);
    }

    public List<GetUsernameAndIdDTO> getAllCollaboratorNamesByPatientId(Long patientId) {
        return collaboratorDAO.getAllCollaboratorNamesByPatientId(patientId);
    }

    public boolean existsByPatientIdAndUserId(Long patientId, Long userId) {
        return jpaRepository.existsByPatientIdAndUserId(patientId, userId);
    }

    public long deleteAllByPatientId(Long patientId) {
        return jpaRepository.deleteAllByPatientId(patientId);
    }

    public Optional<Collaborator> getByPatientIdAndUserId(Long patientId, Long userId) {
        return jpaRepository.findByPatientIdAndUserId(patientId, userId);
    }
}
