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

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Oct 21, 2010 $
 *
 */
public abstract class ClassAnnotationMapGenerator extends BaseGenerator
{
   /**
    * @see org.exoplatform.ide.generator.BaseGenerator#doGenerate(com.google.gwt.core.ext.typeinfo.JClassType, java.lang.String, com.google.gwt.uibinder.rebind.IndentedWriter, com.google.gwt.core.ext.GeneratorContext)
    */
   @Override
   protected void doGenerate(JClassType interfaceType, String implName, ConsolePrintWriter writer,
      GeneratorContext context)
   {
      writeImports(writer);
      writeClassIntro(interfaceType, implName, writer);
      writeConstructor(writer, interfaceType, implName, context);
      writeOutro(writer);
   }

   /**
    * Writes imports source of the class.
    * 
    * @param writer source writer
    */
   private void writeImports(ConsolePrintWriter writer)
   {
      writer.write(IMPORT, java.util.HashMap.class.getName());
      writer.println();
      writer.write(IMPORT, java.util.List.class.getName());
      writer.println();
      writer.write(IMPORT, java.util.ArrayList.class.getName());
      writer.println();
   }

   /**
    * Writes the source of class introduction
    * 
    * @param interfaceType implemented interface type
    * @param implName class name
    * @param writer source write
    */
   protected void writeClassIntro(JClassType interfaceType, String implName, ConsolePrintWriter writer)
   {
      writer.write("public class %1$s extends %2$s {", implName, interfaceType.getName());
      writer.println();
   }

   protected void writeOutro(ConsolePrintWriter writer)
   {
      writer.write("}");
   }

   /**
    * @param writer source write
    * @param interfaceType implemented interface type
    * @param implName class name
    * @param context generator context
    */
   abstract protected void writeConstructor(ConsolePrintWriter writer, JClassType interfaceType, String implName,
      GeneratorContext context);

}
