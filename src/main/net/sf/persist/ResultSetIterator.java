// $Id$

package net.sf.persist;

import java.util.Iterator;

/**
 * Iterator backed by a ResultSet that should be closed after use.
 */
public interface ResultSetIterator<T> extends Iterator<T> {
    public void close();
}
