package andre.chamis.healthproject.domain.health.patient.model;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * Converter class for converting strings to Gender enum.
 */
@Component
public class StringToGenderConverter implements Converter<String, Gender> {

    /**
     * Converts a string to a Gender enum.
     *
     * @param source the source string to convert
     * @return the corresponding Gender enum, or {@code Gender.UNKNOWN} if the source string is empty or not recognized
     */
    @Override
    public Gender convert(@NonNull String source) {
        if (source.isEmpty()) {
            return Gender.UNKNOWN;
        }

        for (Gender gender : Gender.values()) {
            if (gender.name().equalsIgnoreCase(source)) {
                return gender;
            }
        }

        return Gender.UNKNOWN;
    }
}
