package utils;

import entities.Entity;
import entities.annotations.Column;
import entities.annotations.Range;
import entities.annotations.Size;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.*;

import static utils.ReflectionService.ClassExclusionPredicate.ABSTRACT;

public class EntityReflection<T extends Entity> {

    private final ReflectionService<T> rs;
    public final Class<T> clazz;

    public final List<Field> COLUMN_FIELDS;
    public final List<Field> COLUMN_FIELDS_NOT_AI;

    public EntityReflection(Class<T> clazz) {
        this.rs = new ReflectionService<>(clazz);
        this.clazz = clazz;

        this.COLUMN_FIELDS = Collections.unmodifiableList(this.rs.getFieldsByAnnotation(Column.class));
        this.COLUMN_FIELDS_NOT_AI = COLUMN_FIELDS.stream()
                .filter(field -> !field.getAnnotation(Column.class).autoIncrement())
                .toList();
    }

    public static Class<? extends Entity> chooseEntity() {
        List<Class<? extends Entity>> classes = ReflectionService.getSubclassesOf(Entity.class, "entities", ABSTRACT)
                .stream().filter(Entity.class::isAssignableFrom).toList();

        int classIndex = InputService.selectIndexFromList("Select an entity: ", classes.stream().map(Class::getSimpleName).toList(), "CANCEL");

        return classIndex == -1 ? null : classes.get(classIndex);
    }

    public T readNewInstance(boolean readAutoIncrementFields) throws Exception {
        List<Field> fields = readAutoIncrementFields ? COLUMN_FIELDS : COLUMN_FIELDS_NOT_AI;
        Object[] paramValues = new Object[fields.size()];

        for (int i = 0; i < fields.size(); i++) {
            Field currField = fields.get(i);

            Range rangeAnn = currField.getAnnotation(Range.class);
            Size sizeAnn = currField.getAnnotation(Size.class);
            paramValues[i] = readValue(currField.getType(), currField.getName(), rangeAnn, sizeAnn);
        }

        return rs.createInstance(paramValues);
    }

    private Map<String, Object> readValues(Field[] fields) {
        Map<String, Object> valuesMap = new HashMap<>();

        do {
            int fieldIndex = InputService.selectIndexFromList(
                    "Select a field: ",
                    Arrays.stream(fields).map(f -> String.format("[%s] %s", f.getType().getSimpleName(), f.getName())).toList(),
                    "FINISH"
            );

            if (fieldIndex == -1)
                break;

            Field currField = fields[fieldIndex];
            Range rangeAnn = currField.getAnnotation(Range.class);
            Size sizeAnn = currField.getAnnotation(Size.class);
            valuesMap.put(
                    currField.getAnnotation(Column.class).name(),
                    readValue(currField.getType(), currField.getName(), rangeAnn, sizeAnn)
            );

        } while (true);

        return valuesMap;
    }

    public Map<String, Object> readNewValues(boolean readAutoIncrementFields) {
        List<Field> fields = readAutoIncrementFields ? COLUMN_FIELDS : COLUMN_FIELDS_NOT_AI;
        return readValues(fields.toArray(Field[]::new));
    }

    public Map<String, Object> readConditionValues() {
        return readValues(COLUMN_FIELDS.toArray(Field[]::new));
    }

    private Object readValue(Class<?> fieldType, String fieldName, Range rangeAnn, Size sizeAnn) {
        String inputMsg = String.format("Enter [%s] for %s: ", fieldType.getSimpleName(), fieldName);

        Class<?> wrapperType = fieldType.isPrimitive() ? getWrapperClass(fieldType) : fieldType;
        if (Number.class.isAssignableFrom(wrapperType)) {
            double min = rangeAnn != null ? rangeAnn.min() : Double.MIN_VALUE;
            double max = rangeAnn != null ? rangeAnn.max() : Double.MAX_VALUE;
            String errorMsg = String.format("Invalid value for %s. Try again (%.2f - %.2f): ", fieldName, min, max);

            return InputService.readNumber(inputMsg, errorMsg, min, max, wrapperType.asSubclass(Number.class));
        } else if (wrapperType == String.class) {
            int min = sizeAnn != null ? sizeAnn.min() : 0;
            int max = sizeAnn != null ? sizeAnn.max() : Integer.MAX_VALUE;

            return InputService.readString(inputMsg, min, max);
        } else if (wrapperType == LocalDate.class) {
            return InputService.readValidDate();
        }
        return null;
    }

    private Class<?> getWrapperClass(Class<?> fieldType) {
        return switch (fieldType.getName()) {
            case "int" -> Integer.class;
            case "byte" -> Byte.class;
            case "short" -> Short.class;
            case "long" -> Long.class;
            case "float" -> Float.class;
            case "double" -> Double.class;
            case "char" -> Character.class;
            case "boolean" -> Boolean.class;
            default -> fieldType;
        };
    }

}
