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
