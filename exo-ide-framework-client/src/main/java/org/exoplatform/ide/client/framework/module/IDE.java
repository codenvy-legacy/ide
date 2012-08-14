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

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

import org.exoplatform.gwtframework.ui.client.command.Control;
import org.exoplatform.ide.client.framework.control.ControlsFormatter;
import org.exoplatform.ide.client.framework.control.Docking;
import org.exoplatform.ide.client.framework.outline.OutlineItemCreator;
import org.exoplatform.ide.client.framework.paas.Paas;
import org.exoplatform.ide.client.framewt.framework.ui.api.View;
import org.exoplatform.ide.editor.api.EditorProducer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href=   private static IDE instance;
   
tic final HandlerManager EVENT_BUS =   
   private static HandlerManager eventBus = new SafeHandlerManager();
   
   /**
    * @return the instance
    */
   public static IDE getInstance()
   {
      return instance;
   }
   
   /**
    * Get list of registered extensions.
    * 
    * @return list of registered extensions
    */
   public static List<Extension> getExtensions()
 SafeHandlerManager();

   private static List<Extension> extensions = new ArrayList<Extension>();

   private static IDE instance;

   public static <H extends EventHandler> HandlerRegistration addHandler(GwtEvent.Type<H> type, final H handler)
   {
      return eventBus.addH      return extensions;
   public static void registerExtension(Extension extension)
      extensions.add(extension);
ler(type, h   protected IDE()
      instance = this;
> event)
   {
      ev   
   /**
    * Returns EventBus.
    * 
    * @return EventBus.
    */
ntBus.fireEvent(event);
   }

   public static HandlerManager eventBus()
   {
      return eventBus;
   }

   protected IDE()
   {
      instance =    
   /**
    * Add handler to EventBus.
    * 
    * @param type
    * @param handler
    * @return
    */
   public static <H extends EventHandler> HandlerRegistration addHandler(GwtEvent.Type<H> type, final H handler)
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

      return eventBus.addHandler(type, handler);
xtension> extensions()
   {
      retur    * Remove handler from EventBus.
    * 
    * @param type
    * @param handler
    * Add control to main menu/tool bar or status bar
    * 
    public static <H extends EventHandler> void removeHandler(GwtEvent.Type<H> type, final H handler)
cking where control dock(toolbar/statusbar)
    */
   public abstract v      eventBus.removeHandler(type, handler);
?> control, Docking docking)   /**
    * Fire event to EventBus.
    * 
    * @param event
    */
   public static void fireEvent(GwtEvent<?> event)
control control to be added
    */
   public abstract void addCon      eventBus.fireEvent(event);

       
   
   
o be added
    */
   public abstract void addControlsFormatter(ControlsFormatter controlsFormatter);

   /**
    * Get list of controls.
    * 
    * @return
    */
   public abstract List<Control> getControls();

   /**
    * Open {@link View}
    * 
    * @param view to open
    */
   public abstract void openView(View view);

   /**
    * Close view
    * 
    * @param viewId ID of view
    */
   public abstract void closeView(String viewId);

   /**
    * Add new editor extension
    * 
    * @param editorProducer
    */
   public abstract void addEditor(EditorProducer editorProducer);

   /**
    * Get    
ditorProducer for mimeType
    * 
    * @param mimeType of file
    * @return {@link EditorProducer} for mimeType
    * @throws    * Close {@link View}
ception if {@link EditorProducer} not found for mimeType
    */
   public abstract EditorProducer getEdi   
   
   
//   /**
//    * Add new editor.
//    * 
//    * @param editor
//    */
//   public abstract void addEditor(Editor editor);
//
//   /**
//    * Returns array of EditorBuilder for mimeType
//    * 
//    * @param mimeType of file
//    * @return {@link EditorBuilder} for mimeType
//    * @throws EditorNotFoundException if {@link EditorProducer} not found for mimeType
//    */
//   public abstract Editor[] getEditors(String mimeType) throws EditorNotFoundException;
   */
   public abstract void addOutlineItemCreator(String mimeType, OutlineItemCreator outlineItemCreator);

   /**
    * Get OutlineItemCreator for mimeType
    * 
    * @param mimeType of file
    * @return {@link OutlineItemCreator} for mimeType
    */
   public abstract OutlineItemCreator getOutlineItemCreator(String mimeType);

   /**
    * Returns FileTypeRegistry. 
s.    * @return
   public abstract FileTypeRegistry getFileTypeRegistry();
   
   
s paas);

   public abstract List<PaaS> getPaaSes();

   public abstract void registerPaaS(PaaS paas);
}
