package andre.chamis.healthproject.context;

import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A utility class for managing contextual information within service operations.
 */
@Data
@Slf4j
public class ServiceContext {
    public static String EXECUTION_ID_KEY = "execution-id";
    private static ThreadLocal<ServiceContext> threadLocal = new ThreadLocal<>();
    private String executionId;

    @Getter
    private List<String> metadataMessages = new ArrayList<>();
    private List<Exception> exceptions = new ArrayList<>();
    private Instant startTime;
    private Instant endTime;
    private Long sessionId;

    /**
     * Retrieves the current ServiceContext associated with the current thread. If no context exists, a new one is created.
     *
     * @return The current ServiceContext.
     */
    public synchronized static ServiceContext getContext() {
        return getContext(null);
    }

    /**
     * Retrieves the current ServiceContext associated with the current thread, optionally specifying an execution ID.
     * If no context exists, a new one is created.
     *
     * @param executionId An optional execution ID to associate with the context.
     * @return The current ServiceContext.
     */
    public synchronized static ServiceContext getContext(String executionId) {
        ServiceContext context = threadLocal.get();
        if (context == null){
            return createNewContext(executionId);
        }

        return context;
    }

    /**
     * Creates and associates a new ServiceContext with the current thread, optionally using the provided execution ID.
     * If no execution ID is provided or is blank, a new UUID-based execution ID is generated.
     *
     * @param executionId An optional execution ID to associate with the context.
     * @return The newly created ServiceContext.
     */
    private static ServiceContext createNewContext(String executionId) {
        ServiceContext context = new ServiceContext(executionId);
        threadLocal.set(context);

        return context;
    }

    /**
     * Constructs a new ServiceContext with an execution ID. If the provided execution ID is null or blank,
     * a new UUID-based execution ID is generated. The constructed context is associated with the current thread,
     * and the execution ID is added to the Mapped Diagnostic Context (MDC) for logging purposes.
     *
     * @param executionId An optional execution ID to associate with the context.
     */
    private ServiceContext(String executionId) {
        executionId = executionId == null || executionId.isBlank()
                ? UUID.randomUUID().toString()
                : executionId;

        this.executionId = executionId;

        MDC.put(EXECUTION_ID_KEY, executionId);
    }

    /**
     * Clears the context associated with the current thread.
     */
    public static void clearContext(){
        threadLocal.remove();
        MDC.remove(EXECUTION_ID_KEY);
    }

    /**
     * Adds an exception to the list of exceptions in the current context.
     *
     * @param ex The exception to add.
     */
    public synchronized static void addException(Exception ex) {
        getContext().exceptions.add(ex);
    }

    /**
     * Adds a message to the metadata messages associated with the context.
     *
     * @param message The message to add.
     */
    public synchronized static void addMessage(String message){
        getContext().metadataMessages.add(message);
    }
}
