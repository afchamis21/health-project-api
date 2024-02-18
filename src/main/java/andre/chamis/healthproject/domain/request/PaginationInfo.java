package andre.chamis.healthproject.domain.request;


import lombok.Data;

@Data
public class PaginationInfo {
    private int page = 0;
    private int size = 10;
}
