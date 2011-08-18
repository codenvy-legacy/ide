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
package org.exoplatform.ide.shell.client;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.shell.client.CloudShell;
import org.exoplatform.ide.shell.client.Constants;
import org.exoplatform.ide.shell.shared.CLIResource;
import org.exoplatform.ide.shell.shared.CLIResourceParameter;
import org.exoplatform.ide.shell.shared.CLIResourceParameter.Type;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Aug 4, 2011 4:34:11 PM anya $
 *
 */
public class CLIResourceUnmarshaller implements Unmarshallable, Constants
{
   private Set<CLIResource> resources;

   public CLIResourceUnmarshaller(Set<CLIResource> resources)
   {
      this.resources = resources;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response)
    */
   @Override
   public void unmarshal(Response response) throws UnmarshallerException
   {
      try
      {
         JSONArray array = JSONParser.parseStrict(response.getText()).isArray();
         if (array == null || array.size() <= 0)
         {
            return;
         }

         for (int i = 0; i < array.size(); i++)
         {
            CLIResource cliResource = new CLIResource();
            JSONObject jsonRes = array.get(i).isObject();
            cliResource.setCommand(getStringSet(jsonRes.get(COMMAND).isArray()));
            cliResource.setPath(jsonRes.get(PATH).isString().stringValue());
            cliResource.setMethod(jsonRes.get(METHOD).isString().stringValue());

            if (jsonRes.get(DESCRIPTION) != null && jsonRes.get(DESCRIPTION).isString() != null)
            {
               cliResource.setDescription(jsonRes.get(DESCRIPTION).isString().stringValue());
            }

            if (jsonRes.containsKey(CONSUMES))
            {
               cliResource.setConsumes(getStringSet(jsonRes.get(CONSUMES).isArray()));
            }
            if (jsonRes.containsKey(PRODUCES))
            {
               cliResource.setProduces(getStringSet(jsonRes.get(PRODUCES).isArray()));
            }
            if (jsonRes.containsKey(PARAMS))
            {
               cliResource.setParams(getParams(jsonRes.get(PARAMS).isArray()));
            }
            resources.add(cliResource);
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         throw new UnmarshallerException(CloudShell.messages.commandsUnmarshallerError());
      }
   }

   private Set<String> getStringSet(JSONArray array)
   {
      Set<String> set = new HashSet<String>();
      if (array == null || array.size() <= 0)
      {
         return set;
      }

      for (int i = 0; i < array.size(); i++)
      {
         set.add(array.get(i).isString().stringValue());
      }
      return set;
   }

   private Set<CLIResourceParameter> getParams(JSONArray array)
   {
      Set<CLIResourceParameter> set = new HashSet<CLIResourceParameter>();
      if (array == null || array.size() <= 0)
      {
         return set;
      }

      for (int i = 0; i < array.size(); i++)
      {
         CLIResourceParameter parameter = new CLIResourceParameter();
         JSONObject jsonParam = array.get(i).isObject();
         parameter.setName(jsonParam.get(NAME).isString().stringValue());
         parameter.setMandatory(jsonParam.get(MANDATORY).isBoolean().booleanValue());
         if (jsonParam.containsKey(OPTIONS))
         {
            parameter.setOptions(getStringSet(jsonParam.get(OPTIONS).isArray()));
         }
         parameter.setHasArg(jsonParam.get(HAS_ARG).isBoolean().booleanValue());
         parameter.setType(Type.valueOf(jsonParam.get(TYPE).isString().stringValue()));
         set.add(parameter);
      }
      return set;
   }

}
