package org.memgraphd.request;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.joda.time.DateTime;
import org.json.JSONObject;


public abstract class AbstractGraphRequest implements GraphRequest {
    private String requestUri;
    private DateTime time;
    private JSONObject data;
    
    public AbstractGraphRequest(String uri, DateTime time, JSONObject obj) {
        this.requestUri = uri;
        this.time = time;
        this.data = obj;
    }
    
    @Override
    public final String getRequestUri() {
        return requestUri;
    }

    @Override
    public final DateTime getTime() {
        return time;
    }

    @Override
    public final JSONObject getData() {
        return data;
    }
    
    @Override
    public final String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
