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
package org.exoplatform.ide.client.framework.settings;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.client.framework.settings.SaveApplicationSettingsEvent.SaveType;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ApplicationSettingsSavedEvent extends GwtEvent<ApplicationSettingsSavedHandler> {

    public static final GwtEvent.Type<ApplicationSettingsSavedHandler> TYPE =
            new GwtEvent.Type<ApplicationSettingsSavedHandler>();

    private ApplicationSettings applicationSettings;

    private SaveType saveType;

    public ApplicationSettingsSavedEvent(ApplicationSettings applicationSettings, SaveType saveType) {
        this.applicationSettings = applicationSettings;
        this.saveType = saveType;
    }

    @Override
    protected void dispatch(ApplicationSettingsSavedHandler handler) {
        handler.onApplicationSettingsSaved(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ApplicationSettingsSavedHandler> getAssociatedType() {
        return TYPE;
    }

    public ApplicationSettings getApplicationSettings() {
        return applicationSettings;
    }

    public SaveType getSaveType() {
        return saveType;
    }

}
