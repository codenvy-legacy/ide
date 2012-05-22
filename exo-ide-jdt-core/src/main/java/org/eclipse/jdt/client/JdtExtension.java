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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Window;

import org.eclipse.jdt.client.codeassistant.ContentAssistHistory;
import org.eclipse.jdt.client.codeassistant.QualifiedTypeNameHistory;
import org.eclipse.jdt.client.core.JavaCore;
import org.eclipse.jdt.client.core.formatter.FormatterProfilePresenter;
import org.eclipse.jdt.client.create.CreatePackagePresenter;
import org.eclipse.jdt.client.internal.codeassist.impl.AssistOptions;
import org.eclipse.jdt.client.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.client.internal.corext.codemanipulation.OrganizeImportsControl;
import org.eclipse.jdt.client.internal.corext.codemanipulation.OrganizeImportsPresenter;
import org.eclipse.jdt.client.outline.OutlinePresenter;
import org.eclipse.jdt.client.outline.QuickOutlinePresenter;
import org.eclipse.jdt.client.outline.ShowQuickOutlineControl;
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
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.editor.AddCodeFormatterEvent;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.userinfo.UserInfo;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedEvent;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedHandler;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;

import java.util.HashMap;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Jan 20, 2012 1:08:51 PM evgen $
 */
public class JdtExtension extends Extension implements InitializeServicesHandler, UserInfoReceivedHandler,
   CloseHandler<Window>, ProjectClosedHandler, ApplicationSettingsReceivedHandler
{

   static String DOC_CONTEXT;

   static String REST_CONTEXT;

   public static final String JAVA_CODE_FORMATTER = "JavaCodeFormatter";

   private static JdtExtension instance;

   private ContentAssistHistory contentAssistHistory;

   /**
    * The code template context type registry for the java editor.
    * 
    * @since 3.0
    */
   private ContextTypeRegistry fCodeTemplateContextTypeRegistry;

   private TemplateStore templateStore;

   private UserInfo userInfo;

   private static HashMap<String, String> options = new HashMap<String, String>();

   private FormatterProfilePresenter formatterProfileManager;

   static
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

   }
   
   /**
    * 
    */
   public JdtExtension()
   {
      instance = this;
   }

   /** @see org.exoplatform.ide.client.framework.module.Extension#initialize() */
   @Override
   public void initialize()
   {
      IDE.addHandler(InitializeServicesEvent.TYPE, this);
      IDE.addHandler(UserInfoReceivedEvent.TYPE, this);
      IDE.addHandler(ProjectClosedEvent.TYPE, this);
      IDE.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
      new CodeAssistantPresenter();
      new JavaCodeController();
      new OutlinePresenter();
      new TypeInfoUpdater();
      new CleanProjectCommandHandler();
      new OrganizeImportsPresenter(IDE.eventBus());
      IDE.getInstance().addControl(new CleanProjectControl());
      IDE.getInstance().addControl(new FormatterProfilesControl());
      IDE.getInstance().addControl(new OrganizeImportsControl());
      IDE.getInstance().addControl(new CreatePackageControl());
      IDE.getInstance().addControl(new QuickFixControl());
      IDE.getInstance().addControl(new ShowQuickOutlineControl());
      IDE.fireEvent(new AddCodeFormatterEvent(new JavaCodeFormatter(), MimeType.APPLICATION_JAVA));

      //      Window.addCloseHandler(this);
      formatterProfileManager = new FormatterProfilePresenter(IDE.eventBus());
      new QuickFixPresenter(IDE.eventBus());
      new QuickOutlinePresenter(IDE.eventBus());
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler#onInitializeServices(org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent)
    */
   @Override
   public void onInitializeServices(InitializeServicesEvent event)
   {
      REST_CONTEXT = event.getApplicationConfiguration().getContext();
      DOC_CONTEXT = REST_CONTEXT + "/ide/code-assistant/java/class-doc?fqn=";
      new CreatePackagePresenter(IDE.eventBus(), VirtualFileSystem.getInstance(), IDE.getInstance());
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

   /**
    * @return
    */
   public ContentAssistHistory getContentAssistHistory()
   {
      if (contentAssistHistory == null)
      {
         Preferences preferences = GWT.create(Preferences.class);
         contentAssistHistory =
            ContentAssistHistory.load(preferences, Preferences.CODEASSIST_LRU_HISTORY + userInfo.getName());

         if (contentAssistHistory == null)
            contentAssistHistory = new ContentAssistHistory();
      }

      return contentAssistHistory;
   }

   /**
    * @see org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedHandler#onUserInfoReceived(org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedEvent)
    */
   @Override
   public void onUserInfoReceived(UserInfoReceivedEvent event)
   {
      userInfo = event.getUserInfo();
   }

   /**
    * @see com.google.gwt.event.logical.shared.CloseHandler#onClose(com.google.gwt.event.logical.shared.CloseEvent)
    */
   @Override
   public void onClose(CloseEvent<Window> event)
   {
      if (userInfo == null)
         return;
      Preferences preferences = GWT.create(Preferences.class);
      ContentAssistHistory.store(contentAssistHistory, preferences,
         Preferences.CODEASSIST_LRU_HISTORY + userInfo.getName());
      QualifiedTypeNameHistory.getDefault().save();
   }

   /**
    * @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework.project.ProjectClosedEvent)
    */
   @Override
   public void onProjectClosed(ProjectClosedEvent event)
   {
      TypeInfoStorage.get().clear();
   }

   /**
    * @return
    */
   public HashMap<String, String> getOptions()
   {
      return options;
   }

   /**
    * @see org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedHandler#onApplicationSettingsReceived(org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedEvent)
    */
   @Override
   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      ApplicationSettings settings = event.getApplicationSettings();
      if (settings.containsKey(JAVA_CODE_FORMATTER))
      {
         options.putAll(formatterProfileManager.getProfile(settings.getValueAsString(JAVA_CODE_FORMATTER))
            .getSettings());
      }
      else
      {
         options.putAll(formatterProfileManager.getDefault().getSettings());
      }
   }

}
