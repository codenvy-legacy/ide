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
package org.exoplatform.ide.extension.java.jdi.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.java.jdi.client.DebuggerExtension;
import org.exoplatform.ide.extension.java.jdi.client.ReLaunchDebuggerPresenter;

public class RunDebuggerView extends ViewImpl implements ReLaunchDebuggerPresenter.Display {
    private static final String ID = "ideRunDebuggerView";

    private static final int WIDTH = 320;

    private static final int HEIGHT = 130;

    private static RunDebuggerViewUiBinder uiBinder = GWT.create(RunDebuggerViewUiBinder.class);

    interface RunDebuggerViewUiBinder extends UiBinder<Widget, RunDebuggerView> {
    }


    /** Cancel button. */
    @UiField
    ImageButton cancelButton;

    public RunDebuggerView() {
        super(ID, ViewType.MODAL, DebuggerExtension.LOCALIZATION_CONSTANT.debug(), null, WIDTH, HEIGHT, false);

        add(uiBinder.createAndBindUi(this));
        setCanBeClosed(false);
    }

    /** @see org.exoplatform.ide.extension.ReLaunchDebuggerPresenter.client.create.CreateApplicationPresenter.Display#getCloseButton() */
    @Override
    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }

}
