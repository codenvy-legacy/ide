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
package org.exoplatform.ide.editor.codeassistant;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: ExamplesBuandle Feb 25, 2011 9:37:15 AM evgen $
 *
 */
public interface ExamplesBundle extends ClientBundle
{
   ExamplesBundle INSTANCE = GWT.create(ExamplesBundle.class);

   @Source("org/exoplatform/ide/editor/public/example/example.txt")
   TextResource htmlExample();

   @Source("org/exoplatform/ide/editor/public/example/jsTest.txt")
   TextResource jsExample();

   @Source("org/exoplatform/ide/editor/public/example/googleGadget.txt")
   TextResource googleGadgetExample();
   
   @Source("org/exoplatform/ide/editor/public/example/xmlEx.txt")
   TextResource xmlExample();
   
   @Source("org/exoplatform/ide/editor/public/example/netvibes.txt")
   TextResource netvibesExample();

   @Source("org/exoplatform/ide/editor/public/example/pojo.txt")
   TextResource groovyExample();
   
   @Source("org/exoplatform/ide/editor/public/example/service.txt")
   TextResource groovyServiceExample();   
   
   @Source("org/exoplatform/ide/editor/public/example/dataObject.txt")
   TextResource dataObjectExample();   
   
   @Source("org/exoplatform/ide/editor/public/example/groovyTemplate.txt")   
   TextResource groovyTemplateExample();

   @Source("org/exoplatform/ide/editor/public/example/java.txt")   
   TextResource javaExample();   
}
