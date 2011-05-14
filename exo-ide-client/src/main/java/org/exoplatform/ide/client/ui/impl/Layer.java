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
package org.exoplatform.ide.client.ui.impl;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class Layer extends AbsolutePanel
{

   /**
    * Layer's ID.
    */
   private String layerId;

   /**
    * Child layers.
    */
   private List<Layer> layers = new ArrayList<Layer>();

   /**
    * Creates a new instance of this Layer.
    * 
    * @param layerId ID of this layer
    */
   public Layer(String layerId)
   {
      this.layerId = layerId;
      DOM.setElementAttribute(getElement(), "layer-id", layerId);

      setWidth("0px");
      setHeight("0px");
      DOM.setStyleAttribute(getElement(), "overflow", "visible");
   }

   /**
    * Adds a child layer.
    * 
    * @param layer layer
    */
   public void addLayer(Layer layer)
   {
      layers.add(layer);
      add(layer, 0, 0);
   }

   /**
    * Resize layer.
    * 
    * @param width new width
    * @param height new height
    */
   public final void resize(int width, int height)
   {
      /*
       * Resize widgets.
       */
      onResize(width, height);
      
      /*
       * Resize child layers.
       */
      for (Layer layer : layers)
      {
         layer.resize(width, height);
      }
   }
   
   /**
    * Resize user defined widgets.
    * Override this method to complete resizing of this Layer.
    * 
    * @param width width
    * @param height height
    */
   public void onResize(int width, int height) {
   }

   /**
    * @see com.google.gwt.user.client.ui.AbsolutePanel#add(com.google.gwt.user.client.ui.Widget)
    */
   @Override
   public void add(Widget w)
   {
      super.add(w, 0, 0);
   }

   /**
    * Get ID of this layer.
    * 
    * @return ID of this layer
    */
   public String getLayerId()
   {
      return layerId;
   }

}
