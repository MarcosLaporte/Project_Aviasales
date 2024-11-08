package services;

import entities.Entity;
import org.apache.logging.log4j.Level;
import utils.EntityReflection;
import utils.InputService;
import utils.LoggerService;
import utils.ReflectionService;

import java.util.List;
import java.util.Map;

public class CrudMenu {
    public static <T extends Entity> void handleCrudOperation(Menu operation) {
        @SuppressWarnings("unchecked")
        Class<T> entityClass = (Class<T>) EntityReflection.chooseEntity();
        if (entityClass == null) {
            LoggerService.println("Going back...");
            return;
        }

        try (MyBatis<T> dao = new MyBatis<>(entityClass)) {
            EntityReflection<T> rs = new EntityReflection<>(entityClass);
            switch (operation) {
                case GET -> get(dao, rs);
                case CREATE -> create(dao, rs);
                case UPDATE -> update(dao, rs);
                case DELETE -> delete(dao, rs);
                default -> throw new Exception(operation + " is not a CRUD operation.");
            }
        } catch (Exception e) {
            LoggerService.log(Level.ERROR, e.getMessage() != null ? e.getMessage() : e.getClass().toString());
        }
    }

    private static <T extends Entity> void get(MyBatis<T> dao, EntityReflection<T> rs) {
        LoggerService.print("\nFill with fields to filter by.");
        Map<String, Object> columnFilters = rs.readConditionValues();
        List<T> values = dao.get(columnFilters);

        if (!columnFilters.isEmpty()) {
            LoggerService.print("Rows found with values");
            for (Map.Entry<String, Object> entry : columnFilters.entrySet())
                LoggerService.print(" [" + entry.getKey() + " = " + entry.getValue() + ']');
            LoggerService.println(": ");
        }

        LoggerService.println(ReflectionService.toTable(values));
    }

    private static <T extends Entity> void create(MyBatis<T> dao, EntityReflection<T> rs) throws Exception {
        if (dao.create(rs.readNewInstance(false)) > 0)
            LoggerService.println(dao.clazz.getSimpleName() + " created!");
        else
            LoggerService.println("No " + dao.clazz.getSimpleName() + " was created.");
    }

    private static <T extends Entity> void update(MyBatis<T> dao, EntityReflection<T> rs) {
        LoggerService.print("\nEnter the new values.");
        Map<String, Object> newValues = rs.readNewValues(false);

        if (newValues.isEmpty()) {
            LoggerService.consoleLog(Level.WARN, "No new values entered. Going back.");
            return;
        }

        LoggerService.print("\nEnter the conditions to follow.");
        Map<String, Object> columnFilters = rs.readConditionValues();

        int rowsAffected = dao.update(newValues, columnFilters);
        LoggerService.println(String.format("%d %s updated.", rowsAffected, dao.clazz.getSimpleName()));
    }

    private static <T extends Entity> void delete(MyBatis<T> dao, EntityReflection<T> rs) {
        LoggerService.print("\nEnter the conditions to follow.");
        Map<String, Object> columnFilters = rs.readConditionValues();

        if (columnFilters.isEmpty()) {
            LoggerService.consoleLog(Level.WARN, "No filters selected. This will delete the entire table.");
            char confirmContinue = InputService.readCharInValues("Continue? Y/N: ", "ERROR. Input Y or N: ", new char[]{'Y', 'N'});
            if (confirmContinue == 'N') {
                LoggerService.consoleLog(Level.INFO, "Operation cancelled.");
                return;
            }
        }

        int rowsAffected = dao.delete(columnFilters);
        LoggerService.println(String.format("%d %s deleted.", rowsAffected, dao.clazz.getSimpleName()));
    }

}