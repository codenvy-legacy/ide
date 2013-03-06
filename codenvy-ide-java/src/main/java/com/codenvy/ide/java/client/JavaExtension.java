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
package com.codenvy.ide.java.client;

import com.codenvy.ide.java.client.codeassistant.ContentAssistHistory;
import com.codenvy.ide.java.client.core.JavaCore;
import com.codenvy.ide.java.client.editor.JavaEditorProvider;
import com.codenvy.ide.java.client.internal.codeassist.impl.AssistOptions;
import com.codenvy.ide.java.client.internal.compiler.impl.CompilerOptions;
import com.codenvy.ide.java.client.perspective.DebugPerspectivePresenter;
import com.codenvy.ide.java.client.perspective.JavaPerspectivePresenter;
import com.codenvy.ide.java.client.projectmodel.JavaProject;
import com.codenvy.ide.java.client.projectmodel.JavaProjectModelProvider;
import com.codenvy.ide.java.client.templates.CodeTemplateContextType;
import com.codenvy.ide.java.client.templates.ContextTypeRegistry;
import com.codenvy.ide.java.client.templates.ElementTypeResolver;
import com.codenvy.ide.java.client.templates.ExceptionVariableNameResolver;
import com.codenvy.ide.java.client.templates.FieldResolver;
import com.codenvy.ide.java.client.templates.ImportsResolver;
import com.codenvy.ide.java.client.templates.JavaContextType;
import com.codenvy.ide.java.client.templates.JavaDocContextType;
import com.codenvy.ide.java.client.templates.LinkResolver;
import com.codenvy.ide.java.client.templates.LocalVarResolver;
import com.codenvy.ide.java.client.templates.NameResolver;
import com.codenvy.ide.java.client.templates.StaticImportResolver;
import com.codenvy.ide.java.client.templates.TemplateStore;
import com.codenvy.ide.java.client.templates.TypeResolver;
import com.codenvy.ide.java.client.templates.TypeVariableResolver;
import com.codenvy.ide.java.client.templates.VarResolver;
import com.codenvy.ide.java.client.wizard.NewJavaClassPagePresenter;
import com.codenvy.ide.java.client.wizard.NewJavaProjectPagePresenter;
import com.codenvy.ide.java.client.wizard.NewPackagePagePresenter;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.menu.MainMenuAgent;
import com.codenvy.ide.api.ui.wizard.WizardAgent;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.core.editor.EditorRegistry;
import com.codenvy.ide.extension.Extension;
import com.codenvy.ide.resources.FileType;

import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.rest.MimeType;

import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.event.shared.EventBus;


import java.util.HashMap;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
@Extension(title = "Java Support : syntax highlighting and autocomplete.", id = "ide.ext.java", version = "2.0.0")
public class JavaExtension
{
   private static final String JAVA_PERSPECTIVE = "Java";

   private static final String JAVA_DEBUG_PERSPECTIVE = "Java Debug";

   private static JavaExtension instance;

   private HashMap<String, String> options;

   private ContextTypeRegistry codeTemplateContextTypeRegistry;

   private TemplateStore templateStore;

   private ContentAssistHistory contentAssistHistory;

   /**
    *
    */
   @Inject
   public JavaExtension(ResourceProvider resourceProvider, EditorRegistry editorRegistry,
      final WorkspaceAgent workspace, JavaEditorProvider javaEditorProvider, EventBus eventBus,
      WizardAgent wizardAgent, Provider<NewJavaProjectPagePresenter> wizardProvider, MainMenuAgent mainMenu,
      Provider<NewPackagePagePresenter> packageProvider, Provider<NewJavaClassPagePresenter> classProvider,
      Provider<JavaPerspectivePresenter> javaPerspProvider, Provider<DebugPerspectivePresenter> debugPerspProvider)
   {
      this();
      FileType javaFile = new FileType(JavaClientBundle.INSTANCE.java(), MimeType.APPLICATION_JAVA, "java");
      editorRegistry.register(javaFile, javaEditorProvider);
      resourceProvider.registerFileType(javaFile);
      resourceProvider.registerModelProvider(JavaProject.PRIMARY_NATURE, new JavaProjectModelProvider(eventBus));
      JavaClientBundle.INSTANCE.css().ensureInjected();
      wizardAgent.registerNewProjectWizard("Java Project", "Create new Java Project", JavaProject.PRIMARY_NATURE,
         JavaClientBundle.INSTANCE.newJavaProject(), wizardProvider, JsonCollections.<String> createArray());

      wizardAgent.registerNewResourceWizard(JAVA_PERSPECTIVE, "Package", JavaClientBundle.INSTANCE.packageItem(),
         packageProvider);
      wizardAgent.registerNewResourceWizard(JAVA_PERSPECTIVE, "Java Class", JavaClientBundle.INSTANCE.newClassWizz(),
         classProvider);

      // register Perspectives
      workspace.registerPerspective(JAVA_PERSPECTIVE, null, javaPerspProvider);
      workspace.registerPerspective(JAVA_DEBUG_PERSPECTIVE, null, debugPerspProvider);
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

   /**
    * @return
    */
   public static JavaExtension get()
   {
      return instance;
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
      {
         templateStore = new TemplateStore();
      }
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
         {
            contentAssistHistory = new ContentAssistHistory();
         }
      }

      return contentAssistHistory;
   }
}
