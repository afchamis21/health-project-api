package andre.chamis.healthproject;

import andre.chamis.healthproject.domain.health.patient.model.Patient;
import andre.chamis.healthproject.exception.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PatientTest {

    @Test
    public void Should_Throw_ValidationException_When_CPF_Is_Invalid() {
        Patient patient = new Patient();

        assertThrows(ValidationException.class, () -> patient.setCpf("123.456.789.10"));
        assertThrows(ValidationException.class, () -> patient.setCpf(""));
        assertThrows(ValidationException.class, () -> patient.setCpf(null));
    }

    @Test
    public void Should_Not_Throw_ValidationException_When_CPF_Is_Valid() {
        Patient patient = new Patient();

        assertDoesNotThrow(() -> {
            patient.setCpf("086.394.329-29");
            patient.setCpf("064.868.633-74");
        });
    }

    @Test
    public void Should_Throw_ValidationException_When_RG_Is_Invalid() {
        Patient patient = new Patient();

        assertThrows(ValidationException.class, () -> patient.setRg("11.222.633-8"));
        assertThrows(ValidationException.class, () -> patient.setRg(""));
        assertThrows(ValidationException.class, () -> patient.setRg(null));
    }

    @Test
    public void Should_Not_Throw_ValidationException_When_RG_Is_Valid() {
        Patient patient = new Patient();

        assertDoesNotThrow(() -> patient.setRg("58.062.642-8"));
    }
}
