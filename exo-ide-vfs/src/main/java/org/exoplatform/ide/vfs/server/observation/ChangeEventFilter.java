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
package org.exoplatform.ide.vfs.server.observation;

import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

import java.util.List;

/**
 * Can be used to narrow the set of messages received by the EventListener.
 * Filter may be passed when new listener is registered in EventListenerList.
 * Filter may be combined, see methods <code>createAndFilter</code> or <code>createOrFilter</code>.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public abstract class ChangeEventFilter {
    public static ChangeEventFilter createAndFilter(ChangeEventFilter... filters) {
        if (filters == null || filters.length < 2) {
            throw new IllegalArgumentException("At least two filters required. ");
        }
        ChangeEventFilter[] copy = new ChangeEventFilter[filters.length];
        System.arraycopy(filters, 0, copy, 0, filters.length);
        return new AndChangeEventFilter(copy);
    }

    public static ChangeEventFilter createAndFilter(List<ChangeEventFilter> filters) {
        return createAndFilter(filters.toArray(new ChangeEventFilter[filters.size()]));
    }

    private static class AndChangeEventFilter extends ChangeEventFilter {
        private final ChangeEventFilter[] filters;

        @Override
        public boolean matched(ChangeEvent event) throws VirtualFileSystemException {
            for (ChangeEventFilter filter : filters) {
                if (!filter.matched(event)) {
                    return false;
                }
            }
            return true;
        }

        private AndChangeEventFilter(ChangeEventFilter[] filters) {
            this.filters = filters;
        }
    }

    public static ChangeEventFilter createOrFilter(ChangeEventFilter... filters) {
        if (filters == null || filters.length < 2) {
            throw new IllegalArgumentException("At least two filters required. ");
        }
        ChangeEventFilter[] copy = new ChangeEventFilter[filters.length];
        System.arraycopy(filters, 0, copy, 0, filters.length);
        return new OrChangeEventFilter(copy);
    }

    public static ChangeEventFilter createOrFilter(List<ChangeEventFilter> filters) {
        return createOrFilter(filters.toArray(new ChangeEventFilter[filters.size()]));
    }

    private static class OrChangeEventFilter extends ChangeEventFilter {
        private final ChangeEventFilter[] filters;

        @Override
        public boolean matched(ChangeEvent event) throws VirtualFileSystemException {
            for (ChangeEventFilter filter : filters) {
                if (filter.matched(event)) {
                    return true;
                }
            }
            return false;
        }

        private OrChangeEventFilter(ChangeEventFilter[] filters) {
            this.filters = filters;
        }
    }

    public static final ChangeEventFilter ANY_FILTER = new AnyFilter();

    private static class AnyFilter extends ChangeEventFilter {
        @Override
        public boolean matched(ChangeEvent event) {
            return true;
        }
    }

   /* ================================================= */

    public abstract boolean matched(ChangeEvent event) throws VirtualFileSystemException;

}
