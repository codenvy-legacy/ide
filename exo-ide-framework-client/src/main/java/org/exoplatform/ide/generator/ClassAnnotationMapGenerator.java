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
import com.google.gwt.uibinder.rebind.IndentedWriter;

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
   protected void doGenerate(JClassType interfaceType, String implName, IndentedWriter writer, GeneratorContext context)
   {
      writeImports(writer);
      writeClassIntro(interfaceType, implName, writer);
      writeFieldsIntro(writer);
      writeConstructor(writer, interfaceType, implName, context);
      writeMethodIntro(writer);
      writeOutro(writer);
   }

   /**
    * Writes implemented method source.
    * 
    * @param writer source writer
    */
   private void writeMethodIntro(IndentedWriter writer)
   {
      writer.write("public HashMap<String, List<String>> getClassAnnotation()");
      writer.newline();
      writer.write("{");
      writer.write("return classAnnotations;");
      writer.write("}");
   }

   /**
    * Writes class fields describtion and initialization.
    * 
    * @param writer source writer
    */
   private void writeFieldsIntro(IndentedWriter writer)
   {
      writer.write("private static HashMap<String, List<String>> classAnnotations = new HashMap<String, List<String>>();");
      writer.newline();
   }

   /**
    * Writes imports source of the class.
    * 
    * @param writer source writer
    */
   private void writeImports(IndentedWriter writer)
   {
      writer.write(IMPORT, java.util.HashMap.class.getName());
      writer.newline();
      writer.write(IMPORT, java.util.List.class.getName());
      writer.newline();
      writer.write(IMPORT, java.util.ArrayList.class.getName());
      writer.newline();
   }
   
   /**
    * Writes the source of class introduction
    * 
    * @param interfaceType implemented interface type
    * @param implName class name
    * @param writer source write
    */
   protected void writeClassIntro(JClassType interfaceType, String implName, IndentedWriter writer)
   {
      writer.write("public class %1$s implements %2$s {", implName, interfaceType.getName());
      writer.indent();
      writer.newline();
   }
   
   protected void writeOutro(IndentedWriter writer)
   {
      writer.outdent();
      writer.write("}");
   }
   
   /**
    * @param writer source write
    * @param interfaceType implemented interface type
    * @param implName class name
    * @param context generator context
    */
   abstract protected void writeConstructor(IndentedWriter writer, JClassType interfaceType, String implName, GeneratorContext context);
 
}
