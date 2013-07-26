package org.memgraphd.data;

import org.joda.time.DateTime;
/**
 * All implementations of {@link ExpiringData} should subclass this abstract class.
 * @author Ilirjan Papa
 * @since January 27, 2013
 *
 */
public abstract class AbstractExpiringData extends AbstractData implements ExpiringData {

    private static final long serialVersionUID = -585213396218671489L;
    
    protected DateTime expirationDate;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final DateTime getExpirationDate() {
        return expirationDate;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean hasExpired() {
        if(getExpirationDate() != null) {
            return getExpirationDate().isBeforeNow();
        }
        return false;
    }

}
