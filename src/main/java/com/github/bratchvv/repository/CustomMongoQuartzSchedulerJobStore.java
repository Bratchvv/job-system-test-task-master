package com.github.bratchvv.repository;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.novemberain.quartz.mongodb.MongoDBJobStore;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

import javax.el.PropertyNotFoundException;
import java.util.Properties;

/**
 * @author Vladimir Bratchikov
 */
public class CustomMongoQuartzSchedulerJobStore extends MongoDBJobStore {

    private static final String DEFAULT_APP_CONFIG_FILE_PATH = "config/application.yml";
    private static String mongoAddresses;
    private static String dbName;

    public CustomMongoQuartzSchedulerJobStore() {
        super();
        initializeMongo();
        setMongoUri(mongoAddresses);
        setDbName(dbName);
    }

    /**
     * <p>
     * This method will initialize the mongo instance required by the Quartz scheduler.
     * <p>
     * The use case here is that we have two profiles;
     * </p>
     *
     * <p>
     * So when constructing the mongo instance to be used for the Quartz scheduler,
     * we need to read the various properties set within the system to
     * determine which would be appropriate depending on which spring profile is active.
     * </p>
     */
    private void initializeMongo() {
        YamlPropertiesFactoryBean properties = getPropertiesFactoryClassPath(DEFAULT_APP_CONFIG_FILE_PATH);

        Properties propertiesObject = properties.getObject();
        if (propertiesObject != null) {
            mongoAddresses = propertiesObject.getProperty("spring.data.mongodb.uri");
            try (MongoClient mongoClient = MongoClients.create(mongoAddresses)) {
                dbName = mongoClient.getDatabase("jobs").getName();
            }
        } else {
            throw new PropertyNotFoundException("Properties not found");
        }

    }

    private YamlPropertiesFactoryBean getPropertiesFactoryClassPath(String file) {
        YamlPropertiesFactoryBean bootstrapProperties = new YamlPropertiesFactoryBean();
        ClassPathResource bootstrapResource = new ClassPathResource(file);
        bootstrapProperties.setResources(bootstrapResource);
        return bootstrapProperties;
    }

}
