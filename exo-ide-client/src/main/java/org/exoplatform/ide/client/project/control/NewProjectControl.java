/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.client.project.control;

import java.util.List;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.project.event.CreateProjectEvent;
import org.exoplatform.ide.vfs.shared.Item;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
@RolesAllowed({"administrators", "developers"})
public class NewProjectControl extends SimpleControl implements IDEControl
{
   public static final String ID = "Project/New/Empty Project...";
   
    private static final String TITLE = "Empty Project...";
   
   private static final String PROMPT = "Create Empty Project...";
   
   private List<Item> selectedItems;

   public NewProjectControl()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(PROMPT);
      setEvent(new CreateProjectEvent());
      setImages(IDEImageBundle.INSTANCE.newProject(), IDEImageBundle.INSTANCE.newProjectDisabled());
      setGroup(0);
   }
     

//   @Override
//   public void onCreateProject(CreateProjectEvent event)
//   {
//      createDialogBox().show();
//   }
   
//   private Window createDialogBox() {
//      // Create a dialog box and set the caption text
//      final Window form = new Window("gGGGGGGGGG");
//
//      // Create a table to layout the content
//      VerticalPanel dialogContents = new VerticalPanel();
//      final TextInputBase textInputBase = new TextInputBase(new TextBox().getElement());
//      textInputBase.setTitle("Project name: ");
//      dialogContents.add(textInputBase);
//      dialogContents.setSpacing(4);
//      form.setWidget(dialogContents);
//      // Add a close button at the bottom of the dialog
//      Button closeButton = new Button("Close");
//      
//      closeButton.addClickHandler(new ClickHandler()
//       {
//            public void onClick(ClickEvent event) {
//              form.hide();
//            }
//          });
//      dialogContents.add(closeButton);
//      
//      Button createButton = new Button("Create");
//      
//      createButton.addClickHandler(new ClickHandler()
//       {
//            public void onClick(ClickEvent event) {
//              
//               if (selectedItems == null || selectedItems.get(0) == null)
//                  return;
//               
//               if (selectedItems.size() > 1)
//               {
//                  IDE.EVENT_BUS.fireEvent( new ExceptionThrownEvent("Can't create project you must select only one parent folder"));
//                  return;
//               }
//               if (selectedItems.get(0).getItemType() == ItemType.FILE) 
//               {
//                  IDE.EVENT_BUS.fireEvent( new ExceptionThrownEvent("Can't create project you must select as parent folder"));
//                  return;
//               }
//              ProjectModel model = new ProjectModel();
//              model.setMimeType(Project.PROJECT_MIME_TYPE);
//              model.setName(textInputBase.getText());
//              model.setProjectType("project/java");
//              model.setParent((FolderModel)selectedItems.get(0));
//              System.out.println("NewProjectControl.createDialogBox().new ClickHandler() {...}.onClick()" + textInputBase.getText());
//              try
//            {
//               VirtualFileSystem.getInstance().createProject((Folder)selectedItems.get(0), new AsyncRequestCallback<ProjectModel>(new ProjectUnmarshaller(model))
//               {
//
//                  @Override
//                  protected void onSuccess(ProjectModel result)
//                  {
//                     form.hide();
//                     System.out.println("NewProjectControl.createDialogBox()" + result.getParent());
//                     IDE.EVENT_BUS.fireEvent(new RefreshBrowserEvent(result.getParent()));
//                  }
//
//                  @Override
//                  protected void onFailure(Throwable exception)
//                  {
//                     IDE.EVENT_BUS.fireEvent(new ExceptionThrownEvent(exception,
//                              "Service is not deployed.<br>Resource already exist.<br>Parent folder not found."));
//                  }
//                    
//               });
//            }
//            catch (RequestException e)
//            {
//               e.printStackTrace();
//            }
//            }
//          });
//      dialogContents.add(createButton);
//      
//      if (LocaleInfo.getCurrentLocale().isRTL()) {
//        dialogContents.setCellHorizontalAlignment(
//            closeButton, HasHorizontalAlignment.ALIGN_LEFT);
//
//      } else {
//        dialogContents.setCellHorizontalAlignment(
//            closeButton, HasHorizontalAlignment.ALIGN_RIGHT);
//      }
//      form.center();
//      return form;
//    }

   @Override
   public void initialize(HandlerManager eventBus)
   {
//      IDE.EVENT_BUS.addHandler(CreateProjectEvent.TYPE, this);
//      IDE.EVENT_BUS.addHandler(ItemsSelectedEvent.TYPE, this);
      setVisible(true);
      setEnabled(true);
   }

}
