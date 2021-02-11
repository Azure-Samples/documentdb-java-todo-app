package com.microsoft.azure.cosmos.sample.dao;

public class TodoDaoFactory {
	private static TodoDao myTodoDao = new DocDbDao();

	public static TodoDao getDao() {
		return myTodoDao;
	}
}
