package andre.chamis.healthproject.domain.workspace.dto;

import andre.chamis.healthproject.domain.workspace.model.Workspace;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.SqlResultSetMapping;
import org.hibernate.annotations.NamedNativeQuery;

import java.util.Date;

/**
 * Represents a data transfer object (DTO) for retrieving workspace information.
 */
//@NamedNativeQuery(name = "GetWorkspaceDtoQuery",resultSetMapping = "GetWorkspaceDtoResult", query = """
//                SELECT w.workspace_id, CONCAT(p.name, COALESCE(' ' || p.surname, '')) AS name, w.owner_id, w.patient_id, w.is_active, w.create_dt
//                FROM workspaces w
//                    JOIN workspace_user wu ON w.workspace_id = wu.workspace_id
//                    JOIN patients p ON w.patient_id = p.patient_id
//                WHERE w.owner_id = wu.user_id OR (wu.user_id = :userId AND wu.is_active = true)
//                  AND w.create_dt <= :now
//""")
//@SqlResultSetMapping(
//        name="GetWorkspaceDtoResult",
//        classes = @ConstructorResult(
//                targetClass = GetWorkspaceDTO.class,
//                columns = {
//                        @ColumnResult(name="workspace_id", type = Long.class),
//                        @ColumnResult(name="name", type = String.class),
//                        @ColumnResult(name="owner_id", type = Long.class),
//                        @ColumnResult(name="patient_id", type = Long.class),
//                        @ColumnResult(name="is_active", type = Boolean.class),
//                        @ColumnResult(name="create_dt", type = Date.class),
//                }
//        )
//
//)
public record GetWorkspaceDTO(Long workspaceId, String name, Long ownerId, Long patientId, boolean isActive,
                              Date createDt) {

    /**
     * Constructs a GetWorkspaceDTO object from a Workspace entity.
     *
     * @param workspace The Workspace entity to extract data from.
     * @param name      The name of the Workspace
     */
    public GetWorkspaceDTO(Workspace workspace, String name) {
        this(
                workspace.getWorkspaceId(),
                name,
                workspace.getOwnerId(),
                workspace.getPatientId(),
                workspace.isActive(),
                workspace.getCreateDt()
        );
    }
}
