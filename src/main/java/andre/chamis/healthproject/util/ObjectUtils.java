package andre.chamis.healthproject.util;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

@Slf4j
public class ObjectUtils {
    public static boolean areAnyPropertiesNull(Object object) {
        if (object == null) {
            return true;
        }

        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            try {
                if (field.get(object) == null) {
                    return true;
                }
            } catch (IllegalAccessException e) {
                log.error("Error verifying if field [{}] of object [{}] is null", field.getName(), object.getClass(), e);
            }
        }

        return false;
    }
}
