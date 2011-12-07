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
package org.exoplatform.ide.editor.java.client.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.editor.codeassistant.util.ModifierHelper;
import org.exoplatform.ide.editor.java.client.codeassistant.services.JavaCodeAssistantService;
import org.exoplatform.ide.editor.java.client.model.ShortTypeInfo;
import org.exoplatform.ide.editor.java.client.model.TypeSelectedCallback;
import org.exoplatform.ide.editor.java.client.model.Types;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}:  Dec 2, 2011 4:44:04 PM evgen $
 *
 */
public class TypeSearchPresenter implements ViewClosedHandler
{

   public interface Display extends IsView
   {
      String ID = "ideTypeSeqrchView";

      void setTitle(String header);

      HasClickHandlers getSearchButton();

      HasClickHandlers getCancelButton();

      HasClickHandlers getOkButton();

      ListGridItem<ShortTypeInfo> getTypesList();

      HasValue<String> getSearchInput();

   }

   private Display display;

   private HandlerRegistration addHandler;

   private Types type;

   private String projectId;

   private TypeSelectedCallback callback;

   private ShortTypeInfo selectedType;

   /**
    * 
    */
   public TypeSearchPresenter(String title, Types type, String projectId, TypeSelectedCallback callback)
   {
      this.type = type;
      this.projectId = projectId;
      this.callback = callback;
      addHandler = IDE.addHandler(ViewClosedEvent.TYPE, this);
      display = GWT.create(Display.class);
      display.setTitle(title);
      IDE.getInstance().openView(display.asView());
      bind();
   }

   /**
    * 
    */
   private void bind()
   {
      display.getCancelButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(Display.ID);
         }
      });

      display.getSearchButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            doSearch();
         }
      });

      display.getOkButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            typesSelected();
         }
      });

      display.getTypesList().addDoubleClickHandler(new DoubleClickHandler()
      {

         @Override
         public void onDoubleClick(DoubleClickEvent event)
         {
            typesSelected();
         }
      });

      display.getTypesList().addSelectionHandler(new SelectionHandler<ShortTypeInfo>()
      {

         @Override
         public void onSelection(SelectionEvent<ShortTypeInfo> event)
         {
            selectedType = event.getSelectedItem();
         }
      });

   }

   private void typesSelected()
   {
      callback.typeSelected(selectedType);
      IDE.getInstance().closeView(Display.ID);
   }

   /**
    * 
    */
   private void doSearch()
   {
      String namePrefix = display.getSearchInput().getValue();
      if (namePrefix != null && !namePrefix.isEmpty())
      {
         JavaCodeAssistantService.get().findTypeByPrefix(namePrefix, type, projectId,
            new AsyncRequestCallback<List<ShortTypeInfo>>()
            {

               @Override
               protected void onSuccess(List<ShortTypeInfo> result)
               {
                  List<ShortTypeInfo> nonFinalType = new ArrayList<ShortTypeInfo>();
                  for (ShortTypeInfo info : result)
                  {
                     if (!ModifierHelper.isFinal(info.getModifiers()))
                     {
                        nonFinalType.add(info);
                     }
                  }
                  display.getTypesList().setValue(nonFinalType);
               }
            });
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView().getId().equals(Display.ID))
      {
         display = null;
         addHandler.removeHandler();
         addHandler = null;
      }
   }
}
