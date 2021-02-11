package com.microsoft.azure.cosmos.sample.controller;

import java.util.List;
import java.util.UUID;

import lombok.NonNull;

import com.microsoft.azure.cosmos.sample.dao.TodoDao;
import com.microsoft.azure.cosmos.sample.dao.TodoDaoFactory;
import com.microsoft.azure.cosmos.sample.model.TodoItem;

public class TodoItemController {
    public static TodoItemController getInstance() {
        if (todoItemController == null) {
            todoItemController = new TodoItemController(TodoDaoFactory.getDao());
        }
        return todoItemController;
    }

    private static TodoItemController todoItemController;

    private final TodoDao todoDao;

    TodoItemController(TodoDao todoDao) {
        this.todoDao = todoDao;
    }

    public TodoItem createTodoItem(@NonNull String name,
            @NonNull String category, boolean isComplete) {
        TodoItem todoItem = new TodoItem();
        
        todoItem.setName(name);
        todoItem.setCategory(category);
        todoItem.setComplete(isComplete);
        todoItem.setId(UUID.randomUUID().toString());

        return todoDao.createTodoItem(todoItem);
    }

    public boolean deleteTodoItem(@NonNull String id) {
        return todoDao.deleteTodoItem(id);
    }

    public TodoItem getTodoItemById(@NonNull String id) {
        return todoDao.readTodoItem(id);
    }

    public List<TodoItem> getTodoItems() {
        return todoDao.readTodoItems();
    }

    public TodoItem updateTodoItem(@NonNull String id, boolean isComplete) {
        return todoDao.updateTodoItem(id, isComplete);
    }
}
