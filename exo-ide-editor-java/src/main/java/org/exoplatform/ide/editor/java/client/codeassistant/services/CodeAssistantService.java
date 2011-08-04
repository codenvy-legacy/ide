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
package org.exoplatform.ide.editor.java.client.codeassistant.services;

import com.google.gwt.http.client.RequestBuilder;

import com.google.gwt.event.shared.HandlerManager;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.java.client.codeassistant.services.marshal.ClassDescriptionUnmarshaller;
import org.exoplatform.ide.editor.java.client.codeassistant.services.marshal.FindClassesUnmarshaller;
import org.exoplatform.ide.editor.java.client.codeassistant.services.marshal.JavaClass;

/**
 * This service for auto-complete feature.
 * Service need for retrieve information about Groovy classes.      
 * <br>
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 17, 2010 2:44:13 PM evgen $
 *
 */
public abstract class CodeAssistantService
{

   protected String FIND_URL;

   protected String GET_CLASS_URL;

   protected String FIND_CLASS_BY_PREFIX;

   protected String FIND_TYPE;

   protected HandlerManager eventBus;

   protected Loader loader;

   protected String restServiceContext;

   protected CodeAssistantService(HandlerManager eventBus, String restServiceContext, Loader loader, String findUrl,
      String getClassUrl, String findClassByPrefix, String findType)
   {
      this.eventBus = eventBus;
      this.loader = loader;
      this.restServiceContext = restServiceContext;

      this.FIND_URL = findUrl;
      this.GET_CLASS_URL = getClassUrl;
      this.FIND_CLASS_BY_PREFIX = findClassByPrefix;
      this.FIND_TYPE = findType;
   }

   /**
    * Get Classes FQN by name.
    *   
    * @param className
    * @param fileHref for who autocompletion called (Need for find classpath)
    * @param callback - the callback which client has to implement
    */
   public void findClass(String className, String fileHref, AsyncRequestCallback<List<Token>> callback)
   {
      String url = restServiceContext + FIND_URL + className;

      List<Token> tokens = new ArrayList<Token>();
      callback.setResult(tokens);

      FindClassesUnmarshaller unmarshaller = new FindClassesUnmarshaller(tokens);

      callback.setEventBus(eventBus);
      callback.setPayload(unmarshaller);
      if (fileHref == null)
      {
         AsyncRequest.build(RequestBuilder.GET, url, loader).send(callback);
      }
      else
      {
         AsyncRequest.build(RequestBuilder.GET, url, loader).header(HTTPHeader.LOCATION, fileHref).send(callback);
      }
   }

   /**
    * Get Class description (methods, fields etc.) by class FQN
    * 
    * @param fqn
    * @param fileHref for who autocompletion called (Need for find classpath)
    * @param callback - the callback which client has to implement
    */
   public void getClassDescription(String fqn, String fileHref, AsyncRequestCallback<JavaClass> callback)
   {
      String url = restServiceContext + GET_CLASS_URL + fqn;

      JavaClass classInfo = new JavaClass();
      callback.setResult(classInfo);
      ClassDescriptionUnmarshaller unmarshaller = new ClassDescriptionUnmarshaller(classInfo);
      int status[] = {HTTPStatus.NO_CONTENT, HTTPStatus.OK};
      callback.setEventBus(eventBus);
      callback.setPayload(unmarshaller);
      callback.setSuccessCodes(status);
      AsyncRequest.build(RequestBuilder.GET, url, loader).header(HTTPHeader.LOCATION, fileHref).send(callback);
   }

   /**
    * Find classes by prefix
    * @param prefix the first letters of class name
    * @param fileHref for who autocompletion called (Need for find classpath)
    * @param callback - the callback which client has to implement
    */
   public void findClassesByPrefix(String prefix, String fileHref, AsyncRequestCallback<List<Token>> callback)
   {
      String url = restServiceContext + FIND_CLASS_BY_PREFIX + prefix + "?where=className";

      List<Token> tokens = new ArrayList<Token>();
      callback.setResult(tokens);
      FindClassesUnmarshaller unmarshaller = new FindClassesUnmarshaller(tokens);

      callback.setEventBus(eventBus);
      callback.setPayload(unmarshaller);
      if (fileHref == null)
      {
         AsyncRequest.build(RequestBuilder.GET, url, loader).send(callback);
      }
      else
      {
         AsyncRequest.build(RequestBuilder.GET, url, loader).header(HTTPHeader.LOCATION, fileHref).send(callback);
      }
   }

   /**
    * Find all classes or annotations or interfaces
    * 
    * @param type class type
    * @param prefix the prefix with type name starts (can be null)
    * @param callback - the callback which client has to implement
    */
   public void findType(Types type, String prefix, AsyncRequestCallback<List<Token>> callback)
   {
      String url = restServiceContext + FIND_TYPE + type.toString();
      if (prefix != null && !prefix.isEmpty())
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
