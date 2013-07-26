package org.memgraphd.test.library;

import org.memgraphd.data.GraphData;
import org.memgraphd.data.library.DataPredicate;
import org.memgraphd.test.data.OnlineVideo;

public class ShortFormVideoPredicate implements DataPredicate {

    @Override
    public boolean apply(GraphData data) {
        if(data.getData() ==  null ||
            !data.getData().getClass().equals(OnlineVideo.class)) {
             return false;
         }
         OnlineVideo video = (OnlineVideo) data.getData();
         return !video.isLongForm();
    }
    
}
