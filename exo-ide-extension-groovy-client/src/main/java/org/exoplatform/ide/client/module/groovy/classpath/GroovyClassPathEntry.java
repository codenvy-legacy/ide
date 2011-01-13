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
package org.exoplatform.ide.client.module.groovy.classpath;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Description of one source entry in groovy classpath.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 24, 2010 $
 *
 */
public class GroovyClassPathEntry extends JavaScriptObject
{
   /**
    * Special for {@link JavaScriptObject}.
    */
   protected GroovyClassPathEntry()
   {
   }

   /**
    * @return {@link String} kind of source
    */
   public final native String getKind() /*-{
      return this.kind;
   }-*/;

   /**
    * @return {@link String} path to source
    */
   public final native String getPath() /*-{
      return this.path;
   }-*/;

   /**
    * Build instance of {@link GroovyClassPathEntry} from JSON.
    * 
    * @param json JSON object
    * @return {@link GroovyClassPathEntry} instance
    */
   public static final native GroovyClassPathEntry build(String json) /*-{
      try 
      {
         var object = eval('(' + json + ')');
         return object;
      }
      catch (e)
      {
         return null;
      }
   }-*/;

   /**
    * Build instance of {@link GroovyClassPathEntry} with pointed kind and path to source.
    * 
    * @param kind source kind
    * @param path source path
    * @return {@link GroovyClassPathEntry} instance
    */
   public static final native GroovyClassPathEntry build(String kind, String path) /*-{
      var json = {};
      json.kind = kind;
      json.path = path;
      return json;
   }-*/;
}
