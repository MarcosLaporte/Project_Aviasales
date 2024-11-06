package utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public record ReflectionService<T>(Class<T> clazz) {
    @SuppressWarnings("unused")
    public enum ClassExclusionPredicate {
        INTERFACE(clazz -> !clazz.isInterface()),
        EXCEPTION(clazz -> !Exception.class.isAssignableFrom(clazz)),
        ABSTRACT(clazz -> !Modifier.isAbstract(clazz.getModifiers())),
        PRIVATE(clazz -> !Modifier.isPrivate(clazz.getModifiers())),
        STATIC(clazz -> !Modifier.isStatic(clazz.getModifiers())),
        ANNOTATION(clazz -> !clazz.isAnnotation()),
        ENUM(clazz -> !clazz.isEnum());

        private final Predicate<Class<?>> predicate;

        ClassExclusionPredicate(Predicate<Class<?>> predicate) {
            this.predicate = predicate;
        }
    }

    /**
     * Retrieves all classes within the specified package, applying optional exclusion predicates.
     *
     * @param packageName              the name of the package to search for classes
     * @param classExclusionPredicates optional predicates for excluding certain classes
     * @return a list of classes within the package that pass the specified predicates
     */
    public static List<Class<?>> getClassesInPackage(String packageName, ClassExclusionPredicate... classExclusionPredicates) {
        List<Class<?>> classes = new ArrayList<>();
        String path = packageName.replace('.', '/');
        File directory = new File("src/main/java/" + path);

        if (directory.exists()) {
            addClassesFromDirectory(directory, packageName, classes, classExclusionPredicates);
        }

        return classes;
    }

    /**
     * Retrieves all subclasses of a specified superclass within the specified package, applying optional exclusion predicates.
     *
     * @param <T>                      the superclass type parameter
     * @param superClass               the superclass to find subclasses of
     * @param packageName              the name of the package to search for subclasses
     * @param classExclusionPredicates optional predicates for excluding certain classes
     * @return a list of subclasses of the specified superclass that pass the specified predicates
     */
    public static <T> List<Class<? extends T>> getSubclassesOf(Class<T> superClass, String packageName, ClassExclusionPredicate... classExclusionPredicates) {
        List<Class<?>> allClasses = getClassesInPackage(packageName, classExclusionPredicates);
        List<Class<? extends T>> filteredClasses = new ArrayList<>();

        for (Class<?> clazz : allClasses) {
            if (superClass.isAssignableFrom(clazz) && !superClass.equals(clazz)) {
                filteredClasses.add(clazz.asSubclass(superClass));
            }
        }

        return filteredClasses;
    }

    /**
     * Recursively adds classes from a directory to the specified list, applying optional exclusion predicates.
     *
     * @param directory                the directory containing classes
     * @param packageName              the package name for the directory
     * @param classes                  the list to which classes are added
     * @param classExclusionPredicates optional predicates for excluding certain classes
     */
    private static void addClassesFromDirectory(File directory, String packageName, List<Class<?>> classes, ClassExclusionPredicate... classExclusionPredicates) {
        File[] files = directory.listFiles();
        if (files != null) {
            main:
            for (File file : files) {
                if (file.isDirectory()) {
                    String subPackageName = packageName + "." + file.getName();
                    addClassesFromDirectory(file, subPackageName, classes, classExclusionPredicates);
                } else if (file.getName().endsWith(".java")) {
                    String className = file.getName().substring(0, file.getName().length() - 5);
                    Class<?> clazz;
                    try {
                        clazz = Class.forName(packageName + "." + className);
                    } catch (ClassNotFoundException e) {
                        LoggerService.log(Level.WARN, String.format("%s.%s not found.", packageName, className));
                        continue;
                    }

                    for (ClassExclusionPredicate pred : classExclusionPredicates) {
                        if (!pred.predicate.test(clazz)) {
//                            LoggerService.println(clazz.getSimpleName() + " didn't pass predicate: " + pred);
                            continue main;
                        }
                    }

                    classes.add(clazz);
                }
            }
        }
    }

    /**
     * Creates a new instance of the class with the specified arguments, finding a matching constructor by parameter types.
     *
     * @param args the arguments to be passed to the constructor
     * @return a new instance of the class
     * @throws RuntimeException      if instantiation fails
     * @throws NoSuchMethodException if no constructor matches the provided arguments
     */
    @SuppressWarnings("unchecked")
    public T createInstance(Object... args) throws RuntimeException, NoSuchMethodException {
        Constructor<?>[] constructors = this.clazz.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            Class<?>[] paramTypes = constructor.getParameterTypes();
            constructor.setAccessible(true);

            if (matchParameterTypes(paramTypes, args)) {
                try {
                    return (T) constructor.newInstance(args);
                } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException("Error while invoking constructor: " + e.getCause(), e);
                }
            }
        }

        throw new NoSuchMethodException("No constructor matched with given arguments in class " + this.clazz.getSimpleName());
    }

    /**
     * Checks if the specified arguments match the parameter types of the constructor.
     *
     * @param paramTypes the parameter types of the constructor
     * @param args       the arguments to be checked against the parameter types
     * @return true if the arguments match the parameter types, otherwise false
     */
    private boolean matchParameterTypes(Class<?>[] paramTypes, Object[] args) {
        if (paramTypes.length != args.length) return false;
        for (int i = 0; i < paramTypes.length; i++) {
            if (args[i] != null && !paramTypes[i].isAssignableFrom(args[i].getClass())) {
//                LoggerService.println(String.format("Arg%d (%s) does not match with parameter type (%s)", i+1, args[i].getClass(), paramTypes[i]));
                return false;
            }
        }
        return true;
    }

    /**
     * Retrieves a map of field names and their values for the given instance.
     *
     * @param instance the instance whose fields are to be retrieved
     * @return a map of field names and values
     * @throws RuntimeException if an error occurs while accessing fields
     */
    public Map<String, Object> getFieldsWithValue(T instance) throws RuntimeException {
        Map<String, Object> fieldMap = new HashMap<>();
        Field[] fields = this.clazz.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            try {
                field.setAccessible(true);
                fieldMap.put(fieldName, field.get(instance));
            } catch (IllegalAccessException _) {
                fieldMap.put(fieldName, null);
                LoggerService.log(Level.WARN, fieldName + " was inaccessible, value set to null.");
            }
        }

        return fieldMap;
    }

    /**
     * Retrieves a list of fields annotated with any of the specified annotations.
     *
     * @param annotations the annotations to filter fields by
     * @return a list of fields that have all specified annotations
     */
    @SafeVarargs
    public final List<Field> getFieldsByAnnotation(Class<? extends Annotation>... annotations) {
        List<Field> fields = new ArrayList<>();
        for (Field field : this.clazz.getDeclaredFields()) {
            if (Arrays.stream(annotations).allMatch(field::isAnnotationPresent)) {
                field.setAccessible(true);
                fields.add(field);
            }
        }

        return fields;
    }

    /**
     * Retrieves a list of fields based on inclusion and exclusion annotations.
     *
     * @param includeAnnotations the annotations that must be present on the field
     * @param excludeAnnotations the annotations that must not be present on the field
     * @return a list of fields matching the specified criteria
     */
    public List<Field> getFieldsByAnnotation(Class<? extends Annotation>[] includeAnnotations, Class<? extends Annotation>[] excludeAnnotations) {
        List<Field> fields = new ArrayList<>();
        for (Field field : this.clazz.getDeclaredFields()) {
            boolean includeMatch = (
                    includeAnnotations == null || includeAnnotations.length == 0 ||
                            Arrays.stream(includeAnnotations).allMatch(field::isAnnotationPresent)
            );
            boolean excludeMatch = (
                    excludeAnnotations == null || excludeAnnotations.length == 0 ||
                            Arrays.stream(excludeAnnotations).noneMatch(field::isAnnotationPresent)
            );

            if (includeMatch && excludeMatch) {
                field.setAccessible(true);
                fields.add(field);
            }
        }
        return fields;
    }

    /**
     * Generates a formatted table representation of the specified list of objects.
     *
     * @param <T>     the type of the objects in the list
     * @param objects the list of objects to display in table format
     * @return a string representation of the table
     */
    public static <T> String toTable(List<T> objects) {
        if (objects == null || objects.isEmpty()) return "";

        final Field[] fields = objects.getFirst().getClass().getDeclaredFields();
        Map<String, Integer> fieldSizeMap = calculateFieldSizes(fields, objects);

        StringBuilder table = new StringBuilder();
        int totalWidth = fieldSizeMap.values().stream().mapToInt(v -> v + 3).sum() - 1;
        String rowSeparator = "-".repeat(totalWidth);
        table.append('+').append(rowSeparator).append("+\n");

        String header = Arrays.stream(fields)
                .map(f -> {
                    String fieldName = f.getName();
                    int size = fieldSizeMap.get(fieldName);
                    return String.format(" %s ", StringUtils.center(fieldName, size));
                })
                .collect(Collectors.joining("|"));
        table.append('|').append(header).append("|\n");
        table.append('|').append(rowSeparator).append("|\n");

        for (T object : objects) {
            String row = Arrays.stream(fields)
                    .map(f -> {
                        String value;
                        try {
                            f.setAccessible(true);
                            value = f.get(object) == null ? "" : f.get(object).toString();
                        } catch (IllegalAccessException ignored) {
                            value = "#N/A";
                        }
                        return String.format(" %s ", StringUtils.rightPad(value, fieldSizeMap.get(f.getName())));
                    })
                    .collect(Collectors.joining("|"));
            table.append('|').append(row).append("|\n");
        }
        table.append('+').append(rowSeparator).append("+\n");

        return table.toString();
    }

    /**
     * Calculates the maximum field sizes for each field across a list of objects.
     *
     * @param <T>     the type of the objects
     * @param fields  the fields to calculate sizes for
     * @param objects the list of objects to calculate field sizes from
     * @return a map of field names to their maximum size
     */
    private static <T> Map<String, Integer> calculateFieldSizes(Field[] fields, List<T> objects) {
        Map<String, Integer> fieldSizeMap = Arrays.stream(fields)
                .collect(Collectors.toMap(Field::getName, f -> f.getName().length()));

        for (T object : objects) {
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    String fieldValueStr = field.get(object) == null ? "" : field.get(object).toString();
                    fieldSizeMap.merge(field.getName(), fieldValueStr.length(), Math::max);
                } catch (IllegalAccessException _) {
                    /*If field is inaccessible, fieldSizeMap entry won't be updated.*/
                }
            }
        }

        return fieldSizeMap;
    }

}
