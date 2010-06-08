/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client.model.gadget.marshal;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ideall.client.model.gadget.GadgetMetadata;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Window;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class GadgetMetadataUnmarshaler implements Unmarshallable
{

   private HandlerManager eventBus;

   private GadgetMetadata metadata;

   public GadgetMetadataUnmarshaler(HandlerManager eventBus, GadgetMetadata gadgetMetadata)
   {
      this.metadata = gadgetMetadata;
      this.eventBus = eventBus;
   }

   public void unmarshal(Response response) throws UnmarshallerException
   {
      try
      {
         parseGadgetMetadata(response.getText());
      }
      catch (Exception exc)
      {
         String message = "Can't parse gadget meta data at <b>" + metadata.getTitle() + "</b>";
         throw new UnmarshallerException(message);
      }
   }

   private void parseGadgetMetadata(String body)
   {
      JSONObject jsonObj = new JSONObject(toJsonObject(body));

      JSONObject gm = jsonObj.get(GadgetMetadata.GADGETS).isArray().get(0).isObject();

      if (gm.containsKey(GadgetMetadata.AUTHOR))
         metadata.setAuthor(gm.get(GadgetMetadata.AUTHOR).isString().stringValue());

      if (gm.containsKey(GadgetMetadata.AUTHOR_AFFILIATION))
         metadata.setAuthorAffiliation(gm.get(GadgetMetadata.AUTHOR_AFFILIATION).isString().stringValue());

      if (gm.containsKey(GadgetMetadata.AUTHOR_EMAIL))
         metadata.setAuthorEmail(gm.get(GadgetMetadata.AUTHOR_EMAIL).isString().stringValue());

      if (gm.containsKey(GadgetMetadata.AUTHOR_LINK))
         metadata.setAuthorLink(gm.get(GadgetMetadata.AUTHOR_LINK).isString().stringValue());

      if (gm.containsKey(GadgetMetadata.AUTHOR_LOCATION))
         metadata.setAuthorLocation(gm.get(GadgetMetadata.AUTHOR_LOCATION).isString().stringValue());

      if (gm.containsKey(GadgetMetadata.AUTHOR_PHOTO))
         metadata.setAuthorPhoto(gm.get(GadgetMetadata.AUTHOR_PHOTO).isString().stringValue());

      if (gm.containsKey(GadgetMetadata.CATEGORIES))
         metadata.setCategories(toArray(gm.get(GadgetMetadata.CATEGORIES).isArray()));

      if (gm.containsKey(GadgetMetadata.DIRECTORY_TITLE))
         metadata.setDirectoryTitle(gm.get(GadgetMetadata.DIRECTORY_TITLE).isString().stringValue());

      if (gm.containsKey(GadgetMetadata.FEATURES))
         metadata.setFeatures(toArray(gm.get(GadgetMetadata.FEATURES).isArray()));

      if (gm.containsKey(GadgetMetadata.HEIGHT))
         metadata.setHeight((gm.get(GadgetMetadata.HEIGHT).isNumber().doubleValue()));

      if (gm.containsKey(GadgetMetadata.LINKS))
         metadata.setLinks((gm.get(GadgetMetadata.LINKS).isObject()));

      if (gm.containsKey(GadgetMetadata.MODULE_ID))
         metadata.setModuleId((gm.get(GadgetMetadata.MODULE_ID).isNumber().doubleValue()));

      if (gm.containsKey(GadgetMetadata.SCALING))
         metadata.setScaling((gm.get(GadgetMetadata.SCALING).isBoolean().booleanValue()));

      if (gm.containsKey(GadgetMetadata.SCREENSHOT))
         metadata.setScreenshot((gm.get(GadgetMetadata.SCREENSHOT).isString().stringValue()));

      if (gm.containsKey(GadgetMetadata.SCROLLING))
         metadata.setScrolling((gm.get(GadgetMetadata.SCROLLING).isBoolean().booleanValue()));

      if (gm.containsKey(GadgetMetadata.SHOW_IN_DIRECTORY))
         metadata.setShowInDirectory((gm.get(GadgetMetadata.SHOW_IN_DIRECTORY).isBoolean().booleanValue()));

      if (gm.containsKey(GadgetMetadata.SHOWSTATS))
         metadata.setShowStats((gm.get(GadgetMetadata.SHOWSTATS).isBoolean().booleanValue()));

      if (gm.containsKey(GadgetMetadata.SINGLETON))
         metadata.setSingleton((gm.get(GadgetMetadata.SINGLETON).isBoolean().booleanValue()));

      if (gm.containsKey(GadgetMetadata.THUMBNAIL))
         metadata.setThumbnail((gm.get(GadgetMetadata.THUMBNAIL).isString().stringValue()));

      if (gm.containsKey(GadgetMetadata.TITLE))
         metadata.setTitle((gm.get(GadgetMetadata.TITLE).isString().stringValue()));

      if (gm.containsKey(GadgetMetadata.TITLE_URL))
         metadata.setTitleUrl((gm.get(GadgetMetadata.TITLE_URL).isString().stringValue()));

      if (gm.containsKey(GadgetMetadata.URL))
         metadata.setUrl((gm.get(GadgetMetadata.URL).isString().stringValue()));
      
      if (gm.containsKey(GadgetMetadata.IFRAME_URL))
         metadata.setIframeUrl((gm.get(GadgetMetadata.IFRAME_URL).isString().stringValue()));

      if (gm.containsKey(GadgetMetadata.USER_PREFS))
         metadata.setUserPrefs((gm.get(GadgetMetadata.USER_PREFS).isObject()));

      if (gm.containsKey(GadgetMetadata.VIEWS))
         metadata.setViews((gm.get(GadgetMetadata.VIEWS).isObject()));

      if (gm.containsKey(GadgetMetadata.WIDTH))
         metadata.setWidth((gm.get(GadgetMetadata.WIDTH).isNumber().doubleValue()));

      metadata.setSource(body);

   }

   private String[] toArray(JSONArray jsonArray)
   {
      String[] strings = new String[jsonArray.size()];
      for (int i = 0; i < jsonArray.size(); i++)
      {
         strings[i] = jsonArray.get(i).isString().stringValue();
      }
      return strings;
   }

   private static native JavaScriptObject toJsonObject(String jsonString) /*-{
                                                                          return eval('(' + jsonString + ')');
                                                                          }-*/;

}
