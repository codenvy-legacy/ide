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
package org.exoplatform.ide.extension.aws.server.beanstalk;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalk;
import com.amazonaws.services.elasticbeanstalk.model.ApplicationDescription;
import com.amazonaws.services.elasticbeanstalk.model.ApplicationVersionDescription;
import com.amazonaws.services.elasticbeanstalk.model.ConfigurationOptionDescription;
import com.amazonaws.services.elasticbeanstalk.model.ConfigurationOptionSetting;
import com.amazonaws.services.elasticbeanstalk.model.ConfigurationSettingsDescription;
import com.amazonaws.services.elasticbeanstalk.model.CreateApplicationRequest;
import com.amazonaws.services.elasticbeanstalk.model.CreateApplicationVersionRequest;
import com.amazonaws.services.elasticbeanstalk.model.CreateConfigurationTemplateRequest;
import com.amazonaws.services.elasticbeanstalk.model.CreateConfigurationTemplateResult;
import com.amazonaws.services.elasticbeanstalk.model.CreateEnvironmentRequest;
import com.amazonaws.services.elasticbeanstalk.model.CreateEnvironmentResult;
import com.amazonaws.services.elasticbeanstalk.model.DeleteApplicationRequest;
import com.amazonaws.services.elasticbeanstalk.model.DeleteApplicationVersionRequest;
import com.amazonaws.services.elasticbeanstalk.model.DeleteConfigurationTemplateRequest;
import com.amazonaws.services.elasticbeanstalk.model.DescribeApplicationVersionsRequest;
import com.amazonaws.services.elasticbeanstalk.model.DescribeApplicationsRequest;
import com.amazonaws.services.elasticbeanstalk.model.DescribeConfigurationOptionsRequest;
import com.amazonaws.services.elasticbeanstalk.model.DescribeConfigurationSettingsRequest;
import com.amazonaws.services.elasticbeanstalk.model.DescribeEnvironmentsRequest;
import com.amazonaws.services.elasticbeanstalk.model.DescribeEventsRequest;
import com.amazonaws.services.elasticbeanstalk.model.DescribeEventsResult;
import com.amazonaws.services.elasticbeanstalk.model.EnvironmentDescription;
import com.amazonaws.services.elasticbeanstalk.model.EnvironmentInfoDescription;
import com.amazonaws.services.elasticbeanstalk.model.EventDescription;
import com.amazonaws.services.elasticbeanstalk.model.RebuildEnvironmentRequest;
import com.amazonaws.services.elasticbeanstalk.model.RequestEnvironmentInfoRequest;
import com.amazonaws.services.elasticbeanstalk.model.RestartAppServerRequest;
import com.amazonaws.services.elasticbeanstalk.model.RetrieveEnvironmentInfoRequest;
import com.amazonaws.services.elasticbeanstalk.model.S3Location;
import com.amazonaws.services.elasticbeanstalk.model.SolutionStackDescription;
import com.amazonaws.services.elasticbeanstalk.model.SourceConfiguration;
import com.amazonaws.services.elasticbeanstalk.model.TerminateEnvironmentRequest;
import com.amazonaws.services.elasticbeanstalk.model.TerminateEnvironmentResult;
import com.amazonaws.services.elasticbeanstalk.model.UpdateApplicationRequest;
import com.amazonaws.services.elasticbeanstalk.model.UpdateApplicationVersionRequest;
import com.amazonaws.services.elasticbeanstalk.model.UpdateConfigurationTemplateRequest;
import com.amazonaws.services.elasticbeanstalk.model.UpdateConfigurationTemplateResult;
import com.amazonaws.services.elasticbeanstalk.model.UpdateEnvironmentRequest;
import com.amazonaws.services.elasticbeanstalk.model.UpdateEnvironmentResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.codenvy.commons.lang.NameGenerator;

import org.exoplatform.ide.extension.aws.server.AWSClient;
import org.exoplatform.ide.extension.aws.server.AWSException;
import org.exoplatform.ide.extension.aws.shared.beanstalk.ApplicationInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.ApplicationVersionInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.Configuration;
import org.exoplatform.ide.extension.aws.shared.beanstalk.ConfigurationOption;
import org.exoplatform.ide.extension.aws.shared.beanstalk.ConfigurationOptionInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.EnvironmentInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.EventsList;
import org.exoplatform.ide.extension.aws.shared.beanstalk.EventsSeverity;
import org.exoplatform.ide.extension.aws.shared.beanstalk.InstanceLog;
import org.exoplatform.ide.extension.aws.shared.beanstalk.SolutionStack;
import org.exoplatform.ide.security.paas.CredentialStore;
import org.exoplatform.ide.security.paas.CredentialStoreException;
import org.exoplatform.ide.vfs.server.ContentStream;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.ide.vfs.shared.PropertyImpl;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: Beanstalk.java Aug 23, 2012
 */
public class Beanstalk extends AWSClient {
    public Beanstalk(CredentialStore credentialStore) {
        super(credentialStore);
    }

    //

    /**
     * List of available solutions stacks.
     *
     * @return list of available solutions stacks
     *         if any error occurs when make request to Amazon API
     */
    public List<SolutionStack> listAvailableSolutionStacks() throws AWSException, CredentialStoreException {
        try {
            return listAvailableSolutionStacks(getBeanstalkClient());
        } catch (AmazonClientException e) {
            throw new AWSException(e);
        }
    }

    private List<SolutionStack> listAvailableSolutionStacks(AWSElasticBeanstalk beanstalkClient) {
        List<SolutionStackDescription> awsStacks = beanstalkClient.listAvailableSolutionStacks().getSolutionStackDetails();
        List<SolutionStack> stacks = new ArrayList<SolutionStack>(awsStacks.size());
        for (SolutionStackDescription awsStack : awsStacks) {
            stacks.add(new SolutionStackImpl(awsStack.getSolutionStackName(),
                                             new ArrayList<String>(awsStack.getPermittedFileTypes())));
        }
        return stacks;
    }

    /**
     * Get all possible configuration options of specified solution stack.
     *
     * @param solutionStackName
     *         name of solution stack
     * @return list of configuration options
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     */
    public List<ConfigurationOptionInfo> listSolutionStackConfigurationOptions(String solutionStackName)
            throws AWSException, CredentialStoreException {
        try {
            return listSolutionStackConfigurationOptions(getBeanstalkClient(),
                                                         new DescribeConfigurationOptionsRequest()
                                                                 .withSolutionStackName(solutionStackName));
        } catch (AmazonClientException e) {
            throw new AWSException(e);
        }
    }

    private List<ConfigurationOptionInfo> listSolutionStackConfigurationOptions(
            AWSElasticBeanstalk beanstalkClient, DescribeConfigurationOptionsRequest request) {
        List<ConfigurationOptionDescription> awsOptions = beanstalkClient.describeConfigurationOptions(request)
                                                                         .getOptions();
        List<ConfigurationOptionInfo> options = new ArrayList<ConfigurationOptionInfo>(awsOptions.size());
        for (ConfigurationOptionDescription awsOption : awsOptions) {
            options.add(new ConfigurationOptionInfoImpl.Builder()
                                .name(awsOption.getName())
                                .namespace(awsOption.getNamespace())
                                .defaultValue(awsOption.getDefaultValue())
                                .changeSeverity(awsOption.getChangeSeverity())
                                .userDefined(awsOption.isUserDefined())
                                .valueType(awsOption.getValueType())
                                .valueOptions(awsOption.getValueOptions())
                                .minValue(awsOption.getMinValue())
                                .maxValue(awsOption.getMaxValue())
                                .maxLength(awsOption.getMaxLength())
                                .optionRestriction(awsOption.getRegex())
                                .build());
        }
        return options;
    }

