package andre.chamis.healthproject.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * An abstract class representing an in-memory cache.
 *
 * @param <KeyType>   The type of keys used in the cache.
 * @param <ValueType> The type of values stored in the cache.
 */
public abstract class InMemoryCache<KeyType, ValueType> {
    private final Function<ValueType, KeyType> keyExtractorFunction;

    /**
     * Constructs an InMemoryCache with a specified key extractor function.
     *
     * @param keyExtractorFunction The function to extract keys from values.
     */
    public InMemoryCache(Function<ValueType, KeyType> keyExtractorFunction) {
        this.keyExtractorFunction = keyExtractorFunction;
    }

    private final Map<KeyType, ValueType> cache = new HashMap<>();

    private KeyType getKey(ValueType value) {
        return keyExtractorFunction.apply(value);
    }

    /**
     * Retrieves the underlying cache map.
     *
     * @return The map containing stored key-value pairs.
     */
    protected synchronized Map<KeyType, ValueType> getCache() {
        return cache;
    }

    /**
     * Adds a key-value pair to the cache.
     *
     * @param value The value to store in the cache.
     */
    public synchronized void put(ValueType value) {
        cache.put(getKey(value), value);
    }

    /**
     * Checks if the cache contains a specific key.
     *
     * @param key The key to check for existence in the cache.
     * @return {@code true} if the key is found, otherwise {@code false}.
     */
    public synchronized boolean containsKey(KeyType key) {
        return cache.containsKey(key);
    }

    /**
     * Removes a key-value pair from the cache.
     *
     * @param key The key of the value to remove from the cache.
     */
    public synchronized void remove(KeyType key) {
        cache.remove(key);
    }

    /**
     * Retrieves a value from the cache based on the provided key.
     *
     * @param key The key of the value to retrieve.
     * @return An {@link Optional} containing the value if found, or empty if not found.
     */
    public synchronized Optional<ValueType> get(KeyType key) {
        ValueType value = cache.get(key);
        return Optional.ofNullable(value);
    }

    /**
     * Initializes the cache with a list of values and a key extractor function.
     * <p>
     * This method clears the existing cache and populates it with the provided values.
     *
     * @param values The list of values to populate the cache with.
     */
    public synchronized void initializeCache(List<ValueType> values) {
        cache.clear();
        addMultiple(values);
    }

    /**
     * Adds multiple values to the cache using a key extractor function.
     *
     * @param values The list of values to add to the cache.
     */
    public synchronized void addMultiple(List<ValueType> values) {
        for (ValueType value : values) {
            put(value);
        }
    }

    /**
     * Deletes multiple values from the cache using a key extractor function.
     *
     * @param values The list of values to delete from the cache.
     */
    public synchronized void deleteFromList(List<ValueType> values) {
        for (ValueType value : values) {
            remove(getKey(value));
        }
    }

    /**
     * Retrieves the size of the cache.
     *
     * @return The number of key-value pairs in the cache.
     */
    public synchronized int getSize() {
        return cache.size();
    }
}
