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
package org.exoplatform.ide.client.module.groovy.service.codeassistant;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.codeassistant.TokenExt;
import org.exoplatform.ide.client.module.groovy.codeassistant.autocompletion.GroovyClass;
import org.exoplatform.ide.client.module.groovy.service.codeassistant.event.ClassDescriptionReceivedEvent;
import org.exoplatform.ide.client.module.groovy.service.codeassistant.event.ClassesNamesReceivedEvent;
import org.exoplatform.ide.client.module.groovy.service.codeassistant.marshal.ClassDescriptionUnmarshaller;
import org.exoplatform.ide.client.module.groovy.service.codeassistant.marshal.FindClassesUnmarshaller;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 17, 2010 4:44:53 PM evgen $
 *
 */
public class CodeAssistantServiceImpl extends CodeAssistantService
{

   private static final String FIND_URL = "/ide/code-assistant/find?class=";
   
   private static final String GET_CLASS_URL = "/ide/code-assistant/class-description?fqn=";

   private HandlerManager eventBus;

   private Loader loader;
   
   private String restServiceContext;

   public CodeAssistantServiceImpl(HandlerManager eventBus, String restServiceContext,Loader loader)
   {
      this.eventBus = eventBus;
      this.loader = loader;
      this.restServiceContext = restServiceContext;
   }

   /**
    * @see org.exoplatform.ide.client.module.groovy.service.codeassistant.CodeAssistantService#findClass(java.lang.String)
    */
   @Override
   public void findClass(String className)
   {
      String url = restServiceContext + FIND_URL + className;
      
      List<TokenExt> tokens = new ArrayList<TokenExt>();
      ClassesNamesReceivedEvent event = new ClassesNamesReceivedEvent(tokens);
      FindClassesUnmarshaller unmarshaller = new FindClassesUnmarshaller(tokens);
      
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus,unmarshaller, event);
      AsyncRequest.build(RequestBuilder.GET, url, loader).send(callback);
   }

   /**
    * @see org.exoplatform.ide.client.module.groovy.service.codeassistant.CodeAssistantService#getClassDescription(java.lang.String)
    */
   @Override
   public void getClassDescription(String fqn)
   {
      String url = restServiceContext + GET_CLASS_URL + fqn;
      
      GroovyClass classInfo = new GroovyClass();
      ClassDescriptionReceivedEvent event = new ClassDescriptionReceivedEvent(classInfo);
      ClassDescriptionUnmarshaller unmarshaller = new ClassDescriptionUnmarshaller(classInfo);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus,unmarshaller, event);
      AsyncRequest.build(RequestBuilder.GET, url, loader).send(callback);
   }

}
