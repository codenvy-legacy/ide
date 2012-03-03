/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.eclipse.jdt.client;

import org.eclipse.jdt.client.outline.OutlinePresenter;
import org.eclipse.jdt.client.templates.CodeTemplateContextType;
import org.eclipse.jdt.client.templates.ContextTypeRegistry;
import org.eclipse.jdt.client.templates.ElementTypeResolver;
import org.eclipse.jdt.client.templates.ExceptionVariableNameResolver;
import org.eclipse.jdt.client.templates.FieldResolver;
import org.eclipse.jdt.client.templates.ImportsResolver;
import org.eclipse.jdt.client.templates.JavaContextType;
import org.eclipse.jdt.client.templates.JavaDocContextType;
import org.eclipse.jdt.client.templates.LinkResolver;
import org.eclipse.jdt.client.templates.LocalVarResolver;
import org.eclipse.jdt.client.templates.NameResolver;
import org.eclipse.jdt.client.templates.StaticImportResolver;
import org.eclipse.jdt.client.templates.TemplateStore;
import org.eclipse.jdt.client.templates.TypeResolver;
import org.eclipse.jdt.client.templates.TypeVariableResolver;
import org.eclipse.jdt.client.templates.VarResolver;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Jan 20, 2012 1:08:51 PM evgen $
 */
public class JdtExtension extends Extension implements InitializeServicesHandler
{

   static String DOC_CONTEXT;

   static String REST_CONTEXT;

   private static JdtExtension instance;

   /**
    * The code template context type registry for the java editor.
    * 
    * @since 3.0
    */
   private ContextTypeRegistry fCodeTemplateContextTypeRegistry;

   private TemplateStore templateStore;

   /** @see org.exoplatform.ide.client.framework.module.Extension#initialize() */
   @Override
   public void initialize()
   {
      instance = this;
      IDE.addHandler(InitializeServicesEvent.TYPE, this);
      new CodeAssistantController();
      new JavaCodeController();
      new OutlinePresenter();
      new TypeInfoUpdater();
   }


   /**
    * @see org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler#onInitializeServices(org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent)
    */
   @Override
   public void onInitializeServices(InitializeServicesEvent event)
   {
      REST_CONTEXT = event.getApplicationConfiguration().getContext();
      DOC_CONTEXT = REST_CONTEXT + "/ide/code-assistant/java/class-doc?fqn=";
   }

   public static JdtExtension get()
   {
      return instance;
   }

   /**
    * @return
    */
   public ContextTypeRegistry getTemplateContextRegistry()
   {
      if (fCodeTemplateContextTypeRegistry == null)
      {
         fCodeTemplateContextTypeRegistry = new ContextTypeRegistry();

         CodeTemplateContextType.registerContextTypes(fCodeTemplateContextTypeRegistry);
         JavaContextType contextTypeAll = new JavaContextType(JavaContextType.ID_ALL);

         contextTypeAll.initializeContextTypeResolvers();

         FieldResolver fieldResolver = new FieldResolver();
         fieldResolver.setType("field");
         contextTypeAll.addResolver(fieldResolver);

         LocalVarResolver localVarResolver = new LocalVarResolver();
         localVarResolver.setType("localVar");
         contextTypeAll.addResolver(localVarResolver);
         VarResolver varResolver = new VarResolver();
         varResolver.setType("var");
         contextTypeAll.addResolver(varResolver);
         NameResolver nameResolver = new NameResolver();
         nameResolver.setType("newName");
         contextTypeAll.addResolver(nameResolver);
         TypeResolver typeResolver = new TypeResolver();
         typeResolver.setType("newType");
         contextTypeAll.addResolver(typeResolver);
         ElementTypeResolver elementTypeResolver = new ElementTypeResolver();
         elementTypeResolver.setType("elemType");
         contextTypeAll.addResolver(elementTypeResolver);
         TypeVariableResolver typeVariableResolver = new TypeVariableResolver();
         typeVariableResolver.setType("argType");
         contextTypeAll.addResolver(typeVariableResolver);
         LinkResolver linkResolver = new LinkResolver();
         linkResolver.setType("link");
         contextTypeAll.addResolver(linkResolver);
         ImportsResolver importsResolver = new ImportsResolver();
         importsResolver.setType("import");
         StaticImportResolver staticImportResolver = new StaticImportResolver();
         staticImportResolver.setType("importStatic");
         contextTypeAll.addResolver(staticImportResolver);
         ExceptionVariableNameResolver exceptionVariableNameResolver = new ExceptionVariableNameResolver();
         exceptionVariableNameResolver.setType("exception_variable_name");
         contextTypeAll.addResolver(exceptionVariableNameResolver);
         fCodeTemplateContextTypeRegistry.addContextType(contextTypeAll);
         fCodeTemplateContextTypeRegistry.addContextType(new JavaDocContextType());
         JavaContextType contextTypeMembers = new JavaContextType(JavaContextType.ID_MEMBERS);
         JavaContextType contextTypeStatements = new JavaContextType(JavaContextType.ID_STATEMENTS);
         contextTypeMembers.initializeResolvers(contextTypeAll);
         contextTypeStatements.initializeResolvers(contextTypeAll);
         fCodeTemplateContextTypeRegistry.addContextType(contextTypeMembers);
         fCodeTemplateContextTypeRegistry.addContextType(contextTypeStatements);
      }

      return fCodeTemplateContextTypeRegistry;
   }

   /**
    * @return
    */
   public TemplateStore getTemplateStore()
   {
      if (templateStore == null)
         templateStore = new TemplateStore();
      return templateStore;
   }

}
