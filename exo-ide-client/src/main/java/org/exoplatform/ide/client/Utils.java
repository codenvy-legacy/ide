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
package org.exoplatform.ide.client;


/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $Id: $
 */
public class Utils
{

   /**
    * Emulate String.format(String, String)
    * 
    * @param format format string with <i>format specifiers</i> '%s'
    * @param substitute string by which will be replaced one '%s' format specifier in the format parameter
    * @return format string with one time replaced '%s' by substitute string
    */
   public static String stringFormat(String format, String substitute)
   {
      return Utils.javaScriptDecodeURI(format.replaceFirst("%s", substitute));
   }

   public static String stringFormat(String format)
   {
      return Utils.javaScriptDecodeURI(format);
   }

   public static native String getGadgetURLFromUtil() /*-{
       // gathering the gadget's URL from the properties url of document.URL
       if ($wnd.gadgets != null) {
         return $wnd.gadgets.util.getUrlParameters().url.match(/(.*)\//)[1] + "/";
       } else {
         return "";
       }
     }-*/;

   public static native void expandGadgetWidth() /*-{
       // set width of gadget to 100%
       if ($wnd.frameElement == null) {
         return;
       }
       $wnd.frameElement.style.width = '100%';
     }-*/;
   
   public static native void expandGadgetHeight() /*-{
   // set width of gadget to 100%
   if ($wnd.frameElement == null) {
     return;
   }
   $wnd.frameElement.style.width = '100%';
 }-*/;

   // return if result of JavaScript function string.match(new RegExp(pattern, modifiers)) is not null
   public static native boolean match(String string, String pattern, String modifiers) /*-{
       return (string.match(new RegExp(pattern, modifiers)) !== null);
     }-*/;

   // emulate java.net.URLDecoder.decode(string,"UTF-8"): before calling decodeURIComponent we replacing "+" on "%20", and "%2F" on "/".
   public static native String urlDecode_decode(String string) /*-{
       string = string.replace(/[+]/g,"%20"); // replace "+" on "%20"
       string = string.replace("%2F", "/");  // replace "%2F" on "/"      
       return decodeURIComponent(string);
     }-*/;

   /**
    * @param url
    * @return result of javaScript function <code>unescape(url)</code>
    */
   public static native String unescape(String text) /*-{
       return unescape(text);
     }-*/;

   /**
    * @param url
    * @return result of javaScript function <code>escape(url)</code>
    */
   public static native String escape(String text) /*-{
       return escape(text);
     }-*/;
   
   /**
    * @param url
    * @return result of javaScript function <code>decodeURI(url)</code>
    */
   public static native String javaScriptDecodeURI(String url) /*-{
       return decodeURI(url);
     }-*/;

   /**
    * @param url
    * @return result of javaScript function <code>encodeURI(url)</code>
    */
   public static native String encodeURI(String url) /*-{
       return encodeURI(url);
     }-*/;

   public static native String getUserAgent() /*-{
      return navigator.userAgent.toLowerCase();
   }-*/;   
   
}
