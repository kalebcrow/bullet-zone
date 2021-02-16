package edu.unh.cs.cs619.bulletzone.datalayer.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import edu.unh.cs.cs619.bulletzone.datalayer.BulletZoneData;
import edu.unh.cs.cs619.bulletzone.datalayer.core.AggregateRepository;
import edu.unh.cs.cs619.bulletzone.datalayer.core.Entity;
import edu.unh.cs.cs619.bulletzone.datalayer.core.EntityRepository;

public class AssociationManager {
    BulletZoneData data;
    GameUserRepository userRepo;
    EntityRepository entityRepo;
    HashMap<String, HashMap<Integer, UserAssociation>> associations = new HashMap<>();

    public class UserAssociation {
        public final GameUser user;
        public final Entity entity;
        public final double value;
        public final String info;

        public UserAssociation(UserAssociationRecord rec) {
            user = userRepo.getUser(rec.user_entityID);
            entity = entityRepo.getTarget(rec.entityID);
            value = rec.value;
            info = rec.info;
        }
    }

    public void add(GameUser user, String associationType, Entity e, double val, String info) {
        UserAssociation ua = insert(new UserAssociationRecord(user.getId(), associationType, e.getId(), val, info));
        if (ua != null){
            add(ua, associationType);
        }
    }

    public void remove(GameUser user, String associationType) {
        if (delete(user.getId(), associationType)) {
            associations.get(associationType).remove(user.getId());
            if (associations.get(associationType).size() <= 0) {
                associations.remove(associationType);
            }
        }
    }

    public Collection<UserAssociation> get(String associationType) {
        return associations.get(associationType).values();
    }

    public UserAssociation get(GameUser user, String associationType) {
        return associations.get(associationType).get(user.getId());
    }

    //----------------------------------END OF PUBLIC METHODS--------------------------------------

    /** Adds the given userAssociation under the given tag in the associations map
     *
     * @param ua                UserAssocation to add
     * @param associationType   Tag to add it under
     */
    void add(UserAssociation ua, String associationType) {
        if (!associations.containsKey(associationType)) {
            associations.put(associationType, new HashMap<>());
        }
        associations.get(associationType).put(ua.user.getId(), ua);
    }

    /** inserts the passed record into the database and returns a new UserAssociation with
     * the appropriate data
     * @param record    Filled UserAssociationRecord with all the data need to add
     * @return A properly filled UserAssociation if successful, otherwise null.
     */
    UserAssociation insert(UserAssociationRecord record) {
        Connection dataConnection = data.getConnection();
        if (dataConnection == null)
            return null;

        try {
            // Create base item
            PreparedStatement insertStatement = dataConnection.prepareStatement(record.getInsertString());
            if (insertStatement.executeUpdate() == 0) {
                dataConnection.close();
                return null;
            }
            dataConnection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Error while adding user association.", e);
        }
        return new UserAssociation(record);
    }

    boolean delete(int id, String associationType) {
        Connection dataConnection = data.getConnection();
        if (dataConnection == null)
            return false;

        try {
            // Create base item
            PreparedStatement insertStatement = dataConnection.prepareStatement(
                    " DELETE FROM UserAssociation WHERE " +
                    " User_EntityID=" + id + " AND " +
                    " Tag='" + associationType + "'; ");
            if (insertStatement.executeUpdate() == 0) {
                dataConnection.close();
                return false;
            }
            dataConnection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Error while deleting user association.", e);
        }
        return true;
    }

    /**
     * Reads the database and fills the HashMaps as appropriate. Intended to be called once
     * at time of initialization.
     *
     * @param bzData    reference to BulletZoneData for making further SQL queries
     * @param repos     List of repositories of entities that can be owned
     * @param gameUserRepo repository of users that can have associations
     */
    public void refresh(BulletZoneData bzData, List<? extends EntityRepository> repos, GameUserRepository gameUserRepo) {
        entityRepo = new AggregateRepository(repos);
        userRepo = gameUserRepo;
        data = bzData;
        associations.clear();

        Connection dataConnection = data.getConnection();
        if (dataConnection == null)
            return;
        try {
            Statement statement = dataConnection.createStatement();

            // Read mapping of users to items that they own
            ResultSet mappingResult = statement.executeQuery("SELECT * FROM UserAssociation");
            while (mappingResult.next()) {
                UserAssociationRecord record = new UserAssociationRecord(mappingResult);
                UserAssociation ua = new UserAssociation(record);
                add(ua, record.tag);
            }
            dataConnection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot read static info!", e);
        }
    }
}
