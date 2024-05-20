package com.translate.demo.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ReadConcern;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.connection.ConnectionPoolSettings;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.translate.demo.repo")
public class DatabaseConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.data.mongodb.database}")
    private String database;

    private MongoClient mongoClient;

    @Override
    protected void configureClientSettings(MongoClientSettings.Builder builder) {
        // customization hook
     builder.applyConnectionString(new ConnectionString(mongoUri));
    }

    @Override
    public MongoClient mongoClient() {
        return getMongoClient();
    }

    @Bean
    public MongoClient getMongoClient() {
        ConnectionPoolSettings.builder();
        ConnectionString connectionString = new ConnectionString(mongoUri);
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
          .applyConnectionString(connectionString)
          .applyToConnectionPoolSettings(builder ->  {builder.minSize(150); builder.maxSize(200);} )
          .writeConcern(WriteConcern.MAJORITY)
          .readConcern(ReadConcern.SNAPSHOT)
          .build();

        mongoClient = MongoClients.create(mongoClientSettings);

        return mongoClient;
    }


    @Override
    protected String getDatabaseName() {
        return database;
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(getMongoClient(), database);
    }

    @Bean
    MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }
}

