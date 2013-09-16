/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
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
