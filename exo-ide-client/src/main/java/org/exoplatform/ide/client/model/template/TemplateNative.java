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
package org.exoplatform.ide.client.model.template;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Represent template, that received from server.
 * <p/>
 * This template will be converted to {@link Template} for
 * correctly displaying in template list grid.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: TemplateNative , 11.04.2011 17:56:51 vereshchaka $
 *
 */
public class TemplateNative extends JavaScriptObject
{
   protected TemplateNative()
   {
   }

   public final native String getName() /*-{
      return this.name;
   }-*/;

   public final native String getDescription() /*-{
      return this.description;
   }-*/;
   
   public final native String getMimeType() /*-{
   return this.mimeType;
}-*/;

   public static final native TemplateNative build(String json) /*-{
      return eval('(' + json + ')');
   }-*/;

}
