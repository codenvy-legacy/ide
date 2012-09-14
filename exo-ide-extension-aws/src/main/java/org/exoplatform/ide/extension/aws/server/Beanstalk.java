/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.aws.server;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalk;
import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalkClient;
import com.amazonaws.services.elasticbeanstalk.model.ApplicationDescription;
import com.amazonaws.services.elasticbeanstalk.model.ApplicationVersionDescription;
import com.amazonaws.services.elasticbeanstalk.model.ConfigurationOptionDescription;
import com.amazonaws.services.elasticbeanstalk.model.ConfigurationOptionSetting;
import com.amazonaws.services.elasticbeanstalk.model.CreateApplicationRequest;
import com.amazonaws.services.elasticbeanstalk.model.CreateApplicationVersionRequest;
import com.amazonaws.services.elasticbeanstalk.model.CreateEnvironmentRequest;
import com.amazonaws.services.elasticbeanstalk.model.CreateEnvironmentResult;
import com.amazonaws.services.elasticbeanstalk.model.DeleteApplicationRequest;
import com.amazonaws.services.elasticbeanstalk.model.DeleteApplicationVersionRequest;
import com.amazonaws.services.elasticbeanstalk.model.DescribeApplicationVersionsRequest;
import com.amazonaws.services.elasticbeanstalk.model.DescribeApplicationsRequest;
import com.amazonaws.services.elasticbeanstalk.model.DescribeConfigurationOptionsRequest;
import com.amazonaws.services.elasticbeanstalk.model.DescribeEnvironmentsRequest;
import com.amazonaws.services.elasticbeanstalk.model.EnvironmentDescription;
import com.amazonaws.services.elasticbeanstalk.model.OptionRestrictionRegex;
import com.amazonaws.services.elasticbeanstalk.model.S3Location;
import com.amazonaws.services.elasticbeanstalk.model.SolutionStackDescription;
import com.amazonaws.services.elasticbeanstalk.model.TerminateEnvironmentRequest;
import com.amazonaws.services.elasticbeanstalk.model.TerminateEnvironmentResult;
import com.amazonaws.services.elasticbeanstalk.model.UpdateApplicationRequest;
import com.amazonaws.services.elasticbeanstalk.model.UpdateApplicationVersionRequest;
import com.amazonaws.services.elasticbeanstalk.model.UpdateEnvironmentRequest;
import com.amazonaws.services.elasticbeanstalk.model.UpdateEnvironmentResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.exoplatform.ide.commons.NameGenerator;
import org.exoplatform.ide.extension.aws.shared.ApplicationInfo;
import org.exoplatform.ide.extension.aws.shared.ApplicationVersionInfo;
import org.exoplatform.ide.extension.aws.shared.ConfigurationOption;
import org.exoplatform.ide.extension.aws.shared.ConfigurationOptionInfo;
import org.exoplatform.ide.extension.aws.shared.EnvironmentInfo;
import org.exoplatform.ide.extension.aws.shared.SolutionStack;
import org.exoplatform.ide.vfs.server.ContentStream;
import org.exoplatform.ide.vfs.server.ConvertibleProperty;
import org.exoplatform.ide.vfs.server.PropertyFilter;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: Beanstalk.java Aug 23, 2012
 */
public class Beanstalk
{
   private static final Log LOG = ExoLogger.getLogger(Beanstalk.class);

   private final AWSAuthenticator authenticator;

   public Beanstalk(AWSAuthenticator authenticator)
   {
      this.authenticator = authenticator;
   }

   //

   /**
    * Login AWS Beanstalk API. Specified access and secret keys stored for next usage by current user.
    *
    * @param accessKey
    *    AWS access key
    * @param secret
    *    AWS secret key
    * @throws AWSException
    *    if any error occurs when attempt to login to Amazon server
    */
   public void login(String accessKey, String secret) throws AWSException
   {
      authenticator.login(accessKey, secret);
   }

   /**
    * Remove access and secret keys previously saved for current user. User will be not able to use this class any more
    * before next login.
    *
    * @throws AWSException
    */
   public void logout() throws AWSException
   {
      authenticator.logout();
   }

   //

   /**
    * List of available solutions stacks.
    *
    * @return list of available solutions stacks
    *         if any error occurs when make request to Amazon API
    */
   public List<SolutionStack> listAvailableSolutionStacks() throws AWSException
   {
      AWSElasticBeanstalk beanstalkClient = getBeanstalkClient();
      try
      {
         return listAvailableSolutionStacks(beanstalkClient);
      }
      catch (AmazonClientException e)
      {
         throw new AWSException(e);
      }
      finally
      {
         beanstalkClient.shutdown();
      }
   }

