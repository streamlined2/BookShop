package com.streamlined.bookshop.config.database;

import java.util.List;

import org.bson.UuidRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.connection.ClusterSettings;
import com.mongodb.connection.ClusterType;

@Configuration
@EnableMongoRepositories(basePackages = "com.streamlined.bookshop.dao")
public class MongoConfig extends AbstractMongoClientConfiguration {

	private static final String DB_NAME = "bookshop";
	private static final String APPLICATION_NAME = "BookShop";
	private static final String REPLICA_SET = "replicaset";
	private static final ServerAddress HOST_ADDRESS = new ServerAddress("localhost");
	@Value("${spring.data.mongodb.username}")
	private String userName;
	@Value("${spring.data.mongodb.password}")
	private String password;

	@Bean
	MongoTransactionManager getTransactionManager(MongoDatabaseFactory databaseFactory) {
		return new MongoTransactionManager(databaseFactory);
	}

	@Override
	protected String getDatabaseName() {
		return DB_NAME;
	}

	@Override
	public MongoClient mongoClient() {
		final MongoCredential credential = MongoCredential.createCredential(userName, DB_NAME, password.toCharArray());
		var clusterSettings = ClusterSettings.builder().requiredReplicaSetName(REPLICA_SET).hosts(List.of(HOST_ADDRESS))
				.requiredClusterType(ClusterType.REPLICA_SET).build();
		final MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
				.applyToClusterSettings(builder -> builder.applySettings(clusterSettings)).credential(credential)
				.applicationName(APPLICATION_NAME).uuidRepresentation(UuidRepresentation.STANDARD).build();
		return MongoClients.create(mongoClientSettings);
	}

}
