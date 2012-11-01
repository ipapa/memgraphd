package org.memgraphd.request;

import org.joda.time.DateTime;


public class GraphDeleteRequestImpl extends AbstractGraphRequest implements GraphDeleteRequest {

    public GraphDeleteRequestImpl(String uri, DateTime time) {
        super(uri, time, null);
    }

    @Override
    public RequestType getType() {
        return RequestType.DELETE;
    }

}
