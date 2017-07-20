package com.java.jdbc;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class MemoryFactory {
	
	private MemoryFactory() {

    }

    private static class SingletonHolder {
        public static final Memory MEMORY = new Memory(getDataSource());
        //public static final Memory MEMORY = new Memory(new SimpleDataSource());
    }

    public static Memory getInstance() {
        return SingletonHolder.MEMORY;
    }

    public static final DataSource getDataSource() {
       return new ComboPooledDataSource("mySource");
    }
    
}