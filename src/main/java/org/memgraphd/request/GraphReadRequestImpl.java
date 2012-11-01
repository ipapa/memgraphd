package org.memgraphd.request;

import org.joda.time.DateTime;


public class GraphReadRequestImpl extends AbstractGraphRequest implements GraphReadRequest {

    public GraphReadRequestImpl(String uri, DateTime time) {
        super(uri, time, null);
    }

    @Override
    public RequestType getType() {
        return RequestType.GET;
    }

}
