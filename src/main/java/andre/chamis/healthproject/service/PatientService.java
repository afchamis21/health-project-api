package andre.chamis.healthproject.service;

import andre.chamis.healthproject.context.ServiceContext;
import andre.chamis.healthproject.domain.health.attendance.dto.GetAttendanceWithUsernameDTO;
import andre.chamis.healthproject.domain.health.collaborator.dto.GetCollaboratorDTO;
import andre.chamis.healthproject.domain.health.collaborator.model.Collaborator;
import andre.chamis.healthproject.domain.health.collaborator.repository.CollaboratorRepository;
import andre.chamis.healthproject.domain.health.patient.dto.CreatePatientDTO;
import andre.chamis.healthproject.domain.health.patient.dto.GetPatientDTO;
import andre.chamis.healthproject.domain.health.patient.dto.GetPatientSummaryDTO;
import andre.chamis.healthproject.domain.health.patient.dto.UpdatePatientDTO;
import andre.chamis.healthproject.domain.health.patient.model.Patient;
import andre.chamis.healthproject.domain.health.patient.repository.PatientRepository;
import andre.chamis.healthproject.domain.user.dto.GetUsernameAndIdDTO;
import andre.chamis.healthproject.domain.user.model.User;
import andre.chamis.healthproject.exception.BadArgumentException;
import andre.chamis.healthproject.exception.ForbiddenException;
import andre.chamis.healthproject.exception.ValidationException;
import andre.chamis.healthproject.infra.request.request.PaginationInfo;
import andre.chamis.healthproject.infra.request.response.ErrorMessage;
import andre.chamis.healthproject.infra.request.response.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// TODO começar a implementar documentos (também vou precisar adicionar fotos aos enfermeiros)

