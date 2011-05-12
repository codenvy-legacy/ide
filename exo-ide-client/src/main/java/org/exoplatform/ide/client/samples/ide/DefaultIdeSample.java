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
package org.exoplatform.ide.client.samples.ide;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

/**
 * Resources for default sample project template.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: DefaultIdeSample.java May 12, 2011 3:23:42 PM vereshchaka $
 *
 */
public interface DefaultIdeSample extends ClientBundle
{
   DefaultIdeSample INSTANCE = GWT.create(DefaultIdeSample.class);

   @Source("data/DataObject.groovy")
   TextResource getDataObjectSource();

   @Source("data/Pojo.groovy")
   TextResource getPojoSource();
   
   @Source("logic/GreetingRESTService.grs")
   TextResource getGreetingRESTServiceSource();
   
   @Source("UI/GreetingGoogleGadget.xml")
   TextResource getGreetingGoogleGadgetSource();
   
}