    //

    /**
     * Create new AWS Beanstalk application. New version of application created. This version got name
     * 'initial version'.
     *
     * @param applicationName
     *         application name. This name must be unique within AWS Beanstalk account. Length: 1-100 characters
     * @param description
     *         optional description of application. Length: 0 - 200 characters
     * @param s3Bucket
     *         optional name of S3 bucket where initial version of application uploaded before deploy to AWS Beanstalk. If
     *         this parameter not specified random name generated and new S3 bucket created
     * @param s3Key
     *         optional name of S3 key where initial version of  application uploaded before deploy to AWS Beanstalk. If this
     *         parameter not specified random name generated and new S3 file created. If file with specified key already
     *         exists it content will be overridden
     * @param vfs
     *         virtual file system instance for access to source code and properties of project. Some info may be stored in
     *         properties of project after creation an application
     * @param projectId
     *         project id
     * @param war
     *         URL to pre-build war file. May be present for java applications ONLY
     * @return info about newly created application
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     * @throws org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException
     *         if any VFS error occurs
     * @throws java.io.IOException
     *         i/o error, e.g. when try to download pre-build binary file
     */
    public ApplicationInfo createApplication(String applicationName,
                                             String description,
                                             String s3Bucket,
                                             String s3Key,
                                             VirtualFileSystem vfs,
                                             String projectId,
                                             URL war)
            throws AWSException, VirtualFileSystemException, IOException, CredentialStoreException {
        if (applicationName == null || applicationName.isEmpty()) {
            throw new IllegalArgumentException("Application name required. ");
        }
        if (vfs == null || projectId == null) {
            throw new IllegalArgumentException("Project directory required. ");
        }

        try {
            return createApplication(getBeanstalkClient(), getS3Client(), applicationName, description, s3Bucket, s3Key, vfs,
                                     projectId, war);
        } catch (AmazonClientException e) {
            throw new AWSException(e);
        }
    }

    private ApplicationInfo createApplication(AWSElasticBeanstalk beanstalkClient,
                                              AmazonS3 s3Client,
                                              String applicationName,
                                              String description,
                                              String s3Bucket,
                                              String s3Key,
                                              VirtualFileSystem vfs,
                                              String projectId,
                                              URL war) throws VirtualFileSystemException, IOException {
        // Be sure project is accessible before start creation an application.
        // VirtualFileSystemException thrown if something wrong.
        S3Location s3Location = war == null
                                ? createS3Location(s3Client, applicationName, s3Bucket, s3Key, vfs.exportZip(projectId))
                                : createS3Location(s3Client, applicationName, s3Bucket, s3Key, war);
        beanstalkClient.createApplication(new CreateApplicationRequest().withApplicationName(applicationName)
                                                                        .withDescription(description));
        beanstalkClient.createApplicationVersion(new CreateApplicationVersionRequest()
                                                         .withApplicationName(applicationName)
                                                         .withVersionLabel("initial version")
                                                         .withSourceBundle(s3Location)
                                                         .withDescription("Initial version of application " + applicationName));
        writeApplicationName(vfs, projectId, applicationName);
        return getApplicationInfo(beanstalkClient, applicationName);
    }

    /**
     * Get info about AWS Beanstalk application. Name of application retrieved from project properties.
     *
     * @param vfs
     *         virtual file system instance for reading application name from properties of project
     * @param projectId
     *         project id
     * @return info about application
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     * @throws org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException
     *         if any VFS error occurs
     */
    public ApplicationInfo getApplicationInfo(VirtualFileSystem vfs, String projectId) throws AWSException,
                                                                                              VirtualFileSystemException,
                                                                                              CredentialStoreException {
        return getApplicationInfo(detectApplicationName(vfs, projectId));
    }

    /**
     * Get info about AWS Beanstalk application.
     *
     * @param applicationName
     *         name of application
     * @return info about application
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     */
    public ApplicationInfo getApplicationInfo(String applicationName) throws AWSException, CredentialStoreException {
        try {
            ApplicationInfo application = getApplicationInfo(getBeanstalkClient(), applicationName);
            if (application == null) {
                throw new AWSException("Application '" + applicationName + "' not found. ");
            }
            return application;
        } catch (AmazonClientException e) {
            throw new AWSException(e);
        }
    }

    private ApplicationInfo getApplicationInfo(AWSElasticBeanstalk beanstalkClient, String applicationName) {
        List<ApplicationDescription> awsApplications = beanstalkClient.describeApplications(
                new DescribeApplicationsRequest().withApplicationNames(applicationName)).getApplications();
        if (awsApplications.isEmpty()) {
            return null;
        }
        ApplicationDescription awsApplication = awsApplications.get(0);
        return new ApplicationInfoImpl(
                awsApplication.getApplicationName(),
                awsApplication.getDescription(),
                awsApplication.getDateCreated(),
                awsApplication.getDateUpdated(),
                awsApplication.getVersions(),
                awsApplication.getConfigurationTemplates());
    }

    /**
     * Update AWS Beanstalk application. Name of application retrieved from project properties.
     *
     * @param vfs
     *         virtual file system instance for reading application name from properties of project
     * @param projectId
     *         project id
     * @return info about application
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     * @throws org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException
     *         if any VFS error occurs
     */
    public ApplicationInfo updateApplication(VirtualFileSystem vfs, String projectId, String description)
            throws AWSException, VirtualFileSystemException, CredentialStoreException {
        return updateApplication(detectApplicationName(vfs, projectId), description);
    }

    /**
     * Update AWS Beanstalk application.
     *
     * @param applicationName
     *         name of application
     * @param description
     *         new application description
     * @return info about application
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     */
    public ApplicationInfo updateApplication(String applicationName, String description)
            throws AWSException, CredentialStoreException {
        try {
            return updateApplication(getBeanstalkClient(), applicationName, description);
        } catch (AmazonClientException e) {
            throw new AWSException(e);
        }
    }

    private ApplicationInfo updateApplication(AWSElasticBeanstalk beanstalkClient,
                                              String applicationName,
                                              String description) {
        ApplicationDescription awsApplication = beanstalkClient.updateApplication(new UpdateApplicationRequest()
                                                                                          .withApplicationName(applicationName)
                                                                                          .withDescription(description)).getApplication();
        return new ApplicationInfoImpl(
                awsApplication.getApplicationName(),
                awsApplication.getDescription(),
                awsApplication.getDateCreated(),
                awsApplication.getDateUpdated(),
                awsApplication.getVersions(),
                awsApplication.getConfigurationTemplates());
    }

    /**
     * Get list of application events. Name of application retrieved from project properties.
     *
     * @param versionLabel
     *         application version identifier to get events. If <code>null</code> get events for all versions of application
     * @param templateName
     *         configuration template to get events. If <code>null</code> get events for all configuration template
     * @param environmentId
     *         environment id
     * @param severity
     *         events severity. If specified only events with specified severity or higher will be returned
     * @param startTime
     *         start time to get events
     * @param endTime
     *         end time to get events
     * @param maxRecords
     *         restrict the number of events in result. Max value: 1000
     * @param nextToken
     *         token to get the next batch of results. See org.exoplatform.ide.extension.aws.shared.EventsList#getNextToken()
     * @param vfs
     *         virtual file system instance for reading application name from properties of project
     * @param projectId
     *         project id
     * @return list of events
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     * @throws org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException
     *         if any VFS error occurs
     * @see org.exoplatform.ide.extension.aws.shared.beanstalk.Event
     */
    public EventsList listApplicationEvents(String versionLabel,
                                            String templateName,
                                            String environmentId,
                                            EventsSeverity severity,
                                            long startTime,
                                            long endTime,
                                            int maxRecords,
                                            String nextToken,
                                            VirtualFileSystem vfs,
                                            String projectId)
            throws AWSException, VirtualFileSystemException, CredentialStoreException {
        return listApplicationEvents(detectApplicationName(vfs, projectId), versionLabel, templateName, environmentId,
                                     severity, startTime, endTime, maxRecords, nextToken);
    }

