package org.memgraphd.test.library;

import org.memgraphd.data.GraphData;
import org.memgraphd.data.library.DataPredicate;
import org.memgraphd.test.data.Movie;

public class MoviePredicate implements DataPredicate {

    @Override
    public boolean apply(GraphData data) {
        if(data.getData() ==  null ||
            !data.getData().getClass().equals(Movie.class)) {
             return false;
         }
         return true;
    }


}
