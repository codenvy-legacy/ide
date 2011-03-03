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
package org.exoplatform.ide.editor.codeassistant.java.service;

import java.util.List;

import org.exoplatform.gwtframework.commons.rest.ClientRequestCallback;
import org.exoplatform.ide.editor.api.codeassitant.Token;

import com.google.gwt.http.client.Request;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: TokensCallback.java Feb 16, 2011 4:50:07 PM vereshchaka $
 *
 */
public abstract class TokensCallback extends ClientRequestCallback
{
   private List<Token> tokens;
   
   /**
    * @return the tokens
    */
   public List<Token> getTokens()
   {
      return tokens;
   }
   
   /**
    * @param tokens the tokens to set
    */
   public void setTokens(List<Token> tokens)
   {
      this.tokens = tokens;
   }

   /**
    * @see com.google.gwt.http.client.RequestCallback#onError(com.google.gwt.http.client.Request, java.lang.Throwable)
    */
   @Override
   public void onError(Request request, Throwable exception)
   {
      handleError(exception);
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.ClientRequestCallback#onUnsuccess(java.lang.Throwable)
    */
   @Override
   public void onUnsuccess(Throwable exception)
   {
      handleError(exception);
   }
   
   public abstract void handleError(Throwable exc);

}
