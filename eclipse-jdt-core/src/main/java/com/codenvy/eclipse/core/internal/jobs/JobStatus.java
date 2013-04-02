/*******************************************************************************
 * Copyright (c) 2004, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM - Initial API and implementation
 *******************************************************************************/
package com.codenvy.eclipse.core.internal.jobs;

import com.codenvy.eclipse.core.runtime.Status;
import com.codenvy.eclipse.core.runtime.jobs.IJobStatus;
import com.codenvy.eclipse.core.runtime.jobs.Job;

/** Standard implementation of the IJobStatus interface. */
public class JobStatus extends Status implements IJobStatus {
    private Job job;

    /**
     * Creates a new job status with no interesting error code or exception.
     *
     * @param severity
     * @param job
     * @param message
     */
    public JobStatus(int severity, Job job, String message) {
        super(severity, JobManager.PI_JOBS, 1, message, null);
        this.job = job;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.jobs.IJobStatus#getJob()
     */
    public Job getJob() {
        return job;
    }
}
