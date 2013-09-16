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
package org.exoplatform.ide.extension.jenkins.client;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

import org.exoplatform.ide.extension.jenkins.shared.Job;
import org.exoplatform.ide.extension.jenkins.shared.JobStatus;

/**
 * The interface for the AutoBean generator.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: JenkinsAutoBeanFactory.java Mar 15, 2012 3:35:51 PM azatsarynnyy $
 */
public interface JenkinsAutoBeanFactory extends AutoBeanFactory {
    /**
     * A factory method for a job info bean.
     *
     * @return an {@link AutoBean} of type {@link Job}
     */
    AutoBean<Job> job();

    /**
     * A factory method for a job status bean.
     *
     * @return an {@link AutoBean} of type {@link JobStatus}
     */
    AutoBean<JobStatus> jobStatus();
}
