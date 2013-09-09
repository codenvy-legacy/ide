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
package org.exoplatform.ide.extension.googleappengine.client.project;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 23, 2012 12:14:23 PM anya $
 */
public class MainTabPain extends Composite {

    private static MainTabPainUiBinder uiBinder = GWT.create(MainTabPainUiBinder.class);

    interface MainTabPainUiBinder extends UiBinder<Widget, MainTabPain> {
    }

    @UiField
    ImageButton updateApplicationButton;

    @UiField
    ImageButton rollbackApplicationButton;

    @UiField
    ImageButton getLogsButton;

    @UiField
    ImageButton updateDosButton;

    @UiField
    ImageButton updateIndexesButton;

    @UiField
    ImageButton vacuumIndexesButton;

    @UiField
    ImageButton updatePageSpeedButton;

    @UiField
    ImageButton updateQueuesButton;

    public MainTabPain() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    /** @return the updateApplicationButton */
    public ImageButton getUpdateApplicationButton() {
        return updateApplicationButton;
    }

    /** @return the rollbackApplicationButton */
    public ImageButton getRollbackApplicationButton() {
        return rollbackApplicationButton;
    }

    /** @return the getLogsButton */
    public ImageButton getGetLogsButton() {
        return getLogsButton;
    }

    /** @return the updateDosButton */
    public ImageButton getUpdateDosButton() {
        return updateDosButton;
    }

    /** @return the updateIndexesButton */
    public ImageButton getUpdateIndexesButton() {
        return updateIndexesButton;
    }

    /** @return the vacuumIndexesButton */
    public ImageButton getVacuumIndexesButton() {
        return vacuumIndexesButton;
    }

    /** @return the updatePageSpeedButton */
    public ImageButton getUpdatePageSpeedButton() {
        return updatePageSpeedButton;
    }

    /** @return the updateQueuesButton */
    public ImageButton getUpdateQueuesButton() {
        return updateQueuesButton;
    }
}