    /**
     * Get list of application events.
     *
     * @param applicationName
     *         name of application
     * @param versionLabel
     *         application version identifier to get events. If <code>null</code> get events for all versions of application
     * @param templateName
     *         configuration template to get events. If <code>null</code> get events for all configuration template
     * @param environmentId
     *         environment id
     * @param severity
     *         events severity. If specified only events with specified severity or higher will be returned
     * @param startTime
     *         start time to get events
     * @param endTime
     *         end time to get events
     * @param maxRecords
     *         restrict the number of events in result. Max value: 1000
     * @param nextToken
     *         token to get the next batch of results. See org.exoplatform.ide.extension.aws.shared.EventsList#getNextToken()
     * @return list of events
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     * @see org.exoplatform.ide.extension.aws.shared.beanstalk.Event
     */
    public EventsList listApplicationEvents(String applicationName,
                                            String versionLabel,
                                            String templateName,
                                            String environmentId,
                                            EventsSeverity severity,
                                            long startTime,
                                            long endTime,
                                            int maxRecords,
                                            String nextToken) throws AWSException, CredentialStoreException {
        try {
            return listApplicationEvents(getBeanstalkClient(), applicationName, versionLabel, templateName, environmentId,
                                         severity, startTime, endTime, maxRecords, nextToken);
        } catch (AmazonClientException e) {
            throw new AWSException(e);
        }
    }

    private EventsList listApplicationEvents(AWSElasticBeanstalk beanstalkClient,
                                             String applicationName,
                                             String versionLabel,
                                             String templateName,
                                             String environmentId,
                                             EventsSeverity severity,
                                             long startTime,
                                             long endTime,
                                             int maxRecords,
                                             String nextToken) {
        DescribeEventsRequest request = new DescribeEventsRequest().withApplicationName(applicationName)
                                                                   .withVersionLabel(versionLabel).withTemplateName(templateName)
                                                                   .withEnvironmentId(environmentId)
                                                                   .withNextToken(nextToken);
        if (severity != null) {
            request.withSeverity(severity.toString());
        }
        if (startTime > 0) {
            request.setStartTime(new Date(startTime));
        }
        if (endTime > 0) {
            request.setEndTime(new Date(endTime));
        }
        if (maxRecords > 0) {
            request.setMaxRecords(maxRecords);
        }
        DescribeEventsResult result = beanstalkClient.describeEvents(request);
        EventsList events = new EventsListImpl();
        for (EventDescription awsEvent : result.getEvents()) {
            events.getEvents().add(new EventImpl.Builder()
                                           .eventDate(awsEvent.getEventDate())
                                           .message(awsEvent.getMessage())
                                           .severity(awsEvent.getSeverity())
                                           .applicationName(awsEvent.getApplicationName())
                                           .versionLabel(awsEvent.getVersionLabel())
                                           .templateName(awsEvent.getTemplateName())
                                           .environmentName(awsEvent.getEnvironmentName())
                                           .build());
        }
        events.setNextToken(result.getNextToken());
        return events;
    }

    /**
     * Delete specified  application. Name of application retrieved from project properties.
     * <p/>
     * After successful delete:
     * <ul>
     * <li>All running environments are terminated</li>
     * <li>All versions associated with this application are deleted</li>
     * <li>Any attached Amazon RDS DB Instance are deleted</li>
     * <li>Versions bundles are NOT deleted from Amazon S3</li>
     * </ul>
     *
     * @param vfs
     *         virtual file system instance for reading application name from properties of project
     * @param projectId
     *         project id
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     * @throws org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException
     *         if any VFS error occurs
     */
    public void deleteApplication(VirtualFileSystem vfs, String projectId) throws AWSException,
                                                                                  VirtualFileSystemException, CredentialStoreException {
        deleteApplication(detectApplicationName(vfs, projectId));
        writeApplicationName(vfs, projectId, null);
    }

    /**
     * Delete specified  application.
     * <p/>
     * After successful delete:
     * <ul>
     * <li>All running environments are terminated</li>
     * <li>All versions associated with this application are deleted</li>
     * <li>Any attached Amazon RDS DB Instance are deleted</li>
     * <li>Versions bundles are NOT deleted from Amazon S3</li>
     * </ul>
     *
     * @param applicationName
     *         of application to delete
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     */
    public void deleteApplication(String applicationName) throws AWSException, CredentialStoreException {
        try {
            deleteApplication(getBeanstalkClient(), applicationName);
        } catch (AmazonClientException e) {
            throw new AWSException(e);
        }
    }

    private void deleteApplication(AWSElasticBeanstalk beanstalkClient, String applicationName) {
        beanstalkClient.deleteApplication(new DeleteApplicationRequest(applicationName));
    }

    /**
     * Get existing applications.
     *
     * @return list of applications
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     */
    public List<ApplicationInfo> listApplications() throws AWSException, CredentialStoreException {
        try {
            return listApplications(getBeanstalkClient());
        } catch (AmazonClientException e) {
            throw new AWSException(e);
        }
    }

    private List<ApplicationInfo> listApplications(AWSElasticBeanstalk beanstalkClient) {
        List<ApplicationDescription> awsApplications = beanstalkClient.describeApplications().getApplications();
        List<ApplicationInfo> applications = new ArrayList<ApplicationInfo>(awsApplications.size());
        for (ApplicationDescription awsApplication : awsApplications) {
            applications.add(
                    new ApplicationInfoImpl(
                            awsApplication.getApplicationName(),
                            awsApplication.getDescription(),
                            awsApplication.getDateCreated(),
                            awsApplication.getDateUpdated(),
                            new ArrayList<String>(awsApplication.getVersions()),
                            new ArrayList<String>(awsApplication.getConfigurationTemplates()))
                            );
        }
        return applications;
    }

    //

    /**
     * Create new configuration template. Name of current application retrieved from project properties.
     * Template is associated with a current application and are used to deploy different versions of the application
     * with the same configuration settings.
     *
     * @param templateName
     *         name of template. This name must be unique per application. Length: 1 - 100 characters.
     * @param solutionStackName
     *         name of amazon solution stack used by this new template , e.g. '64bit Amazon Linux running Tomcat 6'
     * @param sourceApplicationName
     *         source application to copy configuration values to create a new configuration
     * @param sourceTemplateName
     *         source template name
     * @param environmentId
     *         id of the environment used with this configuration template
     * @param description
     *         configuration template description. Length: 0 - 200 characters
     * @param options
     *         Set configuration options. Options specified in this list override options copied from solution stack or
     *         source configuration template.
     * @param vfs
     *         virtual file system instance for reading application name from properties of project
     * @param projectId
     *         project id
     * @return info about application
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     * @throws org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException
     *         if any VFS error occurs
     */
    public Configuration createConfigurationTemplate(String templateName,
                                                     String solutionStackName,
                                                     String sourceApplicationName,
                                                     String sourceTemplateName,
                                                     String environmentId,
                                                     String description,
                                                     List<ConfigurationOption> options,
                                                     VirtualFileSystem vfs,
                                                     String projectId)
            throws AWSException, VirtualFileSystemException, CredentialStoreException {
        return createConfigurationTemplate(detectApplicationName(vfs, projectId), templateName, solutionStackName,
                                           sourceApplicationName, sourceTemplateName, environmentId, description, options);
    }

