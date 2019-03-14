//add to dependencies in build.gradle:    implementation 'org.mariadb.jdbc:mariadb-java-client:2.1.2'

package edu.unh.cs.cs619.bulletzone.datalayer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.stream.Collectors;

public class BulletZoneData {
    private Connection dataConnection;
    ItemTypeRepository repo = new ItemTypeRepository();

    public BulletZoneData(String url, String username, String password)
    {
        try {
            dataConnection = DriverManager.getConnection(url, username, password);
            System.out.println("Database connected.");
        } catch (
                SQLException e) {
            throw new IllegalStateException("Cannot connect to the database!", e);
        }

        try {
            Statement statement = dataConnection.createStatement();
            ResultSet resultSet = statement.executeQuery("show tables");
            if (!resultSet.next()) //no next row means we don't have any tables
                initializeData();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot access tables!", e);
        }

        repo.readStaticInfo(dataConnection);
    }

    public void listTables() {
        try {
            Statement statement = dataConnection.createStatement();
            ResultSet resultSet = statement.executeQuery("show tables");
            System.out.println("Database tables:");
            while (resultSet.next())
                System.out.println(resultSet.getString(1));

        } catch (SQLException e) {
            throw new IllegalStateException("Cannot show tables!", e);
        }
    }

    public void close() {
        try {
            dataConnection.close();
            System.out.println("Database connection closed.");
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot close the database!", e);
        }
    }

    public Collection<ItemCategory> getItemCategories() {
        return repo.categoryMap.values();
    }

    public Collection<ItemType> getItemTypes() {
        return repo.typeMap.values();
    }

    public Collection<FrameType> getFrameTypes() {
        return repo.frameMap.values();
    }

    public Collection<WeaponType> getWeaponTypes() {
        return repo.weaponMap.values();
    }

    public Collection<GeneratorType> getGeneratorTypes() {
        return repo.generatorMap.values();
    }

    public Collection<EngineType> getEngineTypes() {
        return repo.engineMap.values();
    }

    public Collection<DriveType> getDriveTypes() {
        return repo.driveMap.values();
    }

    private void initializeData() {
        executeScript("/CreateTables.sql", "Cannot create tables!");
        executeScript("/PopulateTables.sql", "Cannot populate tables!");
    }

    public void rebuildData() {
        executeScript("/DropTables.sql", "Cannot drop all tables!");
        initializeData();
    }

    ResultSet executeScript(String resourceName, String errorMessage)
    {
        ResultSet resultSet = null;
        try {
            Statement statement = dataConnection.createStatement();
            InputStream in = getClass().getResourceAsStream(resourceName);
            if (in == null)
                System.out.println("missing resource file");
            else {
                String createQuery = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
                resultSet = statement.executeQuery(createQuery);
            }
        } catch (SQLException e) {
            throw new IllegalStateException(errorMessage, e);
        }
        return resultSet;
    }
}