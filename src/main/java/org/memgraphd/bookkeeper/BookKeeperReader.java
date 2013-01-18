package org.memgraphd.bookkeeper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.memgraphd.GraphRequestType;
import org.memgraphd.data.Data;
import org.memgraphd.decision.Decision;
import org.memgraphd.decision.DecisionImpl;
import org.memgraphd.decision.Sequence;

/**
 * It is responsible for reading decisions already made from the book on behalf of the book-keeper.
 * 
 * @author Ilirjan Papa
 * @since October 21, 2012
 * @see BookKeeper
 */
public class BookKeeperReader extends BookKeeperBase {
    
    /**
     * Constructs a new instance of {@link BookKeeperReader}.
     * @param threadName thread name as {@link String}
     * @param persistenceStore {@link PersistenceStore}
     */
    public BookKeeperReader(String threadName, PersistenceStore persistenceStore) {
        super(threadName, persistenceStore);
    }
    
    /**
     * Returns the list of decisions between start and end sequence, sorted by sequence number in ascending order.
     * @param start {@link Sequence}
     * @param end  {@link Sequence}
     * @return {@link List} of {@link Decision}.
     * @throws Exception
     */
    public List<Decision> readRange(Sequence start, Sequence end) throws SQLException {
        List<Decision> result = new ArrayList<Decision>();
        PreparedStatement statement = 
           getPersistenceStore().openConnection().prepareStatement(
                String.format("SELECT * FROM %s WHERE SEQUENCE_ID BETWEEN ? AND ? ORDER BY SEQUENCE_ID ASC", 
                        getPersistenceStore().getDatabaseName()));
        statement.setLong(1, start.number());
        statement.setLong(2, end.number());
        
        ResultSet rs = statement.executeQuery();
        
        while(rs.next()) {
            result.add(new DecisionImpl(
                    Sequence.valueOf(rs.getLong("SEQUENCE_ID")), 
                    new DateTime(rs.getTimestamp("DECISION_TIME").getTime()),
                    GraphRequestType.valueOf(rs.getString("REQUEST_TYPE")),
                    rs.getString("DATA_ID"),
                    (Data) rs.getObject("DATA")));
        }
        return result;
    }

}
