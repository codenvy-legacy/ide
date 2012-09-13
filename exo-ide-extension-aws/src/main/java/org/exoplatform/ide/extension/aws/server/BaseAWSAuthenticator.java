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

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.STSSessionCredentialsProvider;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public abstract class BaseAWSAuthenticator implements AWSAuthenticator
{
   private static final Log LOG = ExoLogger.getExoLogger(BaseAWSAuthenticator.class);
   protected final Map<String, STSSessionCredentialsProvider> credentialCache;

   public BaseAWSAuthenticator()
   {
      this.credentialCache = new ConcurrentHashMap<String, STSSessionCredentialsProvider>();
   }

   @Override
   public void login(String accessKey, String secret) throws AWSException
   {
      AWSCredentials longTimeCredentials = new BasicAWSCredentials(accessKey, secret);
      STSSessionCredentialsProvider sessionCredentialsProvider = new STSSessionCredentialsProvider(longTimeCredentials);
      try
      {
         sessionCredentialsProvider.getCredentials();
      }
      catch (AmazonServiceException e)
      {
         throw new AWSException("Unable login. " + e.getMessage(), e);
      }
      credentialCache.put(getUserId(), sessionCredentialsProvider);
      try
      {
         writeCredentials(longTimeCredentials);
      }
      catch (Exception e)
      {
         // Log about error but not fail. User may enter credentials again if credentials in cache expired.
         LOG.error("Unable save user credentials in storage. ", e);
      }
   }

   @Override
   public void logout() throws AWSException
   {
      credentialCache.remove(getUserId());
      try
      {
         removeCredentials();
      }
      catch (Exception e)
      {
         throw new AWSException("Unable logout. ", e);
      }
   }

   @Override
   public AWSCredentials getCredentials()
   {
      final String userId = getUserId();
      STSSessionCredentialsProvider sessionCredentialsProvider = credentialCache.get(userId);
      if (sessionCredentialsProvider != null)
      {
         try
         {
            return sessionCredentialsProvider.getCredentials();
         }
         catch (AmazonServiceException ignored)
         {
            // ignore error here try read credentials from storage.
         }
      }

      try
      {
         AWSCredentials longTimeCredentials = readCredentials();
         sessionCredentialsProvider = new STSSessionCredentialsProvider(longTimeCredentials);
         AWSCredentials sessionCredentials = sessionCredentialsProvider.getCredentials();
         credentialCache.put(getUserId(), sessionCredentialsProvider);
         return sessionCredentials;
      }
      catch (Exception e)
      {
         // User may re-login. Something wrong with persistent credentials.
         return null;
      }
   }

   protected String getUserId()
   {
      return ConversationState.getCurrent().getIdentity().getUserId();
   }

   protected abstract void writeCredentials(AWSCredentials credentials) throws Exception;

   protected abstract AWSCredentials readCredentials() throws Exception;

   protected abstract void removeCredentials() throws Exception;
}
