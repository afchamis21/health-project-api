package andre.chamis.healthproject.domain.collaborator.repository;

import andre.chamis.healthproject.domain.collaborator.dto.GetCollaboratorDTO;
import andre.chamis.healthproject.domain.collaborator.model.Collaborator;
import andre.chamis.healthproject.domain.request.PaginationInfo;
import andre.chamis.healthproject.domain.response.PaginatedResponse;
import andre.chamis.healthproject.domain.user.dto.GetUsernameAndIdDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

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
        // TODO refactor dao method
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
}