   private List<SolutionStack> listAvailableSolutionStacks(AWSElasticBeanstalk beanstalkClient)
   {
      List<SolutionStackDescription> awsStacks = beanstalkClient.listAvailableSolutionStacks().getSolutionStackDetails();
      List<SolutionStack> stacks = new ArrayList<SolutionStack>(awsStacks.size());
      for (SolutionStackDescription awsStack : awsStacks)
      {
         stacks.add(new SolutionStackImpl(awsStack.getSolutionStackName(), awsStack.getPermittedFileTypes()));
      }
      return stacks;
   }

   /**
    * Get all possible configuration options of specified solution stack.
    *
    * @param solutionStackName
    *    name of solution stack
    * @return list of configuration options
    * @throws AWSException
    *    if any error occurs when make request to Amazon API
    */
   public List<ConfigurationOptionInfo> listSolutionStackConfigurationOptions(String solutionStackName)
      throws AWSException
   {
      AWSElasticBeanstalk beanstalkClient = getBeanstalkClient();
      try
      {
         return listSolutionStackConfigurationOptions(beanstalkClient,
            new DescribeConfigurationOptionsRequest().withSolutionStackName(solutionStackName));
      }
      catch (AmazonClientException e)
      {
         throw new AWSException(e);
      }
      finally
      {
         beanstalkClient.shutdown();
      }
   }

   private List<ConfigurationOptionInfo> listSolutionStackConfigurationOptions(
      AWSElasticBeanstalk beanstalkClient, DescribeConfigurationOptionsRequest request)
   {
      List<ConfigurationOptionDescription> awsOptions = beanstalkClient.describeConfigurationOptions(request)
         .getOptions();
      List<ConfigurationOptionInfo> options = new ArrayList<ConfigurationOptionInfo>(awsOptions.size());
      for (ConfigurationOptionDescription awsOption : awsOptions)
      {
         ConfigurationOptionInfoImpl.Builder builder = new ConfigurationOptionInfoImpl.Builder()
            .name(awsOption.getName())
            .namespace(awsOption.getNamespace())
            .defaultValue(awsOption.getDefaultValue())
            .changeSeverity(awsOption.getChangeSeverity())
            .userDefined(awsOption.isUserDefined())
            .valueType(awsOption.getValueType())
            .valueOptions(awsOption.getValueOptions())
            .minValue(awsOption.getMinValue())
            .maxValue(awsOption.getMaxValue())
            .maxLength(awsOption.getMaxLength());
         OptionRestrictionRegex regex = awsOption.getRegex();
         if (regex != null)
         {
            builder.optionRestriction(regex.getLabel(), regex.getPattern());
         }
         options.add(builder.build());
      }
      return options;
   }

   //

   /**
    * Create new AWS Beanstalk application. New version of application created. This version got name
    * 'initial version'.
    *
    * @param name
    *    application name. This name must be unique within AWS Beanstalk account. Length: 1-100 characters
    * @param description
    *    optional description of application. Length: 0 - 200 characters
    * @param s3Bucket
    *    optional name of S3 bucket where initial version of application uploaded before deploy to AWS Beanstalk. If
    *    this parameter not specified random name generated and new S3 bucket created
    * @param s3Key
    *    optional name of S3 key where initial version of  application uploaded before deploy to AWS Beanstalk. If this
    *    parameter not specified random name generated and new S3 file created. If file with specified key already
    *    exists it content will be overridden
    * @param vfs
    *    virtual file system instance for access to source code and properties of project. Some info may be stored in
    *    properties of project after creation an application
    * @param projectId
    *    project id
    * @param war
    *    URL to pre-build war file. May be present for java applications ONLY
    * @return info about newly created application
    * @throws AWSException
    *    if any error occurs when make request to Amazon API
    * @throws VirtualFileSystemException
    *    if any VFS error occurs
    * @throws IOException
    *    i/o error, e.g. when try to download pre-build binary file
    */
   public ApplicationInfo createApplication(String name,
                                            String description,
                                            String s3Bucket,
                                            String s3Key,
                                            VirtualFileSystem vfs,
                                            String projectId,
                                            URL war) throws AWSException, VirtualFileSystemException, IOException
   {
      if (name == null || name.isEmpty())
      {
         throw new IllegalArgumentException("Application name required. ");
      }
      if (vfs == null || projectId == null)
      {
         throw new IllegalArgumentException("Project directory required. ");
      }

      // Be sure project is accessible before start creation an application.
      // VirtualFileSystemException thrown if something wrong.
      vfs.getItem(projectId, PropertyFilter.NONE_FILTER);

      AWSElasticBeanstalk beanstalkClient = getBeanstalkClient();
      AmazonS3 s3Client = getS3Client();
      try
      {
         return createApplication(beanstalkClient, s3Client, name, description, s3Bucket, s3Key, vfs, projectId, war);
      }
      catch (AmazonClientException e)
      {
         throw new AWSException(e);
      }
      finally
      {
         beanstalkClient.shutdown();
      }
   }