@Slf4j
@Repository
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;
    private final CollaboratorRepository collaboratorRepository;
    private final AttendanceService attendanceService;
    private final CollaboratorService collaboratorService;

    /**
     * Retrieves a patient by their ID.
     *
     * @param id the ID of the patient
     * @return the DTO representing the patient
     */
    public GetPatientDTO getPatient(Long id) {
        return GetPatientDTO.fromPatient(fetchPatientByIdOrThrow(id));
    }

    /**
     * Fetches a patient by their ID.
     *
     * @param patientId the ID of the patient
     * @return an Optional containing the patient, or empty if not found
     */
    public Optional<Patient> fetchPatientById(Long patientId) {
        return patientRepository.findById(patientId);
    }

    /**
     * Fetches a patient by their ID or throws an exception if not found.
     *
     * @param patientId the ID of the patient
     * @return the patient if found
     * @throws BadArgumentException if the patient is not found
     */
    public Patient fetchPatientByIdOrThrow(Long patientId) {
        return fetchPatientById(patientId).orElseThrow(() -> new BadArgumentException(ErrorMessage.PATIENT_NOT_FOUND));
    }

    /**
     * Creates a new patient.
     *
     * @param createPatientDTO the DTO containing the patient information
     * @return the newly created patient
     * @throws BadArgumentException if the patient already exists or if the RG (Registro Geral) is invalid
     */
    protected GetPatientSummaryDTO createPatient(CreatePatientDTO createPatientDTO) {
        try {
            Long currentUserId = ServiceContext.getContext().getUserId();

            log.info("Creating patient with name [{} {}] and ownerId [{}]", createPatientDTO.name(), createPatientDTO.surname(), currentUserId);

            Patient patient = new Patient(createPatientDTO, ServiceContext.getContext().getUserId());

            patient = patientRepository.save(patient);

            log.info("Adding owner as collaborator of patient!");
            Collaborator collaborator = new Collaborator(patient.getPatientId(), currentUserId);
            collaboratorRepository.save(collaborator);
            log.info("Owner saved to database");

            return GetPatientSummaryDTO.fromPatient(patient);
        } catch (ValidationException e) {
            throw new BadArgumentException(e.getError());
        }
    }

    /**
     * Updates an existing patient.
     *
     * @param patientId        the ID of the patient to be updated
     * @param updatePatientDTO the DTO containing the updated patient information
     * @return the updated patient
     * @throws BadArgumentException if the patient is not found, or if any of the non-null fields provided are invalid
     */
    public Patient updatePatient(Long patientId, UpdatePatientDTO updatePatientDTO) {
        Patient patient = fetchPatientByIdOrThrow(patientId);

        try {
            boolean updated = patient.update(updatePatientDTO);

            if (updated) {
                patient = patientRepository.save(patient);
            }

            return patient;
        } catch (ValidationException e) {
            throw new BadArgumentException(e.getError());
        }
    }

    public void deletePatient(Long patientId) {
        log.info("Preparing to delete patient [{}]", patientId);
        Patient patient = patientRepository.getIfOwnerOrThrow(patientId, false);

        if (patient.isActive()) {
            log.warn("Patient [{}] is deactivated!", patient);
            throw new ForbiddenException(ErrorMessage.CAN_NOT_DELETE_ACTIVE_PATIENT);
        }

        patientRepository.deleteByPatientId(patientId);
        log.info("Patient [{}] permanently deleted!", patientId);

        long deletedUsers = collaboratorRepository.deleteAllByPatientId(patientId);
        log.info("Deleted all ({}) collaborators of patient [{}]", deletedUsers, patientId);
    }

    public void activePatient(Long patientId) {
        Patient patient = patientRepository.getIfOwnerOrThrow(patientId, false);

        log.info("Activating patient [{}]", patientId);
        patient.setActive(true);

        patientRepository.save(patient);
    }

    public void deactivatePatient(Long patientId) {
        Patient patient = patientRepository.getIfOwnerOrThrow(patientId, false);

        log.info("Deactivating patient [{}]", patientId);
        patient.setActive(false);

        patientRepository.save(patient);
    }

    public void checkPatientOwnershipOrThrow(Long patientId) {
        Long currentUserId = ServiceContext.getContext().getUserId();
        boolean isPatientOwner = patientRepository.existsByPatientIdAndOwnerId(patientId, currentUserId);

        if (!isPatientOwner) {
            throw new ForbiddenException(ErrorMessage.PATIENT_OWNERSHIP);
        }
    }

    public GetPatientSummaryDTO getPatientSummaryDTOById(Long patientId) {
        Long userId = ServiceContext.getContext().getUserId();
        Optional<GetPatientSummaryDTO> result = patientRepository.getPatientSummaryByIdIfOwnerOrCollaboratorAndActive(
                patientId, userId
        );

        return result.orElseThrow(() -> new ForbiddenException(ErrorMessage.INVALID_PATIENT_ACCESS));
    }

    public PaginatedResponse<GetAttendanceWithUsernameDTO> getAttendances(
            Long patientId, Optional<Long> userId, PaginationInfo paginationInfo
    ) {
        checkPatientOwnershipOrThrow(patientId);

        return attendanceService.getAttendances(patientId, userId, paginationInfo);
    }

    public PaginatedResponse<GetCollaboratorDTO> getCollaborators(Long patientId, PaginationInfo paginationInfo) {
        checkPatientOwnershipOrThrow(patientId);

        return collaboratorService.getAllCollaboratorsOfPatient(patientId, paginationInfo);
    }

    public List<GetUsernameAndIdDTO> getAllCollaboratorNames(Long patientId) {
        checkPatientOwnershipOrThrow(patientId);

        return collaboratorService.getAllCollaboratorNamesOfPatient(patientId);
    }

    public GetCollaboratorDTO addCollaborator(User user, Long patientId, String description) {
        checkPatientOwnershipOrThrow(patientId);

        return collaboratorService.addCollaboratorToPatient(user, patientId, description);
    }

    public void activateCollaborator(Long patientId, Long userId) {
        checkPatientOwnershipOrThrow(patientId);

        collaboratorService.activateCollaborator(patientId, userId);

    }

    public void deactivateCollaborator(Long patientId, Long userId) {
        checkPatientOwnershipOrThrow(patientId);

        collaboratorService.deactivateCollaborator(patientId, userId);
    }

    protected PaginatedResponse<GetPatientSummaryDTO> findPatientsByCollaboratorId(Long collaboratorId, PaginationInfo paginationInfo) {
        return patientRepository.findPatientsByCollaboratorId(collaboratorId, paginationInfo);
    }

    public PaginatedResponse<GetPatientSummaryDTO> searchPatientsByNameAndCollaboratorId(Long collaboratorId, String name, PaginationInfo paginationInfo) {
        return patientRepository.searchPatientsByNameAndCollaboratorId(collaboratorId, name, paginationInfo);
    }
}
