package org.memgraphd.data;

public abstract class AbstractDataPermissions implements DataPermissions {

    /**
     *
     */
    private static final long serialVersionUID = -2377165350111562720L;

    protected boolean read, write, update, delete;

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean canRead() {
        return read;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean canWrite() {
        return write;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean canUpdate() {
        return update;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean canDelete() {
        return delete;
    }

}
