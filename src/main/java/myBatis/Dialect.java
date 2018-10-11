package com.org.zyy.mybatis;

public interface Dialect {

	String getLimitString(String querySql, int pateNum, int pageSize);
	
}
