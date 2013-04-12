/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
