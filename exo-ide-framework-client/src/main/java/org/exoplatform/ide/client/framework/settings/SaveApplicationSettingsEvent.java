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

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class SaveApplicationSettingsEvent extends GwtEvent<SaveApplicationSettingsHandler> {

    public enum SaveType {
        COOKIES, SERVER, BOTH
    }

    public static final GwtEvent.Type<SaveApplicationSettingsHandler> TYPE =
            new GwtEvent.Type<SaveApplicationSettingsHandler>();

    private SaveType saveType;

    private ApplicationSettings applicationSettings;

    public SaveApplicationSettingsEvent(ApplicationSettings applicationSettings) {
        this.applicationSettings = applicationSettings;
        saveType = SaveType.BOTH;
    }

    public SaveApplicationSettingsEvent(ApplicationSettings applicationSettings, SaveType saveType) {
        this.applicationSettings = applicationSettings;
        this.saveType = saveType;
    }

    public ApplicationSettings getApplicationSettings() {
        return applicationSettings;
    }

    public SaveType getSaveType() {
        return saveType;
    }

    @Override
    protected void dispatch(SaveApplicationSettingsHandler handler) {
        handler.onSaveApplicationSettings(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<SaveApplicationSettingsHandler> getAssociatedType() {
        return TYPE;
    }

}
