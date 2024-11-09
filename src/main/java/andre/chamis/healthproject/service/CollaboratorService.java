package andre.chamis.healthproject.service;

import andre.chamis.healthproject.domain.health.collaborator.dto.GetCollaboratorDTO;
import andre.chamis.healthproject.domain.health.collaborator.dto.UpdateCollaboratorRequest;
import andre.chamis.healthproject.domain.health.collaborator.model.Collaborator;
import andre.chamis.healthproject.domain.health.collaborator.repository.CollaboratorRepository;
import andre.chamis.healthproject.domain.user.dto.GetUsernameAndIdDTO;
import andre.chamis.healthproject.domain.user.model.User;
import andre.chamis.healthproject.exception.BadArgumentException;
import andre.chamis.healthproject.infra.request.request.PaginationInfo;
import andre.chamis.healthproject.infra.request.response.ErrorMessage;
import andre.chamis.healthproject.infra.request.response.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CollaboratorService {
    private final CollaboratorRepository collaboratorRepository;

    protected GetCollaboratorDTO addCollaboratorToPatient(User user, Long patientId) {
        log.debug("Checking if user [{}] already is collaborator of patient [{}]!", user.getUserId(), patientId);

        if (collaboratorRepository.existsByPatientIdAndUserId(patientId, user.getUserId())) {
            log.warn("User already is collaborator of patient!");
            throw new BadArgumentException(ErrorMessage.USER_ALREADY_COLLABORATOR);
        }

        log.debug("Check passed!");

        Collaborator collaborator = new Collaborator(patientId, user.getUserId());
        log.debug("Created collaborator [{}]", collaborator);

        collaboratorRepository.save(collaborator);
        log.info("Added collaborator to database [{}]", collaborator);

        return new GetCollaboratorDTO(
                patientId,
                collaborator,
                user
        );
    }

    protected PaginatedResponse<GetCollaboratorDTO> getAllCollaboratorsOfPatient(Long patientId, PaginationInfo paginationInfo) {
        log.info("Searching for all collaborators of patient [{}]. Pagination options: page [{}] size [{}]", patientId, paginationInfo.getPage(), paginationInfo.getSize());
        PaginatedResponse<GetCollaboratorDTO> collaborator = collaboratorRepository.getAllCollaboratorsByPatientId(patientId, paginationInfo);

        log.info("Found collaborators [{}]", collaborator);

        return collaborator;
    }

    protected void activateCollaborator(Long patientId, Long userId) {
        // TODO might want to implement ADMIN users later on
        collaboratorRepository.updateCollaboratorSetActive(patientId, userId, true);
    }


    protected void deactivateCollaborator(Long patientId, Long userId) {
        // TODO might want to implement ADMIN users later on
        collaboratorRepository.updateCollaboratorSetActive(patientId, userId, false);
    }

    protected List<GetUsernameAndIdDTO> getAllCollaboratorNamesOfPatient(Long patientId) {
        // TODO might want to implement ADMIN users later on
        return collaboratorRepository.getAllCollaboratorNamesByPatientId(patientId);
    }

    public boolean isUserActiveCollaboratorOfPatient(Long patientId, Long userId) {
        return collaboratorRepository.existsByPatientIdAndUserIdAndIsActive(patientId, userId);
    }

    public void updateCollaborator(UpdateCollaboratorRequest updateCollaboratorRequest) {
        if (updateCollaboratorRequest.description() == null || updateCollaboratorRequest.description().isBlank()) {
            throw new BadArgumentException(ErrorMessage.MISSING_COLLABORATOR_DESCRIPTION);
        }

        Collaborator collaborator = collaboratorRepository.getByPatientIdAndUserId(updateCollaboratorRequest.patientId(), updateCollaboratorRequest.userId())
                .orElseThrow(() -> new BadArgumentException(ErrorMessage.COLLABORATOR_NOT_FOUND));


        collaborator.setDescription(updateCollaboratorRequest.description());

        collaboratorRepository.save(collaborator);
    }
}
