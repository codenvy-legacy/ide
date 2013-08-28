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
package org.exoplatform.ide.extension.logreader.client.event;

import com.google.gwt.event.shared.GwtEvent;

import java.util.Date;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public class LogReaderSettingsChangedEvent extends GwtEvent<LogReaderSettingsChangedHandler> {

    public static GwtEvent.Type<LogReaderSettingsChangedHandler> TYPE = new Type<LogReaderSettingsChangedHandler>();

    private Date date;

    private int limit;

    private int offset;

    /**
     * @param date
     * @param limit
     * @param offset
     */
    public LogReaderSettingsChangedEvent(Date date, int limit, int offset) {
        super();
        this.date = date;
        this.limit = limit;
        this.offset = offset;
    }

    /** @return the date */
    public Date getDate() {
        return date;
    }

    /** @return the limit */
    public int getLimit() {
        return limit;
    }

    /** @return the offset */
    public int getOffset() {
        return offset;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<LogReaderSettingsChangedHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(LogReaderSettingsChangedHandler handler) {
        handler.onLogRederSettingsChanged(this);
    }

}