    /**
     * Create new configuration template. Template is associated with a specified application and are used to deploy
     * different versions of the application with the same configuration settings.
     *
     * @param applicationName
     *         name of the application to associate with new configuration template
     * @param templateName
     *         name of template. This name must be unique per application. Length: 1 - 100 characters.
     * @param solutionStackName
     *         name of amazon solution stack used by this new template , e.g. '64bit Amazon Linux running Tomcat 6'
     * @param sourceApplicationName
     *         source application to copy configuration values to create a new configuration
     * @param sourceTemplateName
     *         source template name
     * @param environmentId
     *         id of the environment used with this configuration template
     * @param description
     *         configuration template description. Length: 0 - 200 characters
     * @param options
     *         Set configuration options. Options specified in this list override options copied from solution stack or
     *         source configuration template.
     * @return info about newly create configuration template
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     */
    public Configuration createConfigurationTemplate(String applicationName,
                                                     String templateName,
                                                     String solutionStackName,
                                                     String sourceApplicationName,
                                                     String sourceTemplateName,
                                                     String environmentId,
                                                     String description,
                                                     List<ConfigurationOption> options)
            throws AWSException, CredentialStoreException {
        try {
            return createConfigurationTemplate(getBeanstalkClient(), applicationName, templateName, solutionStackName,
                                               sourceApplicationName, sourceTemplateName, environmentId, description, options);
        } catch (AmazonClientException e) {
            throw new AWSException(e);
        }
    }

    private Configuration createConfigurationTemplate(AWSElasticBeanstalk beanstalkClient,
                                                      String applicationName,
                                                      String templateName,
                                                      String solutionStackName,
                                                      String sourceApplicationName,
                                                      String sourceTemplateName,
                                                      String environmentId,
                                                      String description,
                                                      List<ConfigurationOption> options) {
        CreateConfigurationTemplateRequest request = new CreateConfigurationTemplateRequest()
                .withApplicationName(applicationName)
                .withTemplateName(templateName)
                .withSolutionStackName(solutionStackName)
                .withEnvironmentId(environmentId)
                .withDescription(description);
        if (!(options == null || options.isEmpty())) {
            for (ConfigurationOption option : options) {
                request.getOptionSettings().add(
                        new ConfigurationOptionSetting(option.getNamespace(), option.getName(), option.getValue()));
            }
        }
        if (sourceApplicationName != null || sourceTemplateName != null) {
            request.setSourceConfiguration(new SourceConfiguration().withApplicationName(sourceApplicationName)
                                                                    .withTemplateName(sourceTemplateName));
        }
        CreateConfigurationTemplateResult result = beanstalkClient.createConfigurationTemplate(request);
        return new ConfigurationImpl.Builder()
                .solutionStackName(result.getSolutionStackName())
                .applicationName(result.getApplicationName())
                .templateName(result.getTemplateName())
                .description(result.getDescription())
                .environmentName(result.getEnvironmentName())
                .deploymentStatus(result.getDeploymentStatus())
                .created(result.getDateCreated())
                .updated(result.getDateUpdated())
                .options(result.getOptionSettings())
                .build();
    }

    /**
     * Update configuration template. Name of current application retrieved from project properties.
     *
     * @param templateName
     *         name of template to delete
     * @param description
     *         new description of configuration template. Length: 0 - 200 characters
     * @param vfs
     *         virtual file system instance for reading application name from properties of project
     * @param projectId
     *         project id
     * @return info about updated configuration template
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     * @throws org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException
     *         if any VFS error occurs
     */
    public Configuration updateConfigurationTemplate(String templateName,
                                                     String description,
                                                     VirtualFileSystem vfs,
                                                     String projectId)
            throws AWSException, VirtualFileSystemException, CredentialStoreException {
        return updateConfigurationTemplate(detectApplicationName(vfs, projectId), templateName, description);
    }

    /**
     * Update configuration template.
     *
     * @param applicationName
     *         name of the application to associate with configuration template
     * @param templateName
     *         name of template to update
     * @param description
     *         new description of configuration template. Length: 0 - 200 characters
     * @return info about updated configuration template
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     */
    public Configuration updateConfigurationTemplate(String applicationName,
                                                     String templateName,
                                                     String description) throws AWSException, CredentialStoreException {
        try {
            return updateConfigurationTemplate(getBeanstalkClient(), applicationName, templateName, description);
        } catch (AmazonClientException e) {
            throw new AWSException(e);
        }
    }

    private Configuration updateConfigurationTemplate(AWSElasticBeanstalk beanstalkClient,
                                                      String applicationName,
                                                      String templateName,
                                                      String description) {
        UpdateConfigurationTemplateResult result = beanstalkClient.updateConfigurationTemplate(
                new UpdateConfigurationTemplateRequest()
                        .withApplicationName(applicationName)
                        .withTemplateName(templateName)
                        .withDescription(description));
        return new ConfigurationImpl.Builder()
                .solutionStackName(result.getSolutionStackName())
                .applicationName(result.getApplicationName())
                .templateName(result.getTemplateName())
                .description(result.getDescription())
                .environmentName(result.getEnvironmentName())
                .deploymentStatus(result.getDeploymentStatus())
                .created(result.getDateCreated())
                .updated(result.getDateUpdated())
                .options(result.getOptionSettings())
                .build();
    }

    /**
     * Delete configuration template. Name of current application retrieved from project properties.
     *
     * @param templateName
     *         name of template to delete
     * @param vfs
     *         virtual file system instance for reading application name from properties of project
     * @param projectId
     *         project id
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     * @throws org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException
     *         if any VFS error occurs
     * @see #createConfigurationTemplate(String, String, String, String, String, String, java.util.List,
     *      org.exoplatform.ide.vfs.server.VirtualFileSystem, String)
     */
    public void deleteConfigurationTemplate(String templateName, VirtualFileSystem vfs, String projectId)
            throws AWSException, VirtualFileSystemException, CredentialStoreException {
        deleteConfigurationTemplate(detectApplicationName(vfs, projectId), templateName);
    }

    /**
     * Delete configuration template.
     *
     * @param applicationName
     *         name of the application to associate with configuration template
     * @param templateName
     *         name of template to delete
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     * @see #createConfigurationTemplate(String, String, String, String, String, String, String, java.util.List)
     */
    public void deleteConfigurationTemplate(String applicationName, String templateName)
            throws AWSException, CredentialStoreException {
        try {
            deleteConfigurationTemplate(getBeanstalkClient(), applicationName, templateName);
        } catch (AmazonClientException e) {
            throw new AWSException(e);
        }
    }

    private void deleteConfigurationTemplate(AWSElasticBeanstalk beanstalkClient,
                                             String applicationName,
                                             String templateName) {
        beanstalkClient.deleteConfigurationTemplate(new DeleteConfigurationTemplateRequest()
                                                            .withApplicationName(applicationName).withTemplateName(templateName));
    }

    //

