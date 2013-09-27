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
package org.exoplatform.ide.client.project.create;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.project.CreateModuleEvent;
import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
@RolesAllowed({"developer"})
public class CreateModuleControl extends SimpleControl implements IDEControl, ItemsSelectedHandler {

    public static final String ID = "Project/New/Create Module...";

    private static final String TITLE = "Create Module...";

    private static final String PROMPT = "Create Module...";

    //   private static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.createProjectFromTemplateTitleControl();

    //   private static final String PROMPT = IDE.IDE_LOCALIZATION_CONSTANT.createProjectFromTemplatePromptControl();

    public CreateModuleControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(IDEImageBundle.INSTANCE.newProject(), IDEImageBundle.INSTANCE.newProjectDisabled());
        //setEvent(new CreateProjectEvent());
        setEvent(new CreateModuleEvent());
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        setVisible(true);
        IDE.addHandler(ItemsSelectedEvent.TYPE, this);
    }

    @Override
    public void onItemsSelected(ItemsSelectedEvent event) {
        if (event.getSelectedItems().size() != 1) {
            setEnabled(false);
            return;
        }

//      if (event.getSelectedItems() == null || event.getSelectedItems().size() != 1)
//      {
//         setEnabled(false);
//         return;
//      }

        Item item = event.getSelectedItems().get(0);
        ProjectModel project = ((ItemContext)item).getProject();
        if (project == null && item instanceof ProjectModel) {
            project = (ProjectModel)item;
        }

        if (project == null) {
            setEnabled(false);
            return;
        }

        setEnabled(ProjectType.MultiModule.equals(ProjectType.fromValue(project.getProjectType())));

      /*
      Item selectedItem = event.getSelectedItems().get(0);
      if (!(selectedItem instanceof ItemContext))
      {
         setEnabled(false);
         return;
      }

      ItemContext context = (ItemContext)selectedItem;
      if (context.getProject() == null)
      {
         setEnabled(false);
         return;
      }

      setEnabled(ProjectType.MultiModule.equals(ProjectType.fromValue(context.getProject().getProjectType())));
      */
    }

}
