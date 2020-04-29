package com.microsoft.azure.documentdb.sample.dao;

import com.azure.cosmos.ConnectionPolicy;
import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;

public class CosmosClientFactory {
    private static final String HOST = "https://docdb-java-sample.documents.azure.com:443/";
    private static final String MASTER_KEY = "[YOUR_KEY_HERE]";

    private static CosmosClient cosmosClient = new CosmosClientBuilder()
            .setEndpoint(HOST)
            .setKey(MASTER_KEY)
            .setConnectionPolicy(ConnectionPolicy.getDefaultPolicy())
            .setConsistencyLevel(ConsistencyLevel.EVENTUAL)
            .buildClient();

    public static CosmosClient getCosmosClient() {
        return cosmosClient;
    }

}