   private ApplicationInfo createApplication(AWSElasticBeanstalk beanstalkClient,
                                             AmazonS3 s3Client,
                                             String name,
                                             String description,
                                             String s3Bucket,
                                             String s3Key,
                                             VirtualFileSystem vfs,
                                             String projectId,
                                             URL war) throws VirtualFileSystemException, IOException
   {
      beanstalkClient.createApplication(new CreateApplicationRequest().withApplicationName(name)
         .withDescription(description));
      S3Location s3Location = war == null
         ? createS3Location(s3Client, name, s3Bucket, s3Key, vfs.exportZip(projectId))
         : createS3Location(s3Client, name, s3Bucket, s3Key, war);
      beanstalkClient.createApplicationVersion(new CreateApplicationVersionRequest()
         .withApplicationName(name)
         .withVersionLabel("initial version")
         .withSourceBundle(s3Location)
         .withDescription("Initial version of application " + name));
      writeApplicationName(vfs, projectId, name);
      return getApplicationInfo(beanstalkClient, name);
   }

   /**
    * Get info about AWS Beanstalk application. Name of application retrieved from project properties.
    *
    * @param vfs
    *    virtual file system instance for reading project name from properties of project
    * @param projectId
    *    project id
    * @return info about application
    * @throws AWSException
    *    if any error occurs when make request to Amazon API
    * @throws VirtualFileSystemException
    *    if any VFS error occurs
    */
   public ApplicationInfo getApplicationInfo(VirtualFileSystem vfs, String projectId) throws AWSException,
      VirtualFileSystemException
   {
      return getApplicationInfo(detectApplicationName(vfs, projectId));
   }

   /**
    * Get info about AWS Beanstalk application.
    *
    * @param name
    *    name of application
    * @return info about application
    * @throws AWSException
    *    if any error occurs when make request to Amazon API
    */
   public ApplicationInfo getApplicationInfo(String name) throws AWSException
   {
      AWSElasticBeanstalk beanstalkClient = getBeanstalkClient();
      try
      {
         ApplicationInfo application = getApplicationInfo(beanstalkClient, name);
         if (application == null)
         {
            throw new AWSException("Application '" + name + "' not found. ");
         }
         return application;
      }
      catch (AmazonClientException e)
      {
         throw new AWSException(e);
      }
      finally
      {
         beanstalkClient.shutdown();
      }
   }

