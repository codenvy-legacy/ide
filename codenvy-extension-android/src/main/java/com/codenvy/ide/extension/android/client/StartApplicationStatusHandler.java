/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.extension.android.client;

import org.exoplatform.gwtframework.commons.rest.RequestStatusHandler;
import org.exoplatform.ide.client.framework.job.Job;
import org.exoplatform.ide.client.framework.job.JobChangeEvent;
import org.exoplatform.ide.client.framework.module.IDE;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class StartApplicationStatusHandler implements RequestStatusHandler {
    private String projectName;

    public StartApplicationStatusHandler(String projectName) {
        this.projectName = projectName;
    }

    @Override
    public void requestInProgress(String id) {
        Job job = new Job(id, Job.JobStatus.STARTED);
        job.setStartMessage(AndroidExtension.LOCALIZATION.startingProjectMessage(projectName));
        IDE.fireEvent(new JobChangeEvent(job));
    }

    @Override
    public void requestFinished(String id) {
        Job job = new Job(id, Job.JobStatus.FINISHED);
        job.setFinishMessage(AndroidExtension.LOCALIZATION.projectStartedMessage(projectName));
        IDE.fireEvent(new JobChangeEvent(job));
    }

    @Override
    public void requestError(String id, Throwable exception) {
        Job job = new Job(id, Job.JobStatus.ERROR);
        job.setError(exception);
        IDE.fireEvent(new JobChangeEvent(job));
    }
}
