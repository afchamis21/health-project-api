package andre.chamis.healthproject.domain.request;

import lombok.Getter;

@Getter
public enum SortingMode {
    ASC("ASC"), DESC("DESC");

    private final String value;

    SortingMode(String value) {
        this.value = value;
    }
}
