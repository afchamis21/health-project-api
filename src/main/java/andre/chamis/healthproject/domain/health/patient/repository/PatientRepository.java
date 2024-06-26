package andre.chamis.healthproject.domain.health.patient.repository;

import andre.chamis.healthproject.context.ServiceContext;
import andre.chamis.healthproject.domain.health.patient.dto.GetPatientSummaryDTO;
import andre.chamis.healthproject.domain.health.patient.model.Patient;
import andre.chamis.healthproject.exception.BadArgumentException;
import andre.chamis.healthproject.exception.ForbiddenException;
import andre.chamis.healthproject.infra.request.request.PaginationInfo;
import andre.chamis.healthproject.infra.request.response.ErrorMessage;
import andre.chamis.healthproject.infra.request.response.PaginatedResponse;
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
    private final PatientDAO patientDAO;

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
     * Finds a patient by their ID.
     *
     * @param patientId the ID of the patient
     * @return an Optional containing the patient, or empty if not found
     */
    public Optional<Patient> findById(Long patientId) {
        return jpaRepository.findById(patientId);
    }

    public void deleteByPatientId(Long patientId) {
        jpaRepository.deleteById(patientId);
    }

    public Patient findByPatientIdOrThrow(Long patientId) {
        return jpaRepository.findById(patientId).orElseThrow(() -> new BadArgumentException(ErrorMessage.PATIENT_NOT_FOUND));
    }

    public Patient getIfActiveOrThrow(Long patientId) {
        Patient patient = findByPatientIdOrThrow(patientId);

        if (!patient.isActive()) {
            throw new ForbiddenException(ErrorMessage.INACTIVE_PATIENT);
        }

        return patient;
    }

    public Patient getIfOwnerOrThrow(Long patientId, boolean activeOnly) {
        Patient patient;

        if (activeOnly) {
            patient = getIfActiveOrThrow(patientId);
        } else {
            patient = findByPatientIdOrThrow(patientId);
        }

        if (!patient.getOwnerId().equals(ServiceContext.getContext().getUserId())) {
            throw new ForbiddenException(ErrorMessage.PATIENT_OWNERSHIP);
        }

        return patient;
    }

    public PaginatedResponse<GetPatientSummaryDTO> findPatientsByCollaboratorId(Long userId, PaginationInfo paginationInfo) {
        return patientDAO.getPatientsByCollaboratorId(userId, paginationInfo);
    }

    public PaginatedResponse<GetPatientSummaryDTO> searchPatientsByNameAndCollaboratorId(Long userId, String name, PaginationInfo paginationInfo) {
        return patientDAO.searchPatientsByNameAndCollaboratorId(userId, name, paginationInfo);
    }

    public boolean existsByPatientIdAndOwnerId(Long patientId, Long ownerId) {
        return jpaRepository.existsByPatientIdAndOwnerId(patientId, ownerId);
    }

    public Optional<GetPatientSummaryDTO> getPatientSummaryByIdIfOwnerOrCollaboratorAndActive(Long patientId, Long userId) {
        return jpaRepository.getPatientSummaryByIdIfOwnerOrCollaboratorAndActive(patientId, userId);
    }
}
