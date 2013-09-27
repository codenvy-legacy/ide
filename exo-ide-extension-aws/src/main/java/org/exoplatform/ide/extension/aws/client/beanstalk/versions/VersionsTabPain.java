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
package org.exoplatform.ide.extension.aws.client.beanstalk.versions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 23, 2012 12:14:23 PM anya $
 */
public class VersionsTabPain extends Composite {
    private static VersionsTabPainUiBinder uiBinder = GWT.create(VersionsTabPainUiBinder.class);

    interface VersionsTabPainUiBinder extends UiBinder<Widget, VersionsTabPain> {
    }

    @UiField
    VersionsGrid versionsGrid;

    public VersionsTabPain() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    /** @return the versionsGrid */
    public VersionsGrid getVersionsGrid() {
        return versionsGrid;
    }
}