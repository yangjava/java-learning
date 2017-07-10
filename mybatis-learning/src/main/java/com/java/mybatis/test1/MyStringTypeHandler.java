package com.java.mybatis.test1;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.apache.log4j.Logger;


@MappedJdbcTypes(value={JdbcType.VARCHAR})
@MappedTypes(value={String.class})
public class MyStringTypeHandler extends BaseTypeHandler<String>{
      
	   private static Logger LOGGER=Logger.getLogger(MyStringTypeHandler.class);
	  @Override
	  public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType)
	      throws SQLException {
		  LOGGER.info("使用我得自定义MyStringTypeHandler  setNonNullParameter");
	    ps.setString(i, parameter);
	  }

	  @Override
	  public String getNullableResult(ResultSet rs, String columnName)
	      throws SQLException {
		  LOGGER.info("使用我得自定义MyStringTypeHandler  getNullableResult 1");
	    return rs.getString(columnName);
	  }

	  @Override
	  public String getNullableResult(ResultSet rs, int columnIndex)
	      throws SQLException {
		  LOGGER.info("使用我得自定义MyStringTypeHandler  getNullableResult 2");
	    return rs.getString(columnIndex);
	  }

	  @Override
	  public String getNullableResult(CallableStatement cs, int columnIndex)
	      throws SQLException {
		  LOGGER.info("使用我得自定义MyStringTypeHandler  getNullableResult 3" );
	    return cs.getString(columnIndex);
	  }

}
