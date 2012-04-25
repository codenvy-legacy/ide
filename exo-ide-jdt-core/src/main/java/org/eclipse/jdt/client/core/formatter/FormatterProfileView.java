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
package org.eclipse.jdt.client.core.formatter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

import org.eclipse.jdt.client.JdtClientBundle;
import org.eclipse.jdt.client.core.formatter.FormatterProfilePresenter.Display;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.SelectItem;
import org.exoplatform.ide.client.framework.editor.EditorNotFoundException;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.EditorParameters;
import org.exoplatform.ide.editor.api.EditorProducer;
import org.exoplatform.ide.editor.text.IDocument;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 10:09:02 AM Apr 4, 2012 evgen $
 * 
 */
public class FormatterProfileView extends ViewImpl implements Display
{

   private static FormatterProfileViewUiBinder uiBinder = GWT.create(FormatterProfileViewUiBinder.class);

   @UiField
   FlowPanel editorPanel;

   @UiField
   SelectItem profilesSelect;

   @UiField
   ImageButton okButton;

   @UiField
   ImageButton cancelButton;

   private Editor editor;

   private HandlerManager eventBus;

   interface FormatterProfileViewUiBinder extends UiBinder<Widget, FormatterProfileView>
   {
   }

   public FormatterProfileView()
   {
      super(ID, ViewType.MODAL, "Formatter", null, 600, 700, true);
      add(uiBinder.createAndBindUi(this));
      try
      {
         final HashMap<String, Object> params = new HashMap<String, Object>();
         params.put(EditorParameters.IS_READ_ONLY, true);
         params.put(EditorParameters.IS_SHOW_LINE_NUMER, false);
         params.put(EditorParameters.HOT_KEY_LIST, new ArrayList<String>());
         params.put(EditorParameters.IS_SHOW_OVERVIEW_PANEL, Boolean.FALSE);
         EditorProducer editorProducer = IDE.getInstance().getEditor(MimeType.APPLICATION_JAVA);
         eventBus = new HandlerManager(null);
         editor = editorProducer.createEditor(JdtClientBundle.INSTANCE.formatterSample().getText(), eventBus, params);
         editor.setSize("100%", "100%");
         editorPanel.add(editor);
      }
      catch (EditorNotFoundException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   /**
    * @see org.eclipse.jdt.client.core.formatter.FormatterProfilePresenter.Display#getOkButton()
    */
   @Override
   public HasClickHandlers getOkButton()
   {
      return okButton;
   }

   /**
    * @see org.eclipse.jdt.client.core.formatter.FormatterProfilePresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.eclipse.jdt.client.core.formatter.FormatterProfilePresenter.Display#getProfilesSelect()
    */
   @Override
   public SelectItem getProfilesSelect()
   {
      return profilesSelect;
   }

   /**
    * @see org.eclipse.jdt.client.core.formatter.FormatterProfilePresenter.Display#getDocument()
    */
   @Override
   public IDocument getDocument()
   {
      return editor.getDocument();
   }

   /**
    * @see org.eclipse.jdt.client.core.formatter.FormatterProfilePresenter.Display#getEditorEventBus()
    */
   @Override
   public HandlerManager getEditorEventBus()
   {
      return eventBus;
   }

}
