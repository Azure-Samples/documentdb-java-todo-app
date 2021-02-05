package com.microsoft.azure.cosmos.sample.dao;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.CosmosException;
import com.azure.cosmos.implementation.Utils;
import com.azure.cosmos.models.CosmosContainerProperties;
import com.azure.cosmos.models.CosmosContainerResponse;
import com.azure.cosmos.models.CosmosDatabaseResponse;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.FeedResponse;
import com.azure.cosmos.models.PartitionKey;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.microsoft.azure.cosmos.sample.model.TodoItem;

import java.util.ArrayList;
import java.util.List;

public class DocDbDao implements TodoDao {
    // The name of our database.
    private static final String DATABASE_ID = "TestDB";

    // The name of our collection.
    private static final String CONTAINER_ID = "TestCollection";

    // We'll use Gson for POJO <=> JSON serialization for this example.
    private static Gson gson = new Gson();

    // The Cosmos DB Client
    private static CosmosClient cosmosClient = CosmosClientFactory
        .getCosmosClient();

    // The Cosmos DB database
    private static CosmosDatabase cosmosDatabase = null;

    // The Cosmos DB container
    private static CosmosContainer cosmosContainer = null;

    // For POJO/JsonNode interconversion
    private static final ObjectMapper OBJECT_MAPPER = Utils.getSimpleObjectMapper();

    @Override
    public TodoItem createTodoItem(TodoItem todoItem) {
        // Serialize the TodoItem as a JSON Document.

        JsonNode todoItemJson = OBJECT_MAPPER.valueToTree(todoItem);

        ((ObjectNode) todoItemJson).put("entityType", "todoItem");

        try {
            // Persist the document using the DocumentClient.
            todoItemJson =
                getContainerCreateResourcesIfNotExist()
                    .createItem(todoItemJson)
                    .getItem();
        } catch (CosmosException e) {
            System.out.println("Error creating TODO item.\n");
            e.printStackTrace();
            return null;
        }


        try {

            return OBJECT_MAPPER.treeToValue(todoItemJson, TodoItem.class);
            //return todoItem;
        } catch (Exception e) {
            System.out.println("Error deserializing created TODO item.\n");
            e.printStackTrace();

            return null;
        }

    }

