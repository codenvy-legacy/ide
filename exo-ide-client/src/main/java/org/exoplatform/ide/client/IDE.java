/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;

import org.exoplatform.gwtframework.ui.client.command.Control;
import org.exoplatform.ide.client.application.ApplicationStateSnapshotListener;
import org.exoplatform.ide.client.application.ControlsRegistration;
import org.exoplatform.ide.client.application.IDEForm;
import org.exoplatform.ide.client.application.IDEPresenter;
import org.exoplatform.ide.client.application.MainMenuControlsFormatter;
import org.exoplatform.ide.client.application.NewItemControlsFormatter;
import org.exoplatform.ide.client.application.ViewControlsFormatter;
import org.exoplatform.ide.client.authentication.LoginPresenter;
import org.exoplatform.ide.client.dialogs.AskForValueDialog;
import org.exoplatform.ide.client.dialogs.IDEDialogs;
import org.exoplatform.ide.client.documentation.DocumentationPresenter;
import org.exoplatform.ide.client.edit.TextEditModule;
import org.exoplatform.ide.client.editor.EditorController;
import org.exoplatform.ide.client.editor.EditorFactory;
import org.exoplatform.ide.client.framework.control.ControlsFormatter;
import org.exoplatform.ide.client.framework.control.Docking;
import org.exoplatform.ide.client.framework.editor.EditorNotFoundException;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.outline.ui.OutlineItemCreator;
import org.exoplatform.ide.client.framework.paas.Paas;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.impl.ViewHighlightManager;
import org.exoplatform.ide.client.messages.IdeEditorLocalizationConstant;
import org.exoplatform.ide.client.messages.IdeErrorsLocalizationConstant;
import org.exoplatform.ide.client.messages.IdeLocalizationMessages;
import org.exoplatform.ide.client.messages.IdeNavigationLocalizationConstant;
import org.exoplatform.ide.client.messages.IdeOperationLocalizationConstant;
import org.exoplatform.ide.client.messages.IdePermissionsLocalizationConstant;
import org.exoplatform.ide.client.messages.IdePreferencesLocalizationConstant;
import org.exoplatform.ide.client.messages.IdeTemplateLocalizationConstant;
import org.exoplatform.ide.client.messages.IdeUploadLocalizationConstant;
import org.exoplatform.ide.client.messages.IdeVersionsLocalizationConstant;
import org.exoplatform.ide.client.navigation.NavigationModule;
import org.exoplatform.ide.client.outline.OutlinePresenter;
import org.exoplatform.ide.client.outline.ui.OutlineItemCreatorFactory;
import org.exoplatform.ide.client.output.OutputPresenter;
import org.exoplatform.ide.client.preferences.PreferencesModule;
import org.exoplatform.ide.client.preview.PreviewHTMLPresenter;
import org.exoplatform.ide.client.project.ProjectSupportingModule;
import org.exoplatform.ide.client.properties.PropertiesPresenter;
import org.exoplatform.ide.client.selenium.SeleniumTestsHelper;
import org.exoplatform.ide.editor.api.EditorProducer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS .
 * @author <a href="mailto:dmitry.ndp@exoplatform.com.ua">Dmytro Nochevnov</a>
 * @version $Id: $
*/
public class IDE extends org.exoplatform.ide.client.framework.module.IDE
{

   private ControlsRegistration controlsRegistration;

   private List<Paas> paasRegistration;

   private IDEPresenter presenter;

   /**
    * Initialize constants  for UI 
    */
   public static final IdeLocalizationConstant IDE_LOCALIZATION_CONSTANT = GWT.create(IdeLocalizationConstant.class);

   public static final IdeTemplateLocalizationConstant TEMPLATE_CONSTANT = GWT
      .create(IdeTemplateLocalizationConstant.class);

   public static final IdePreferencesLocalizationConstant PREFERENCES_CONSTANT = GWT
      .create(IdePreferencesLocalizationConstant.class);

   public static final IdeVersionsLocalizationConstant VERSIONS_CONSTANT = GWT
      .create(IdeVersionsLocalizationConstant.class);

   public static final IdeUploadLocalizationConstant UPLOAD_CONSTANT = GWT.create(IdeUploadLocalizationConstant.class);

   public static final IdePermissionsLocalizationConstant PERMISSIONS_CONSTANT = GWT
      .create(IdePermissionsLocalizationConstant.class);

   public static final IdeNavigationLocalizationConstant NAVIGATION_CONSTANT = GWT
      .create(IdeNavigationLocalizationConstant.class);

   public static final IdeEditorLocalizationConstant EDITOR_CONSTANT = GWT.create(IdeEditorLocalizationConstant.class);

   public static final IdeErrorsLocalizationConstant ERRORS_CONSTANT = GWT.create(IdeErrorsLocalizationConstant.class);

   public static final IdeOperationLocalizationConstant OPERATION_CONSTANT = GWT
      .create(IdeOperationLocalizationConstant.class);

   public static final IdeLocalizationMessages IDE_LOCALIZATION_MESSAGES = GWT.create(IdeLocalizationMessages.class);

