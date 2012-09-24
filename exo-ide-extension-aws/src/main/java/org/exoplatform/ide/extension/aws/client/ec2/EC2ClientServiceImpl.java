/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.extension.aws.client.ec2;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.extension.aws.client.AWSExtension;
import org.exoplatform.ide.extension.aws.shared.ec2.ImagesList;
import org.exoplatform.ide.extension.aws.shared.ec2.InstanceStatusInfo;
import org.exoplatform.ide.extension.aws.shared.ec2.SecurityGroupInfo;
import org.exoplatform.ide.extension.aws.shared.ec2.StatusRequest;

import java.util.List;

/**
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: EC2ClientServiceImpl.java Sep 21, 2012 12:31:29 PM azatsarynnyy $
 *
 */
public class EC2ClientServiceImpl extends EC2ClientService
{

   private static final String BASE_URL = "/ide/aws/ec2";

   private static final String IMAGES = BASE_URL + "/images";

   private static final String INSTANCE_SATUS = BASE_URL + "/instance/status";

   private static final String SECURITY_GROUPS = BASE_URL + "/security_groups";

   /**
    * REST service context.
    */
   private String restServiceContext;

   /**
    * Loader to be displayed.
    */
   private Loader loader;

   public EC2ClientServiceImpl(String restContext, Loader loader)
   {
      this.loader = loader;
      this.restServiceContext = restContext;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.ec2.EC2ClientService#images(java.lang.String, boolean,
    *       java.lang.String, int, int, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void images(String owner, boolean isPublic, String architecture, int skipCount, int maxItems,
      AsyncRequestCallback<ImagesList> callback) throws RequestException
   {
      final String url = restServiceContext + IMAGES;

      String params = "owner=" + owner;
      params += "&ispublic=" + isPublic;
      if (architecture != null)
      {
         params += "&architecture=" + architecture;
      }
      params += "&skipcount=" + skipCount;
      params += "&maxitems=" + maxItems;

      AsyncRequest.build(RequestBuilder.GET, url + "?" + params).loader(loader)
         .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.ec2.EC2ClientService#securityGroupInfo(org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void securityGroupInfo(AsyncRequestCallback<List<SecurityGroupInfo>> callback) throws RequestException
   {
      final String url = restServiceContext + SECURITY_GROUPS;
      AsyncRequest.build(RequestBuilder.GET, url).loader(loader).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
         .send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.ec2.EC2ClientService#status(org.exoplatform.ide.extension.aws.shared.ec2.StatusRequest,
    *       org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void status(StatusRequest request, AsyncRequestCallback<List<InstanceStatusInfo>> callback)
      throws RequestException
   {
      final String url = restServiceContext + INSTANCE_SATUS;

      String statusRequest = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(request)).getPayload();

      AsyncRequest.build(RequestBuilder.GET, url).data(statusRequest).loader(loader)
         .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON)
         .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
   }

}
