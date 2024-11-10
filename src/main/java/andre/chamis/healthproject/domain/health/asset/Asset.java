package andre.chamis.healthproject.domain.health.asset;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Entity
@Table(name = "asset")
@IdClass(Asset.AssetId.class)
@AllArgsConstructor
@NoArgsConstructor
public class Asset {
    @Id
    private Long ownerId;

    @Id
    private String name;

    private String description;

    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class AssetId implements Serializable {
        private Long ownerId;
        private String name;
    }
}
