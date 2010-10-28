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
package org.exoplatform.ide.generator;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JWildcardType.BoundType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;

import org.exoplatform.ide.client.framework.annotation.RolesAllowed;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Oct 21, 2010 $
 *
 */
public class ControlAnnotationMapGenerator extends ClassAnnotationMapGenerator
{
   public static final String className = org.exoplatform.gwtframework.ui.client.component.command.Control.class
      .getName();

   /**
    * @see org.exoplatform.ide.generator.ClassAnnotationMapGenerator#writeConstructor(com.google.gwt.uibinder.rebind.IndentedWriter, com.google.gwt.core.ext.typeinfo.JClassType, java.lang.String, com.google.gwt.core.ext.GeneratorContext)
    */
   @Override
   protected void writeConstructor(ConsolePrintWriter writer, JClassType interfaceType, String implName,
      GeneratorContext context)
   {
      writer.write("public %s()", implName);
      writer.println();
      writer.write("{");
      writer.println();
      JClassType[] subTypes = getSubTypes(className, context);
      if (subTypes != null)
      {
         writer.write("List<String> values;");
         writer.println();
         for (JClassType type : subTypes)
         {
            if (type.isAnnotationPresent(RolesAllowed.class))
            {
               writer.write("values = new ArrayList<String>();");
               writer.println();
               for (String value : type.getAnnotation(RolesAllowed.class).value())
               {
                  writer.write("values.add(\"" + value + "\");");
                  writer.println();
               }
               writer.write("classAnnotations.put(\"%s\", values);", type.getQualifiedSourceName());
               writer.println();
            }
            
         }
      }
      writer.write("}");
      writer.println();
   }

   /** 
    * @param className name of the super class
    * @param context generator context
    * @return {@link JClassType[]} sub types of the pointed super class
    */
   private JClassType[] getSubTypes(String className, GeneratorContext context)
   {
      try
      {
         JClassType superClass = context.getTypeOracle().getType(className);
         JClassType[] subTypes = context.getTypeOracle().getWildcardType(BoundType.EXTENDS, superClass).getSubtypes();
         return subTypes;
      }
      catch (NotFoundException e)
      {
         e.printStackTrace();
         return null;
      }
   }

}
