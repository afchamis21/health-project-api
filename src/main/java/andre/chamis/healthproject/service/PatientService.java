package andre.chamis.healthproject.service;

import andre.chamis.healthproject.domain.exception.BadArgumentException;
import andre.chamis.healthproject.domain.exception.ValidationException;
import andre.chamis.healthproject.domain.patient.dto.CreatePatientDTO;
import andre.chamis.healthproject.domain.patient.dto.GetPatientDTO;
import andre.chamis.healthproject.domain.patient.dto.UpdatePatientDTO;
import andre.chamis.healthproject.domain.patient.model.Patient;
import andre.chamis.healthproject.domain.patient.repository.PatientRepository;
import andre.chamis.healthproject.domain.response.ErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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
        try {
            Patient patient = new Patient(createPatientDTO);

            return patientRepository.save(patient);
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
}