    @Override
    public TodoItem readTodoItem(String id) {
        // Retrieve the document by id using our helper method.
        JsonNode todoItemJson = getDocumentById(id);

        if (todoItemJson != null) {
            // De-serialize the document in to a TodoItem.
            try {
                return OBJECT_MAPPER.treeToValue(todoItemJson, TodoItem.class);
            } catch (JsonProcessingException e) {
                System.out.println("Error deserializing read TODO item.\n");
                e.printStackTrace();

                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public List<TodoItem> readTodoItems() {

        List<TodoItem> todoItems = new ArrayList<TodoItem>();

        String sql = "SELECT * FROM root r WHERE r.entityType = 'todoItem'";
        int maxItemCount = 1000;
        int maxDegreeOfParallelism = 1000;
        int maxBufferedItemCount = 100;

        CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();
        options.setMaxBufferedItemCount(maxBufferedItemCount);
        options.setMaxDegreeOfParallelism(maxDegreeOfParallelism);
        options.setQueryMetricsEnabled(false);

        int error_count = 0;
        int error_limit = 10;

        String continuationToken = null;
        do {

            for (FeedResponse<JsonNode> pageResponse :
                getContainerCreateResourcesIfNotExist()
                    .queryItems(sql, options, JsonNode.class)
                    .iterableByPage(continuationToken, maxItemCount)) {

                continuationToken = pageResponse.getContinuationToken();

                for (JsonNode item : pageResponse.getElements()) {

                    try {
                        todoItems.add(OBJECT_MAPPER.treeToValue(item, TodoItem.class));
                    } catch (JsonProcessingException e) {
                        if (error_count < error_limit) {
                            error_count++;
                            if (error_count >= error_limit) {
                                System.out.println("\n...reached max error count.\n");
                            } else {
                                System.out.println("Error deserializing TODO item JsonNode. " +
                                    "This item will not be returned.");
                                e.printStackTrace();
                            }
                        }
                    }

                }
            }

        } while (continuationToken != null);

        return todoItems;
    }

    @Override
    public TodoItem updateTodoItem(String id, boolean isComplete) {
        // Retrieve the document from the database
        JsonNode todoItemJson = getDocumentById(id);

        // You can update the document as a JSON document directly.
        // For more complex operations - you could de-serialize the document in
        // to a POJO, update the POJO, and then re-serialize the POJO back in to
        // a document.
        ((ObjectNode) todoItemJson).put("complete", isComplete);

        try {
            // Persist/replace the updated document.
            todoItemJson =
                getContainerCreateResourcesIfNotExist()
                    .replaceItem(todoItemJson, id, new PartitionKey(id), new CosmosItemRequestOptions())
                    .getItem();
        } catch (CosmosException e) {
            System.out.println("Error updating TODO item.\n");
            e.printStackTrace();
            return null;
        }

        // De-serialize the document in to a TodoItem.
        try {
            return OBJECT_MAPPER.treeToValue(todoItemJson, TodoItem.class);
        } catch (JsonProcessingException e) {
            System.out.println("Error deserializing updated item.\n");
            e.printStackTrace();

            return null;
        }
    }

    @Override
    public boolean deleteTodoItem(String id) {
        // CosmosDB refers to documents by self link rather than id.

        // Query for the document to retrieve the self link.
        JsonNode todoItemJson = getDocumentById(id);

        try {
            // Delete the document by self link.
            getContainerCreateResourcesIfNotExist()
                .deleteItem(id, new PartitionKey(id), new CosmosItemRequestOptions());
        } catch (CosmosException e) {
            System.out.println("Error deleting TODO item.\n");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /*
    
    private CosmosDatabase getTodoDatabase() {
        if (databaseCache == null) {
            // Get the database if it exists
            List<CosmosDatabase> databaseList = cosmosClient
                    .queryDatabases(
                            "SELECT * FROM root r WHERE r.id='" + DATABASE_ID
                                    + "'", null).getQueryIterable().toList();

            if (databaseList.size() > 0) {
                // Cache the database object so we won't have to query for it
                // later to retrieve the selfLink.
                databaseCache = databaseList.get(0);
            } else {
                // Create the database if it doesn't exist.
                try {
                    CosmosDatabase databaseDefinition = new CosmosDatabase();
                    databaseDefinition.setId(DATABASE_ID);

                    databaseCache = cosmosClient.createDatabase(
                            databaseDefinition, null).getResource();
                } catch (CosmosException e) {
                    // TODO: Something has gone terribly wrong - the app wasn't
                    // able to query or create the collection.
                    // Verify your connection, endpoint, and key.
                    e.printStackTrace();
                }
            }
        }

        return databaseCache;
    }

	*/

    private CosmosContainer getContainerCreateResourcesIfNotExist() {

        try {

            if (cosmosDatabase == null) {
                CosmosDatabaseResponse cosmosDatabaseResponse = cosmosClient.createDatabaseIfNotExists(DATABASE_ID);
                cosmosDatabase = cosmosClient.getDatabase(cosmosDatabaseResponse.getProperties().getId());
            }

        } catch (CosmosException e) {
            // TODO: Something has gone terribly wrong - the app wasn't
            // able to query or create the collection.
            // Verify your connection, endpoint, and key.
            System.out.println("Something has gone terribly wrong - " +
                "the app wasn't able to create the Database.\n");
            e.printStackTrace();
        }

        try {

            if (cosmosContainer == null) {
                CosmosContainerProperties properties = new CosmosContainerProperties(CONTAINER_ID, "/id");
                CosmosContainerResponse cosmosContainerResponse = cosmosDatabase.createContainerIfNotExists(properties);
                cosmosContainer = cosmosDatabase.getContainer(cosmosContainerResponse.getProperties().getId());
            }

        } catch (CosmosException e) {
            // TODO: Something has gone terribly wrong - the app wasn't
            // able to query or create the collection.
            // Verify your connection, endpoint, and key.
            System.out.println("Something has gone terribly wrong - " +
                "the app wasn't able to create the Container.\n");
            e.printStackTrace();
        }

        return cosmosContainer;
    }

    private JsonNode getDocumentById(String id) {

        String sql = "SELECT * FROM root r WHERE r.id='" + id + "'";
        int maxItemCount = 1000;
        int maxDegreeOfParallelism = 1000;
        int maxBufferedItemCount = 100;

        CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();
        options.setMaxBufferedItemCount(maxBufferedItemCount);
        options.setMaxDegreeOfParallelism(maxDegreeOfParallelism);
        options.setQueryMetricsEnabled(false);

        List<JsonNode> itemList = new ArrayList();

        String continuationToken = null;
        do {
            for (FeedResponse<JsonNode> pageResponse :
                getContainerCreateResourcesIfNotExist()
                    .queryItems(sql, options, JsonNode.class)
                    .iterableByPage(continuationToken, maxItemCount)) {

                continuationToken = pageResponse.getContinuationToken();

                for (JsonNode item : pageResponse.getElements()) {
                    itemList.add(item);
                }
            }

        } while (continuationToken != null);

        if (itemList.size() > 0) {
            return itemList.get(0);
        } else {
            return null;
        }
    }

}
