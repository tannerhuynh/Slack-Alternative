package com.neu.prattle.utils;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides functionality to integrate the Hibernate library & tools.
 * @author team 9 and Hibernate
 * @date Nov 2019
 */
public class HibernateUtil {
    /**
     * The standard registry of Hibernate.
     */
    private static StandardServiceRegistry registry;
    /**
     * The session factory
     */
    private static SessionFactory sessionFactory;
    /**
     * The logger - uses Hibernate's library
     */
    private static Logger logger = Logger.getLogger(HibernateUtil.class.getName());

    /**
     * Initialization of the HibernateUtil class. Provides static functionality to the methods in this class.
     */
    private HibernateUtil(){
        // nothing
    }

    /** Sets the session factory for this object
     * @param sessionFactory the sessionFactory to set
     */
    public static void setSessionFactory(SessionFactory sessionFactory) {
        HibernateUtil.sessionFactory = sessionFactory;
    }

    /** Sets the registry for this object
     * @param registry the registry to set
     */
    public static void setRegistry(StandardServiceRegistry registry) {
        HibernateUtil.registry = registry;
    }

    /**
     * The getter for Hibernate's session factory
     * @return the Session Factory
     */
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                // Create registry
                registry = new StandardServiceRegistryBuilder().configure().build();

                // Create MetadataSources
                MetadataSources sources = new MetadataSources(registry);

                // Create Metadata
                Metadata metadata = sources.getMetadataBuilder().build();

                // Create SessionFactory
                sessionFactory = metadata.getSessionFactoryBuilder().build();

            } catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage());
                if (registry != null) {
                    StandardServiceRegistryBuilder.destroy(registry);
                }
            }
        }
        return sessionFactory;
    }

    /**
     * Destroys the registry of this object.
     */
    public static void shutdown() {
        if (registry != null) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}