   public IDE()
   {
      /*
       * Remember browser's window.alert(...) function
       */
      Alert.init();

      /*
       * Create the list of available icons.
       * 
       */
      IDEIconSet.init();

      new IDEDialogs();
      new AskForValueDialog();

      /*
       * Initialize SeleniumTestsHelper.
       * It creates HTML DIV elements and logs to them IDE current application state.
       * ( opened files, active file, current project, etc. )
       */
      new SeleniumTestsHelper();

      new ExceptionThrownEventHandler();

      //new CookieManager(eventBus);
      // new HistoryManager(eventBus, context); // commented to fix the bug with javascript error in IE8 (WBT-321)

      controlsRegistration = new ControlsRegistration();
      controlsRegistration.addControlsFormatter(new NewItemControlsFormatter());
      controlsRegistration.addControlsFormatter(new ViewControlsFormatter());

      paasRegistration = new ArrayList<Paas>();

      IDEForm ideForm = new IDEForm();
      presenter = new IDEPresenter(ideForm, controlsRegistration);

      new EditorController();

      new LoginPresenter();

      new ViewHighlightManager(IDE.eventBus());

      new ApplicationStateSnapshotListener();

      // MODULES INITIALIZATION
      new NavigationModule();
      new ProjectSupportingModule();
      new TextEditModule();

      new PropertiesPresenter();
      new OutputPresenter();
      new OutlinePresenter();
      new PreviewHTMLPresenter();
      new DocumentationPresenter();

      new PreferencesModule();

      //initialize extensions
      for (Extension ext : extensions())
      {
         ext.initialize();
      }
      controlsRegistration.addControlsFormatter(new MainMenuControlsFormatter());
      controlsRegistration.formatControls();
      /*
       * Find a method to disable selection of text and elements on the page ( exclude text fields ).
       */
      //disableTextSelectInternal(RootPanel.get().getElement(), true);
   }

   /**
    * Disables selection of HTML on element. 
    * 
    * @param e element
    * @param disable <b>true</b> disables all selection on element, <b>false</b> enables selection
    */
   private native static void disableTextSelectInternal(Element e, boolean disable)/*-{
                                                                                   if (disable) {
                                                                                   e.ondrag = function () { return false; };
                                                                                   e.onselectstart = function () { return false; };
                                                                                   e.style.MozUserSelect="none"
                                                                                   } else {
                                                                                   e.ondrag = null;
                                                                                   e.onselectstart = null;
                                                                                   e.style.MozUserSelect="text"
                                                                                   }
                                                                                   }-*/;

   /**
    * @see org.exoplatform.ide.client.framework.module.IDE#addControl(org.exoplatform.gwtframework.ui.client.command.Control, org.exoplatform.ide.client.framework.control.event.RegisterControlEvent.DockTarget)
    */
   @Override
   public void addControl(Control<?> control, Docking docking)
   {
      controlsRegistration.addControl(control, docking);
   }

   /**
    * @see org.exoplatform.ide.client.framework.module.IDE#addControl(org.exoplatform.gwtframework.ui.client.command.Control)
    */
   @Override
   public void addControl(Control<?> control)
   {
      controlsRegistration.addControl(control, Docking.NONE);
   }

   /**
    * @see org.exoplatform.ide.client.framework.module.IDE#closeView(java.lang.String)
    */
   @Override
   public void closeView(String viewId)
   {
      presenter.closeView(viewId);
   }

   /**
    * @see org.exoplatform.ide.client.framework.module.IDE#addEditor(org.exoplatform.ide.editor.api.EditorProducer)
    */
   @Override
   public void addEditor(EditorProducer editorProducer)
   {
      EditorFactory.addEditor(editorProducer);
   }

   @Override
   public void openView(View view)
   {
      presenter.openView(view);
   }

   /**
    * @see org.exoplatform.ide.client.framework.module.IDE#getEditor(java.lang.String)
    */
   @Override
   public EditorProducer getEditor(String mimeType) throws EditorNotFoundException
   {
      return EditorFactory.getDefaultEditor(mimeType);
   }

   /**
    * @see org.exoplatform.ide.client.framework.module.IDE#addOutlineItemCreator(java.lang.String, org.exoplatform.ide.client.framework.outline.ui.OutlineItemCreator)
    */
   @Override
   public void addOutlineItemCreator(String mimeType, OutlineItemCreator outlineItemCreator)
   {
      OutlineItemCreatorFactory.addOutlineItemCreator(mimeType, outlineItemCreator);
   }

   /**
    * @see org.exoplatform.ide.client.framework.module.IDE#getOutlineItemCreator(java.lang.String)
    */
   @Override
   public OutlineItemCreator getOutlineItemCreator(String mimeType)
   {
      return OutlineItemCreatorFactory.getOutlineItemCreator(mimeType);
   }

   /**
    * @see org.exoplatform.ide.client.framework.module.IDE#getControls()
    */
   @Override
   public List<Control> getControls()
   {
      return controlsRegistration.getRegisteredControls();
   }

   /**
    * @see org.exoplatform.ide.client.framework.module.IDE#addControlsFormatter(org.exoplatform.ide.client.framework.control.ControlsFormatter)
    */
   @Override
   public void addControlsFormatter(ControlsFormatter controlsFormatter)
   {
      controlsRegistration.addControlsFormatter(controlsFormatter);
   }

   /**
    * @see org.exoplatform.ide.client.framework.module.IDE#getPaases()
    */
   @Override
   public List<Paas> getPaases()
   {
      return paasRegistration;
   }

   /**
    * @see org.exoplatform.ide.client.framework.module.IDE#addPaas(org.exoplatform.ide.client.framework.paas.Paas)
    */
   @Override
   public void addPaas(Paas paas)
   {
      paasRegistration.add(paas);
   }

}
