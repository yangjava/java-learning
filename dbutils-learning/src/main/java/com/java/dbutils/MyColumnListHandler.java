package com.java.dbutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.ResultSetHandler;

class MyColumnListHandler implements ResultSetHandler<Object> {

    private String columnName;

    public MyColumnListHandler(String columnName) {
        this.columnName = columnName;
    }

    @Override
    public Object handle(ResultSet rs) throws SQLException {
        List<Object> list = new ArrayList<Object>();
        while (rs.next()) {
            list.add(rs.getObject(columnName));
        }
        return list;
    }

}
