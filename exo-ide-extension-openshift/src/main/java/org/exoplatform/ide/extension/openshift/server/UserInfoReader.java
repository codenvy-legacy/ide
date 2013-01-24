/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.openshift.server;

import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonParser;
import org.everrest.core.impl.provider.json.JsonValue;
import org.everrest.core.impl.provider.json.JsonWriter;
import org.exoplatform.ide.extension.openshift.shared.AppInfo;
import org.exoplatform.ide.extension.openshift.shared.RHUserInfo;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class UserInfoReader implements ExpressResponseReader<RHUserInfo>
{
   private static final Pattern TD_DATE_FORMAT = Pattern
      .compile("(\\d{4})-(\\d{2})-(\\d{2})[Tt](\\d{2}):(\\d{2}):(\\d{2})(\\.(\\d{1,3}))?([+-])((\\d{2}):(\\d{2}))");

   private final boolean appsInfo;

   public UserInfoReader(boolean appsInfo)
   {
      this.appsInfo = appsInfo;
   }

   @Override
   public RHUserInfo readObject(InputStream in) throws ParsingResponseException
   {
      try
      {
         JsonParser jsonParser = new JsonParser();
         jsonParser.parse(in);
         JsonValue resultJson = jsonParser.getJsonObject().getElement("data");
         String resultSrc = resultJson.getStringValue();
         jsonParser.parse(new StringReader(resultSrc));
         JsonValue userInfoJson = jsonParser.getJsonObject().getElement("user_info");
         JsonValue namespace = userInfoJson.getElement("namespace");
         RHUserInfo rhUserInfo = new RHUserInfoImpl( //
            userInfoJson.getElement("rhc_domain").getStringValue(), //
            userInfoJson.getElement("uuid").getStringValue(), //
            userInfoJson.getElement("rhlogin").getStringValue(), //
            namespace == null ? "Doesn't exist" : namespace.getStringValue() //
         );

         if (appsInfo)
         {
            JsonValue appsInfoJson = jsonParser.getJsonObject().getElement("app_info");
            if (appsInfoJson != null)
            {
               Iterator<String> keys = appsInfoJson.getKeys();
               List<AppInfo> l = new ArrayList<AppInfo>();
               while (keys.hasNext())
               {
                  String app = keys.next();
                  JsonValue appData = appsInfoJson.getElement(app);
                  String type = appData.getElement("framework").getStringValue();
                  String uuid = appData.getElement("uuid").getStringValue();
                  Calendar created = parseDate(appData.getElement("creation_time").getStringValue());
                  l.add(new AppInfoImpl( //
                     app, //
                     type, //
                     gitUrl(rhUserInfo, app, uuid), //
                     publicUrl(rhUserInfo, app), //
                     created != null ? created.getTimeInMillis() : -1 //
                  ));
               }
               rhUserInfo.setApps(l);
            }
         }
         return rhUserInfo;
      }
      catch (JsonException jsone)
      {
         throw new ParsingResponseException(jsone.getMessage(), jsone);
      }
   }

   private String gitUrl(RHUserInfo userInfo, String app, String uuid)
   {
      StringBuilder sb = new StringBuilder();
      sb.append("ssh://");
      sb.append(uuid);
      sb.append('@');
      sb.append(app);
      sb.append('-');
      sb.append(userInfo.getNamespace());
      sb.append('.');
      sb.append(userInfo.getRhcDomain());
      sb.append("/~/git/");
      sb.append(app);
      sb.append(".git/");
      return sb.toString();
   }

   private String publicUrl(RHUserInfo userInfo, String app)
   {
      StringBuilder sb = new StringBuilder();
      sb.append("http://");
      sb.append(app);
      sb.append('-');
      sb.append(userInfo.getNamespace());
      sb.append('.');
      sb.append(userInfo.getRhcDomain());
      sb.append('/');
      return sb.toString();
   }

   private Calendar parseDate(String date)
   {
      Matcher m = TD_DATE_FORMAT.matcher(date);
      if (m.matches())
      {
         int t = m.group(9).equals("+") ? 1 : -1;
         Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
         c.set(Calendar.YEAR, Integer.parseInt(m.group(1)));
         c.set(Calendar.MONTH, Integer.parseInt(m.group(2)) - 1);
         c.set(Calendar.DATE, Integer.parseInt(m.group(3)));
         c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(m.group(4)));
         c.set(Calendar.MINUTE, Integer.parseInt(m.group(5)));
         c.set(Calendar.SECOND, Integer.parseInt(m.group(6)));
         c.set(Calendar.MILLISECOND, m.group(7) == null ? 0 : Integer.parseInt(m.group(8)));
         int zoneOffset =
            t * (Integer.parseInt(m.group(11)) * 60 * 60 * 1000 + Integer.parseInt(m.group(12)) * 60 * 1000);
         c.set(Calendar.ZONE_OFFSET, zoneOffset);
         return c;
      }
      // Unsupported format of date.
      return null;
   }
}
