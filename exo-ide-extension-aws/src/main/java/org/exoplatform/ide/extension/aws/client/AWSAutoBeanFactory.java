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
package org.exoplatform.ide.extension.aws.client;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

import org.exoplatform.ide.extension.aws.client.login.Credentials;
import org.exoplatform.ide.extension.aws.shared.beanstalk.ApplicationInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.ApplicationVersionInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.ConfigurationOption;
import org.exoplatform.ide.extension.aws.shared.beanstalk.ConfigurationOptionInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.ConfigurationOptionRestriction;
import org.exoplatform.ide.extension.aws.shared.beanstalk.CreateApplicationRequest;
import org.exoplatform.ide.extension.aws.shared.beanstalk.CreateApplicationVersionRequest;
import org.exoplatform.ide.extension.aws.shared.beanstalk.CreateEnvironmentRequest;
import org.exoplatform.ide.extension.aws.shared.beanstalk.DeleteApplicationVersionRequest;
import org.exoplatform.ide.extension.aws.shared.beanstalk.EnvironmentInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.Event;
import org.exoplatform.ide.extension.aws.shared.beanstalk.EventsList;
import org.exoplatform.ide.extension.aws.shared.beanstalk.S3Item;
import org.exoplatform.ide.extension.aws.shared.beanstalk.SolutionStack;
import org.exoplatform.ide.extension.aws.shared.beanstalk.UpdateApplicationRequest;
import org.exoplatform.ide.extension.aws.shared.ec2.ImagesList;
import org.exoplatform.ide.extension.aws.shared.ec2.SecurityGroupInfo;
import org.exoplatform.ide.extension.aws.shared.s3.S3Bucket;
import org.exoplatform.ide.extension.aws.shared.s3.S3ObjectsList;

/**
 * The interface for the AutoBean generator.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 14, 2012 10:28:09 AM anya $
 * 
 */
public interface AWSAutoBeanFactory extends AutoBeanFactory
{

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
    * A factory method for environment info bean.
    * 
    * @return an {@link AutoBean} of type {@link EnvironmentInfo}
    */
   AutoBean<EnvironmentInfo> environmentInfo();

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
   
   
   /**
    * @return
    */
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
    * A factory method for create environment request bean.
    * 
    * @return {@link CreateEnvironmentRequest} create environment request
    */
   AutoBean<CreateEnvironmentRequest> createEnvironmentRequest();

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
}
