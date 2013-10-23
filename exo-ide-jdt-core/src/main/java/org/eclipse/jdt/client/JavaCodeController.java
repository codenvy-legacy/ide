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
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Timer;

import org.eclipse.jdt.client.core.compiler.IProblem;
import org.eclipse.jdt.client.core.dom.AST;
import org.eclipse.jdt.client.core.dom.ASTNode;
import org.eclipse.jdt.client.core.dom.ASTParser;
import org.eclipse.jdt.client.core.dom.CompilationUnit;
import org.eclipse.jdt.client.disable.CodeAssistantPropertiesUtil;
import org.eclipse.jdt.client.disable.DisableEnableCodeAssistantControl;
import org.eclipse.jdt.client.disable.DisableEnableCodeAssistantEvent;
import org.eclipse.jdt.client.disable.DisableEnableCodeAssistantHandler;
import org.eclipse.jdt.client.event.CancelParseEvent;
import org.eclipse.jdt.client.event.CancelParseHandler;
import org.eclipse.jdt.client.event.ReparseOpenedFilesEvent;
import org.eclipse.jdt.client.event.ReparseOpenedFilesHandler;
import org.eclipse.jdt.client.internal.compiler.env.INameEnvironment;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileContentChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileContentChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.job.Job;
import org.exoplatform.ide.client.framework.job.Job.JobStatus;
import org.exoplatform.ide.client.framework.job.JobChangeEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.client.marking.Markable;
import org.exoplatform.ide.editor.client.marking.Marker;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Java code controller is used for getting AST and updating all modules, that depend on the received AST.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Feb 6, 2012 10:26:58 AM anya $
 */
