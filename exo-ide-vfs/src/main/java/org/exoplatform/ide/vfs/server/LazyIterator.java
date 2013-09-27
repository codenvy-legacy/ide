/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.ide.vfs.server;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: LazyIterator.java 79579 2012-02-17 13:27:25Z andrew00x $
 */
public abstract class LazyIterator<T> implements Iterator<T> {
    public static final LazyIterator<Object> EMPTY_ITEMS_ITERATOR = new EmptyIterator();

    private static class EmptyIterator extends LazyIterator<Object> {
        /** @see org.exoplatform.ide.vfs.server.LazyIterator#fetchNext() */
        @Override
        protected void fetchNext() {
        }

        /** @see org.exoplatform.ide.vfs.server.LazyIterator#size() */
        @Override
        public int size() {
            return 0;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> LazyIterator<T> emptyItemsIterator() {
        return (LazyIterator<T>)EMPTY_ITEMS_ITERATOR;
    }

    // -----------------------------------

    protected T next;

    /** To fetch next item and set it in field <code>next</code> */
    protected abstract void fetchNext();

    /** @see java.util.Iterator#hasNext() */
    public boolean hasNext() {
        return next != null;
    }

    /** @see java.util.Iterator#next() */
    public T next() {
        if (next == null) {
            throw new NoSuchElementException();
        }
        T n = next;
        fetchNext();
        return n;
    }

    /** @see java.util.Iterator#remove() */
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }

    /**
     * Get total number of items in iterator. If not able determine number of items then -1 will be returned.
     *
     * @return number of items or -1
     */
    public int size() {
        return -1;
    }

    /**
     * Skip specified number of element in collection.
     *
     * @param skip
     *         the number of items to skip
     * @throws NoSuchElementException
     *         if skipped past the last item in the iterator
     */
    public void skip(int skip) throws NoSuchElementException {
        while (skip-- > 0) {
            fetchNext();
            if (next == null) {
                throw new NoSuchElementException();
            }
        }
    }
}
