package andre.chamis.healthproject.infra.request.request;

import lombok.Getter;

/**
 * Represents the sorting mode for pagination.
 */
@Getter
public enum SortingMode {
    /**
     * Ascending sorting mode.
     */
    ASC("ASC"),

    /**
     * Descending sorting mode.
     */
    DESC("DESC");

    /**
     * The string value representing the sorting mode.
     */
    private final String value;

    /**
     * Constructs a SortingMode enum with the specified value.
     *
     * @param value The string value representing the sorting mode.
     */
    SortingMode(String value) {
        this.value = value;
    }
}
