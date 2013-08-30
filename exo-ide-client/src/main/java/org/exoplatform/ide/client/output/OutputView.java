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
package org.exoplatform.ide.client.output;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.IconButton;
import org.exoplatform.gwtframework.ui.client.component.Toolbar;
import org.exoplatform.gwtframework.ui.client.component.ToolbarItem;
import org.exoplatform.gwtframework.ui.client.util.ImageHelper;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class OutputView extends ViewImpl implements org.exoplatform.ide.client.output.OutputPresenter.Display {

    private static final String ID = "ideOutputView";

    private static final String CONTENT_ID = "ideOutputContent";

    public static final int WIDTH = 450;

    public static final int HEIGHT = 250;

    private static OutputViewExUiBinder uiBinder = GWT.create(OutputViewExUiBinder.class);

    private static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.outputTitle();

    private static final String CLEAR_OUTPUT = IDE.IDE_LOCALIZATION_CONSTANT.outputClear();

    interface OutputViewExUiBinder extends UiBinder<Widget, OutputView> {
    }

    private IconButton clearOutputButton;

    @UiField
    ScrollPanel scrollPanel;

    @UiField
    FlowPanel contentPanel;

    @UiField
    Toolbar toolbar;

    private boolean odd = true;

    public OutputView() {
        super(ID, ViewType.OPERATION, TITLE, new Image(IDEImageBundle.INSTANCE.output()), WIDTH, HEIGHT);
        add(uiBinder.createAndBindUi(this));

        String normalIcon = ImageHelper.getImageHTML(Images.OutputPanel.CLEAR_OUTPUT);
        String disabledIcon = ImageHelper.getImageHTML(Images.OutputPanel.CLEAR_OUTPUT);
        clearOutputButton = new IconButton(normalIcon, disabledIcon);
        clearOutputButton.setTitle(CLEAR_OUTPUT);
        clearOutputButton.setEnabled(true);
        ToolbarItem toolbarItem = toolbar.addItem(clearOutputButton, true);
        toolbarItem.getElement().getStyle().setPaddingTop(2, Unit.PX);
        toolbarItem.getElement().getStyle().setPaddingRight(2, Unit.PX);

        contentPanel.getElement().setId(CONTENT_ID);
    }

    @Override
    public void clearOutput() {
        contentPanel.clear();
        scrollPanel.scrollToTop();
    }

    @Override
    public void outMessage(OutputMessage message) {
        OutputRecord record = new OutputRecord(message, odd);
        odd = !odd;
        contentPanel.add(record);
        scrollPanel.scrollToBottom();

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                scrollPanel.scrollToBottom();
            }
        });
    }

    @Override
    public HasClickHandlers getClearOutputButton() {
        return clearOutputButton;
    }

}
