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
package org.exoplatform.ide.extension.heroku.client.imports;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event occurs, when user tries to import application from Heroku.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Mar 19, 2012 10:39:02 AM anya $
 */
public class ImportApplicationEvent extends GwtEvent<ImportApplicationHandler> {
    /** Type used to register this event. */
    public static final GwtEvent.Type<ImportApplicationHandler> TYPE = new GwtEvent.Type<ImportApplicationHandler>();

    /** Application to import. */
    private String application;

    /**
     * @param application
     *         application to import
     */
    public ImportApplicationEvent(String application) {
        this.application = application;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ImportApplicationHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(ImportApplicationHandler handler) {
        handler.onImportApplication(this);
    }

    /** @return the application to import */
    public String getApplication() {
        return application;
    }
}
