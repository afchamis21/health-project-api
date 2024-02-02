package andre.chamis.healthproject.domain.workspace.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@Table(name = "workspaces")
public class Workspace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workspace_id")
    private Long workspaceId;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "workspace_name")
    private String workspaceName;

    @Column(name = "create_dt")
    private Date createDt;

    @Column(name = "update_dt")
    private Date updateDt;

    @Column(name = "is_active")
    private boolean active;

    @Override
    public String toString() {
        return "Workspace{" +
                "workspaceId=" + workspaceId +
                ", ownerId=" + ownerId +
                ", workspaceName='" + workspaceName + '\'' +
                ", createDt=" + createDt +
                ", updateDt=" + updateDt +
                ", active=" + active +
                '}';
    }
}
