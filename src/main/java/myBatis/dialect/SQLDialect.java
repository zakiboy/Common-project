package com.org.zyy.mybatis.dialect;

import com.org.zyy.mybatis.Dialect;

public class SQLDialect implements Dialect{

	@Override
	public String getLimitString(String querySql, int pateNum, int pageSize) {
		return getlimisStringOrale(querySql, pateNum, pageSize);
	}
	
	private String getlimisStringOrale(String sql, int offset, int limit){
		boolean isforUpate = false;
		if(sql.toLowerCase().endsWith(" for update")){
			sql = sql.substring(0, sql.length() - 11);
			isforUpate = true;
		}
		
		StringBuffer pagingSelect =  new StringBuffer(sql.length() + 100);
		if(offset > 0){
			pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
		}else{
			pagingSelect.append("select * from (");
		}
		pagingSelect.append(sql);
		
		if(offset > 0){
			pagingSelect.append(") row_ ) where rownum_ <= ").append(limit*offset).append(" and rownum_ >").append((offset-1)*limit);
		}else{
			pagingSelect.append(") where rownum <= ").append(limit);
		}
		
		if (isforUpate) {
			pagingSelect.append(" for update");
		}
		return pagingSelect.toString().toUpperCase();
	}

}
