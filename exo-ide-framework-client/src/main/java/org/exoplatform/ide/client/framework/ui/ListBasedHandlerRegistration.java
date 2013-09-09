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
package org.exoplatform.ide.client.framework.ui;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;

import java.util.List;

/**
 * This HandlerRegistration points on event which stores in the List of handlers.
 * <p/>
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ListBasedHandlerRegistration implements HandlerRegistration {

    /** List where handler stores. */
    private List<?> handlerList;

    /** Event Handler instance. */
    private EventHandler handler;

    /**
     * Creates new instance of this HandlerRegistration.
     *
     * @param handlerList
     *         list where handlers stores
     * @param handler
     *         event handler
     */
    public ListBasedHandlerRegistration(List<?> handlerList, EventHandler handler) {
        this.handlerList = handlerList;
        this.handler = handler;
    }

    /** @see com.google.gwt.event.shared.HandlerRegistration#removeHandler() */
    @Override
    public void removeHandler() {
        handlerList.remove(handler);
    }

}