    /**
     * Create new version of application. Name of application retrieved from project properties
     *
     * @param s3Bucket
     *         optional name of S3 bucket where application version uploaded before deploy to AWS Beanstalk. If this
     *         parameter not specified random name generated and new S3 bucket created.
     * @param s3Key
     *         optional name of S3 key where application version uploaded before deploy to AWS Beanstalk. If this parameter
     *         not specified random name generated and new S3 file created. If file with specified key already exists it
     *         content will be overridden.
     * @param versionLabel
     *         label for identification this version. Length: 1 - 100 characters.
     * @param description
     *         optional description of application version. Length: 0 - 200 characters.
     * @param vfs
     *         virtual file system instance for access to source code and properties of project.
     * @param projectId
     *         project id
     * @param war
     *         URL to pre-build war file. May be present for java applications ONLY
     * @return info about an application version
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     * @throws org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException
     *         if any VFS error occurs
     * @throws java.io.IOException
     *         i/o error, e.g. when try to download pre-build binary file
     */
    public ApplicationVersionInfo createApplicationVersion(String s3Bucket,
                                                           String s3Key,
                                                           String versionLabel,
                                                           String description,
                                                           VirtualFileSystem vfs,
                                                           String projectId,
                                                           URL war)
            throws AWSException, VirtualFileSystemException, IOException, CredentialStoreException {
        String applicationName = detectApplicationName(vfs, projectId);
        // Two possible location for project file(s).
        // 1. Location to binary file. Typically actual for Java applications.
        // 2. Get project from VFS. Typically actual for application that do not need compilation, e.g. PHP applications
        AmazonS3 s3Client = getS3Client();
        try {
            S3Location s3Location = war == null
                                    ? createS3Location(s3Client, applicationName, s3Bucket, s3Key, vfs.exportZip(projectId))
                                    : createS3Location(s3Client, applicationName, s3Bucket, s3Key, war);
            return createApplicationVersion(getBeanstalkClient(), new CreateApplicationVersionRequest()
                    .withApplicationName(applicationName)
                    .withVersionLabel(versionLabel)
                    .withSourceBundle(s3Location)
                    .withDescription(description));
        } catch (AmazonClientException e) {
            throw new AWSException(e);
        }
    }

    private ApplicationVersionInfo createApplicationVersion(AWSElasticBeanstalk beanstalkClient,
                                                            CreateApplicationVersionRequest request) {
        ApplicationVersionDescription awsVersion =
                beanstalkClient.createApplicationVersion(request).getApplicationVersion();
        return new ApplicationVersionInfoImpl.Builder()
                .name(awsVersion.getApplicationName())
                .description(awsVersion.getDescription())
                .versionLabel(awsVersion.getVersionLabel())
                .s3Location(awsVersion.getSourceBundle().getS3Bucket(), awsVersion.getSourceBundle().getS3Key())
                .created(awsVersion.getDateCreated())
                .updated(awsVersion.getDateUpdated())
                .build();
    }

    /**
     * Update application version. Name of application retrieved from project properties.
     *
     * @param versionLabel
     *         label of the version to update
     * @param description
     *         new description of application version. Length: 0 - 200 characters.
     * @param vfs
     *         virtual file system instance for access to properties of project.
     * @param projectId
     *         project id
     * @return application version info
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     * @throws org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException
     *         if any VFS error occurs
     */
    public ApplicationVersionInfo updateApplicationVersion(String versionLabel,
                                                           String description,
                                                           VirtualFileSystem vfs,
                                                           String projectId)
            throws AWSException, VirtualFileSystemException, CredentialStoreException {
        return updateApplicationVersion(detectApplicationName(vfs, projectId), versionLabel, description);
    }

    /**
     * Update application version.
     *
     * @param applicationName
     *         name of application
     * @param versionLabel
     *         label of the version to update
     * @param description
     *         new description of application version. Length: 0 - 200 characters.
     * @return application version info
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     */
    public ApplicationVersionInfo updateApplicationVersion(String applicationName,
                                                           String versionLabel,
                                                           String description)
            throws AWSException, CredentialStoreException {
        try {
            return updateApplicationVersion(getBeanstalkClient(), applicationName, versionLabel, description);
        } catch (AmazonClientException e) {
            throw new AWSException(e);
        }
    }

    private ApplicationVersionInfo updateApplicationVersion(AWSElasticBeanstalk beanstalkClient,
                                                            String applicationName,
                                                            String versionLabel,
                                                            String description) {
        ApplicationVersionDescription awsVersion = beanstalkClient.updateApplicationVersion(
                new UpdateApplicationVersionRequest()
                        .withApplicationName(applicationName)
                        .withVersionLabel(versionLabel)
                        .withDescription(description)).getApplicationVersion();
        return new ApplicationVersionInfoImpl.Builder()
                .name(awsVersion.getApplicationName())
                .description(awsVersion.getDescription())
                .versionLabel(awsVersion.getVersionLabel())
                .s3Location(awsVersion.getSourceBundle().getS3Bucket(), awsVersion.getSourceBundle().getS3Key())
                .created(awsVersion.getDateCreated())
                .updated(awsVersion.getDateUpdated())
                .build();
    }

    /**
     * Delete application version. Name of application retrieved from project properties.
     * <p/>
     * Version cannot be deleted if it is associated with running environment. See {@link #stopEnvironment(String)}.
     *
     * @param versionLabel
     *         label of the version to delete
     * @param deleteS3Bundle
     *         if <code>true</code> also delete version application bundle uploaded to S3 when create version.
     * @param vfs
     *         virtual file system instance for access properties of project.
     * @param projectId
     *         project id
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     * @throws org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException
     *         if any VFS error occurs
     */
    public void deleteApplicationVersion(String versionLabel,
                                         boolean deleteS3Bundle,
                                         VirtualFileSystem vfs,
                                         String projectId)
            throws AWSException, VirtualFileSystemException, CredentialStoreException {
        deleteApplicationVersion(detectApplicationName(vfs, projectId), versionLabel, deleteS3Bundle);
    }

    /**
     * Delete application version.
     * <p/>
     * Version cannot be deleted if it is associated with running environment. See {@link #stopEnvironment(String)}.
     *
     * @param applicationName
     *         name of application
     * @param versionLabel
     *         label of the version to delete
     * @param deleteS3Bundle
     *         if <code>true</code> also delete version application bundle uploaded to S3 when create version.
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     */
    public void deleteApplicationVersion(String applicationName,
                                         String versionLabel,
                                         boolean deleteS3Bundle) throws AWSException, CredentialStoreException {
        try {
            deleteApplicationVersion(getBeanstalkClient(), applicationName, versionLabel, deleteS3Bundle);
        } catch (AmazonClientException e) {
            throw new AWSException(e);
        }
    }

    private void deleteApplicationVersion(AWSElasticBeanstalk beanstalkClient,
                                          String applicationName,
                                          String versionLabel,
                                          boolean deleteS3Bundle) {
        beanstalkClient.deleteApplicationVersion(new DeleteApplicationVersionRequest()
                                                         .withApplicationName(applicationName).withVersionLabel(versionLabel)
                                                         .withDeleteSourceBundle(deleteS3Bundle));
    }

    /**
     * Get application versions. Name of application retrieved from project properties.
     *
     * @param vfs
     *         virtual file system instance for access properties of project.
     * @param projectId
     *         project id
     * @return application version
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     * @throws org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException
     *         if any VFS error occurs
     */
    public List<ApplicationVersionInfo> listApplicationVersions(VirtualFileSystem vfs, String projectId)
            throws AWSException, VirtualFileSystemException, CredentialStoreException {
        return listApplicationVersions(detectApplicationName(vfs, projectId));
    }

