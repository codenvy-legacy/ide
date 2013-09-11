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

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Manages list of EventListener.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class EventListenerList {
    private static final ErrorHandler DEFAULT_ERROR_HANDLER = new LogErrorHandler();

    private final CopyOnWriteArrayList<ListenerHolder> listeners;
    private final ErrorHandler                         errorHandler;

    public EventListenerList(ErrorHandler errorHandler) {
        if (errorHandler == null) {
            throw new IllegalArgumentException("ErrorHandler may not be null. ");
        }
        this.errorHandler = errorHandler;
        listeners = new CopyOnWriteArrayList<ListenerHolder>();
    }

    public EventListenerList() {
        this(DEFAULT_ERROR_HANDLER);
    }

    /**
     * Add new EventListener to the list if the same combination of listener and filter is not present.
     * Here is example how to add new EventListener that get events about updating content of all web.xml files in VFS
     * 'my-vfs':
     * <pre>
     *    ...
     *    EventListener listener = new EventListener()
     *    {
     *       public void handleEvent(ChangeEvent event) throws VirtualFileSystemException
     *       {
     *          // do something
     *       }
     *    };
     *    addEventListener(ChangeEventFilter.createAndFilter(
     *       new VfsIDFilter("my-vfs"), // from "my-vfs" only
     *       new TypeFilter(ChangeEvent.ChangeType.CONTENT_UPDATED), // only update of content
     *       new MimeTypeFilter("application/xml"), // media type application/xml
     *       new PathFilter("^(.&#042/)?web\\.xml")), // any web.xml files
     *       listener
     *    );
     * </pre>
     *
     * @param eventFilter
     *         filter for events. Listener get only events matched to the filter. If this parameter is <code>null</code>
     *         listener will get all events
     * @param listener
     *         EventListener
     * @return <code>true</code> if listener was added and <code>false</code> otherwise
     */
    public boolean addEventListener(ChangeEventFilter eventFilter, EventListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("EventListener may not be null. ");
        }
        return listeners.addIfAbsent(
                new ListenerHolder(eventFilter == null ? ChangeEventFilter.ANY_FILTER : eventFilter, listener));
    }

    /**
     * Remove listener from the list.
     *
     * @param eventFilter
     *         ChangeEventFilter
     * @param listener
     *         EventListener
     * @return <code>true</code> if listener was removed and <code>false</code> otherwise
     * @see #addEventListener(ChangeEventFilter, EventListener)
     */
    public boolean removeEventListener(ChangeEventFilter eventFilter, EventListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("EventListener may not be null. ");
        }
        return listeners.remove(new ListenerHolder(eventFilter == null ? ChangeEventFilter.ANY_FILTER : eventFilter, listener));
    }

    /**
     * Notify all EventListeners subscribed to this type of event.
     *
     * @param event
     *         ChangeEvent
     * @throws VirtualFileSystemException
     *         if any error occurs
     */
    public void notifyListeners(ChangeEvent event) throws VirtualFileSystemException {
        try {
            for (ListenerHolder listenerHolder : listeners) {
                if (listenerHolder.eventFilter.matched(event)) {
                    listenerHolder.listener.handleEvent(event);
                }
            }
        } catch (Throwable e) {
            errorHandler.onError(event, e);
        }
    }

    public int size() {
        return listeners.size();
    }

    private static class ListenerHolder {
        final ChangeEventFilter eventFilter;
        final EventListener     listener;

        private ListenerHolder(ChangeEventFilter eventFilter, EventListener listener) {
            this.eventFilter = eventFilter;
            this.listener = listener;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof ListenerHolder)) {
                return false;
            }
            ListenerHolder other = (ListenerHolder)o;
            return eventFilter.equals(other.eventFilter) && listener.equals(other.listener);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 31 * hash + eventFilter.hashCode();
            hash = 31 * hash + listener.hashCode();
            return hash;
        }
    }
}
