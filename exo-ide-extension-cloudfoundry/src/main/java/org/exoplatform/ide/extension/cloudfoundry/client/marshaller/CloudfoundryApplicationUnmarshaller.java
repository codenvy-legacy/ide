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
package org.exoplatform.ide.extension.cloudfoundry.client.marshaller;

import com.google.gwt.json.client.JSONArray;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryApplication;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryApplicationResources;
import org.exoplatform.ide.extension.cloudfoundry.shared.Staging;

import java.util.ArrayList;
import java.util.List;


/**
 * Unmarshaller for response from server, when {@link CloudfoundryApplication} is returned.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: CloudfoundryApplicationUnmarshaller Jun 22, 2011 5:06:40 PM vereshchaka $
 *
 */
public class CloudfoundryApplicationUnmarshaller implements Unmarshallable
{
   interface Constants
   {
      /* String */
      public static final String NAME = "name";
      
      /* List */
      public static final String URIS = "uris";
      
      /* int */
      public static final String INSTANCES = "instances";
      
      /* int */
      public static final String RUNNING_INSTANCES = "runningInstances";
      
      /* String */
      public static final String STATE = "state";
      
      /* List */
      public static final String SERVICES = "services";
      
      /* String */
      public static final String VERSION = "version";
      
      /* List */
      public static final String ENV = "env";
      
      /* {@link CloudfoundryApplicationResources} */
      public static final String RESOURCES = "resources";
      
      /* {@link Staging} */
      public static final String STAGING = "staging";
      
      //CloudfoundryApplicationResource
      /* int */
      public static final String MEMORY = "memory";
      
      /* int */
      public static final String DISK = "dist";
      
      //Staging
      public static final String MODEL = "model";
      
      public static final String STACK = "stack";
   }

   private CloudfoundryApplication cloudfoundryApplication;
   
   public CloudfoundryApplicationUnmarshaller(CloudfoundryApplication application)
   {
      cloudfoundryApplication = application;
   }


   /**
    * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response)
    */
   @Override
   public void unmarshal(Response response) throws UnmarshallerException
   {
      JavaScriptObject json = build(response.getText());
      if (json == null)
         return;
      JSONObject jsonObject = new JSONObject(json).isObject();
      if (jsonObject == null)
         return;

      parseObject(jsonObject);
   }
   
   private void parseArray(JSONArray jsonArray, List<String> list)
   {
      for (int i = 0; i < jsonArray.size(); i++)
      {
         JSONValue value = jsonArray.get(i);
         list.add(value.isString().stringValue());
      }
   }
   
   private void parseObject(JSONObject jsonObject)
   {
      for (String key : jsonObject.keySet())
      {
         JSONValue jsonValue = jsonObject.get(key);
         if (key.equals(Constants.NAME))
         {
            if (jsonValue.isString() != null)
            {
               cloudfoundryApplication.setName(jsonValue.isString().stringValue());
            }
            
         }
         else if (key.equals(Constants.URIS))
         {
            //parse list
            List<String> uris = new ArrayList<String>();
            if (jsonObject.get(key).isArray() != null)
            {
               JSONArray jsonArray = jsonObject.get(key).isArray();

               parseArray(jsonArray, uris);
            }
            cloudfoundryApplication.setUris(uris);
         }
         else if (key.equals(Constants.INSTANCES))
         {
            if (jsonValue.isNumber() != null)
             {
                cloudfoundryApplication.setInstances((int)jsonValue.isNumber().doubleValue());
             }
         }
         else if (key.equals(Constants.RUNNING_INSTANCES))
         {
            if (jsonValue.isNumber() != null)
             {
                cloudfoundryApplication.setRunningInstances((int)jsonValue.isNumber().doubleValue());
             }
         }
         else if (key.equals(Constants.STATE))
         {
            if (jsonValue.isString() != null)
            {
               cloudfoundryApplication.setState(jsonValue.isString().stringValue());
            }
         }
         else if (key.equals(Constants.SERVICES))
         {
            List<String> services = new ArrayList<String>();
            if (jsonValue.isArray() != null)
            {
               JSONArray jsonArray = jsonValue.isArray();

               parseArray(jsonArray, services);
            }
            cloudfoundryApplication.setServices(services);

         }
         else if (key.equals(Constants.VERSION))
         {
            if (jsonValue.isString() != null)
            {
               cloudfoundryApplication.setVersion(jsonValue.isString().stringValue());
            }
         }
         else if (key.equals(Constants.ENV))
         {
            List<String> envs = new ArrayList<String>();
            if (jsonValue.isArray() != null)
            {
               JSONArray jsonArray = jsonValue.isArray();

               parseArray(jsonArray, envs);
            }
            cloudfoundryApplication.setUris(envs);

         }
         else if (key.equals(Constants.RESOURCES))
         {
            CloudfoundryApplicationResources resources = new CloudfoundryApplicationResources();
            if (jsonValue.isObject() != null)
            {
               parseCloudfoundryApplicationResources(jsonValue.isObject(), resources);
            }
            cloudfoundryApplication.setResources(resources);

         }
         else if (key.equals(Constants.STAGING))
         {
            Staging staging = new Staging();
            if (jsonValue.isObject() != null)
            {
               parseStaging(jsonValue.isObject(), staging);
            }
            cloudfoundryApplication.setStaging(staging);
         }
      }
   }
   
   private void parseStaging(JSONObject jsonObject, Staging staging)
   {
      for (String key : jsonObject.keySet())
      {
         JSONValue jsonValue = jsonObject.get(key);
         if (key.equals(Constants.MODEL))
         {
            if (jsonValue.isString() != null)
             {
               staging.setModel(jsonValue.isString().stringValue());
             }
         }
         else if (key.equals(Constants.STACK))
         {
            if (jsonValue.isString() != null)
             {
               staging.setStack(jsonValue.isString().stringValue());
             }
         }
      }
   }
   
   private void parseCloudfoundryApplicationResources(JSONObject jsonObject, CloudfoundryApplicationResources resources)
   {
      for (String key : jsonObject.keySet())
      {
         JSONValue jsonValue = jsonObject.get(key);
         if (key.equals(Constants.DISK))
         {
            if (jsonValue.isNumber() != null)
             {
               resources.setDisk((int)jsonValue.isNumber().doubleValue());
             }
         }
         else if (key.equals(Constants.MEMORY))
         {
            if (jsonValue.isNumber() != null)
             {
               resources.setMemory((int)jsonValue.isNumber().doubleValue());
             }
         }
      }
   }

   /**
    * Build {@link JavaScriptObject} from string.
    * 
    * @param json string that contains object
    * @return {@link JavaScriptObject}
    */
   protected static native JavaScriptObject build(String json) /*-{
      try {
         var object = eval('(' + json + ')');
         return object;
      } catch (e) {
         return null;
      }
   }-*/;

}