    /**
     * Get application versions.
     *
     * @param applicationName
     *         name of application
     * @return application version
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     */
    public List<ApplicationVersionInfo> listApplicationVersions(String applicationName)
            throws AWSException, CredentialStoreException {
        try {
            return listApplicationVersions(getBeanstalkClient(), applicationName);
        } catch (AmazonClientException e) {
            throw new AWSException(e);
        }
    }

    private List<ApplicationVersionInfo> listApplicationVersions(AWSElasticBeanstalk beanstalkClient,
                                                                 String applicationName) {
        List<ApplicationVersionDescription> awsVersions = beanstalkClient.describeApplicationVersions(
                new DescribeApplicationVersionsRequest().withApplicationName(applicationName)).getApplicationVersions();
        List<ApplicationVersionInfo> versions = new ArrayList<ApplicationVersionInfo>(awsVersions.size());
        for (ApplicationVersionDescription awsVersion : awsVersions) {
            versions.add(new ApplicationVersionInfoImpl.Builder()
                                 .name(awsVersion.getApplicationName())
                                 .description(awsVersion.getDescription())
                                 .versionLabel(awsVersion.getVersionLabel())
                                 .s3Location(awsVersion.getSourceBundle().getS3Bucket(), awsVersion.getSourceBundle().getS3Key())
                                 .created(awsVersion.getDateCreated())
                                 .updated(awsVersion.getDateUpdated())
                                 .build()
                        );
        }
        return versions;
    }

    //

    /**
     * Create environment for running version of application. Name of application retrieved from project properties.
     *
     * @param environmentName
     *         name for new environment. This name must be unique within AWS Beanstalk account. Length: 4 -23 characters.
     * @param solutionStackName
     *         name of Amazon solution stack. NOTE: if this parameter is specified <code>templateName</code> must be
     *         <code>null</code>
     * @param templateName
     *         name of template for new environment. NOTE: if this parameter is specified <code>solutionStackName</code> must
     *         be <code>null</code>
     * @param versionLabel
     *         version of application to deploy
     * @param description
     *         optional description for created application environment. Length: 0 - 200 characters.
     * @param vfs
     *         virtual file system instance for access properties of project
     * @param projectId
     *         project id
     * @param options
     *         configuration options. See {@link #listSolutionStackConfigurationOptions(String)}
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     * @throws org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException
     *         if any VirtualFileSystem error occurs
     */
    public EnvironmentInfo createApplicationEnvironment(String environmentName,
                                                        String solutionStackName,
                                                        String templateName,
                                                        String versionLabel,
                                                        String description,
                                                        VirtualFileSystem vfs,
                                                        String projectId,
                                                        List<ConfigurationOption> options)
            throws AWSException, VirtualFileSystemException, CredentialStoreException {
        return createApplicationEnvironment(detectApplicationName(vfs, projectId), environmentName, solutionStackName,
                                            templateName, versionLabel, description, options);
    }

    /**
     * Create environment for running version of application.
     *
     * @param applicationName
     *         name of application
     *         name for new environment. This name must be unique within AWS Beanstalk account. Length: 4 -23 characters.
     * @param environmentName
     *         name for new environment. This name must be unique within AWS Beanstalk account. Length: 4 -23 characters.
     * @param solutionStackName
     *         name of Amazon solution stack. NOTE: if this parameter is specified <code>templateName</code> must be
     *         <code>null</code>
     * @param templateName
     *         name of template for new environment. NOTE: if this parameter is specified <code>solutionStackName</code> must
     *         be <code>null</code>
     * @param versionLabel
     *         version of application to deploy
     * @param description
     *         optional description for created application environment. Length: 0 - 200 characters.
     * @param options
     *         configuration options. See {@link #listSolutionStackConfigurationOptions(String)}
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     */
    public EnvironmentInfo createApplicationEnvironment(String applicationName,
                                                        String environmentName,
                                                        String solutionStackName,
                                                        String templateName,
                                                        String versionLabel,
                                                        String description,
                                                        List<ConfigurationOption> options)
            throws AWSException, CredentialStoreException {
        try {
            return createApplicationEnvironment(getBeanstalkClient(), applicationName, environmentName, solutionStackName,
                                                templateName, versionLabel, description, options);
        } catch (AmazonClientException e) {
            throw new AWSException(e);
        }
    }

    private EnvironmentInfo createApplicationEnvironment(AWSElasticBeanstalk beanstalkClient,
                                                         String applicationName,
                                                         String environmentName,
                                                         String solutionStackName,
                                                         String templateName,
                                                         String versionLabel,
                                                         String description,
                                                         List<ConfigurationOption> options) {
        CreateEnvironmentRequest request = new CreateEnvironmentRequest()
                .withApplicationName(applicationName)
                .withEnvironmentName(environmentName)
                .withSolutionStackName(solutionStackName)
                .withTemplateName(templateName)
                .withVersionLabel(versionLabel)
                .withDescription(description);
        if (!(options == null || options.isEmpty())) {
            for (ConfigurationOption option : options) {
                request.getOptionSettings().add(
                        new ConfigurationOptionSetting(option.getNamespace(), option.getName(), option.getValue()));
            }
        }
        CreateEnvironmentResult result = beanstalkClient.createEnvironment(request);
        return new EnvironmentInfoImpl.Builder()
                .name(result.getEnvironmentName())
                .id(result.getEnvironmentId())
                .applicationName(result.getApplicationName())
                .versionLabel(result.getVersionLabel())
                .solutionStackName(result.getSolutionStackName())
                .templateName(result.getTemplateName())
                .description(result.getDescription())
                .endpointUrl(result.getEndpointURL())
                .cNAME(result.getCNAME())
                .created(result.getDateCreated())
                .updated(result.getDateUpdated())
                .status(result.getStatus())
                .health(result.getHealth())
                .build();
    }

    /**
     * Get info about specified environment.
     *
     * @param id
     *         id of environment
     * @return info about environment
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     */
    public EnvironmentInfo getEnvironmentInfo(String id) throws AWSException, CredentialStoreException {
        try {
            EnvironmentInfo environment = getEnvironmentInfo(getBeanstalkClient(), id);
            if (environment == null) {
                throw new AWSException("Environment '" + id + "' not found. ");
            }
            return environment;
        } catch (AmazonClientException e) {
            throw new AWSException(e);
        }
    }

    private EnvironmentInfo getEnvironmentInfo(AWSElasticBeanstalk beanstalkClient, String id) {
        List<EnvironmentDescription> awsEnvironments = beanstalkClient.describeEnvironments(
                new DescribeEnvironmentsRequest().withEnvironmentIds(id)).getEnvironments();
        if (awsEnvironments.isEmpty()) {
            return null;
        }
        EnvironmentDescription awsEnvironment = awsEnvironments.get(0);
        return new EnvironmentInfoImpl.Builder()
                .name(awsEnvironment.getEnvironmentName())
                .id(awsEnvironment.getEnvironmentId())
                .applicationName(awsEnvironment.getApplicationName())
                .versionLabel(awsEnvironment.getVersionLabel())
                .solutionStackName(awsEnvironment.getSolutionStackName())
                .templateName(awsEnvironment.getTemplateName())
                .description(awsEnvironment.getDescription())
                .endpointUrl(awsEnvironment.getEndpointURL())
                .cNAME(awsEnvironment.getCNAME())
                .created(awsEnvironment.getDateCreated())
                .updated(awsEnvironment.getDateUpdated())
                .status(awsEnvironment.getStatus())
                .health(awsEnvironment.getHealth())
                .build();
    }

