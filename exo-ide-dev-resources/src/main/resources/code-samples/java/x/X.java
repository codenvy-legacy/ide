/*
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
 */
package x;

// Explicit imports

//Do not write

import java.io.IOException;
import java.util.Map;

import javax.naming.InitialContext;
import javax.sql.XADataSource;

// DO NOT USE "TAB" TO INDENT CODE USE *3* SPACES FOR PORTABILITY AMONG EDITORS

/**
 * A description of this class.
 *
 * @see SomeRelatedClass.
 *
 * @version <tt>$Revision: 34360 $</tt>
 * @author  <a href="mailto:{email}">{full name}</a>
 */
public class X extends Y implements Z
{
   public void startService() throws Exception
   {
      // Use the newline for the opening bracket so we can match top
      // and bottom bracket visually

      Class cls = Class.forName(dataSourceClass);
      vendorSource = (XADataSource)cls.newInstance();

      // Jump a line between logically distinct steps and add<
      // line of comment to it
      cls = vendorSource.getClass();

      // Comment lines always start with an uppercase
      // except if it is the second line
      if (properties != null)
      {

         try
         {
         }
         catch (IOException ioe)
         {
         }
         for (Iterator i = props.entrySet().iterator(); i.hasNext();)
         {

            // Get the name and value for the attributes
            Map.Entry entry = (Map.Entry)i.next();
            String attributeName = (String)entry.getKey();
            String attributeValue = (String)entry.getValue();

            // Print the debug message
            log.debug("Setting attribute '" + attributeName + "' to '" + attributeValue + "'");

            // get the attribute
            Method setAttribute = cls.getMethod("set" + attributeName, new Class[]{String.class});

            // And set the value
            setAttribute.invoke(vendorSource, new Object[]{attributeValue});
         }
      }

      // this is a bad comment line because it starts with a lower case
      vendorSource.getXAConnection().close();

      // Bind in JNDI
      bind(new InitialContext(), "java:/" + getPoolName(), new Reference(vendorSource.getClass().getName(), getClass()
         .getName(), null));

      // Block must always be delimited explicitely
      if (0 == 0)
      {
         System.out.println(true);
      }

   }
}
