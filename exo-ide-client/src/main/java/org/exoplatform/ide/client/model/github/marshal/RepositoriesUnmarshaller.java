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
package org.exoplatform.ide.client.model.github.marshal;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.Response;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.client.model.github.Repository;

import java.text.ParseException;
import java.util.List;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: RepositoriesUnmarshaller.java Aug 29, 2011 4:56:02 PM vereshchaka $
 *
 */
public class RepositoriesUnmarshaller implements Unmarshallable
{
   private List<Repository> repos;
   
   public RepositoriesUnmarshaller(List<Repository> repositories)
   {
      repos = repositories;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response)
    */
   @Override
   public void unmarshal(Response response) throws UnmarshallerException
   {
      JavaScriptObject json = build(response.getText());
      JSONArray jsonArray = new JSONArray(json);

      for (int i = 0; i < jsonArray.size(); i++)
      {
         JSONValue value = jsonArray.get(i);
         
         Repository repository;
         try
         {
            repository = parseObject(value.isObject());
            repos.add(repository);
         }
         catch (ParseException e)
         {
            throw new UnmarshallerException(e.getMessage());
         }
      }
   }
   
   private Repository parseObject(JSONObject jsonObject) throws ParseException
   {
      DateTimeFormat dtf = DateTimeFormat.getFormat("yyyy/MM/dd HH:mm:ss Z");
      Repository repo = new Repository();
      for (String key : jsonObject.keySet())
      {
         JSONValue jsonValue = jsonObject.get(key);
         if (key.equals("name") && jsonValue.isString() != null)
         {
            repo.setName(jsonValue.isString().stringValue());
         }
         else if (key.equals("description") && jsonValue.isString() != null)
         {
            repo.setDescription(jsonValue.isString().stringValue());
         }
         else if (key.equals("url") && jsonValue.isString() != null)
         {
            repo.setUrl(jsonValue.isString().stringValue());
         }
         else if (key.equals("owner") && jsonValue.isString() != null)
         {
            repo.setOwner(jsonValue.isString().stringValue());
         }
         else if (key.equals("homepage") && jsonValue.isString() != null)
         {
            repo.setHomepage(jsonValue.isString().stringValue());
         }
         else if (key.equals("hasWiki") && jsonValue.isBoolean() != null)
         {
            repo.setHasWiki(jsonValue.isBoolean().booleanValue());
         }
         else if (key.equals("openIssues") && jsonValue.isNumber() != null)
         {
            repo.setOpenIssues((int)jsonValue.isNumber().doubleValue());
         }
         else if (key.equals("hasIssues") && jsonValue.isBoolean() != null)
         {
            repo.setHasIssues(jsonValue.isBoolean().booleanValue());
         }
         else if (key.equals("pushed") && jsonValue.isString() != null)
         {
            repo.setPushed(dtf.parseStrict(jsonValue.isString().stringValue()));
         }
         else if (key.equals("created") && jsonValue.isString() != null)
         {
            repo.setCreated(dtf.parseStrict(jsonValue.isString().stringValue()));
         }
         else if (key.equals("watchers") && jsonValue.isNumber() != null)
         {
            repo.setWatchers((int)jsonValue.isNumber().doubleValue());
         }
         else if (key.equals("forks") && jsonValue.isNumber() != null)
         {
            repo.setForks((int)jsonValue.isNumber().doubleValue());
         }
         else if (key.equals("fork") && jsonValue.isBoolean() != null)
         {
            repo.setFork(jsonValue.isBoolean().booleanValue());
         }
         else if (key.equals("size") && jsonValue.isNumber() != null)
         {
            repo.setSize((int)jsonValue.isNumber().doubleValue());
         }
         else if (key.equals("hasDownloads") && jsonValue.isBoolean() != null)
         {
            repo.setHasDownloads(jsonValue.isBoolean().booleanValue());
         }
         else if (key.equals("isPrivate") && jsonValue.isBoolean() != null)
         {
            repo.setPrivate(jsonValue.isBoolean().booleanValue());
         }
      }
      return repo;
   }
   
   public static native JavaScriptObject build(String json) /*-{
   return eval('(' + json + ')');      
   }-*/;

}