    /**
     * Get configuration template. Name of application retrieved from project properties.
     *
     * @param templateName
     *         name of configuration template
     * @param vfs
     *         virtual file system instance for access properties of project
     * @param projectId
     *         project id
     * @return configuration template
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     * @throws org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException
     *         if any VirtualFileSystem error occurs
     */
    public Configuration getConfigurationTemplate(String templateName,
                                                  VirtualFileSystem vfs,
                                                  String projectId)
            throws AWSException, VirtualFileSystemException, CredentialStoreException {
        return getConfigurationTemplate(detectApplicationName(vfs, projectId), templateName);
    }

    /**
     * Get configuration template.
     *
     * @param applicationName
     *         name of application
     * @param templateName
     *         name of configuration template
     * @return environment configuration
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     */
    public Configuration getConfigurationTemplate(String applicationName, String templateName)
            throws AWSException, CredentialStoreException {
        try {
            return getConfigurations(getBeanstalkClient(), applicationName, null, templateName).get(0);
        } catch (AmazonClientException e) {
            throw new AWSException(e);
        }
    }

    /**
     * Get configuration of environment. Name of application retrieved from project properties.
     * List may contains more then one configuration. One is for deployed environment and one for environment that is
     * either in the process of deployment or that failed to deploy.
     *
     * @param environmentName
     *         name of application environment
     * @param vfs
     *         virtual file system instance for access properties of project
     * @param projectId
     *         project id
     * @return environment configuration
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     * @throws org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException
     *         if any VirtualFileSystem error occurs
     */
    public List<Configuration> getEnvironmentConfigurations(String environmentName,
                                                            VirtualFileSystem vfs,
                                                            String projectId)
            throws AWSException, VirtualFileSystemException, CredentialStoreException {
        return getEnvironmentConfigurations(detectApplicationName(vfs, projectId), environmentName);
    }

    /**
     * Get configurations of environment. List may contains more then one configuration. One is for deployed environment
     * and one for environment that is either in the process of deployment or that failed to deploy.
     *
     * @param applicationName
     *         name of application
     * @param environmentName
     *         name of environment
     * @return environment configuration
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     */
    public List<Configuration> getEnvironmentConfigurations(String applicationName, String environmentName)
            throws AWSException, CredentialStoreException {
        try {
            return getConfigurations(getBeanstalkClient(), applicationName, environmentName, null);
        } catch (AmazonClientException e) {
            throw new AWSException(e);
        }
    }

    private List<Configuration> getConfigurations(AWSElasticBeanstalk beanstalkClient,
                                                  String applicationName,
                                                  String environmentName,
                                                  String templateName) {
        List<ConfigurationSettingsDescription> configurations = beanstalkClient.describeConfigurationSettings(
                new DescribeConfigurationSettingsRequest()
                        .withApplicationName(applicationName)
                        .withEnvironmentName(environmentName)
                        .withTemplateName(templateName))
                                                                               .getConfigurationSettings();
        List<Configuration> result = new ArrayList<Configuration>(configurations.size());
        for (ConfigurationSettingsDescription configuration : configurations) {
            result.add(new ConfigurationImpl.Builder()
                               .applicationName(configuration.getApplicationName())
                               .solutionStackName(configuration.getSolutionStackName())
                               .environmentName(configuration.getEnvironmentName())
                               .templateName(configuration.getTemplateName())
                               .description(configuration.getDescription())
                               .deploymentStatus(configuration.getDeploymentStatus())
                               .created(configuration.getDateCreated())
                               .updated(configuration.getDateUpdated())
                               .options(configuration.getOptionSettings())
                               .build());
        }
        return result;
    }

    /**
     * Update specified environment.
     *
     * @param id
     *         id of environment
     * @param description
     *         if specified update description of environment. Length: 0 - 200 characters
     * @param versionLabel
     *         if specified deploy this application version to the environment
     * @param templateName
     *         if specified deploy this configuration template to the environment
     * @param options
     *         environment configuration
     * @return info about environment
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     */
    public EnvironmentInfo updateEnvironment(String id,
                                             String description,
                                             String versionLabel,
                                             String templateName,
                                             List<ConfigurationOption> options)
            throws AWSException, CredentialStoreException {
        try {
            return updateEnvironment(getBeanstalkClient(), id, description, versionLabel, templateName, options);
        } catch (AmazonClientException e) {
            throw new AWSException(e);
        }
    }

    private EnvironmentInfo updateEnvironment(AWSElasticBeanstalk beanstalkClient,
                                              String id,
                                              String description,
                                              String versionLabel,
                                              String templateName,
                                              List<ConfigurationOption> options) {
        UpdateEnvironmentRequest request = new UpdateEnvironmentRequest()
                .withEnvironmentId(id)
                .withDescription(description)
                .withVersionLabel(versionLabel)
                .withTemplateName(templateName);
        if (!(options == null || options.isEmpty())) {
            for (ConfigurationOption option : options) {
                request.getOptionSettings().add(
                        new ConfigurationOptionSetting(option.getNamespace(), option.getName(), option.getValue()));
            }
        }
        UpdateEnvironmentResult result = beanstalkClient.updateEnvironment(request);
        return new EnvironmentInfoImpl.Builder()
                .name(result.getEnvironmentName())
                .id(result.getEnvironmentId())
                .applicationName(result.getApplicationName())
                .versionLabel(result.getVersionLabel())
                .solutionStackName(result.getSolutionStackName())
                .templateName(result.getTemplateName())
                .description(result.getDescription())
                .endpointUrl(result.getEndpointURL())
                .cNAME(result.getCNAME())
                .created(result.getDateCreated())
                .updated(result.getDateUpdated())
                .status(result.getStatus())
                .health(result.getHealth())
                .build();
    }

    /**
     * Re-build specified environment.
     *
     * @param id
     *         name of environment
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     */
    public void rebuildEnvironment(String id) throws AWSException, CredentialStoreException {
        try {
            rebuildEnvironment(getBeanstalkClient(), id);
        } catch (AmazonClientException e) {
            throw new AWSException(e);
        }
    }

    private void rebuildEnvironment(AWSElasticBeanstalk beanstalkClient, String id) {
        beanstalkClient.rebuildEnvironment(new RebuildEnvironmentRequest().withEnvironmentId(id));
    }

    /**
     * Stop specified environment.
     *
     * @param id
     *         name of environment
     * @return info about environment
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     */
    public EnvironmentInfo stopEnvironment(String id) throws AWSException, CredentialStoreException {
        try {
            return stopEnvironment(getBeanstalkClient(), id);
        } catch (AmazonClientException e) {
            throw new AWSException(e);
        }
    }

    private EnvironmentInfo stopEnvironment(AWSElasticBeanstalk beanstalkClient, String id) {
        TerminateEnvironmentResult result = beanstalkClient.terminateEnvironment(
                new TerminateEnvironmentRequest().withEnvironmentId(id));
        return new EnvironmentInfoImpl.Builder()
                .name(result.getEnvironmentName())
                .id(result.getEnvironmentId())
                .applicationName(result.getApplicationName())
                .versionLabel(result.getVersionLabel())
                .solutionStackName(result.getSolutionStackName())
                .templateName(result.getTemplateName())
                .description(result.getDescription())
                .endpointUrl(result.getEndpointURL())
                .cNAME(result.getCNAME())
                .created(result.getDateCreated())
                .updated(result.getDateUpdated())
                .status(result.getStatus())
                .health(result.getHealth())
                .build();
    }

