package andre.chamis.healthproject.domain.health.patient.model;

import andre.chamis.healthproject.domain.health.patient.dto.CreatePatientDTO;
import andre.chamis.healthproject.domain.health.patient.dto.UpdatePatientDTO;
import andre.chamis.healthproject.exception.ValidationException;
import andre.chamis.healthproject.infra.request.response.ErrorMessage;
import andre.chamis.healthproject.util.StringUtils;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Date;
import java.util.regex.Matcher;

/**
 * Represents a patient with personal information.
 */
@Data
@Entity
@Table(name = "patients")
@NoArgsConstructor
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id")
    private Long patientId;
    private String name;
    private String surname;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "contact_phone")
    private String contactPhone;

    @Column(name = "owner_id")
    private Long ownerId;

    @Enumerated(EnumType.ORDINAL)
    private Gender gender;

    private String rg;

    private String cpf;

    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    @Column(name = "create_dt")
    private Date createDt;

    @Column(name = "update_dt")
    private Date updateDt;

    @Column(name = "is_active")
    private boolean active;

    public Patient(CreatePatientDTO createPatientDTO, Long ownerId) throws ValidationException {
        setName(createPatientDTO.name());
        setSurname(createPatientDTO.surname());
        setRg(createPatientDTO.rg());
        setCpf(createPatientDTO.cpf());
        setGender(createPatientDTO.gender());
        setDateOfBirth(createPatientDTO.dateOfBirth());
        setContactPhone(createPatientDTO.contactPhone());
        setCreateDt(Date.from(Instant.now()));
        setOwnerId(ownerId);
        setActive(true);

        String[] names = new String[]{createPatientDTO.name(), createPatientDTO.surname()};
        this.fullName = String.join(" ", names);
    }

    public void setRg(String document) throws ValidationException {
        validateRg(document);

        this.rg = document;
    }

    private void validateRg(String document) throws ValidationException {
        if (document == null || document.trim().isBlank()) {
            throw new ValidationException(ErrorMessage.MISSING_PATIENT_RG);
        }

        String charsOnlyDocument = document.replaceAll("[^0-9]", "");

        // Verify if RG has exactly 9 digits
        if (charsOnlyDocument.length() != 9) {
            throw new ValidationException(ErrorMessage.INVALID_RG);
        }

        char lastDigit = document.charAt(document.length() - 1);

        // Calculate the verification digit
        int sum = 0;
        for (int i = 0; i < 8; i++) {
            int digit = Character.getNumericValue(charsOnlyDocument.charAt(i));
            sum += digit * (i + 1);
        }

        int remainder = sum % 11;
        int verificationDigit = 11 - remainder;

        // Check the last digit of the RG
        if (Integer.toString(verificationDigit).equals(Character.toString(lastDigit))
                || (verificationDigit == 10 && lastDigit == 'X')
                || (verificationDigit == 11 && lastDigit == '0')) {
            return;
        }

        throw new ValidationException(ErrorMessage.INVALID_RG);
    }

    public void setCpf(String document) throws ValidationException {
        validateCpf(document);

        this.cpf = document;
    }

    private void validateCpf(String document) throws ValidationException {
        if (document == null || document.trim().isBlank()) {
            throw new ValidationException(ErrorMessage.MISSING_PATIENT_CPF);
        }

        String charsOnlyDocument = document.replaceAll("[^0-9]", "");

        // Verify if RG has exactly 9 digits
        if (charsOnlyDocument.length() != 11) {
            throw new ValidationException(ErrorMessage.INVALID_CPF);
        }

        int firstVerificationDigit = calculateFirstCpfVerificationDigit(charsOnlyDocument);

        if (firstVerificationDigit != Character.getNumericValue(charsOnlyDocument.charAt(9))) {
            throw new ValidationException(ErrorMessage.INVALID_CPF);
        }

        int secondVerificationDigit = calculateSecondCpfVerificationDigit(charsOnlyDocument);
        if (secondVerificationDigit != Character.getNumericValue(charsOnlyDocument.charAt(10))) {
            throw new ValidationException(ErrorMessage.INVALID_CPF);
        }
    }

    private int calculateSecondCpfVerificationDigit(String charsOnlyDocument) {
        int sum = 0;
        int aux = 0;
        for (int i = 0; i < 10; i++) {
            sum += Character.getNumericValue(charsOnlyDocument.charAt(i)) * aux;
            aux++;
        }

        int verificationDigit = sum % 11;
        if (verificationDigit == 10) {
            verificationDigit = 0;
        }

        return verificationDigit;
    }

    private int calculateFirstCpfVerificationDigit(String charsOnlyDocument) {
        int sum = 0;
        int aux = 1;
        for (int i = 0; i < 9; i++) {
            sum += Character.getNumericValue(charsOnlyDocument.charAt(i)) * aux;
            aux++;
        }

        int verificationDigit = sum % 11;
        if (verificationDigit == 10) {
            verificationDigit = 0;
        }

        return verificationDigit;
    }

    public void setName(String name) throws ValidationException {
        validateName(name);

        this.name = name;
    }

    private void validateName(String name) throws ValidationException {
        if (name == null || name.trim().isBlank()) {
            throw new ValidationException(ErrorMessage.MISSING_PATIENT_NAME);
        }
    }

    public void setContactPhone(String contactPhone) throws ValidationException {
        validateContactPhone(contactPhone);

        this.contactPhone = contactPhone;
    }

    private void validateContactPhone(String contactPhone) throws ValidationException {
        if (contactPhone == null || contactPhone.isBlank()) {
            throw new ValidationException(ErrorMessage.MISSING_PATIENT_CONTACT_PHONE);
        }

        Matcher matcher = StringUtils.getPhoneRegex().matcher(contactPhone);
        if (!matcher.matches()) {
            throw new ValidationException(ErrorMessage.INVALID_PHONE);
        }
    }

    public void setGender(Gender gender) throws ValidationException {
        validateGender(gender);

        this.gender = gender;
    }

    private void validateGender(Gender gender) throws ValidationException {
        if (gender == null || gender.equals(Gender.UNKNOWN)) {
            throw new ValidationException(ErrorMessage.INVALID_GENDER);
        }
    }

    public void setDateOfBirth(Date dateOfBirth) throws ValidationException {
        validateDateOfBirth(dateOfBirth);
        this.dateOfBirth = dateOfBirth;
    }

    private void validateDateOfBirth(Date dateOfBirth) throws ValidationException {
        if (dateOfBirth == null) {
            throw new ValidationException(ErrorMessage.MISSING_PATIENT_DATE_OF_BIRTH);
        }
    }

    public void setSurname(String surname) throws ValidationException {
        validateSurname(surname);
        this.surname = surname;
    }

    private void validateSurname(String surname) throws ValidationException {
        if (surname == null || surname.isBlank()) {
            throw new ValidationException(ErrorMessage.MISSING_PATIENT_SURNAME);
        }
    }

    public boolean update(UpdatePatientDTO updatePatientDTO) throws ValidationException {
        boolean updated = false;
        if (updatePatientDTO.name() != null) {
            setName(updatePatientDTO.name());
            updated = true;
        }

        if (updatePatientDTO.surname() != null) {
            setSurname(updatePatientDTO.surname());
            updated = true;
        }

        if (updatePatientDTO.document() != null) {
            setRg(updatePatientDTO.document());
            updated = true;
        }

        if (updatePatientDTO.contactPhone() != null) {
            setContactPhone(updatePatientDTO.contactPhone());
            updated = true;
        }

        if (updatePatientDTO.dateOfBirth() != null) {
            setDateOfBirth(updatePatientDTO.dateOfBirth());
            updated = true;
        }

        if (updatePatientDTO.gender() != null && !Gender.UNKNOWN.equals(updatePatientDTO.gender())) {
            setGender(updatePatientDTO.gender());
            updated = true;
        }

        return updated;
    }
}
