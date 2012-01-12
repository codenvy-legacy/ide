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

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;

import java.io.PrintWriter;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Oct 21, 2010 $
 *
 */
public abstract class BaseGenerator extends Generator
{
   protected static final String IMPORT = "import %1$s;";

   protected static final String PACKAGE = "package %s;";

   /**
    * @see com.google.gwt.core.ext.Generator#generate(com.google.gwt.core.ext.TreeLogger, com.google.gwt.core.ext.GeneratorContext, java.lang.String)
    */
   @Override
   public String generate(TreeLogger logger, GeneratorContext context, String typeName)
      throws UnableToCompleteException
   {
      JClassType interfaceType = getInterfaceType(context.getTypeOracle(), typeName, logger);

      String packageName = interfaceType.getPackage().getName();

      //Create implementation class name
      String implName = interfaceType.getName().replace(".", "_") + "Impl";
      PrintWriter printWriter = context.tryCreate(logger, packageName, implName);
      if (printWriter != null)
      {
         ConsolePrintWriter writer = new ConsolePrintWriter(printWriter);
         //Write package information to generating class
         writer.write(PACKAGE, packageName);
         writer.println();
         // Generate source of the class
         doGenerate(interfaceType, implName, writer, context);
         context.commit(logger, printWriter);
      }
      return packageName + "." + implName;
   }

   protected JClassType getInterfaceType(TypeOracle oracle, String s, TreeLogger treeLogger)
      throws UnableToCompleteException
   {
      JClassType interfaceType;
      try
      {
         interfaceType = oracle.getType(s);
      }
      catch (NotFoundException e)
      {
         treeLogger.log(TreeLogger.ERROR,
            String.format("%s: Could not find the interface [%s]. %s", e.getClass().getName(), s, e.getMessage()));
         throw new UnableToCompleteException();
      }
      return interfaceType;
   }

   /**
    * @param interfaceType interface for generating class
    * @param implName name of the generating class
    * @param writer source writer
    * @param context generator context
    */
   abstract protected void doGenerate(JClassType interfaceType, String implName, ConsolePrintWriter writer,
      GeneratorContext context);
}
