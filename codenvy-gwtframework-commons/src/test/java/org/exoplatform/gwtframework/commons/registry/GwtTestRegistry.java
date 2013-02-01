/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */

package org.exoplatform.gwtframework.commons.registry;

import com.google.gwt.junit.client.GWTTestCase;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class GwtTestRegistry extends GWTTestCase
{

   private static String conf =
      "<?xml version=\"1.0\" ?><BackupManager jcr:primaryType=\"exo:registryEntry\">"
         + "<context jcr:primaryType=\"nt:unstructured\">portal/rest</context>"
         + "<repository jcr:primaryType=\"nt:unstructured\">repository</repository>"
         + "<workspace jcr:primaryType=\"nt:unstructured\">collaboration</workspace>" + "</BackupManager>";

   private final static String PROP_NAME = "repository";

   private final static String PROP_VALUE = "repository";

   @Override
   public String getModuleName()
   {
      return "org.exoplatform.gwt.commons.CommonsJUnit";
   }

   //  public void testGetProperties(){
   //    Document  doc = XMLParser.parse(conf);
   //    Registry registry = new Registry(doc);
   //    String value = registry.getProperties(PROP_NAME);
   //    assertEquals(PROP_VALUE, value);
   //  }

}
