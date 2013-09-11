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
package org.exoplatform.ide.extension.heroku.client.rename;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.extension.heroku.client.marshaller.Property;

import java.util.List;

/**
 * Event occurs after rename Heroku application operation.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Dec 8, 2011 2:24:25 PM anya $
 */
public class ApplicationRenamedEvent extends GwtEvent<ApplicationRenamedHandler> {

    /** Type used to register event. */
    public static final GwtEvent.Type<ApplicationRenamedHandler> TYPE = new GwtEvent.Type<ApplicationRenamedHandler>();

    /** Application properties after rename. */
    private List<Property> properties;

    /** Previous application's name. */
    private String oldName;

    /**
     * @param oldName
     *         previous application's name
     * @param properties
     *         application properties after rename
     */
    public ApplicationRenamedEvent(List<Property> properties, String oldName) {
        this.oldName = oldName;
        this.properties = properties;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ApplicationRenamedHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(ApplicationRenamedHandler handler) {
        handler.onApplicationRenamed(this);
    }

    /** @return the properties application's properties */
    public List<Property> getProperties() {
        return properties;
    }

    /** @return {@link String} previous application's name */
    public String getOldName() {
        return oldName;
    }
}
