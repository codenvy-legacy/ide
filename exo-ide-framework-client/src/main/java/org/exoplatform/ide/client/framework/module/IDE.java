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
package org.exoplatform.ide.client.framework.module;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.ui.client.command.Control;
import org.exoplatform.ide.client.framework.control.Docking;
import org.exoplatform.ide.client.framework.editor.EditorNotFoundException;
import org.exoplatform.ide.client.framework.outline.ui.OutlineItemCreator;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.editor.api.EditorProducer;

import com.google.gwt.event.shared.HandlerManager;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: IDE Feb 4, 2011 11:01:38 AM evgen $
 *
 */
public abstract class IDE
{
   //public static final HandlerManager EVENT_BUS = new HandlerManager(null);
   public static final HandlerManager EVENT_BUS = new SafeHandlerManager();

   protected static List<Extension> extensions = new ArrayList<Extension>();

   private static IDE instance;
   
   protected IDE()
   {
      instance = this;
   }

   /**
    * @return the instance
    */
   public static IDE getInstance()
   {
      return instance;
   }

   public static void registerExtension(Extension extension)
   {
      extensions.add(extension);
   }

   /**
    * Add control to main menu/tool bar or status bar
    * @param control
    * @param docking where control dock(toolbar/statusbar) 
    * @param rightDocking control pleased right on toolbar
    */
   public abstract void addControl(Control<?> control, Docking docking, boolean rightDocking);

   /**
    * Add control to main menu
    * 
    * @param control control to be added
    */
   public abstract void addControl(Control<?> control);
   
   /**
    * Get list of controls.
    * 
    * @return
    */
   public abstract List<Control> getControls();
   
   /**
    * Open {@link View}
    * @param view to open
    */   
   public abstract void openView(View view);
   
   /**
    * Close view
    * @param viewId ID of view
    */
   public abstract void closeView(String viewId);

   /**
    * Add new editor extension
    * @param editorProducer
    */
   public abstract void addEditor(EditorProducer editorProducer);
   
   /**
    * Get EditorProducer for mimeType
    * @param mimeType of file
    * @return {@link EditorProducer} for mimeType
    * @throws EditorNotFoundException if {@link EditorProducer} not found for mimeType
    */
   public abstract EditorProducer getEditor(String mimeType) throws EditorNotFoundException;

   /**
    * Add new outline item creator extension
    * @param outlineItemCreator
    */
   public abstract void addOutlineItemCreator(String mimeType, OutlineItemCreator outlineItemCreator);
   
   /**
    * Get OutlineItemCreator for mimeType
    * @param mimeType of file
    * @return {@link OutlineItemCreator} for mimeType
    */
   public abstract OutlineItemCreator getOutlineItemCreator(String mimeType);
   
}