    /**
     * Get list of environment associated with application. Name of application retrieved from project properties.
     *
     * @param vfs
     *         virtual file system instance for access properties of project
     * @param projectId
     *         project id
     * @return list of environments
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     * @throws org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException
     *         if any VirtualFileSystem error occurs
     */
    public List<EnvironmentInfo> listApplicationEnvironments(VirtualFileSystem vfs, String projectId)
            throws AWSException, VirtualFileSystemException, CredentialStoreException {
        return listApplicationEnvironments(detectApplicationName(vfs, projectId));
    }

    /**
     * Get list of environment associated with application.
     *
     * @param applicationName
     *         of application
     * @return list of environments
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     */
    public List<EnvironmentInfo> listApplicationEnvironments(String applicationName)
            throws AWSException, CredentialStoreException {
        try {
            return listApplicationEnvironments(getBeanstalkClient(), applicationName);
        } catch (AmazonClientException e) {
            throw new AWSException(e);
        }
    }

    private List<EnvironmentInfo> listApplicationEnvironments(AWSElasticBeanstalk beanstalkClient,
                                                              String applicationName) {
        List<EnvironmentDescription> awsEnvironments = beanstalkClient.describeEnvironments(
                new DescribeEnvironmentsRequest().withApplicationName(applicationName)).getEnvironments();
        List<EnvironmentInfo> environments = new ArrayList<EnvironmentInfo>(awsEnvironments.size());
        for (EnvironmentDescription awsEnvironment : awsEnvironments) {
            environments.add(new EnvironmentInfoImpl.Builder()
                                     .name(awsEnvironment.getEnvironmentName())
                                     .id(awsEnvironment.getEnvironmentId())
                                     .applicationName(awsEnvironment.getApplicationName())
                                     .versionLabel(awsEnvironment.getVersionLabel())
                                     .solutionStackName(awsEnvironment.getSolutionStackName())
                                     .templateName(awsEnvironment.getTemplateName())
                                     .description(awsEnvironment.getDescription())
                                     .endpointUrl(awsEnvironment.getEndpointURL())
                                     .cNAME(awsEnvironment.getCNAME())
                                     .created(awsEnvironment.getDateCreated())
                                     .updated(awsEnvironment.getDateUpdated())
                                     .status(awsEnvironment.getStatus())
                                     .health(awsEnvironment.getHealth())
                                     .build());
        }
        return environments;
    }

    //

    /**
     * Restart application server associated with specified environment.
     *
     * @param environmentId
     *         id of environment
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     */
    public void restartApplicationServer(String environmentId) throws AWSException, CredentialStoreException {
        try {
            restartApplicationServer(getBeanstalkClient(), environmentId);
        } catch (AmazonClientException e) {
            throw new AWSException(e);
        }
    }

    private void restartApplicationServer(AWSElasticBeanstalk beanstalkClient, String environmentId) {
        beanstalkClient.restartAppServer(new RestartAppServerRequest().withEnvironmentId(environmentId));
    }

    /**
     * Get web servers startup logs for specified environment ID.
     *
     * @param environmentId
     *         EC2 environment ID on which application is started
     * @return map in format: key => instanceId, value => log url
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     */
    public List<InstanceLog> getEnvironmentLogs(String environmentId) throws AWSException, CredentialStoreException {
        try {
            return getEnvironmentLogs(getBeanstalkClient(), environmentId);
        } catch (AmazonClientException e) {
            throw new AWSException(e);
        }
    }

    private List<InstanceLog> getEnvironmentLogs(AWSElasticBeanstalk beanstalkClient, String environmentId) {
        beanstalkClient.requestEnvironmentInfo(
                new RequestEnvironmentInfoRequest()
                        .withEnvironmentId(environmentId)
                        .withInfoType("tail")
                                              );

        List<EnvironmentInfoDescription> envList = beanstalkClient.retrieveEnvironmentInfo(
                new RetrieveEnvironmentInfoRequest()
                        .withEnvironmentId(environmentId)
                        .withInfoType("tail")
                                                                                          ).getEnvironmentInfo();

        Map<String, EnvironmentInfoDescription> distinctEnvList = new HashMap<String, EnvironmentInfoDescription>();

        for (EnvironmentInfoDescription description : envList) {
            EnvironmentInfoDescription previousEnvDesc = distinctEnvList.get(description.getEc2InstanceId());
            if (previousEnvDesc == null || previousEnvDesc.getSampleTimestamp().before(description.getSampleTimestamp())) {
                distinctEnvList.put(description.getEc2InstanceId(), description);
            }
        }

        List<InstanceLog> result = new ArrayList<InstanceLog>(distinctEnvList.size());

        for (EnvironmentInfoDescription description : distinctEnvList.values()) {
            result.add(new InstanceLogImpl(description.getEc2InstanceId(), description.getMessage()));
        }

        return result;
    }

    //

    private S3Location createS3Location(AmazonS3 s3Client,
                                        String applicationName,
                                        String s3Bucket,
                                        String s3Key,
                                        URL url) throws IOException {
        URLConnection conn = null;
        try {
            conn = url.openConnection();
            return createS3Location(s3Client, applicationName, s3Bucket, s3Key, conn.getInputStream(),
                                    conn.getContentLength());
        } finally {
            if (conn != null) {
                if ("http".equals(url.getProtocol()) || "https".equals(url.getProtocol())) {
                    ((HttpURLConnection)conn).disconnect();
                }
            }
        }
    }

    private S3Location createS3Location(AmazonS3 s3Client,
                                        String applicationName,
                                        String s3Bucket,
                                        String s3Key,
                                        ContentStream file) throws IOException {
        return createS3Location(s3Client, applicationName, s3Bucket, s3Key, file.getStream(), file.getLength());
    }

    private S3Location createS3Location(AmazonS3 s3Client,
                                        String applicationName,
                                        String s3Bucket,
                                        String s3Key,
                                        InputStream stream,
                                        long length) throws IOException {
        // new S3 bucket will be created
        if (s3Bucket == null || s3Bucket.isEmpty()) {
            s3Bucket = NameGenerator.generate(applicationName + '-', 16);
        }

        // new S3 file will be created
        if (s3Key == null || s3Key.isEmpty()) {
            s3Key = NameGenerator.generate("app-", 16);
        }

        if (!s3Client.doesBucketExist(s3Bucket)) {
            s3Client.createBucket(s3Bucket);
        }
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            if (length != -1) {
                metadata.setContentLength(length);
            }
            s3Client.putObject(s3Bucket, s3Key, stream, metadata);
            return new S3Location(s3Bucket, s3Key);
        } finally {
            stream.close();
        }
    }

    private void writeApplicationName(VirtualFileSystem vfs, String projectId, String applicationName)
            throws VirtualFileSystemException {
        Property p = new PropertyImpl("aws-application", applicationName);
        List<Property> properties = new ArrayList<Property>(1);
        properties.add(p);
        vfs.updateItem(projectId, properties, null);
    }

    private String detectApplicationName(VirtualFileSystem vfs, String projectId) throws VirtualFileSystemException {
        String applicationName = null;
        if (vfs != null && projectId != null) {
            Item item = vfs.getItem(projectId, false, PropertyFilter.valueOf("aws-application"));
            applicationName = item.getPropertyValue("aws-application");
        }
        if (applicationName == null || applicationName.isEmpty()) {
            throw new RuntimeException(
                    "Not an Amazon Beanstalk application. Please select root folder of Amazon Beanstalk project. ");
        }
        return applicationName;
    }
}
