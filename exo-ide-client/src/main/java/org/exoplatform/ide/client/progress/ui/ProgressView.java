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
package org.exoplatform.ide.client.progress.ui;

import com.google.gwt.core.client.GWT;
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
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.job.Job;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.client.progress.ProgressPresenter.Display;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Sep 19, 2011 evgen $
 */
public class ProgressView extends ViewImpl implements Display {

    private static final String ID = "ideRequestNotificationView";

    private static RequestNotificationViewUiBinder uiBinder = GWT.create(RequestNotificationViewUiBinder.class);

    interface RequestNotificationViewUiBinder extends UiBinder<Widget, ProgressView> {
    }

    private static final int DEFAULT_WIDTH = 500;

    private static final int DEFAULT_HEIGHT = 300;

    private static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.progressViewTitle();

    private IconButton removeAllFinishedButton;

    @UiField
    ScrollPanel scrollPanel;

    @UiField
    FlowPanel contentPanel;

    @UiField
    Toolbar toolbar;

    private boolean odd;

    private Map<String, Widget> jobs = new HashMap<String, Widget>();

    public ProgressView() {
        super(ID, ViewType.OPERATION, TITLE, new Image(IDEImageBundle.INSTANCE.progresImage()), DEFAULT_WIDTH,
              DEFAULT_HEIGHT);
        add(uiBinder.createAndBindUi(this));
        Image normalIcon = new Image(IDEImageBundle.INSTANCE.progresRemall());
        Image disabledIcon = new Image(IDEImageBundle.INSTANCE.progresRemall());
        removeAllFinishedButton = new IconButton(normalIcon, disabledIcon);
        removeAllFinishedButton.setTitle(IDE.IDE_LOCALIZATION_CONSTANT.progressRemoveAllFinished());

        ToolbarItem toolbarItem = toolbar.addItem(removeAllFinishedButton, true);
        toolbarItem.getElement().getStyle().setPaddingTop(2, Unit.PX);
        toolbarItem.getElement().getStyle().setPaddingRight(2, Unit.PX);
    }

    /** @see org.exoplatform.ide.client.notifications.ProgressPresenter.Display#updateJobs(java.util.LinkedHashMap) */
    @Override
    public void updateJobs(LinkedHashMap<String, Job> jobs) {
        contentPanel.clear();
        this.jobs.clear();
        for (Job j : jobs.values()) {
            Widget jobWidget = new JobWidget(j, odd);
            this.jobs.put(j.getId(), jobWidget);
            contentPanel.insert(jobWidget, 0);
            odd = !odd;
        }
    }

    /** @see org.exoplatform.ide.client.notifications.ProgressPresenter.Display#updateOrAddJob(org.exoplatform.ide.client.framework.job
     * .Job) */
    @Override
    public void updateOrAddJob(Job job) {
        if (jobs.containsKey(job.getId())) {
            Widget widget = jobs.get(job.getId());
            Widget jobWidget = contentPanel.getWidget(contentPanel.getWidgetIndex(widget));
            ((JobWidget)jobWidget).updateJob(job);
        } else {
            JobWidget widget = new JobWidget(job, odd);
            odd = !odd;
            contentPanel.insert(widget, 0);
            jobs.put(job.getId(), widget);
        }
    }

    /** @see org.exoplatform.ide.client.notifications.ProgressPresenter.Display#getRemoveFinishedButton() */
    @Override
    public HasClickHandlers getRemoveFinishedButton() {
        return removeAllFinishedButton;
    }

}
