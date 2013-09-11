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
package org.exoplatform.ide.client.theme;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class ThemeChangedEvent extends GwtEvent<ThemeChangedHandler> {

    public static final GwtEvent.Type<ThemeChangedHandler> TYPE = new GwtEvent.Type<ThemeChangedHandler>();

    private String theme;

    public ThemeChangedEvent(String theme) {
        this.theme = theme;
    }

    public String getTheme() {
        return theme;
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ThemeChangedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ThemeChangedHandler handler) {
        handler.onThemeChanged(this);
    }

}
