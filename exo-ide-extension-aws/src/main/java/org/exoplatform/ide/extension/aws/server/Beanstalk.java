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
import com.amazonaws.services.elasticbeanstalk.model.DescribeApplicationVersionsRequest;
import com.amazonaws.services.elasticbeanstalk.model.DescribeApplicationsRequest;
import com.amazonaws.services.elasticbeanstalk.model.DescribeConfigurationOptionsRequest;
import com.amazonaws.services.elasticbeanstalk.model.DescribeEnvironmentsRequest;
import com.amazonaws.services.elasticbeanstalk.model.EnvironmentDescription;
import com.amazonaws.services.elasticbeanstalk.model.OptionRestrictionRegex;
import com.amazonaws.services.elasticbeanstalk.model.S3Location;
import com.amazonaws.services.elasticbeanstalk.model.SolutionStackDescription;
import com.amazonaws.services.elasticbeanstalk.model.TerminateEnvironmentRequest;
import com.amazonaws.services.elasticbeanstalk.model.UpdateEnvironmentRequest;
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
      AWSElasticBeanstalk beanstalkClient, DescribeConfigurationOptionsRequest request) throws AWSException
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
    * Create new AWS Beanstalk application.
    *
    * @param name
    *    application name. This name must be unique within AWS Beanstalk account. Length: 1-100 characters.
    * @param description
    *    optional description of application. Length: 0 - 200 characters.
    * @param s3Bucket
    *    optional name of S3 bucket where application uploaded before deploy to AWS Beanstalk. If this parameter not
    *    specified random name generated and new S3 bucket created.
    * @param s3Key
    *    optional name of S3 key where application uploaded before deploy to AWS Beanstalk. If this parameter not
    *    specified random name generated and new S3 file created. If file with specified key already exists it content
    *    may be overridden
    * @param vfs
    *    virtual file system instance for access to source code of project. Some info may be stored in properties of
    *    project after creation an application
    * @param projectId
    *    project id
    * @param war
    *    URL to pre-builded war file. May be present for java applications ONLY
    * @return info about newly created application
    * @throws AWSException
    *    if any error occurs when make request to Amazon API
    * @throws VirtualFileSystemException
    *    if any VFS error occurs
    * @throws IOException
    *    i/o error, e.g. when try to download pre-builded binary file
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

      // Be sure project is accessible. VirtualFileSystemException thrown if something wrong.
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
                                             URL url) throws VirtualFileSystemException, IOException
   {
      beanstalkClient.createApplication(new CreateApplicationRequest().withApplicationName(name)
         .withDescription(description));
      createApplicationVersion(beanstalkClient, s3Client, name, s3Bucket, s3Key, "initial version", url);
      writeApplicationName(vfs, projectId, name);
      return getApplicationInfo(beanstalkClient, name);
   }

   /**
    * Get info about AWS Beanstalk application. Name of project retrieved from project properties.
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
      String name = detectApplicationName(vfs, projectId, true);
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
    * Delete specified  application. Name of project retrieved from project properties.
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
      String name = detectApplicationName(vfs, projectId, true);
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
    * Get all existing applications.
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

   public ApplicationInfo createApplicationVersion(String s3Bucket,
                                                   String s3Key,
                                                   String versionLabel,
                                                   VirtualFileSystem vfs,
                                                   String projectId,
                                                   URL war) throws AWSException, VirtualFileSystemException, IOException
   {
      String name = detectApplicationName(vfs, projectId, true);
      // Two possible location for project file(s).
      // 1. Location to binary file. Typically actual for Java applications.
      // 2. Get project from VFS. Typically actual for application that do not need compilation, e.g. PHP applications
      AWSElasticBeanstalk beanstalkClient = getBeanstalkClient();
      AmazonS3 s3Client = getS3Client();
      try
      {
         if (war != null)
         {
            createApplicationVersion(beanstalkClient, s3Client, name, s3Bucket, s3Key, versionLabel, war);
         }
         else
         {
            createApplicationVersion(beanstalkClient, s3Client, name, s3Bucket, s3Key, versionLabel,
               vfs.exportZip(projectId));
         }
         return getApplicationInfo(beanstalkClient, name);
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

   private void createApplicationVersion(AWSElasticBeanstalk beanstalkClient,
                                         AmazonS3 s3Client,
                                         String name,
                                         String s3Bucket,
                                         String s3Key,
                                         String versionLabel,
                                         URL url) throws IOException
   {
      S3Location s3Location = createS3Location(s3Client, name, s3Bucket, s3Key, url);
      beanstalkClient.createApplicationVersion(new CreateApplicationVersionRequest().withApplicationName(name).
         withVersionLabel(versionLabel).withSourceBundle(s3Location));
   }

   private void createApplicationVersion(AWSElasticBeanstalk beanstalkClient,
                                         AmazonS3 s3Client,
                                         String name,
                                         String s3Bucket,
                                         String s3Key,
                                         String versionLabel,
                                         ContentStream file) throws IOException
   {
      S3Location s3Location = createS3Location(s3Client, name, s3Bucket, s3Key, file);
      beanstalkClient.createApplicationVersion(new CreateApplicationVersionRequest().withApplicationName(name).
         withVersionLabel(versionLabel).withSourceBundle(s3Location));
   }

   public List<ApplicationVersionInfo> listApplicationVersions(VirtualFileSystem vfs,
                                                               String projectId,
                                                               List<String> versionNameFilter)
      throws AWSException, VirtualFileSystemException, IOException
   {
      String name = detectApplicationName(vfs, projectId, true);
      AWSElasticBeanstalk beanstalkClient = getBeanstalkClient();
      try
      {
         return listApplicationVersions(beanstalkClient, name, versionNameFilter);
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

   private List<ApplicationVersionInfo> listApplicationVersions(AWSElasticBeanstalk beanstalkClient,
                                                                String name,
                                                                List<String> versionNameFilter)
   {
      List<ApplicationVersionDescription> awsApplicationVersions = beanstalkClient.describeApplicationVersions(
         new DescribeApplicationVersionsRequest()
            .withApplicationName(name)
            .withVersionLabels(versionNameFilter)).getApplicationVersions();
      List<ApplicationVersionInfo> versions = new ArrayList<ApplicationVersionInfo>(awsApplicationVersions.size());
      for (ApplicationVersionDescription awsApplicationVersion : awsApplicationVersions)
      {
         versions.add(new ApplicationVersionInfoImpl.Builder()
            .name(awsApplicationVersion.getApplicationName())
            .description(awsApplicationVersion.getDescription())
            .versionLabel(awsApplicationVersion.getVersionLabel())
            .s3Location(awsApplicationVersion.getSourceBundle().getS3Bucket(),
               awsApplicationVersion.getSourceBundle().getS3Key())
            .created(awsApplicationVersion.getDateCreated())
            .updated(awsApplicationVersion.getDateUpdated()).build()
         );
      }
      return versions;
   }

   //

   /**
    * @param environmentName
    *    name for new environment
    * @param solutionStackName
    *    name of Amazon solution stack. NOTE: if this parameter is specified <code>templateName</code> must be
    *    <code>null</code>
    * @param templateName
    *    name of template for new environment. NOTE: if this parameter is specified <code>solutionStackName</code> must
    *    be <code>null</code>
    * @param versionLabel
    *    version of application to deploy
    * @param description
    *    optional description for created application environment
    * @param vfs
    *    VirtualFileSystem
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
                                                       List<ConfigurationOption> options) throws AWSException, VirtualFileSystemException
   {
      String name = detectApplicationName(vfs, projectId, true);
      AWSElasticBeanstalk beanstalkClient = getBeanstalkClient();
      try
      {
         return createApplicationEnvironment(beanstalkClient,
            name,
            environmentName,
            solutionStackName,
            templateName,
            versionLabel,
            description,
            options);
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
      EnvironmentDescription awsEnvironment = awsEnvironments.get(0); // request by id - only one expected in result
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

   public void updateEnvironment(String name,
                                 List<ConfigurationOption> options) throws AWSException
   {
      AWSElasticBeanstalk beanstalkClient = getBeanstalkClient();
      try
      {
         updateEnvironment(beanstalkClient, name, options);
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

   private void updateEnvironment(AWSElasticBeanstalk beanstalkClient,
                                  String name,
                                  List<ConfigurationOption> options)
   {
      UpdateEnvironmentRequest request = new UpdateEnvironmentRequest().withEnvironmentName(name);
      if (!(options == null || options.isEmpty()))
      {
         for (ConfigurationOption option : options)
         {
            request.getOptionSettings().add(
               new ConfigurationOptionSetting(option.getNamespace(), option.getName(), option.getValue()));
         }
      }
      beanstalkClient.updateEnvironment(request);
   }

   /**
    * Stop specified environment.
    *
    * @param name
    *    name of environment
    * @throws AWSException
    *    if any error occurs when make request to Amazon API
    */
   public void stopEnvironment(String name) throws AWSException
   {
      AWSElasticBeanstalk beanstalkClient = getBeanstalkClient();
      try
      {
         stopEnvironment(beanstalkClient, name);
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

   private void stopEnvironment(AWSElasticBeanstalk beanstalkClient, String name) throws AWSException
   {
      beanstalkClient.terminateEnvironment(new TerminateEnvironmentRequest().withEnvironmentName(name));
   }

   public List<EnvironmentInfo> listApplicationEnvironments(VirtualFileSystem vfs,
                                                            String projectId) throws AWSException, VirtualFileSystemException
   {
      String name = detectApplicationName(vfs, projectId, true);
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

   private List<EnvironmentInfo> listApplicationEnvironments(AWSElasticBeanstalk beanstalkClient,
                                                             String name)
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

   private S3Location createS3Location(AmazonS3 s3Client,
                                       String name,
                                       String s3Bucket,
                                       String s3Key,
                                       URL url) throws IOException
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

   private S3Location createS3Location(AmazonS3 s3Client,
                                       String name,
                                       String s3Bucket,
                                       String s3Key,
                                       ContentStream file) throws IOException
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

   private String detectApplicationName(VirtualFileSystem vfs, String projectId, boolean failIfCannotDetect)
      throws VirtualFileSystemException
   {
      String name = null;
      if (vfs != null && projectId != null)
      {
         Item item = vfs.getItem(projectId, PropertyFilter.valueOf("aws-application"));
         name = (String)item.getPropertyValue("aws-application");
      }
      if (failIfCannotDetect && (name == null || name.isEmpty()))
      {
         throw new RuntimeException(
            "Not an Amazon Beanstalk application. Please select root folder of Amazon Beanstalk project. ");
      }
      return name;
   }

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
}
