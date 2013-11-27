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
package org.exoplatform.ide.client.outline;

import com.google.gwt.user.client.Timer;

import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.outline.OutlineDisplay;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler;
import org.exoplatform.ide.client.model.SettingsService;

/**
 * This class listens the opening and closing of Outline panels and
 * stores this information to cookies. 
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class OutlineStateListener implements ViewOpenedHandler, ViewClosedHandler,
    ApplicationSettingsReceivedHandler {
    
    /** Application's settings. */
    private ApplicationSettings applicationSettings;    
    
    /**
     * Count of opened Outline panels.
     */
    private int openedOutlinePanels = 0;
    
    /**
     * Created instance of this {@link OutlineStateListener}
     */
    public OutlineStateListener() {
        IDE.addHandler(ViewOpenedEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
    }

    /**
     * @see org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler#onViewOpened(org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedEvent)
     */
    @Override
    public void onViewOpened(ViewOpenedEvent event) {
        if (event.getView() instanceof OutlineDisplay) {
            openedOutlinePanels++;
            saveTimer.cancel();
            saveTimer.schedule(500);
        }
    }

    /**
     * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent)
     */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof OutlineDisplay) {
            openedOutlinePanels--;
            saveTimer.cancel();
            saveTimer.schedule(500);
        }
    }
    
    /**
     * @see org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedHandler#onApplicationSettingsReceived(org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedEvent)
     */
    @Override
    public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event) {
        this.applicationSettings = event.getApplicationSettings();
    }
    
    /**
     * Timer to store state of Outline panel to cookies.
     */
    private Timer saveTimer = new Timer() {
        @Override
        public void run() {
            applicationSettings.setValue("outline", (boolean)(openedOutlinePanels > 0), Store.COOKIES);
            SettingsService.getInstance().saveSettingsToCookies(applicationSettings);
        }
    };

}
