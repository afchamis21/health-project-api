package andre.chamis.healthproject.domain.patient.model;

import andre.chamis.healthproject.domain.exception.ValidationException;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Represents a patient with personal information.
 */
@Data
@Entity
@NoArgsConstructor
@Table(name = "patients")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long patientId;

    private String name;
    private String surname;

    @Column(name = "contact_phone")
    private String contactPhone;

    @Enumerated(EnumType.ORDINAL)
    private Gender gender;

    /**
     * The document identifier, typically the RG (Registro Geral).
     */
    @Column(unique = true)
    private String document;

    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    @Column(name = "create_dt")
    private Date createDt;

    @Column(name = "update_dt")
    private Date updateDt;

    /**
     * Validates the provided document number.
     *
     * @param document the document number to validate
     * @return true if the document is valid, false otherwise
     */
    public boolean validateDocument(String document) {
        document = document.replaceAll("[^0-9]", "");

        // Verify if RG has exactly 9 digits
        if (document.length() != 9) {
            return false;
        }

        // Calculate verifying digit
        int sum = 0;
        for (int i = 0; i < 8; i++) {
            int digit = Character.getNumericValue(document.charAt(i));
            sum += digit * (9 - i);
        }

        int rest = sum % 11;
        int verifyingDigit = rest < 2 ? 0 : 11 - rest;

        // Check if expected verifying digit matches actual verifying digit
        int lastDigit = Character.getNumericValue(document.charAt(8));
        return verifyingDigit == lastDigit;
    }

    public void setDocument(String document) throws ValidationException {
        if (!validateDocument(document)) {
            throw new ValidationException();
        }

        this.document = document;
    }
}
