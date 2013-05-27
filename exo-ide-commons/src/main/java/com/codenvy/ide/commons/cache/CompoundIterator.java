package com.codenvy.ide.commons.cache;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author <a href="mailto:dvishinskiy@codenvy.com">Dmitriy Vyshinskiy</a>
 * @version $Id: $
 */

public class CompoundIterator<T> implements Iterator<T> {

    private final LinkedList<Iterator<T>> iteratorQueue;
    private Iterator<T>                   current;

    public CompoundIterator(final Iterator<T>... iterators) {
        this.iteratorQueue = new LinkedList<Iterator<T>>();
        for (final Iterator<T> iterator : iterators) {
            iteratorQueue.push(iterator);
        }
        current = Collections.<T> emptyList().iterator();
    }

    public boolean hasNext() {
        final boolean curHasNext = current.hasNext();
        if (!curHasNext && !iteratorQueue.isEmpty()) {
            current = iteratorQueue.pop();
            return current.hasNext();
        } else {
            return curHasNext;
        }
    }

    public T next() {
        if (current.hasNext()) {
            return current.next();
        }
        if (!iteratorQueue.isEmpty()) {
            current = iteratorQueue.pop();
        }
        return current.next();
    }

    public void remove() {
        current.remove();
    }
}
