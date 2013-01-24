package org.memgraphd.test.library;

import org.memgraphd.data.GraphData;
import org.memgraphd.data.library.DataPredicate;
import org.memgraphd.test.data.TvSeries;

public class TvSeriesPredicate implements DataPredicate {

    @Override
    public boolean apply(GraphData data) {
        if(data.getData() ==  null ||
            !data.getData().getClass().equals(TvSeries.class)) {
             return false;
         }
         return true;
    }

}
