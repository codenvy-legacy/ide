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
package org.eclipse.jdt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;

import org.eclipse.jdt.client.NameEnvironment.JSONTypesInfoUnmarshaller;
import org.eclipse.jdt.client.codeassistant.ContentAssistHistory;
import org.eclipse.jdt.client.codeassistant.QualifiedTypeNameHistory;
import org.eclipse.jdt.client.core.JavaCore;
import org.eclipse.jdt.client.core.formatter.FormatterPreferenceItem;
import org.eclipse.jdt.client.core.formatter.FormatterProfilePresenter;
import org.eclipse.jdt.client.create.CreateJavaClassControl;
import org.eclipse.jdt.client.create.CreateJavaClassPresenter;
import org.eclipse.jdt.client.create.CreatePackageControl;
import org.eclipse.jdt.client.create.CreatePackagePresenter;
import org.eclipse.jdt.client.disable.DisableSyntaxErrorHighlightingControl;
import org.eclipse.jdt.client.internal.codeassist.impl.AssistOptions;
import org.eclipse.jdt.client.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.client.internal.corext.codemanipulation.AddGetterSetterControl;
import org.eclipse.jdt.client.internal.corext.codemanipulation.GenerateNewConstructorUsingFieldsControl;
import org.eclipse.jdt.client.internal.corext.codemanipulation.OrganizeImportsControl;
import org.eclipse.jdt.client.internal.corext.codemanipulation.OrganizeImportsPresenter;
import org.eclipse.jdt.client.outline.OutlinePresenter;
import org.eclipse.jdt.client.outline.QuickOutlinePresenter;
import org.eclipse.jdt.client.outline.ShowQuickOutlineControl;
import org.eclipse.jdt.client.packaging.PackageExplorerPresenter;
import org.eclipse.jdt.client.refactoring.RefactoringClientServiceImpl;
import org.eclipse.jdt.client.refactoring.rename.RefactoringRenameControl;
import org.eclipse.jdt.client.refactoring.rename.RefactoringRenamePresenter;
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
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.application.event.ApplicationClosedEvent;
import org.exoplatform.ide.client.framework.application.event.ApplicationClosedHandler;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.editor.AddCodeFormatterEvent;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.userinfo.UserInfo;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedEvent;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedHandler;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.codeassistant.jvm.shared.TypesInfoList;
import org.exoplatform.ide.editor.codeassistant.CodeAssistantClientBundle;
import org.exoplatform.ide.editor.java.client.codeassistant.services.JavaCodeAssistantService;
import org.exoplatform.ide.editor.java.hover.HoverResources;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Jan 20, 2012 1:08:51 PM evgen $
 */
