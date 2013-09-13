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

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 23, 2012 3:22:35 PM anya $
 */
public class ResourceLimitsTabPane extends Composite {

    private static ResourceLimitsTabPaneUiBinder uiBinder = GWT.create(ResourceLimitsTabPaneUiBinder.class);

    interface ResourceLimitsTabPaneUiBinder extends UiBinder<Widget, ResourceLimitsTabPane> {
    }

    @UiField
    ResourceLimitsGrid resourceLimitsGrid;

    public ResourceLimitsTabPane() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public ResourceLimitsGrid getResourceLimitsGrid() {
        return resourceLimitsGrid;
    }
}
