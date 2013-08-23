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

package org.exoplatform.ide.client.progress;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.util.ImageHelper;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.job.Job;
import org.exoplatform.ide.client.framework.job.Job.JobStatus;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class SingleJobView extends ViewImpl implements
                                            org.exoplatform.ide.client.progress.ProgressPresenter.SingleJobDisplay {

    public static final int DEFAULT_WIDTH = 500;

    public static final int DEFAULT_HEIGHT = 120;

    public static final String ID = "ide.modalJob.view";

    private static ModalJobViewUiBinder uiBinder = GWT.create(ModalJobViewUiBinder.class);

    interface ModalJobViewUiBinder extends UiBinder<Widget, SingleJobView> {
    }

    @UiField
    FlowPanel jobPanel;

    @UiField
    HTML jobInfo;

    @UiField
    ImageButton hideButton;

    private Job currentJob;

    public SingleJobView() {
        super(ID, ViewType.MODAL, "Progress...", null, DEFAULT_WIDTH, DEFAULT_HEIGHT, false);
        setCloseOnEscape(true);
        add(uiBinder.createAndBindUi(this));
    }

    @Override
    public void setCurrentJob(Job job) {
        currentJob = job;

        if (job == null) {
            return;
        }

        if (job.getStatus() == JobStatus.STARTED) {
            String status = job.getStartMessage();
            status = (status.indexOf(": ") != -1) ? status.substring(status.indexOf(": ") + 2) : status;
            status = ImageHelper.getImageHTML(IDEImageBundle.INSTANCE.asyncRequest()) + "&nbsp;&nbsp;&nbsp;" + status;
            jobInfo.setHTML(status);
        }
    }

    @Override
    public HasClickHandlers getHideButton() {
        return hideButton;
    }

    @Override
    public Job getCurrentJob() {
        return currentJob;
    }

}
