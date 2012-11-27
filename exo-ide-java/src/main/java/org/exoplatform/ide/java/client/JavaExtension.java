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
package org.exoplatform.ide.java.client;

import com.google.web.bindery.event.shared.EventBus;

import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;

import org.exoplatform.ide.api.resources.ResourceProvider;
import org.exoplatform.ide.core.editor.EditorRegistry;
import org.exoplatform.ide.java.client.codeassistant.ContentAssistHistory;
import org.exoplatform.ide.java.client.core.JavaCore;
import org.exoplatform.ide.java.client.editor.JavaEditorProvider;
import org.exoplatform.ide.java.client.internal.codeassist.impl.AssistOptions;
import org.exoplatform.ide.java.client.internal.compiler.impl.CompilerOptions;
import org.exoplatform.ide.java.client.projectmodel.JavaProject;
import org.exoplatform.ide.java.client.projectmodel.JavaProjectModeProvider;
import org.exoplatform.ide.java.client.templates.CodeTemplateContextType;
import org.exoplatform.ide.java.client.templates.ContextTypeRegistry;
import org.exoplatform.ide.java.client.templates.ElementTypeResolver;
import org.exoplatform.ide.java.client.templates.ExceptionVariableNameResolver;
import org.exoplatform.ide.java.client.templates.FieldResolver;
import org.exoplatform.ide.java.client.templates.ImportsResolver;
import org.exoplatform.ide.java.client.templates.JavaContextType;
import org.exoplatform.ide.java.client.templates.JavaDocContextType;
import org.exoplatform.ide.java.client.templates.LinkResolver;
import org.exoplatform.ide.java.client.templates.LocalVarResolver;
import org.exoplatform.ide.java.client.templates.NameResolver;
import org.exoplatform.ide.java.client.templates.StaticImportResolver;
import org.exoplatform.ide.java.client.templates.TemplateStore;
import org.exoplatform.ide.java.client.templates.TypeResolver;
import org.exoplatform.ide.java.client.templates.TypeVariableResolver;
import org.exoplatform.ide.java.client.templates.VarResolver;
import org.exoplatform.ide.resources.FileType;
import org.exoplatform.ide.rest.MimeType;

import java.util.HashMap;


/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class JavaExtension
{
   
   private static JavaExtension instance;
   
   private HashMap<String, String> options;

   private ContextTypeRegistry codeTemplateContextTypeRegistry;

   private TemplateStore templateStore;
   
   private ContentAssistHistory contentAssistHistory;
   /**
    * 
    */
   @Inject
   public JavaExtension(ResourceProvider resourceProvider, EditorRegistry editorRegistry, JavaEditorProvider javaEditorProvider, EventBus eventBus)
   {
      this();
      FileType javaFile = new FileType(null, MimeType.APPLICATION_JAVA, "java");
      editorRegistry.register(javaFile, javaEditorProvider);
      resourceProvider.registerFileType(javaFile);
      resourceProvider.registerModelProvider(JavaProject.PRIMARY_NATURE, new JavaProjectModeProvider(eventBus));
      JavaClientBundle.INSTANCE.css().ensureInjected();
   }
   
   /**
    * For test use only. 
    */
   public JavaExtension()
   {
      options = new HashMap<String, String>();
      instance = this;
      initOptions();
   }
   

   private void initOptions()
   {
      options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_6);
      options.put(JavaCore.CORE_ENCODING, "UTF-8");
      options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_6);
      options.put(CompilerOptions.OPTION_TargetPlatform, JavaCore.VERSION_1_6);
      options.put(AssistOptions.OPTION_PerformVisibilityCheck, AssistOptions.ENABLED);
      options.put(CompilerOptions.OPTION_ReportUnusedLocal, CompilerOptions.WARNING);
      options.put(CompilerOptions.OPTION_TaskTags, CompilerOptions.WARNING);
      options.put(CompilerOptions.OPTION_ReportUnusedPrivateMember, CompilerOptions.WARNING);
      options.put(CompilerOptions.OPTION_SuppressWarnings, CompilerOptions.DISABLED);
      options.put(JavaCore.COMPILER_TASK_TAGS, "TODO,FIXME,XXX");
      options.put(JavaCore.COMPILER_PB_UNUSED_PARAMETER_INCLUDE_DOC_COMMENT_REFERENCE, JavaCore.ENABLED);
      options.put(JavaCore.COMPILER_DOC_COMMENT_SUPPORT, JavaCore.ENABLED);
      options.put(CompilerOptions.OPTION_Process_Annotations, JavaCore.DISABLED);

   }
   /**
    * @return
    */
   public static JavaExtension get()
   {
      return instance;
   }

   /**
    * @return
    */
   public HashMap<String, String> getOptions()
   {
      return options;
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

   /**
    * @return
    */
   public ContextTypeRegistry getTemplateContextRegistry()
   {
      if (codeTemplateContextTypeRegistry == null)
      {
         codeTemplateContextTypeRegistry = new ContextTypeRegistry();

         CodeTemplateContextType.registerContextTypes(codeTemplateContextTypeRegistry);
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
         codeTemplateContextTypeRegistry.addContextType(contextTypeAll);
         codeTemplateContextTypeRegistry.addContextType(new JavaDocContextType());
         JavaContextType contextTypeMembers = new JavaContextType(JavaContextType.ID_MEMBERS);
         JavaContextType contextTypeStatements = new JavaContextType(JavaContextType.ID_STATEMENTS);
         contextTypeMembers.initializeResolvers(contextTypeAll);
         contextTypeStatements.initializeResolvers(contextTypeAll);
         codeTemplateContextTypeRegistry.addContextType(contextTypeMembers);
         codeTemplateContextTypeRegistry.addContextType(contextTypeStatements);
      }

      return codeTemplateContextTypeRegistry;
   }

   /**
    * @return
    */
   public ContentAssistHistory getContentAssistHistory()
   {
      if (contentAssistHistory == null)
      {
         Preferences preferences = GWT.create(Preferences.class);
         contentAssistHistory =
                  //TODO get user name
            ContentAssistHistory.load(preferences, Preferences.CODEASSIST_LRU_HISTORY + "todo");

         if (contentAssistHistory == null)
            contentAssistHistory = new ContentAssistHistory();
      }

      return contentAssistHistory;
   }
}
