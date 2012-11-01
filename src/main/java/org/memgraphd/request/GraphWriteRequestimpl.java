package org.memgraphd.request;

import org.joda.time.DateTime;
import org.json.JSONObject;


public class GraphWriteRequestimpl extends AbstractGraphRequest implements GraphWriteRequest {

    public GraphWriteRequestimpl(String uri, DateTime time, JSONObject obj) {
        super(uri, time, obj);
    }

    @Override
    public RequestType getType() {
        return RequestType.PUT;
    }

}
