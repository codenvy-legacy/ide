/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */
package org.exoplatform.ide.extension;

import org.exoplatform.ide.api.ui.menu.MainMenuAgent;

import com.google.gwt.inject.client.AsyncProvider;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JConstructor;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * ExtensionManagerGenerator
 * 
 * <p/>
 * Simple generator that builds an ExtensionManager implementation that serves
 * up NavigatorItems for extensions using the ExtensionDefinition annotation.
 * 
 * @author Rob Cernich
 */
public class ExtensionManagerGenerator extends Generator
{

   @Override
   public String generate(TreeLogger logger, GeneratorContext context, String typeName)
      throws UnableToCompleteException
   {
      TypeOracle typeOracle = context.getTypeOracle();
      JClassType extensionManager = typeOracle.findType(typeName);
      if (extensionManager == null)
      {
         logger.log(TreeLogger.ERROR, "Unable to find metadata for type '" + typeName + "'", null);
         throw new UnableToCompleteException();
      }
      if (extensionManager.isInterface() == null)
      {
         logger.log(TreeLogger.ERROR, extensionManager.getQualifiedSourceName() + " is not an interface", null);
         throw new UnableToCompleteException();
      }

      List<JClassType> extensions = new ArrayList<JClassType>();
      for (JClassType type : typeOracle.getTypes())
      {
         if (type.isAnnotationPresent(Extension.class))
         {
            extensions.add(type);
         }
      }

      String packageName = extensionManager.getPackage().getName();
      String className = extensionManager.getSimpleSourceName() + "Impl";

      generateClass(logger, context, packageName, className, extensions);

      return packageName + "." + className;
   }

   private void generateClass(TreeLogger logger, GeneratorContext context, String packageName, String className,
      List<JClassType> extensions) throws UnableToCompleteException
   {
      PrintWriter pw = context.tryCreate(logger, packageName, className);
      if (pw == null)
      {
         return;
      }

      ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(packageName, className);

      // imports
      composerFactory.addImport(ArrayList.class.getCanonicalName());
      composerFactory.addImport(List.class.getCanonicalName());
      composerFactory.addImport(Extension.class.getCanonicalName());
      composerFactory.addImport(ExtensionManager.class.getCanonicalName());

      // interface
      composerFactory.addImplementedInterface(ExtensionManager.class.getCanonicalName());

      SourceWriter sw = composerFactory.createSourceWriter(context, pw);

      // begin class definition
      sw.indent();

      // fields
      sw.println("private final List<ExtensionDescription> items = new ArrayList<ExtensionDescription>();");

      // constructor
      sw.println("public " + className + "(" + generateAsyncProviders(extensions) + ") {");
      sw.indent();
      for (JClassType extension : extensions)
      {
         sw.println("{");
         sw.println("List<String> deps = new ArrayList<String>();");
         List<String> gatherDependecies = gatherDependecies(extension);
         for (String string : gatherDependecies)
         {
            sw.println("deps.add(\"%s\");", escape(string));
         }
         Extension annotation = extension.getAnnotation(Extension.class);
         //ExtensionDescription description = new ExtensionDescription(id, version, dependencies);
         sw.println("items.add(new ExtensionDescription(\"%s\",\"%s\",deps));", escape(annotation.id()),
            escape(annotation.version()));
         sw.println("}");
      }
      sw.outdent();
      sw.println("}");

      // methods
      // getNavigatorItems
      sw.println("public List<ExtensionDescription> getExtensions() {");
      sw.indentln("return items;");
      sw.println("}");

      // close it out
      sw.outdent();
      sw.println("}");

      context.commit(logger, pw);
   }

   /**
    * @param extensions
    * @return
    */
   private String generateAsyncProviders(List<JClassType> extensions)
   {
      StringBuilder builder = new StringBuilder();
      for (JClassType jClassType : extensions)
      {
         builder.append(String.format("AsyncProvider<%s> %s,", args))
      }
      return null;
   }

   private List<String> gatherDependecies(JClassType extension) throws UnableToCompleteException
   {
      List<String> deps = new ArrayList<String>();
      if (extension.getConstructors().length == 0)
      {
         throw new UnableToCompleteException();
      }
      JConstructor jConstructor = extension.getConstructors()[0];
      JType[] parameterTypes = jConstructor.getParameterTypes();

      for (JType jType : parameterTypes)
      {
         JClassType argType = jType.isClassOrInterface();
         if (argType != null)
         {
            if (argType.isAnnotationPresent(SDK.class))
            {
               deps.add(argType.getAnnotation(SDK.class).title());
            }
            else if (argType.isAnnotationPresent(Extension.class))
            {
               deps.add(argType.getAnnotation(Extension.class).id());
            }
         }
      }
      return deps;
   }
}
