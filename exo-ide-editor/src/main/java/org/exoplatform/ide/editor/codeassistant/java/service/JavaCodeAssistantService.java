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
package org.exoplatform.ide.editor.codeassistant.java.service;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.loader.Loader;

/**
 * Implementation of {@link CodeAssistantService}
 * <br>
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 17, 2010 4:44:53 PM evgen $
 *
 */
public class JavaCodeAssistantService extends CodeAssistantService
{

   private static JavaCodeAssistantService instance;
   
   public JavaCodeAssistantService(HandlerManager eventBus, String restServiceContext, Loader loader)
   {
      super(eventBus, restServiceContext, loader, "/ide/code-assistant/java/find?class=", // FIND_URL
         "/ide/code-assistant/java/class-description?fqn=", //GET_CLASS_URL
         "/ide/code-assistant/java/find-by-prefix/", //  FIND_CLASS_BY_PREFIX
         "/ide/code-assistant/java/find-by-type/" //FIND_TYPE
      );
      instance = this;
   }
   
   public static JavaCodeAssistantService get()
   {
      return instance;
   }

}
