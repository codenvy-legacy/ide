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

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.codeassistant.java.service.marshal.ClassDescriptionUnmarshaller;
import org.exoplatform.ide.editor.codeassistant.java.service.marshal.FindClassesUnmarshaller;
import org.exoplatform.ide.editor.codeassistant.java.service.marshal.GroovyClass;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;

/**
 * Implementation of {@link CodeAssistantService}
 * <br>
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
   
   private static final String FIND_CLASS_BY_PREFIX = "/ide/code-assistant/find-by-prefix/";
   
   private static final String FIND_TYPE = "/ide/code-assistant/find-by-type/";

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
   public void findClass(String className, String fileHref, AsyncRequestCallback<List<Token>> callback)
   {
      String url = restServiceContext + FIND_URL + className;
      
      List<Token> tokens = new ArrayList<Token>();
      callback.setResult(tokens);
      
      FindClassesUnmarshaller unmarshaller = new FindClassesUnmarshaller(tokens);
      
      callback.setEventBus(eventBus);
      callback.setPayload(unmarshaller);
      AsyncRequest.build(RequestBuilder.GET, url, loader).header(HTTPHeader.LOCATION, fileHref).send(callback);
   }

   /**
    * @see org.exoplatform.ide.client.module.groovy.service.codeassistant.CodeAssistantService#getClassDescription(java.lang.String)
    */
   @Override
   public void getClassDescription(String fqn, String fileHref, AsyncRequestCallback<GroovyClass> callback)
   {
      String url = restServiceContext + GET_CLASS_URL + fqn;
      
      GroovyClass classInfo = new GroovyClass();
      callback.setResult(classInfo);
      ClassDescriptionUnmarshaller unmarshaller = new ClassDescriptionUnmarshaller(classInfo);
      
      callback.setEventBus(eventBus);
      callback.setPayload(unmarshaller);
      AsyncRequest.build(RequestBuilder.GET, url, loader).header(HTTPHeader.LOCATION, fileHref).send(callback);
   }

   /**
    * @see org.exoplatform.ide.client.module.groovy.service.codeassistant.CodeAssistantService#findClassesByPrefix(java.lang.String)
    */
   @Override
   public void findClassesByPrefix(String prefix, String fileHref, AsyncRequestCallback<List<Token>> callback)
   {
      String url = restServiceContext + FIND_CLASS_BY_PREFIX + prefix + "?where=className";
      
      List<Token> tokens = new ArrayList<Token>();
      callback.setResult(tokens);
      FindClassesUnmarshaller unmarshaller = new FindClassesUnmarshaller(tokens);
      
      callback.setEventBus(eventBus);
      callback.setPayload(unmarshaller);
      AsyncRequest.build(RequestBuilder.GET, url, loader).header(HTTPHeader.LOCATION, fileHref).send(callback);
   }

   /**
    * @see org.exoplatform.ide.client.module.groovy.service.codeassistant.CodeAssistantService#fintType(org.exoplatform.ide.client.module.groovy.service.codeassistant.Types)
    */
   @Override
   public void fintType(Types type, String prefix, AsyncRequestCallback<List<Token>> callback)
   {
      String url = restServiceContext + FIND_TYPE + type.toString();
      if(prefix != null && !prefix.isEmpty())
      {
       url += "?prefix=" + prefix;
      }
      List<Token> tokens = new ArrayList<Token>();
      callback.setResult(tokens);
      FindClassesUnmarshaller unmarshaller = new FindClassesUnmarshaller(tokens);
      
      callback.setEventBus(eventBus);
      callback.setPayload(unmarshaller);
      AsyncRequest.build(RequestBuilder.GET, url, loader).send(callback);
   }

}
