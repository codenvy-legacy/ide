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
package org.eclipse.jdt.client.refactoring.rename;

import com.google.collide.client.collaboration.CollaborationPropertiesUtil;

import org.eclipse.jdt.client.JdtExtension;
import org.eclipse.jdt.client.internal.corext.codemanipulation.JavaControl;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.event.CollaborationChangedEvent;
import org.exoplatform.ide.client.framework.event.CollaborationChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.java.client.JavaClientBundle;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * Control for rename refactoring.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: RefactoringRenameControl.java Jan 18, 2013 11:32:03 AM azatsarynnyy $
 */
@RolesAllowed("developer")
public class RefactoringRenameControl extends JavaControl
    implements ProjectOpenedHandler, ProjectClosedHandler, CollaborationChangedHandler {

    /**
     * Opened project.
     */
    private ProjectModel project;
    
    /**
     * Active file.
     */
    private FileModel activeFile;

    /**
     * Editor in which active is being edited.
     */
    private Editor activeEditor;    
    
    /**
     * Creates a new instance of this {@link RefactoringRenameControl}
     */
    public RefactoringRenameControl() {
        super(JdtExtension.LOCALIZATION_CONSTANT.refactoringRenameControlId());
        setTitle(JdtExtension.LOCALIZATION_CONSTANT.refactoringRenameControlTitle());
        setPrompt(JdtExtension.LOCALIZATION_CONSTANT.refactoringRenameControlPrompt());
        setEvent(new RefactoringRenameEvent());
        setHotKey("Alt+Shift+R");
        setImages(JavaClientBundle.INSTANCE.blankImage(), JavaClientBundle.INSTANCE.blankImage());

        // TODO remove from context menu while bug with group name will fixed
        //setShowInContextMenu(true);
    }
    
    /**
     * @see org.eclipse.jdt.client.internal.corext.codemanipulation.JavaControl#initialize()
     */
    @Override
    public void initialize() {
        super.initialize();
        
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
        IDE.addHandler(CollaborationChangedEvent.TYPE, this);
    }

    /**
     * @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework.project.ProjectOpenedEvent)
     */
    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        project = event.getProject();
        update();
    }

    /**
     * @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework.project.ProjectClosedEvent)
     */
    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        project = null;
        update();
    }

    /**
     * @see org.exoplatform.ide.client.framework.event.CollaborationChangedHandler#onCollaborationChanged(org.exoplatform.ide.client.framework.event.CollaborationChangedEvent)
     */
    @Override
    public void onCollaborationChanged(CollaborationChangedEvent event) {
        update();
    }
    
    /**
     * Updates state of this command.
     */
    private void update() {
        if (project == null || activeFile == null || activeEditor == null) {
            setEnabled(false);
            setVisible(false);
            return;
        }
        
        if (!activeFile.getMimeType().equals(MimeType.APPLICATION_JAVA)) {
            setEnabled(false);
            setVisible(false);
            return;
        }

        setVisible(true);
        if (CollaborationPropertiesUtil.isCollaborationEnabled(project)) {
            setEnabled(false);
        } else {            
            setEnabled(true);
        }
    }
        
    /**
     * @see org.eclipse.jdt.client.internal.corext.codemanipulation.JavaControl#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
     */
    @Override
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        activeFile = event.getFile();
        activeEditor = event.getEditor();
        update();
    }

}
