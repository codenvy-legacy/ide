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
package org.exoplatform.ide.editor.groovy.client.codeassistant.service;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.ide.editor.java.client.codeassistant.services.CodeAssistantService;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: GroovyCodeAssistant Apr 6, 2011 2:53:10 PM evgen $
 *
 */
public class GroovyCodeAssistantService extends CodeAssistantService
{
   
   private static GroovyCodeAssistantService instance;
   /**
    * @param eventBus
    * @param restServiceContext
    * @param loader
    */
   public GroovyCodeAssistantService(HandlerManager eventBus, String restServiceContext, Loader loader)
   {
      super(eventBus, restServiceContext, loader, "/ide/code-assistant/groovy/find?class=", // FIND_URL
         "/ide/code-assistant/groovy/class-description?fqn=", //GET_CLASS_URL
         "/ide/code-assistant/groovy/find-by-prefix/", //  FIND_CLASS_BY_PREFIX
         "/ide/code-assistant/groovy/find-by-type/" //FIND_TYPE
      );
      instance = this;
   }

   public static GroovyCodeAssistantService get()
   {
      return instance;
   }
}
