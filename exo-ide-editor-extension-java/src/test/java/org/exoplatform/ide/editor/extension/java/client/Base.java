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
package org.exoplatform.ide.editor.extension.java.client;

import com.google.gwt.junit.client.GWTTestCase;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Base Feb 25, 2011 10:59:00 AM evgen $
 *
 */
public class Base extends GWTTestCase
{

   /**
    * @see com.google.gwt.junit.client.GWTTestCase#getModuleName()
    */
//   @Override
   public String getModuleName()
   {
      return "org.exoplatform.ide.editor.EditorTest";
   }
   
   /**
    * Takes in a trusted JSON String and evals it.
    * @param JSON String that you trust
    * @return JavaScriptObject that you can cast to an Overlay Type
    */
   protected native JavaScriptObject parseJson(String json) /*-{
     return eval('(' + json + ')'); ;
   }-*/;

}
