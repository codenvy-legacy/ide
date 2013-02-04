/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.google.collide.client.collaboration.participants;

import com.google.collide.client.CollabEditorExtension;
import com.google.collide.client.Resources;
import com.google.collide.client.code.Participant;
import com.google.collide.client.collaboration.participants.ParticipantsPresenter.Display;
import com.google.collide.client.ui.list.SimpleList;
import com.google.collide.client.ui.list.SimpleList.View;
import com.google.collide.client.util.Elements;
import com.google.collide.json.client.JsoArray;
import com.google.collide.json.shared.JsonArray;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import elemental.html.DivElement;
import elemental.html.Element;
import elemental.html.TableCellElement;
import elemental.html.TableElement;

import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;

import java.util.List;

/**
 * View for displaying the collaborators.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: ParticipantsView.java Jan 30, 2013 3:21:19 PM azatsarynnyy $
 *
 */
public class ParticipantsView extends ViewImpl implements Display
{

   /**
    * View's identifier.
    */
   private static final String ID = "ideCollaborationCollaboratorsView";

   /**
    * View's title.
    */
   private static final String TITLE = "Collaborators";

   /**
    * Initial width of this view.
    */
   private static final int WIDTH = 250;

   /**
    * Initial height of this view.
    */
   private static final int HEIGHT = 450;

   /**
    * Participant list to show.
    */
   private SimpleList<Participant> participantsList;

   @UiField
   ScrollPanel participantsPanel;

   /**
    * UI binder for this view.
    */
   private static ParticipantsViewUiBinder uiBinder = GWT.create(ParticipantsViewUiBinder.class);

   interface ParticipantsViewUiBinder extends UiBinder<Widget, ParticipantsView>
   {
   }

   public ParticipantsView()
   {
      super(ID, ViewType.INFORMATION, TITLE, null, WIDTH, HEIGHT);
      Resources resources = CollabEditorExtension.get().getContext().getResources();
      setIcon(new Image(resources.getCollaboratorsImage()));
      add(uiBinder.createAndBindUi(this));

      TableElement tableElement = Elements.createTableElement();
      tableElement.setAttribute("style", "width: 100%");
      tableElement.setCellSpacing("0");
      participantsList =
         SimpleList.create((View)tableElement, resources.defaultSimpleListCss(), listItemRenderer, listDelegate);
      participantsPanel.add(participantsList);
   }

   /**
    * Called each time we render an item in the list.
    */
   private SimpleList.ListItemRenderer<Participant> listItemRenderer = new SimpleList.ListItemRenderer<Participant>()
   {

      @Override
      public void render(Element listItemBase, Participant itemData)
      {
         TableCellElement iconCell = Elements.createTDElement();
         TableCellElement labelCell = Elements.createTDElement();

         DivElement iconDiv = Elements.createDivElement();
         iconDiv.setAttribute("style", "background-color: " + itemData.getColor()
            + "; height: 13px; width: 13px; border-radius: 2px");
         iconCell.setWidth("15px");
         iconCell.appendChild(iconDiv);

         labelCell.setInnerHTML(itemData.getDisplayName());
         listItemBase.appendChild(iconCell);
         listItemBase.appendChild(labelCell);
      }

      @Override
      public Element createElement()
      {
         return Elements.createTRElement();
      }
   };

   /**
    * Receives events fired on items in the list.
    */
   private SimpleList.ListEventDelegate<Participant> listDelegate = new SimpleList.ListEventDelegate<Participant>()
   {

      @Override
      public void onListItemClicked(Element listItemBase, Participant itemData)
      {
         participantsList.getSelectionModel().setSelectedItem(itemData);
      }

      @Override
      public void onListItemDoubleClicked(Element listItemBase, Participant itemData)
      {
         // TODO Auto-generated method stub

      }
   };

   /**
    * @see com.google.collide.client.collaboration.participants.ParticipantsPresenter.Display#setValue(java.util.List)
    */
   @Override
   public void setValue(List<Participant> value)
   {
      final JsonArray<Participant> itemsToDisplay = JsoArray.<Participant> create();
      if (value != null)
      {
         for (Participant participant : value)
         {
            itemsToDisplay.add(participant);
         }
      }
      participantsList.render(itemsToDisplay);
   }
}
