package andre.chamis.healthproject.domain.workspace.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * Represents a workspace in the system.
 */
@Data
@Entity
@NoArgsConstructor
@Table(name = "workspaces")
public class Workspace {
    /**
     * The unique identifier of the workspace.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workspace_id")
    private Long workspaceId;

    /**
     * The ID of the owner of the workspace.
     */
    @Column(name = "owner_id")
    private Long ownerId;

    /**
     * The ID of the owner of the workspace.
     */
    @Column(name = "patient_id")
    private Long patientId;

    /**
     * The name of the workspace.
     */
    @Column(name = "workspace_name")
    private String workspaceName;

    /**
     * The creation date of the workspace.
     */
    @Column(name = "create_dt")
    private Date createDt;

    /**
     * The update date of the workspace.
     */
    @Column(name = "update_dt")
    private LocalDateTime updateDt;

    /**
     * Indicates whether the workspace is active or not.
     */
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

