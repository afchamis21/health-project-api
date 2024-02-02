package andre.chamis.healthproject.domain.workspace.member.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@Table(name = "workspace_user")
public class WorkspaceMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workspace_user_id")
    private Long workspaceUserId;

    @Column(name = "workspace_id")
    private Long workspaceId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "create_dt")
    private Date createDt;

    @Column(name = "is_active")
    private boolean active;

    @Override
    public String toString() {
        return "WorkspaceMember{" +
                "workspaceUserId=" + workspaceUserId +
                ", workspaceId=" + workspaceId +
                ", userId=" + userId +
                ", createDt=" + createDt +
                ", active=" + active +
                '}';
    }
}