public class JdtExtension extends Extension implements InitializeServicesHandler, UserInfoReceivedHandler,
                                                       ProjectClosedHandler, ApplicationSettingsReceivedHandler, ApplicationClosedHandler,
                                                       SupportedProjectResolver,
                                                       VfsChangedHandler {

    public static String DOC_CONTEXT;

    static String REST_CONTEXT;

    private static final HoverResources resources = GWT.create(HoverResources.class);

    public static final String JAVA_CODE_FORMATTER = "JavaCodeFormatter";

    /** Localization constants. */
    public static final LocalizationConstant LOCALIZATION_CONSTANT = GWT.create(LocalizationConstant.class);

    private static JdtExtension instance;

    private static final Set<String> projectTypes = new HashSet<String>();

    private ContentAssistHistory contentAssistHistory;

    private static final String[] fqns = new String[]{//
                                                      "java.lang.Object",//
                                                      "java.lang.String",//
                                                      "java.lang.System",//
                                                      "java.lang.Boolean",//
                                                      "java.lang.Byte",//
                                                      "java.lang.Character",//
                                                      "java.lang.Class", "java.lang.Cloneable",//
                                                      "java.lang.Double",//
                                                      "java.lang.Error",//
                                                      "java.lang.Exception",//
                                                      "java.lang.Float",//
                                                      "java.lang.Integer",//
                                                      "java.lang.Long",//
                                                      "java.lang.RuntimeException",//
                                                      "java.io.Serializable",//
                                                      "java.lang.Short",//
                                                      "java.lang.StringBuffer",//
                                                      "java.lang.Throwable",//
                                                      "java.lang.Void"};

    static {
        projectTypes.add(ProjectResolver.SERVLET_JSP);
        projectTypes.add(ProjectResolver.SPRING);
        projectTypes.add(ProjectResolver.APP_ENGINE_JAVA);
        projectTypes.add(ProjectType.JAVA.value());
        projectTypes.add(ProjectType.SPRING.value());
        projectTypes.add(ProjectType.JSP.value());
        projectTypes.add(ProjectType.JAR.value());
        projectTypes.add(ProjectType.WAR.value());
        projectTypes.add(ProjectType.ANDROID.value());
    }

    /**
     * The code template context type registry for the java editor.
     *
     * @since 3.0
     */
    private ContextTypeRegistry fCodeTemplateContextTypeRegistry;

    private TemplateStore templateStore;

    private UserInfo userInfo;

    private HashMap<String, String> options = new HashMap<String, String>();

    private FormatterProfilePresenter formatterProfileManager;

    private JdtGinjector injector;

    private void initOptions() {
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
     *
     */
    public JdtExtension() {
        instance = this;
        initOptions();
    }

    /** @see org.exoplatform.ide.client.framework.module.Extension#initialize() */
    @Override
    public void initialize() {
        IDE.getInstance().addControl(new CreateJavaClassControl());
        new PackageExplorerPresenter();
        DisableSyntaxErrorHighlightingControl disableSyntaxErrorHighlightingControl = new DisableSyntaxErrorHighlightingControl();
        CodeAssistantClientBundle.INSTANCE.css().ensureInjected();
        IDE.addHandler(InitializeServicesEvent.TYPE, this);
        IDE.addHandler(UserInfoReceivedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
        IDE.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
        IDE.addHandler(ApplicationClosedEvent.TYPE, this);
        IDE.addHandler(VfsChangedEvent.TYPE, this);
//      new CodeAssistantPresenter(this);
        new JavaCodeController(Utils.getRestContext(), Utils.getWorkspaceName(), disableSyntaxErrorHighlightingControl, this);
        new OutlinePresenter();
        new TypeInfoUpdater();
        new JavaClasspathResolver(this);
        new OrganizeImportsPresenter(IDE.eventBus());
        new RefactoringRenamePresenter();
        IDE.getInstance().addControl(new CleanProjectControl());
        IDE.getInstance().addControl(disableSyntaxErrorHighlightingControl);
        IDE.getInstance().addControl(new OrganizeImportsControl());
        IDE.getInstance().addControl(new CreatePackageControl());
        IDE.getInstance().addControl(new QuickFixControl());
        IDE.getInstance().addControl(new ShowQuickOutlineControl());
        IDE.getInstance().addControl(new AddGetterSetterControl());
        IDE.getInstance().addControl(new GenerateNewConstructorUsingFieldsControl());
        IDE.getInstance().addControl(new ViewJavadocControl());
        IDE.getInstance().addControl(new RefactoringRenameControl());
        IDE.fireEvent(new AddCodeFormatterEvent(new JavaCodeFormatter(), MimeType.APPLICATION_JAVA));

        formatterProfileManager = new FormatterProfilePresenter(IDE.eventBus());
        org.exoplatform.ide.client.framework.preference.Preferences.get().addPreferenceItem(
                new FormatterPreferenceItem(formatterProfileManager));
        new QuickFixPresenter(IDE.eventBus(), this);
        new QuickOutlinePresenter(IDE.eventBus());
        injector = GWT.create(JdtGinjector.class);
        injector.getNewConstructorUsingFields();
        injector.getSetterGetterPresenter();
        TypeInfoStorage.get().clear();
        resources.hover().ensureInjected();
//      new PackagesUpdater(IDE.eventBus(), this, TypeInfoStorage.get());
        new JavadocPresenter(IDE.eventBus(), resources);
    }

    /** @see org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler#onInitializeServices(org.exoplatform.ide
     * .client.framework.application.event.InitializeServicesEvent) */
    @Override
    public void onInitializeServices(InitializeServicesEvent event) {
        DOC_CONTEXT = Utils.getRestContext() + Utils.getWorkspaceName() + "/code-assistant/java/class-doc?fqn=";
        new CreatePackagePresenter(VirtualFileSystem.getInstance());
        new CreateJavaClassPresenter(VirtualFileSystem.getInstance());
        new RefactoringClientServiceImpl(Utils.getRestContext(), Utils.getWorkspaceName(), event.getLoader(), IDE.messageBus());
    }

    private void loadWellKnownClasses(String[] fqns) {
        final JSONTypesInfoUnmarshaller unmarshaller = new JSONTypesInfoUnmarshaller();
        JavaCodeAssistantService.get().getTypesByFqns(fqns, null, new AsyncRequestCallback<TypesInfoList>(unmarshaller) {

            @Override
            protected void onSuccess(TypesInfoList result) {
                if (unmarshaller.typesInfo != null) {
                    for (int i = 0; i < unmarshaller.typesInfo.size(); i++) {
                        JSONObject o = unmarshaller.typesInfo.get(i).isObject();
                        if (o != null && o.containsKey("name")) {
                            TypeInfoStorage.get().putType(o.get("name").isString().stringValue(), o.toString());
                        }
                    }
                }
            }

            @Override
            protected void onFailure(Throwable exception) {
                IDE.fireEvent(new ExceptionThrownEvent(exception));
            }
        });
    }

    public static JdtExtension get() {
        return instance;
    }

    /** @return  */
    public ContextTypeRegistry getTemplateContextRegistry() {
        if (fCodeTemplateContextTypeRegistry == null) {
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

    /** @return  */
    public TemplateStore getTemplateStore() {
        if (templateStore == null)
            templateStore = new TemplateStore();
        return templateStore;
    }

    /** @return  */
    public ContentAssistHistory getContentAssistHistory() {
        if (contentAssistHistory == null) {
            Preferences preferences = GWT.create(Preferences.class);
            contentAssistHistory =
                    ContentAssistHistory.load(preferences, Preferences.CODEASSIST_LRU_HISTORY + userInfo.getUserId());

            if (contentAssistHistory == null)
                contentAssistHistory = new ContentAssistHistory();
        }

        return contentAssistHistory;
    }

    /** @see org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedHandler#onUserInfoReceived(org.exoplatform.ide.client
     * .framework.userinfo.event.UserInfoReceivedEvent) */
    @Override
    public void onUserInfoReceived(UserInfoReceivedEvent event) {
        userInfo = event.getUserInfo();
    }

    /** @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework
     * .project.ProjectClosedEvent) */
    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        TypeInfoStorage.get().clear();
    }

    /** @return  */
    public HashMap<String, String> getOptions() {
        return options;
    }

    /** @see org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedHandler#onApplicationSettingsReceived(org
     * .exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedEvent) */
    @Override
    public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event) {
        ApplicationSettings settings = event.getApplicationSettings();
        if (settings.containsKey(JAVA_CODE_FORMATTER)) {
            options.putAll(formatterProfileManager.getProfile(settings.getValueAsString(JAVA_CODE_FORMATTER))
                                                  .getSettings());
        } else {
            if (formatterProfileManager.getDefault() != null) {
                options.putAll(formatterProfileManager.getDefault().getSettings());
            }
        }
    }

    /** @see org.exoplatform.ide.client.framework.application.event.ApplicationClosedHandler#onApplicationClosed(org.exoplatform.ide
     * .client.framework.application.event.ApplicationClosedEvent) */
    @Override
    public void onApplicationClosed(ApplicationClosedEvent event) {
        if (userInfo == null)
            return;
        Preferences preferences = GWT.create(Preferences.class);
        if (contentAssistHistory != null) {
            ContentAssistHistory.store(contentAssistHistory, preferences,
                                       Preferences.CODEASSIST_LRU_HISTORY + userInfo.getUserId());
            QualifiedTypeNameHistory.getDefault().save();
        }
    }

    /** @see org.eclipse.jdt.client.SupportedProjectResolver#isProjectSupported(java.lang.String) */
    @Override
    public boolean isProjectSupported(String projectType) {
        return projectTypes.contains(projectType);
    }

    /** @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework.application.event.VfsChangedEvent) */
    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        IDE.removeHandler(VfsChangedEvent.TYPE, this);
        loadWellKnownClasses(fqns);
    }

}
