package services;

import entities.Entity;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.*;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MyBatis<T extends Entity> implements IDao<T>, AutoCloseable {
    private final SqlSession session;
    public final Class<T> clazz;

    public MyBatis(Class<T> clazz) throws IOException {
        Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
        SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(reader);
        this.session = sessionFactory.openSession();
        this.clazz = clazz;
    }

    @Override
    public void close() {
        if (this.session != null)
            this.session.close();
    }

    @Override
    public List<T> get(Map<String, Object> fieldValueFilters) {
        return this.session.selectList(clazz.getName() + ".get", fieldValueFilters);
    }

    @Override
    public int create(T t) {
        int rows = this.session.insert(clazz.getName() + ".create", t);
        this.session.commit();
        return rows;
    }

    @Override
    public int update(Map<String, Object> newValues, Map<String, Object> fieldValueFilters) {
        Map<String, Map<String, Object>> paramsMap = new HashMap<>();
        paramsMap.put("values", newValues);
        paramsMap.put("filters", fieldValueFilters);

        int rows = this.session.update(clazz.getName() + ".update", paramsMap);
        this.session.commit();
        return rows;
    }

    @Override
    public int delete(Map<String, Object> fieldValueFilters) {
        int rows = this.session.delete(clazz.getName() + ".delete", fieldValueFilters);
        this.session.commit();
        return rows;
    }
}
