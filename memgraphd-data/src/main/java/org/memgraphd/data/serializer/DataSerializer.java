package org.memgraphd.data.serializer;

import org.memgraphd.data.Data;

/**
 * It is responsible for serializing and deserializing data to and from V.
 *
 * @author Ilirjan Papa
 * @since July 31, 2013
 *
 * @param <V>
 */
public interface DataSerializer<V> {

    /**
     * Returns a serialized string representation of the data.
     * @param data {@link Data}
     * @return V
     * @throws Exception
     */
    V serialize(Data data) throws Exception;

    /**
     * Returns a new instance of {@link Data} from deserializing the format V.
     * @param data V
     * @return {@link Data}
     * @throws Exception
     */
    Data deserialize(V data) throws Exception;
}
