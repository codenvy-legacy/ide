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
import org.exoplatform.ide.extension.googleappengine.client.cron.CronGrid;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 23, 2012 3:22:35 PM anya $
 */
public class CronTabPane extends Composite {
    private static final String UPDATE_CRON_BUTTON_ID = "ideCronTabPaneUpdateCronButton";

    private static CronTabPaneUiBinder uiBinder = GWT.create(CronTabPaneUiBinder.class);

    interface CronTabPaneUiBinder extends UiBinder<Widget, CronTabPane> {
    }

    @UiField
    ImageButton updateCronButton;

    @UiField
    CronGrid cronGrid;

    public CronTabPane() {
        initWidget(uiBinder.createAndBindUi(this));

        updateCronButton.setButtonId(UPDATE_CRON_BUTTON_ID);
    }

    /** @return the updateCronButton */
    public ImageButton getUpdateCronButton() {
        return updateCronButton;
    }

    public CronGrid getCronGrid() {
        return cronGrid;
    }
}
