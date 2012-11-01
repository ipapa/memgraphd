package org.memgraphd.bookkeeper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.json.JSONObject;
import org.memgraphd.decision.Decision;
import org.memgraphd.decision.DecisionImpl;
import org.memgraphd.decision.Sequence;
import org.memgraphd.request.GraphDeleteRequestImpl;
import org.memgraphd.request.GraphRequest;
import org.memgraphd.request.GraphWriteRequestimpl;
import org.memgraphd.request.RequestType;

/**
 * It is responsible for reading decisions already made from the book on behalf of the book-keeper.
 * 
 * @author Ilirjan Papa
 * @since October 21, 2012
 * @see BookKeeper
 */
public class BookKeeperReader extends BookKeeperBase {
    
    public BookKeeperReader(String threadName, String dbName, Connection connection) {
        super(threadName, dbName, connection);
    }
    
    /**
     * Returns the list of decisions between start and end sequence, sorted by sequence number in ascending order.
     * @param start {@link Sequence}
     * @param end  {@link Sequence}
     * @return {@link List} of {@link Decision}.
     * @throws Exception
     */
    public List<Decision> readRange(Sequence start, Sequence end) throws Exception {
        List<Decision> result = new ArrayList<Decision>();
        PreparedStatement statement = 
           getConnection().prepareStatement(
                String.format("SELECT * FROM %s WHERE DECISION_SEQUENCE BETWEEN ? AND ? ORDER BY DECISION_SEQUENCE ASC", getDbName()));
        statement.setLong(1, start.number());
        statement.setLong(2, end.number());
        
        ResultSet rs = statement.executeQuery();
        
        while(rs.next()) {
            RequestType reqType = RequestType.valueOf(rs.getString("REQUEST_TYPE"));
            String reqUri = rs.getString("REQUEST_URI");
            DateTime reqTime = new DateTime(rs.getTimestamp("REQUEST_TIME").getTime());
            JSONObject reqData = new JSONObject(rs.getString("REQUEST_DATA"));
            result.add(new DecisionImpl(
                    createRequest(reqType, reqUri, reqTime, reqData) , 
                    Sequence.valueOf(rs.getLong("DECISION_SEQUENCE")), 
                    new DateTime(rs.getTimestamp("DECISION_TIME").getTime())));
        }
        return result;
    }
    
    private GraphRequest createRequest(RequestType reqType, String reqUri, DateTime reqTime, JSONObject reqData) {
        switch (reqType) {
            case PUT:
                return new GraphWriteRequestimpl(reqUri, reqTime, reqData);
            default:
                return new GraphDeleteRequestImpl(reqUri, reqTime);
        }
    }

}
