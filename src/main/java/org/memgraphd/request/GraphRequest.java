package org.memgraphd.request;

import org.joda.time.DateTime;
import org.json.JSONObject;

public interface GraphRequest {
    
    RequestType getType();
    
    String getRequestUri();
    
    DateTime getTime();
    
    JSONObject getData();
}
