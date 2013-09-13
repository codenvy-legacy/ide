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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;

import org.exoplatform.ide.client.framework.control.Docking;
import org.exoplatform.ide.client.framework.job.Job;
import org.exoplatform.ide.client.framework.job.Job.JobStatus;
import org.exoplatform.ide.client.framework.job.JobChangeEvent;
import org.exoplatform.ide.client.framework.job.JobChangeHandler;
import org.exoplatform.ide.client.framework.job.JobManager;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;

import java.util.LinkedHashMap;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Sep 16, 2011 evgen $
 */
public class ProgressPresenter extends JobManager implements JobChangeHandler, ShowProgressHandler, ViewClosedHandler {

    public interface Display extends IsView {

        void updateJobs(LinkedHashMap<String, Job> jobs);

        void updateOrAddJob(Job job);

        HasClickHandlers getRemoveFinishedButton();
    }

    public interface SingleJobDisplay extends IsView {

        void setCurrentJob(Job job);

        Job getCurrentJob();

        HasClickHandlers getHideButton();

    }

    private LinkedHashMap<String, Job> jobs = new LinkedHashMap<String, Job>();

    private ProgressNotificationControl control;

    private Display display;

    private SingleJobDisplay singleJobDisplay;

    private boolean showJobSeparated = false;

    /**
     *
     */
    public ProgressPresenter() {
        IDE.addHandler(JobChangeEvent.TYPE, this);
        IDE.addHandler(ShowProgressEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);

        control = new ProgressNotificationControl();
        control.setDelimiterBefore(true);
        IDE.getInstance().addControl(control, Docking.STATUSBAR_RIGHT);
        IDE.getInstance().addControl(new ShowProgressControl());
    }

    private void bind() {
        display.getRemoveFinishedButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                LinkedHashMap<String, Job> job = new LinkedHashMap<String, Job>();
                for (String key : jobs.keySet()) {
                    if (jobs.get(key).getStatus() == JobStatus.STARTED) {
                        job.put(key, jobs.get(key));
                    }
                }
                jobs = job;
                display.updateJobs(jobs);
                if (jobs.isEmpty()) {
                    control.hide();
                }
            }
        });
    }

    /** @see org.exoplatform.ide.client.framework.job.JobChangeHandler#onJobChangeHandler(org.exoplatform.ide.client.framework.job
     * .JobChangeEvent) */
    @Override
    public void onJobChangeHandler(JobChangeEvent event) {
        Job job = event.getJob();
        control.setEnabled(true);
        jobs.put(job.getId(), job);

        if (job.getStatus() == JobStatus.STARTED && showJobSeparated) {
            showJobSeparated = false;
            showJobInWindow(job);
            return;
        }

        if (singleJobDisplay != null && job.getStatus() != JobStatus.STARTED) {
            singleJobDisplay.setCurrentJob(job);
            IDE.getInstance().closeView(singleJobDisplay.asView().getId());
            return;
        }

        if (job.getStatus() == JobStatus.FINISHED) {
            Job nextStartedJob = getNextStartedJob();
            if (nextStartedJob == null) {
                control.hide();
            } else {
                control.updateState(nextStartedJob);
                control.show();
            }
        } else {
            control.updateState(job);
            control.show();
        }

        if (display != null) {
            display.updateOrAddJob(job);
        }
    }

    /**
     * Returns first {@link Job} which is started.
     *
     * @return started {@link Job}
     */
    private Job getNextStartedJob() {
        for (String key : jobs.keySet()) {
            if (jobs.get(key).getStatus() == JobStatus.STARTED) {
                return jobs.get(key);
            }
        }
        return null;
    }

    private void showJobInWindow(Job job) {
        singleJobDisplay = GWT.create(SingleJobDisplay.class);
        IDE.getInstance().openView(singleJobDisplay.asView());
        singleJobDisplay.setCurrentJob(job);

        singleJobDisplay.getHideButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(singleJobDisplay.asView().getId());
            }
        });
    }

    /** @see org.exoplatform.ide.client.ShowProgressHandler.event.ShowJobsHandler#onShowProgress(org.exoplatform.ide.client
     * .ShowProgressEvent.event.ShowJobsEvent) */
    @Override
    public void onShowProgress(ShowProgressEvent event) {
        if (display == null) {
            display = GWT.create(Display.class);
            IDE.getInstance().openView(display.asView());
            bind();
        } else {
            display.asView().activate();
        }

        display.updateJobs(jobs);
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        } else if (event.getView() instanceof SingleJobDisplay) {
            if (singleJobDisplay.getCurrentJob() != null) {
                Job job = singleJobDisplay.getCurrentJob();

                control.updateState(job);
                control.show();

                if (display != null) {
                    display.updateOrAddJob(job);
                    display.asView().activate();
                }

                singleJobDisplay.setCurrentJob(null);
            }

            singleJobDisplay = null;
        }
    }

    @Override
    public void showJobSeparated() {
        showJobSeparated = true;
    }

}
