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
import com.google.collide.client.collaboration.participants.ParticipantsPresenter.Display;
import com.google.collide.client.ui.list.SimpleList;
import com.google.collide.client.ui.list.SimpleList.View;
import com.google.collide.client.util.Elements;
import com.google.collide.json.client.JsoArray;
import com.google.collide.json.shared.JsonArray;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import elemental.html.Element;
import elemental.html.TableCellElement;
import elemental.html.TableElement;

import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;

import java.util.List;

/**
 * View for displaying the participants editing.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: ParticipantsView.java Jan 30, 2013 3:21:19 PM azatsarynnyy $
 *
 */
public class ParticipantsView extends ViewImpl implements Display
{

   private static final String ID = "ideCollaborationParticipantsView";

   private static final String TITLE = "Participants";

   private SimpleList<String> participantsList;

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
      super(ID, ViewType.INFORMATION, TITLE);
      add(uiBinder.createAndBindUi(this));

      Resources resources = CollabEditorExtension.get().getContext().getResources();

      TableElement tableElement = Elements.createTableElement();
      tableElement.setAttribute("style", "width: 100%");
      participantsList = SimpleList.create((View)tableElement, resources.defaultSimpleListCss(), listItemRenderer, listDelegate);

      // participantsPanel.setStyleName(resources.coreCss().simpleListContainer());
      participantsPanel.add(participantsList);
   }

   private SimpleList.ListItemRenderer<String> listItemRenderer = new SimpleList.ListItemRenderer<String>()
   {

      @Override
      public void render(Element listItemBase, String itemData)
      {
         TableCellElement label = Elements.createTDElement();
         label.setInnerHTML(itemData);
         listItemBase.appendChild(label);
      }

      @Override
      public Element createElement()
      {
         return Elements.createTRElement();
      }
   };

   private SimpleList.ListEventDelegate<String> listDelegate = new SimpleList.ListEventDelegate<String>()
   {

      @Override
      public void onListItemClicked(Element listItemBase, String itemData)
      {
         participantsList.getSelectionModel().setSelectedItem(itemData);
      }

      @Override
      public void onListItemDoubleClicked(Element listItemBase, String itemData)
      {
         // TODO Auto-generated method stub

      }
   };

   @Override
   public void setValue(List<String> value)
   {
      // final JsonArray<UserDetails> itemsToDisplay = JsoArray.<UserDetails> create();
      final JsonArray<String> itemsToDisplay = JsoArray.<String> create();
      for (String participantModel : value)
      {
         itemsToDisplay.add(participantModel);
      }
      participantsList.render(itemsToDisplay);
   }
}
