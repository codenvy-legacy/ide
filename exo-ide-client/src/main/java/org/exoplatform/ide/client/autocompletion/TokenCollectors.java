/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.client.autocompletion;

import java.util.HashMap;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.autocompletion.collectors.CssTokenCollector;
import org.exoplatform.ide.client.autocompletion.collectors.HtmlTokenCollector;
import org.exoplatform.ide.client.autocompletion.collectors.JavaScriptTokenCollector;
import org.exoplatform.ide.client.autocompletion.collectors.XmlTokenCollector;
import org.exoplatform.ide.client.framework.codeassistant.TokenCollector;

import com.google.gwt.event.shared.HandlerManager;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public final class TokenCollectors
{
   private HashMap<String, TokenCollector> tokenCollectors = new HashMap<String, TokenCollector>();

   private static TokenCollectors instance;

   private TokenCollectors(HandlerManager eventBus)
   {
      //tokenCollectors.put(MimeType.GROOVY_SERVICE, new GroovyTokenCollector(eventBus));
      tokenCollectors.put(MimeType.APPLICATION_JAVASCRIPT, new JavaScriptTokenCollector(eventBus));
      tokenCollectors.put(MimeType.TEXT_CSS, new CssTokenCollector(eventBus));
      tokenCollectors.put(MimeType.TEXT_HTML, new HtmlTokenCollector(eventBus));
      tokenCollectors.put(MimeType.TEXT_XML, new XmlTokenCollector());

   }

   private HashMap<String, TokenCollector> getTokenCollectors()
   {
      return tokenCollectors;
   }
   
   public static TokenCollector getTokenCollector(HandlerManager eventBus, String mimeType)
   {
      if (instance == null)
      {
         instance = new TokenCollectors(eventBus);
      }
      return instance.getTokenCollectors().get(mimeType);
   }
}
