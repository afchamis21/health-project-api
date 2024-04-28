package andre.chamis.healthproject.service;

import andre.chamis.healthproject.domain.exception.BadArgumentException;
import andre.chamis.healthproject.domain.exception.ValidationException;
import andre.chamis.healthproject.domain.patient.dto.CreatePatientDTO;
import andre.chamis.healthproject.domain.patient.dto.GetPatientDTO;
import andre.chamis.healthproject.domain.patient.dto.UpdatePatientDTO;
import andre.chamis.healthproject.domain.patient.model.Gender;
import andre.chamis.healthproject.domain.patient.model.Patient;
import andre.chamis.healthproject.domain.patient.repository.PatientRepository;
import andre.chamis.healthproject.domain.response.ErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

// TODO implementar controller
// TODO começar a implementar documentos (também vou precisar adicionar fotos aos enfermeiros)

@Repository
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;

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
    public Patient createPatient(CreatePatientDTO createPatientDTO) {
        Patient patient = new Patient();
        patient.setName(createPatientDTO.name());
        patient.setSurname(createPatientDTO.surname());
        patient.setGender(createPatientDTO.gender());
        patient.setDateOfBirth(createPatientDTO.dateOfBirth());
        patient.setContactPhone(createPatientDTO.contactPhone());
        patient.setCreateDt(Date.from(Instant.now()));

        try {
            patient.setDocument(createPatientDTO.document());
        } catch (ValidationException e) {
            throw new BadArgumentException(ErrorMessage.INVALID_RG);
        }

        if (patientRepository.existsByDocument(createPatientDTO.document())) {
            throw new BadArgumentException(ErrorMessage.PATIENT_ALREADY_REGISTERED);
        }

        return patientRepository.save(patient);
    }

    /**
     * Updates an existing patient.
     *
     * @param updatePatientDTO the DTO containing the updated patient information
     * @return the updated patient
     * @throws BadArgumentException if the patient is not found, if the RG (Registro Geral) is invalid
     */
    public Patient updatePatient(UpdatePatientDTO updatePatientDTO) {
        Patient patient = fetchPatientByIdOrThrow(updatePatientDTO.patientId());
        boolean updated = false;


        if (updatePatientDTO.name() != null) {
            patient.setName(updatePatientDTO.name());
            updated = true;
        }

        if (updatePatientDTO.surname() != null) {
            patient.setSurname(updatePatientDTO.surname());
            updated = true;
        }

        if (updatePatientDTO.document() != null) {
            try {
                patient.setDocument(updatePatientDTO.document());
                updated = true;
            } catch (ValidationException e) {
                throw new BadArgumentException(ErrorMessage.PATIENT_ALREADY_REGISTERED);
            }
        }

        if (updatePatientDTO.contactPhone() != null) {
            patient.setContactPhone(updatePatientDTO.contactPhone());
            updated = true;
        }

        if (updatePatientDTO.dateOfBirth() != null) {
            patient.setDateOfBirth(updatePatientDTO.dateOfBirth());
            updated = true;
        }

        if (!Gender.UNKNOWN.equals(updatePatientDTO.gender())) {
            patient.setGender(updatePatientDTO.gender());
            updated = true;
        }

        if (updated) {
            patient = patientRepository.save(patient);
        }

        return patient;
    }
}
