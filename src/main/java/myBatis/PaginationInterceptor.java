package com.org.zyy.mybatis;

import java.sql.Connection;
import java.util.Properties;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;

import com.org.zyy.mybatis.dialect.SQLDialect;

@Intercepts({@Signature(type=StatementHandler.class, method="prepare", args={Connection.class})})
public class PaginationInterceptor implements Interceptor{

	private static final ObjectFactory default_object_factory = new DefaultObjectFactory();
	private static final ObjectWrapperFactory default_object_wrapper_factory = new DefaultObjectWrapperFactory();
	
	
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		
		StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
		
		BoundSql boundSql = statementHandler.getBoundSql();
		
		MetaObject metaObject = MetaObject.forObject(statementHandler, default_object_factory, default_object_wrapper_factory);
		
		RowBounds rowBounds = (RowBounds) metaObject.getValue("delegate.rowBounds");
		
		if(rowBounds.getLimit() > 0 && rowBounds.getLimit() < RowBounds.NO_ROW_LIMIT){
			
			String dialectString = ((Configuration)metaObject.getValue("delegate.configuration")).getVariables().getProperty("dialect");
			
			if(dialectString == null){
				throw new IllegalArgumentException("the value of the dialect property in configuration.xml is not defined ");
			}
			
			SQLDialect sqlDialect = new SQLDialect();
			String sql = sqlDialect.getLimitString(boundSql.getSql(), rowBounds.getOffset(), rowBounds.getLimit());
		
			metaObject.setValue("delegate.boundSql.sql", sql);
			metaObject.setValue("delegate.rowBounds.offset", RowBounds.NO_ROW_OFFSET);
			metaObject.setValue("delegate.rowBounds.limit", RowBounds.NO_ROW_LIMIT);
		
		}
		return invocation.proceed();
	}

	@Override
	public Object plugin(Object targ) {
		return Plugin.wrap(targ, this);
	}

	@Override
	public void setProperties(Properties arg0) {
		return ;
	}

}
