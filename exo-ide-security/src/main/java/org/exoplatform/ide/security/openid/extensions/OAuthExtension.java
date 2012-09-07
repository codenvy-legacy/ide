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
package org.exoplatform.ide.security.openid.extensions;

import org.openid4java.message.MessageException;
import org.openid4java.message.MessageExtension;
import org.openid4java.message.MessageExtensionFactory;
import org.openid4java.message.Parameter;
import org.openid4java.message.ParameterList;

import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class OAuthExtension implements MessageExtension, MessageExtensionFactory
{
   public static final String TYPE_URI = "http://specs.openid.net/extensions/oauth/1.0";

   private ParameterList params;

   public OAuthExtension(String consumer, List<String> scopes)
   {
      if (consumer == null || consumer.isEmpty())
      {
         throw new IllegalArgumentException("Consumer key is required. ");
      }
      if (scopes == null || scopes.isEmpty())
      {
         throw new IllegalArgumentException("List of scopes is required. ");
      }
      this.params = new ParameterList();
      this.params.set(new Parameter("consumer", consumer));
      this.params.set(new Parameter("scope", flattenScopes(scopes)));
   }

   private static String flattenScopes(List<String> scopes)
   {
      StringBuilder flatten = new StringBuilder();
      Iterator<String> iterator = scopes.iterator();
      while (true)
      {
         String s = iterator.next();
         flatten.append(s);
         if (!iterator.hasNext())
         {
            return flatten.toString();
         }
         flatten.append(' ');
      }
   }

   private OAuthExtension(ParameterList params)
   {
      validate(params);
      this.params = params;
   }

   // MessageExtensionFactory
   @Override
   public MessageExtension getExtension(ParameterList parameterList, boolean isRequest) throws MessageException
   {
      return new OAuthExtension(parameterList);
   }
   // -----

   @Override
   public final String getTypeUri()
   {
      return TYPE_URI;
   }

   @Override
   public ParameterList getParameters()
   {
      return this.params;
   }

   @Override
   public void setParameters(ParameterList params)
   {
      validate(params);
      this.params = params;
   }

   @Override
   public final boolean providesIdentifier()
   {
      return false;
   }

   @Override
   public final boolean signRequired()
   {
      return true;
   }

   //

   /**
    * Check is required parameters presents in parameters list.
    *
    * @param params
    *    parameters list for validation
    * @throws IllegalArgumentException
    *    if parameters list does not contains required parameter
    */

   private void validate(ParameterList params)
   {
      if (!params.hasParameter("consumer"))
      {
         throw new IllegalArgumentException("Consumer key is required. ");
      }
      if (!params.hasParameter("scope"))
      {
         throw new IllegalArgumentException("List of scopes is required. ");
      }
   }
}