   private ApplicationInfo getApplicationInfo(AWSElasticBeanstalk beanstalkClient, String name)
   {
      List<ApplicationDescription> awsApplications = beanstalkClient.describeApplications(
         new DescribeApplicationsRequest().withApplicationNames(name)).getApplications();
      if (awsApplications.isEmpty())
      {
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
    *    virtual file system instance for reading project name from properties of project
    * @param projectId
    *    project id
    * @return info about application
    * @throws AWSException
    *    if any error occurs when make request to Amazon API
    * @throws VirtualFileSystemException
    *    if any VFS error occurs
    */
   public ApplicationInfo updateApplication(VirtualFileSystem vfs, String projectId, String description)
      throws AWSException, VirtualFileSystemException
   {
      return updateApplication(detectApplicationName(vfs, projectId), description);
   }

   /**
    * Update AWS Beanstalk application.
    *
    * @param name
    *    name of application
    * @param description
    *    new application description
    * @return info about application
    * @throws AWSException
    *    if any error occurs when make request to Amazon API
    */
   public ApplicationInfo updateApplication(String name, String description) throws AWSException
   {
      AWSElasticBeanstalk beanstalkClient = getBeanstalkClient();
      try
      {
         return updateApplication(beanstalkClient, name, description);
      }
      catch (AmazonClientException e)
      {
         throw new AWSException(e);
      }
      finally
      {
         beanstalkClient.shutdown();
      }
   }

   private ApplicationInfo updateApplication(AWSElasticBeanstalk beanstalkClient, String name, String description)
   {
      ApplicationDescription awsApplication = beanstalkClient.updateApplication(new UpdateApplicationRequest()
         .withApplicationName(name).withDescription(description)).getApplication();
      return new ApplicationInfoImpl(
         awsApplication.getApplicationName(),
         awsApplication.getDescription(),
         awsApplication.getDateCreated(),
         awsApplication.getDateUpdated(),
         awsApplication.getVersions(),
         awsApplication.getConfigurationTemplates());
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
    *    virtual file system instance for reading project name from properties of project
    * @param projectId
    *    project id
    * @throws AWSException
    *    if any error occurs when make request to Amazon API
    * @throws VirtualFileSystemException
    *    if any VFS error occurs
    */
   public void deleteApplication(VirtualFileSystem vfs, String projectId) throws AWSException,
      VirtualFileSystemException
   {
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
    * @param name
    *    of application to delete
    * @throws AWSException
    *    if any error occurs when make request to Amazon API
    */
   public void deleteApplication(String name) throws AWSException
   {
      AWSElasticBeanstalk beanstalkClient = getBeanstalkClient();
      try
      {
         deleteApplication(beanstalkClient, name);
      }
      catch (AmazonClientException e)
      {
         throw new AWSException(e);
      }
      finally
      {
         beanstalkClient.shutdown();
      }
   }

   private void deleteApplication(AWSElasticBeanstalk beanstalkClient, String name)
   {
      beanstalkClient.deleteApplication(new DeleteApplicationRequest(name));
   }

   /**
    * Get existing applications.
    *
    * @return list of applications
    * @throws AWSException
    *    if any error occurs when make request to Amazon API
    */
   public List<ApplicationInfo> listApplications() throws AWSException
   {
      AWSElasticBeanstalk beanstalkClient = getBeanstalkClient();
      try
      {
         return listApplications(beanstalkClient);
      }
      catch (AmazonClientException e)
      {
         throw new AWSException(e);
      }
      finally
      {
         beanstalkClient.shutdown();
      }
   }

   private List<ApplicationInfo> listApplications(AWSElasticBeanstalk beanstalkClient)
   {
      List<ApplicationDescription> awsApplications = beanstalkClient.describeApplications().getApplications();
      List<ApplicationInfo> applications = new ArrayList<ApplicationInfo>(awsApplications.size());
      for (ApplicationDescription awsApplication : awsApplications)
      {
         applications.add(
            new ApplicationInfoImpl(
               awsApplication.getApplicationName(),
               awsApplication.getDescription(),
               awsApplication.getDateCreated(),
               awsApplication.getDateUpdated(),
               awsApplication.getVersions(),
               awsApplication.getConfigurationTemplates())
         );
      }
      return applications;
   }

   //

   /**
    * Create new version of application. Name of application retrieved from project properties
    *
    * @param s3Bucket
    *    optional name of S3 bucket where application version uploaded before deploy to AWS Beanstalk. If this
    *    parameter not specified random name generated and new S3 bucket created.
    * @param s3Key
    *    optional name of S3 key where application version uploaded before deploy to AWS Beanstalk. If this parameter
    *    not specified random name generated and new S3 file created. If file with specified key already exists it
    *    content will be overridden.
    * @param versionLabel
    *    label for identification this version. Length: 1 - 100 characters.
    * @param description
    *    optional description of application version. Length: 0 - 200 characters.
    * @param vfs
    *    virtual file system instance for access to source code and properties of project.
    * @param projectId
    *    project id
    * @param war
    *    URL to pre-build war file. May be present for java applications ONLY
    * @return info about an application version
    * @throws AWSException
    *    if any error occurs when make request to Amazon API
    * @throws VirtualFileSystemException
    *    if any VFS error occurs
    * @throws IOException
    *    i/o error, e.g. when try to download pre-build binary file
    */
   public ApplicationVersionInfo createApplicationVersion(String s3Bucket,
                                                          String s3Key,
                                                          String versionLabel,
                                                          String description,
                                                          VirtualFileSystem vfs,
                                                          String projectId,
                                                          URL war)
      throws AWSException, VirtualFileSystemException, IOException
   {
      String name = detectApplicationName(vfs, projectId);
      // Two possible location for project file(s).
      // 1. Location to binary file. Typically actual for Java applications.
      // 2. Get project from VFS. Typically actual for application that do not need compilation, e.g. PHP applications
      AWSElasticBeanstalk beanstalkClient = getBeanstalkClient();
      AmazonS3 s3Client = getS3Client();
      try
      {
         S3Location s3Location = war == null
            ? createS3Location(s3Client, name, s3Bucket, s3Key, vfs.exportZip(projectId))
            : createS3Location(s3Client, name, s3Bucket, s3Key, war);
         return createApplicationVersion(beanstalkClient, new CreateApplicationVersionRequest().withApplicationName(name).
            withVersionLabel(versionLabel).withSourceBundle(s3Location).withDescription(description));
      }
      catch (AmazonClientException e)
      {
         throw new AWSException(e);
      }
      finally
      {
         beanstalkClient.shutdown();
      }
   }

   private ApplicationVersionInfo createApplicationVersion(AWSElasticBeanstalk beanstalkClient,
                                                           CreateApplicationVersionRequest request)
   {
      ApplicationVersionDescription awsVersion =
         beanstalkClient.createApplicationVersion(request).getApplicationVersion();
      return new ApplicationVersionInfoImpl.Builder()
         .name(awsVersion.getApplicationName())
         .description(awsVersion.getDescription())
         .versionLabel(awsVersion.getVersionLabel())
         .s3Location(awsVersion.getSourceBundle().getS3Bucket(), awsVersion.getSourceBundle().getS3Key())
         .created(awsVersion.getDateCreated())
         .updated(awsVersion.getDateUpdated()).build();
   }

   /**
    * Update application version. Name of application retrieved from project properties.
    *
    * @param versionLabel
    *    label of the version to update
    * @param description
    *    new description of application version. Length: 0 - 200 characters.
    * @param vfs
    *    virtual file system instance for access to properties of project.
    * @param projectId
    *    project id
    * @return application version info
    * @throws AWSException
    *    if any error occurs when make request to Amazon API
    * @throws VirtualFileSystemException
    *    if any VFS error occurs
    */
   public ApplicationVersionInfo updateApplicationVersion(String versionLabel,
                                                          String description,
                                                          VirtualFileSystem vfs,
                                                          String projectId)
      throws AWSException, VirtualFileSystemException
   {
      return updateApplicationVersion(detectApplicationName(vfs, projectId), versionLabel, description);
   }

   /**
    * Update application version.
    *
    * @param name
    *    name of application
    * @param versionLabel
    *    label of the version to update
    * @param description
    *    new description of application version. Length: 0 - 200 characters.
    * @return application version info
    * @throws AWSException
    *    if any error occurs when make request to Amazon API
    */
   public ApplicationVersionInfo updateApplicationVersion(String name, String versionLabel, String description)
      throws AWSException
   {
      AWSElasticBeanstalk beanstalkClient = getBeanstalkClient();
      try
      {
         return updateApplicationVersion(beanstalkClient, name, versionLabel, description);
      }
      catch (AmazonClientException e)
      {
         throw new AWSException(e);
      }
      finally
      {
         beanstalkClient.shutdown();
      }
   }

   private ApplicationVersionInfo updateApplicationVersion(AWSElasticBeanstalk beanstalkClient,
                                                           String name,
                                                           String versionLabel,
                                                           String description)
   {
      ApplicationVersionDescription awsVersion = beanstalkClient.updateApplicationVersion(
         new UpdateApplicationVersionRequest()
            .withApplicationName(name)
            .withVersionLabel(versionLabel)
            .withDescription(description)).getApplicationVersion();
      return new ApplicationVersionInfoImpl.Builder()
         .name(awsVersion.getApplicationName())
         .description(awsVersion.getDescription())
         .versionLabel(awsVersion.getVersionLabel())
         .s3Location(awsVersion.getSourceBundle().getS3Bucket(), awsVersion.getSourceBundle().getS3Key())
         .created(awsVersion.getDateCreated())
         .updated(awsVersion.getDateUpdated()).build();
   }

   /**
    * Delete application version. Name of application retrieved from project properties.
    * <p/>
    * Version cannot be deleted if it is associated with running environment. See {@link #stopEnvironment(String)}.
    *
    * @param versionLabel
    *    label of the version to delete
    * @param deleteS3Bundle
    *    if <code>true</code> also delete version application bundle uploaded to S3 when create version.
    * @param vfs
    *    virtual file system instance for access properties of project.
    * @param projectId
    *    project id
    * @throws AWSException
    *    if any error occurs when make request to Amazon API
    * @throws VirtualFileSystemException
    *    if any VFS error occurs
    */
   public void deleteApplicationVersion(String versionLabel,
                                        boolean deleteS3Bundle,
                                        VirtualFileSystem vfs,
                                        String projectId) throws AWSException, VirtualFileSystemException
   {
      deleteApplicationVersion(detectApplicationName(vfs, projectId), versionLabel, deleteS3Bundle);
   }

   /**
    * Delete application version.
    * <p/>
    * Version cannot be deleted if it is associated with running environment. See {@link #stopEnvironment(String)}.
    *
    * @param name
    *    name of application
    * @param versionLabel
    *    label of the version to delete
    * @param deleteS3Bundle
    *    if <code>true</code> also delete version application bundle uploaded to S3 when create version.
    * @throws AWSException
    *    if any error occurs when make request to Amazon API
    */
   public void deleteApplicationVersion(String name, String versionLabel, boolean deleteS3Bundle) throws AWSException
   {
      AWSElasticBeanstalk beanstalkClient = getBeanstalkClient();
      try
      {
         deleteApplicationVersion(beanstalkClient, name, versionLabel, deleteS3Bundle);
      }
      catch (AmazonClientException e)
      {
         throw new AWSException(e);
      }
      finally
      {
         beanstalkClient.shutdown();
      }
   }

   private void deleteApplicationVersion(AWSElasticBeanstalk beanstalkClient,
                                         String name,
                                         String versionLabel,
                                         boolean deleteS3Bundle)
   {
      beanstalkClient.deleteApplicationVersion(new DeleteApplicationVersionRequest()
         .withApplicationName(name).withVersionLabel(versionLabel).withDeleteSourceBundle(deleteS3Bundle));
   }

   /**
    * Get application versions. Name of application retrieved from project properties.
    *
    * @param vfs
    *    virtual file system instance for access properties of project.
    * @param projectId
    *    project id
    * @return application version
    * @throws AWSException
    *    if any error occurs when make request to Amazon API
    * @throws VirtualFileSystemException
    *    if any VFS error occurs
    */
   public List<ApplicationVersionInfo> listApplicationVersions(VirtualFileSystem vfs, String projectId)
      throws AWSException, VirtualFileSystemException
   {
      return listApplicationVersions(detectApplicationName(vfs, projectId));
   }

   /**
    * Get application versions.
    *
    * @param name
    *    name of application
    * @return application version
    * @throws AWSException
    *    if any error occurs when make request to Amazon API
    */
   public List<ApplicationVersionInfo> listApplicationVersions(String name) throws AWSException
   {
      AWSElasticBeanstalk beanstalkClient = getBeanstalkClient();
      try
      {
         return listApplicationVersions(beanstalkClient, name);
      }
      catch (AmazonClientException e)
      {
         throw new AWSException(e);
      }
      finally
      {
         beanstalkClient.shutdown();
      }
   }

   private List<ApplicationVersionInfo> listApplicationVersions(AWSElasticBeanstalk beanstalkClient, String name)
   {
      List<ApplicationVersionDescription> awsVersions = beanstalkClient.describeApplicationVersions(
         new DescribeApplicationVersionsRequest().withApplicationName(name)).getApplicationVersions();
      List<ApplicationVersionInfo> versions = new ArrayList<ApplicationVersionInfo>(awsVersions.size());
      for (ApplicationVersionDescription awsVersion : awsVersions)
      {
         versions.add(new ApplicationVersionInfoImpl.Builder()
            .name(awsVersion.getApplicationName())
            .description(awsVersion.getDescription())
            .versionLabel(awsVersion.getVersionLabel())
            .s3Location(awsVersion.getSourceBundle().getS3Bucket(), awsVersion.getSourceBundle().getS3Key())
            .created(awsVersion.getDateCreated())
            .updated(awsVersion.getDateUpdated()).build()
         );
      }
      return versions;
   }

   //

   /**
    * Create environment for running version of application. Name of application retrieved from project properties.
    *
    * @param environmentName
    *    name for new environment. This name must be unique within AWS Beanstalk account. Length: 4 -23 characters.
    * @param solutionStackName
    *    name of Amazon solution stack. NOTE: if this parameter is specified <code>templateName</code> must be
    *    <code>null</code>
    * @param templateName
    *    name of template for new environment. NOTE: if this parameter is specified <code>solutionStackName</code> must
    *    be <code>null</code>
    * @param versionLabel
    *    version of application to deploy
    * @param description
    *    optional description for created application environment. Length: 0 - 200 characters.
    * @param vfs
    *    virtual file system instance for access properties of project
    * @param projectId
    *    project id
    * @param options
    *    configuration options. See {@link #listSolutionStackConfigurationOptions(String)}
    * @throws AWSException
    *    if any error occurs when make request to Amazon API
    * @throws VirtualFileSystemException
    *    if any VirtualFileSystem error occurs
    */
   public EnvironmentInfo createApplicationEnvironment(String environmentName,
                                                       String solutionStackName,
                                                       String templateName,
                                                       String versionLabel,
                                                       String description,
                                                       VirtualFileSystem vfs,
                                                       String projectId,
                                                       List<ConfigurationOption> options)
      throws AWSException, VirtualFileSystemException
   {
      return createApplicationEnvironment(detectApplicationName(vfs, projectId), environmentName, solutionStackName,
         templateName, versionLabel, description, options);
   }

   /**
    * Create environment for running version of application.
    *
    * @param name
    *    name of application
    *    name for new environment. This name must be unique within AWS Beanstalk account. Length: 4 -23 characters.
    * @param environmentName
    *    name for new environment. This name must be unique within AWS Beanstalk account. Length: 4 -23 characters.
    * @param solutionStackName
    *    name of Amazon solution stack. NOTE: if this parameter is specified <code>templateName</code> must be
    *    <code>null</code>
    * @param templateName
    *    name of template for new environment. NOTE: if this parameter is specified <code>solutionStackName</code> must
    *    be <code>null</code>
    * @param versionLabel
    *    version of application to deploy
    * @param description
    *    optional description for created application environment. Length: 0 - 200 characters.
    * @param options
    *    configuration options. See {@link #listSolutionStackConfigurationOptions(String)}
    * @throws AWSException
    *    if any error occurs when make request to Amazon API
    */
   public EnvironmentInfo createApplicationEnvironment(String name,
                                                       String environmentName,
                                                       String solutionStackName,
                                                       String templateName,
                                                       String versionLabel,
                                                       String description,
                                                       List<ConfigurationOption> options) throws AWSException
   {
      AWSElasticBeanstalk beanstalkClient = getBeanstalkClient();
      try
      {
         return createApplicationEnvironment(beanstalkClient, name, environmentName, solutionStackName, templateName,
            versionLabel, description, options);
      }
      catch (AmazonClientException e)
      {
         throw new AWSException(e);
      }
      finally
      {
         beanstalkClient.shutdown();
      }
   }

   private EnvironmentInfo createApplicationEnvironment(AWSElasticBeanstalk beanstalkClient,
                                                        String name,
                                                        String environmentName,
                                                        String solutionStackName,
                                                        String templateName,
                                                        String versionLabel,
                                                        String description,
                                                        List<ConfigurationOption> options)
   {
      CreateEnvironmentRequest request = new CreateEnvironmentRequest()
         .withApplicationName(name)
         .withEnvironmentName(environmentName)
         .withSolutionStackName(solutionStackName)
         .withTemplateName(templateName)
         .withVersionLabel(versionLabel)
         .withDescription(description);
      if (!(options == null || options.isEmpty()))
      {
         for (ConfigurationOption option : options)
         {
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
    *    id of environment
    * @return info about environment
    * @throws AWSException
    *    if any error occurs when make request to Amazon API
    */
   public EnvironmentInfo getEnvironmentInfo(String id) throws AWSException
   {
      AWSElasticBeanstalk beanstalkClient = getBeanstalkClient();
      try
      {
         EnvironmentInfo environment = getEnvironmentInfo(beanstalkClient, id);
         if (environment == null)
         {
            throw new AWSException("Environment '" + id + "' not found. ");
         }
         return environment;
      }
      catch (AmazonClientException e)
      {
         throw new AWSException(e);
      }
      finally
      {
         beanstalkClient.shutdown();
      }
   }

   private EnvironmentInfo getEnvironmentInfo(AWSElasticBeanstalk beanstalkClient, String id)
   {
      List<EnvironmentDescription> awsEnvironments = beanstalkClient.describeEnvironments(
         new DescribeEnvironmentsRequest().withEnvironmentIds(id)).getEnvironments();
      if (awsEnvironments.isEmpty())
      {
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
    * Update specified environment.
    *
    * @param id
    *    id of environment
    * @param description
    *    if specified update description of environment. Length: 0 - 200 characters
    * @param versionLabel
    *    if specified deploy this application version to the environment
    * @param templateName
    *    if specified deploy this configuration template to the environment
    * @param options
    *    environment configuration
    * @return info about environment
    * @throws AWSException
    *    if any error occurs when make request to Amazon API
    */
   public EnvironmentInfo updateEnvironment(String id,
                                            String description,
                                            String versionLabel,
                                            String templateName,
                                            List<ConfigurationOption> options) throws AWSException
   {
      AWSElasticBeanstalk beanstalkClient = getBeanstalkClient();
      try
      {
         return updateEnvironment(beanstalkClient, id, description, versionLabel, templateName, options);
      }
      catch (AmazonClientException e)
      {
         throw new AWSException(e);
      }
      finally
      {
         beanstalkClient.shutdown();
      }
   }

   private EnvironmentInfo updateEnvironment(AWSElasticBeanstalk beanstalkClient,
                                             String id,
                                             String description,
                                             String versionLabel,
                                             String templateName,
                                             List<ConfigurationOption> options)
   {
      UpdateEnvironmentRequest request = new UpdateEnvironmentRequest()
         .withEnvironmentId(id)
         .withDescription(description)
         .withVersionLabel(versionLabel)
         .withTemplateName(templateName);
      if (!(options == null || options.isEmpty()))
      {
         for (ConfigurationOption option : options)
         {
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
    * Stop specified environment.
    *
    * @param id
    *    name of environment
    * @return info about environment
    * @throws AWSException
    *    if any error occurs when make request to Amazon API
    */
   public EnvironmentInfo stopEnvironment(String id) throws AWSException
   {
      AWSElasticBeanstalk beanstalkClient = getBeanstalkClient();
      try
      {
         return stopEnvironment(beanstalkClient, id);
      }
      catch (AmazonClientException e)
      {
         throw new AWSException(e);
      }
      finally
      {
         beanstalkClient.shutdown();
      }
   }

   private EnvironmentInfo stopEnvironment(AWSElasticBeanstalk beanstalkClient, String id)
   {
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
    *    virtual file system instance for access properties of project
    * @param projectId
    *    project id
    * @return list of environments
    * @throws AWSException
    *    if any error occurs when make request to Amazon API
    * @throws VirtualFileSystemException
    *    if any VirtualFileSystem error occurs
    */
   public List<EnvironmentInfo> listApplicationEnvironments(VirtualFileSystem vfs, String projectId)
      throws AWSException, VirtualFileSystemException
   {
      return listApplicationEnvironments(detectApplicationName(vfs, projectId));
   }

   /**
    * Get list of environment associated with application.
    *
    * @param name
    *    of application
    * @return list of environments
    * @throws AWSException
    *    if any error occurs when make request to Amazon API
    */
   public List<EnvironmentInfo> listApplicationEnvironments(String name) throws AWSException
   {
      AWSElasticBeanstalk beanstalkClient = getBeanstalkClient();
      try
      {
         return listApplicationEnvironments(beanstalkClient, name);
      }
      catch (AmazonClientException e)
      {
         throw new AWSException(e);
      }
      finally
      {
         beanstalkClient.shutdown();
      }
   }

   private List<EnvironmentInfo> listApplicationEnvironments(AWSElasticBeanstalk beanstalkClient, String name)
   {
      List<EnvironmentDescription> awsEnvironments = beanstalkClient.describeEnvironments(
         new DescribeEnvironmentsRequest().withApplicationName(name)).getEnvironments();
      List<EnvironmentInfo> environments = new ArrayList<EnvironmentInfo>(awsEnvironments.size());
      for (EnvironmentDescription awsEnvironment : awsEnvironments)
      {
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

   protected AWSElasticBeanstalk getBeanstalkClient() throws AWSException
   {
      final AWSCredentials credentials = authenticator.getCredentials();
      if (credentials == null)
      {
         throw new AWSException("Authentication required.");
      }
      return new AWSElasticBeanstalkClient(credentials);
   }

   protected AmazonS3 getS3Client() throws AWSException
   {
      final AWSCredentials credentials = authenticator.getCredentials();
      if (credentials == null)
      {
         throw new AWSException("Authentication required.");
      }
      return new AmazonS3Client(credentials);
   }

   //

   private S3Location createS3Location(AmazonS3 s3Client, String name, String s3Bucket, String s3Key, URL url)
      throws IOException
   {
      URLConnection conn = null;
      try
      {
         conn = url.openConnection();
         return createS3Location(s3Client, name, s3Bucket, s3Key, conn.getInputStream(), conn.getContentLength());
      }
      finally
      {
         if (conn != null)
         {
            if ("http".equals(url.getProtocol()) || "https".equals(url.getProtocol()))
            {
               ((HttpURLConnection)conn).disconnect();
            }
         }
      }
   }

   private S3Location createS3Location(AmazonS3 s3Client, String name, String s3Bucket, String s3Key, ContentStream file)
      throws IOException
   {
      return createS3Location(s3Client, name, s3Bucket, s3Key, file.getStream(), file.getLength());
   }

   private S3Location createS3Location(AmazonS3 s3Client,
                                       String name,
                                       String s3Bucket,
                                       String s3Key,
                                       InputStream stream,
                                       long length) throws IOException
   {
      // new S3 bucket will be created
      if (s3Bucket == null || s3Bucket.isEmpty())
      {
         s3Bucket = NameGenerator.generate(name + '-', 16);
      }

      // new S3 file will be created
      if (s3Key == null || s3Key.isEmpty())
      {
         s3Key = NameGenerator.generate("app-", 16);
      }

      if (!s3Client.doesBucketExist(s3Bucket))
      {
         s3Client.createBucket(s3Bucket);
         LOG.debug("New S3 bucket '{}' created. ", s3Bucket);
      }
      try
      {
         ObjectMetadata metadata = new ObjectMetadata();
         if (length != -1)
         {
            metadata.setContentLength(length);
         }
         s3Client.putObject(s3Bucket, s3Key, stream, metadata);
         return new S3Location(s3Bucket, s3Key);
      }
      finally
      {
         stream.close();
      }
   }

   private void writeApplicationName(VirtualFileSystem vfs, String projectId, String name)
      throws VirtualFileSystemException
   {
      ConvertibleProperty p = new ConvertibleProperty("aws-application", name);
      List<ConvertibleProperty> properties = new ArrayList<ConvertibleProperty>(1);
      properties.add(p);
      vfs.updateItem(projectId, properties, null);
   }

   private String detectApplicationName(VirtualFileSystem vfs, String projectId)
      throws VirtualFileSystemException
   {
      String name = null;
      if (vfs != null && projectId != null)
      {
         Item item = vfs.getItem(projectId, PropertyFilter.valueOf("aws-application"));
         name = (String)item.getPropertyValue("aws-application");
      }
      if (name == null || name.isEmpty())
      {
         throw new RuntimeException(
            "Not an Amazon Beanstalk application. Please select root folder of Amazon Beanstalk project. ");
      }
      return name;
   }
}
