/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.client.editor;

import com.google.gwt.dom.client.Style.Position;

import org.exoplatform.ide.client.editor.EditorPresenter.Display;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.texteditor.BaseTextEditor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * Dummy Editor UI
 * 
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Jul 27, 2012  
 */
public class EditorView extends Composite implements Display
{

   private final BaseTextEditor editor;

   //   interface EditorUiBinder extends UiBinder<Widget, EditorView>
   //   {
   //   }
   //
   //   private static EditorUiBinder uiBinder = GWT.create(EditorUiBinder.class);
   //
   //   @UiField
   //   TextArea textArea;

   /**
    * Because this class has a default constructor, it can
    * be used as a binder template. In other words, it can be used in other
    * *.ui.xml files as follows:
    * <ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder"
     *   xmlns:g="urn:import:**user's package**">
    *  <g:**UserClassName**>Hello!</g:**UserClassName>
    * </ui:UiBinder>
    * Note that depending on the widget that is used, it may be necessary to
    * implement HasHTML instead of HasText.
    */
   @Inject
   public EditorView(BaseTextEditor editor)
   {
      this.editor = editor;
      initWidget(editor.asWidget());
   }

   /**
    * @see org.exoplatform.ide.client.editor.EditorPresenter.Display#getEditor()
    */
   public Editor getEditor()
   {
      return editor;
   }

//   /**
//   * {@inheritDoc}
//   */
//   public HasText getTextArea()
//   {
//      return textArea;
//   }
}