public class JavaCodeController implements EditorFileContentChangedHandler, EditorActiveFileChangedHandler,
                                           CancelParseHandler, EditorFileOpenedHandler, ReparseOpenedFilesHandler, EditorFileClosedHandler,
                                           DisableEnableCodeAssistantHandler, ProjectOpenedHandler {

    /** Get build log method's path. */
    private final String LOG;

    /** Active file in editor. */
    private FileModel activeFile;

    /** Active project. */
    private ProjectModel currentProject;

    private Set<String> needReparse = new HashSet<String>();

    private Map<String, Timer> workingParsers = new HashMap<String, Timer>();

    private Map<String, Markable> editors = new HashMap<String, Markable>();

    public static INameEnvironment NAME_ENVIRONMENT;

    private static JavaCodeController instance;

    private final SupportedProjectResolver resolver;

    private DisableEnableCodeAssistantControl disableEnableCodeAssistantControl;

    public JavaCodeController(String restContest, String ws, DisableEnableCodeAssistantControl disableEnableCodeAssistantControl,
                              SupportedProjectResolver resolver) {
        this.LOG = restContest + ws + "/maven/log";
        this.resolver = resolver;
        this.disableEnableCodeAssistantControl = disableEnableCodeAssistantControl;
        instance = this;
        IDE.addHandler(EditorFileContentChangedEvent.TYPE, this);
        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);

        IDE.addHandler(CancelParseEvent.TYPE, this);
        IDE.addHandler(EditorFileOpenedEvent.TYPE, this);
        IDE.addHandler(ReparseOpenedFilesEvent.TYPE, this);
        IDE.addHandler(EditorFileClosedEvent.TYPE, this);
        IDE.addHandler(DisableEnableCodeAssistantEvent.TYPE, this);
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
    }

    public static JavaCodeController get() {
        return instance;
    }

    /** @return  */
    private CompilationUnit parseFile(FileModel file) {
        if (!editors.containsKey(file.getId()))
            return null;
        ASTParser parser = ASTParser.newParser(AST.JLS3);
        parser.setSource(((Editor)editors.get(file.getId())).getText());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setUnitName(file.getName().substring(0, file.getName().lastIndexOf('.')));
        parser.setResolveBindings(true);
        parser.setNameEnvironment(NAME_ENVIRONMENT);
        ASTNode ast = parser.createAST(null);
        CompilationUnit unit = (CompilationUnit)ast;
        return unit;
    }

    /**
     * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform
     *      .ide.client.framework.editor.event.EditorActiveFileChangedEvent)
     */
    @Override
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        if (event.getFile() == null)
            return;
        if (event.getFile().getMimeType().equals(MimeType.APPLICATION_JAVA)) {
            activeFile = event.getFile();

            if (activeFile.getProject().getProject() != null)
                return; //TODO: checking is multi module project


            if (!resolver.isProjectSupported(activeFile.getProject().getProjectType()))
                return;
            NAME_ENVIRONMENT = new NameEnvironment(activeFile.getProject().getId());
            if (event.getEditor() instanceof Markable) {
                editors.put(activeFile.getId(), (Markable)event.getEditor());
                if (CodeAssistantPropertiesUtil.isCodeAssistantEnabled(activeFile.getProject()) &&
                    needReparse.contains(activeFile.getId())) {
                    startParsing();
                }
            }
        } else {
            activeFile = null;
        }
    }

    private void asyncParse(final FileModel file, final Timer timer, final Map<Integer, IProblem> problemMap) {
        GWT.runAsync(new RunAsyncCallback() {

            @Override
            public void onSuccess() {
                CompilationUnit unit = parseFile(file);
                if (unit == null) {
                    return;
                }
                if (needReparse.contains(file.getId())) {
                    IProblem[] problems = unit.getProblems();
                    if (problems.length == problemMap.size()) {
                        for (IProblem problem : problems) {
                            if (!problemMap.containsKey(problem.hashCode())) {
                                reparse(problems);
                                return;
                            }
                        }
                        needReparse.remove(file.getId());
                        problemMap.clear();
                    } else {
                        reparse(problems);
                        return;
                    }

                }
                finishJob(file);
                Markable editor = editors.get(file.getId());
                editor.unmarkAllProblems();
                IDE.fireEvent(new UpdateOutlineEvent(unit, file));
                IProblem[] tasks = (IProblem[])unit.getProperty("tasks");
                List<Marker> markers = new ArrayList<Marker>();
                if (tasks != null) {
                    for (IProblem p : tasks) {
                        markers.add(new ProblemImpl(p));
                    }
                }
                boolean hasError = false;
                if (unit.getProblems().length != 0 || editor != null) {
                    for (IProblem p : unit.getProblems()) {
                        markers.add(new ProblemImpl(p));
                        if (p.isError())
                            hasError = true;
                    }
                }
                editor.addProblems(markers.toArray(new Marker[markers.size()]));
                if (hasError)
                    checkBuildStatus();
            }

            /**
             * @param problems
             */
            private void reparse(IProblem[] problems) {
                problemMap.clear();
                for (IProblem problem : problems) {
                    problemMap.put(problem.hashCode(), problem);
                }
                startJob(file);
                timer.schedule(2000);
            }

            @Override
            public void onFailure(Throwable reason) {
                reason.printStackTrace();
            }
        });
    }

    /**
     *
     */
    private void checkBuildStatus() {
        try {
            VirtualFileSystem.getInstance().getItemById(activeFile.getProject().getId(),
                                                        new AsyncRequestCallback<ItemWrapper>(
                                                                new ItemUnmarshaller(new ItemWrapper(activeFile.getProject()))) {

                                                            @Override
                                                            protected void onSuccess(ItemWrapper result) {
                                                                Item item = result.getItem();
                                                                if (item instanceof ProjectModel) {
                                                                    ProjectModel project = (ProjectModel)item;
                                                                    if (project.hasProperty("exoide:build_error")) {
                                                                        getBuildLog((String)project.getPropertyValue("exoide:build_error"));
                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            protected void onFailure(Throwable exception) {
                                                                IDE.eventBus().fireEvent(new ExceptionThrownEvent(exception));
                                                            }
                                                        });
        } catch (RequestException e) {
            IDE.eventBus().fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** @param buildid */
    private void getBuildLog(String buildid) {
        final String requestUrl = LOG + "/" + buildid;

        try {
            AsyncRequest.build(RequestBuilder.GET, requestUrl).header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON)
                        .send(new AsyncRequestCallback<StringBuilder>(new StringUnmarshaller(new StringBuilder())) {

                            @Override
                            protected void onSuccess(StringBuilder result) {
                                IDE.eventBus()
                                   .fireEvent(
                                           new OutputEvent("Can't build classpath:<br>" + "<pre>" + result.toString() + "</pre>",
                                                           Type.ERROR));
                                IDE.eventBus().fireEvent(
                                        new OutputEvent(JdtExtension.LOCALIZATION_CONSTANT.updateDependencySuggest(), Type.WARNING));
                            }

                            @Override
                            protected void onFailure(Throwable exception) {
                                IDE.eventBus().fireEvent(new ExceptionThrownEvent(exception));
                            }
                        });
        } catch (RequestException e) {
            IDE.eventBus().fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /**
     * @see org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler#onEditorFileOpened(org.exoplatform.ide.client
     *      .framework.editor.event.EditorFileOpenedEvent)
     */
    @Override
    public void onEditorFileOpened(EditorFileOpenedEvent event) {
        if (event.getFile().getMimeType().equals(MimeType.APPLICATION_JAVA)) {
            if (event.getFile().getProject().getProject() != null)
                return; //TODO: checking is multi module project

            if (resolver.isProjectSupported(event.getFile().getProject().getProjectType())) {
                needReparse.add(event.getFile().getId());
                startJob(event.getFile());
            }
        }
    }

    /** @param event */
    private void startJob(FileModel file) {
        Job job = new Job(file.getId(), JobStatus.STARTED);
        job.setStartMessage("Initialize Java tooling for " + file.getName());
        IDE.fireEvent(new JobChangeEvent(job));
    }

    /** @see org.eclipse.jdt.client.event.CancelParseHandler#onCancelParse(org.eclipse.jdt.client.event.CancelParseEvent) */
    @Override
    public void onCancelParse(CancelParseEvent event) {
        if (activeFile == null)
            return;
        if (workingParsers.containsKey(activeFile.getId())) {
            workingParsers.get(activeFile.getId()).cancel();
        }
    }

    //   RepeatingCommand com = new RepeatingCommand()
    //   {
    //
    //      @Override
    //      public boolean execute()
    //      {
    //         asyncParse(activeFile);
    //         return false;
    //      }
    //   };

    /**
     * @see org.exoplatform.ide.client.framework.editor.event.EditorFileContentChangedHandler#onEditorFileContentChanged(org.exoplatform
     *      .ide.client.framework.editor.event.EditorFileContentChangedEvent)
     */
    @Override
    public void onEditorFileContentChanged(EditorFileContentChangedEvent event) {
        if (activeFile == null)
            return;
        needReparse.remove(event.getFile().getId());
        finishJob(activeFile);
        if (CodeAssistantPropertiesUtil.isCodeAssistantEnabled(activeFile.getProject()) && editors.containsKey(activeFile.getId())) {
            startParsing();
        }
    }

    /**
     *
     */
    private void startParsing() {
        checklInitializingWork();

        int time = 2000;
        if (workingParsers.containsKey(activeFile.getId())) {
            workingParsers.get(activeFile.getId()).cancel();
            workingParsers.get(activeFile.getId()).schedule(time);
        } else {
            Timer t = new Timer() {
                private final FileModel currentFile;

                private Map<Integer, IProblem> problems = new HashMap<Integer, IProblem>();

                {
                    currentFile = activeFile;
                }

                @Override
                public void run() {
                    asyncParse(currentFile, this, problems);
                }
            };
            workingParsers.put(activeFile.getId(), t);
            t.schedule(time);
        }
    }

    /**
     *
     */
    private void finishJob(FileModel file) {
        Job job = new Job(file.getId(), JobStatus.FINISHED);
        job.setFinishMessage("Java Tooling initialized  for " + file.getName());
        IDE.fireEvent(new JobChangeEvent(job));
    }

    /** @see org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler#onEditorFileClosed(org.exoplatform.ide.client
     * .framework.editor.event.EditorFileClosedEvent) */
    @Override
    public void onEditorFileClosed(EditorFileClosedEvent event) {
        String id = event.getFile().getId();
        editors.remove(id);
        workingParsers.remove(id);
    }

    /** @see org.eclipse.jdt.client.event.ReparseOpenedFilesHandler#onPaerseActiveFile(org.eclipse.jdt.client.event
     * .ReparseOpenedFilesEvent) */
    @Override
    public void onReparseOpenedFiles(ReparseOpenedFilesEvent event) {
        if (editors.isEmpty())
            return;
        for (String id : editors.keySet()) {
            needReparse.add(id);
        }
        startJob(activeFile);
        if (CodeAssistantPropertiesUtil.isCodeAssistantEnabled(activeFile.getProject())) {
            startParsing();
        } else {
            checklInitializingWork();
        }
    }

    public FileModel getActiveFile() {
        return activeFile;
    }

    public INameEnvironment getNameEnvironment() {
        return NAME_ENVIRONMENT;
    }

    @Override
    public void onDisableEnableCodeAssistant(DisableEnableCodeAssistantEvent event) {
        disableEnableCodeAssistantControl.setState(event.isEnable());
        CodeAssistantPropertiesUtil.updateCodeAssistant(currentProject, !event.isEnable());
        if (CodeAssistantPropertiesUtil.isCodeAssistantEnabled(currentProject)) {
            if (!editors.isEmpty()) {
                for (String id : editors.keySet()) {
                    needReparse.add(id);
                }
            }
            checklInitializingWork();
            startParsing();
        }
    }

    /** This need delay start parsing then dependency parsing is finish */
    private void checklInitializingWork() {
        new Timer() {
            public void run() {
                if (!JavaClasspathResolver.getInstance().isStillWork())
                    cancel();
            }
        }.scheduleRepeating(1000);
    }

    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        currentProject = event.getProject();
    }
}
