package andre.chamis.healthproject.service;

import andre.chamis.healthproject.domain.collaborator.dto.CreateCollaboratorDTO;
import andre.chamis.healthproject.domain.collaborator.dto.GetCollaboratorDTO;
import andre.chamis.healthproject.domain.collaborator.model.Collaborator;
import andre.chamis.healthproject.domain.collaborator.repository.CollaboratorRepository;
import andre.chamis.healthproject.domain.exception.BadArgumentException;
import andre.chamis.healthproject.domain.request.PaginationInfo;
import andre.chamis.healthproject.domain.response.ErrorMessage;
import andre.chamis.healthproject.domain.response.PaginatedResponse;
import andre.chamis.healthproject.domain.user.dto.GetUserDTO;
import andre.chamis.healthproject.domain.user.dto.GetUsernameAndIdDTO;
import andre.chamis.healthproject.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CollaboratorService {
    private final CollaboratorRepository collaboratorRepository;

    protected GetCollaboratorDTO addCollaboratorToPatient(Long patientId, CreateCollaboratorDTO createCollaboratorDTO) {
        // TODO adding a collaborator to a patient is a user action (because it might register a new user, need to move it there
        String email = createCollaboratorDTO.email();

        log.info("Getting user with email [{}] or registering a new one!", email);

        User user = userService.findUserByEmail(email).orElseGet(() -> {
            log.warn("Creating a new user with email [{}]", email);
            return userService.createUser(email, Optional.empty());
        });

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
                collaborator.isActive(),
                collaborator.getCreateDt(),
                GetUserDTO.fromUser(user)
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
}
