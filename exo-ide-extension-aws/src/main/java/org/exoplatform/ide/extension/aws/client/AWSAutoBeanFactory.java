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
package org.exoplatform.ide.extension.aws.client;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

import org.exoplatform.ide.extension.aws.client.login.Credentials;
import org.exoplatform.ide.extension.aws.shared.beanstalk.*;
import org.exoplatform.ide.extension.aws.shared.ec2.ImagesList;
import org.exoplatform.ide.extension.aws.shared.ec2.InstanceInfo;
import org.exoplatform.ide.extension.aws.shared.ec2.SecurityGroupInfo;
import org.exoplatform.ide.extension.aws.shared.s3.NewS3Object;
import org.exoplatform.ide.extension.aws.shared.s3.S3Bucket;
import org.exoplatform.ide.extension.aws.shared.s3.S3ObjectsList;

/**
 * The interface for the AutoBean generator.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 14, 2012 10:28:09 AM anya $
 */
public interface AWSAutoBeanFactory extends AutoBeanFactory {

    /**
     * A factory method for an application info bean.
     *
     * @return an {@link AutoBean} of type {@link ApplicationInfo}
     */
    AutoBean<ApplicationInfo> applicationInfo();

    /**
     * A factory method for an application version info bean.
     *
     * @return an {@link AutoBean} of type {@link ApplicationVersionInfoBean}
     */
    AutoBean<ApplicationVersionInfo> applicationVersionInfo();

    /**
     * A factory method for configuration bean.
     *
     * @return an {@link AutoBean} of type {@link Configuration}
     */
    AutoBean<Configuration> configuration();

    /**
     * A factory method for configuration request bean.
     *
     * @return an {@link AutoBean} of type {@link ConfigurationRequest}
     */
    AutoBean<ConfigurationRequest> configurationRequest();

    /**
     * A factory method for configuration option bean.
     *
     * @return an {@link AutoBean} of type {@link ConfigurationOption}
     */
    AutoBean<ConfigurationOption> configurationOption();

    /**
     * A factory method for configuration option info bean.
     *
     * @return an {@link AutoBean} of type {@link ConfigurationOptionInfo}
     */
    AutoBean<ConfigurationOptionInfo> configurationOptionInfo();

    /**
     * A factory method for configuration option restriction bean.
     *
     * @return an {@link AutoBean} of type {@link ConfigurationOptionRestriction}
     */
    AutoBean<ConfigurationOptionRestriction> configurationOptionRestriction();

    /**
     * A factory method for SolutionStackConfigurationOptionsRequest bean.
     *
     * @return an {@link AutoBean} of type {@link SolutionStackConfigurationOptionsRequest}
     */
    AutoBean<SolutionStackConfigurationOptionsRequest> solutionStackConfigurationOptionsRequest();

    /**
     * A factory method for S3 item bean.
     *
     * @return an {@link AutoBean} of type {@link S3Item}
     */
    AutoBean<S3Item> s3Item();

    /**
     * A factory method for solution stack bean.
     *
     * @return an {@link AutoBean} of type {@link SolutionStack}
     */
    AutoBean<SolutionStack> solutionStack();

    /**
     * A factory method for credentials bean.
     *
     * @return {@link Credentials} credentials
     */
    AutoBean<Credentials> credentials();

    /** @return  */
    AutoBean<S3Bucket> s3Bucket();

    AutoBean<S3ObjectsList> s3ObjectsList();

    /**
     * A factory method for create application request bean.
     *
     * @return {@link CreateApplicationRequest} create application request
     */
    AutoBean<CreateApplicationRequest> createApplicationRequest();

    /**
     * A factory method for update application request bean.
     *
     * @return {@link UpdateApplicationRequest} update application request
     */
    AutoBean<UpdateApplicationRequest> updateApplicationRequest();

    /**
     * A factory method for list events request bean.
     *
     * @return {@link ListEventsRequest} list events request
     */
    AutoBean<ListEventsRequest> listEventsRequest();

    /**
     * A factory method for create environment request bean.
     *
     * @return {@link CreateEnvironmentRequest} create environment request
     */
    AutoBean<CreateEnvironmentRequest> createEnvironmentRequest();

    /**
     * A factory method for environment info bean.
     *
     * @return an {@link AutoBean} of type {@link EnvironmentInfo}
     */
    AutoBean<EnvironmentInfo> environmentInfo();

    /**
     * A factory method for update environment request bean.
     *
     * @return {@link UpdateEnvironmentRequest} update environment request
     */
    AutoBean<UpdateEnvironmentRequest> updateEnvironmentRequest();

    /**
     * A factory method for create images list bean.
     *
     * @return {@link ImagesList} images list
     */
    AutoBean<ImagesList> imageList();

    /**
     * A factory method for create security group info bean.
     *
     * @return {@link SecurityGroupInfo} info about security group
     */
    AutoBean<SecurityGroupInfo> securityGroupInfo();

    AutoBean<EventsList> eventList();

    AutoBean<Event> event();

    AutoBean<DeleteApplicationVersionRequest> deleteVersionRequest();

    AutoBean<CreateApplicationVersionRequest> createVersionRequest();

    /**
     * A factory method for an instance info bean.
     *
     * @return an {@link AutoBean} of type {@link InstanceInfo}
     */
    AutoBean<InstanceInfo> instanceInfo();

    AutoBean<NewS3Object> newS3Object();

    /**
     * A factory method for an instance log bean.
     *
     * @return an {@link AutoBean} of type {@link InstanceLog}
     */
    AutoBean<InstanceLog> instanceLog();
}
