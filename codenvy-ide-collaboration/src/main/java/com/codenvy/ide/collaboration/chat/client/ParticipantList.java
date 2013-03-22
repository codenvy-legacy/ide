// Copyright 2012 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.codenvy.ide.collaboration.chat.client;

import com.codenvy.ide.client.util.Elements;
import com.google.collide.mvp.CompositeView;
import com.google.collide.mvp.UiComponent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import elemental.html.DivElement;
import elemental.html.Element;
import elemental.html.SpanElement;

import org.exoplatform.ide.json.client.JsoStringMap;
import org.exoplatform.ide.json.shared.JsonStringMap.IterationCallback;

/**
 * Presenter for the participant list in the navigation bar.
 */
public class ParticipantList extends UiComponent<ParticipantList.View>
{

   /**
    * Static factory method for obtaining an instance of the ParticipantList.
    */
   public static ParticipantList create(View view)
   {
      ParticipantList participantList = new ParticipantList(view);
      return participantList;
   }

   /**
    * CSS for the participant list.
    */
   public interface Css extends CssResource
   {
      String name();

      String root();

      String row();

      String swatch();

      String none();
   }

   public interface Resources extends ClientBundle
   {
      @Source("ParticipantList.css")
      Css workspaceNavigationParticipantListCss();

      @Source("redLine.png")
      ImageResource redLine();
   }

   public static class View extends CompositeView<Void>
   {
      final static Resources res = GWT.create(Resources.class);

      final Css css;

      private final JsoStringMap<DivElement> rows = JsoStringMap.create();

      public View()
      {
         this.css = res.workspaceNavigationParticipantListCss();
         css.ensureInjected();
         setElement(Elements.createDivElement(css.root()));
      }

      private void addParticipant(String clientId, String displayEmail, String name, String color)
      {
         DivElement rowElement = Elements.createDivElement(css.row());

         DivElement swatchElement = Elements.createDivElement(css.swatch(), css.none());
         swatchElement.getStyle().setBackgroundColor("white");
         swatchElement.getStyle().setBorderColor(color);
         rowElement.appendChild(swatchElement);

         SpanElement nameElement = Elements.createSpanElement(css.name());
         nameElement.setTextContent(name);
         nameElement.setTitle(displayEmail);
         rowElement.appendChild(nameElement);

         getElement().appendChild(rowElement);
         rows.put(clientId, rowElement);
      }

      private void removeParticipant(String userId)
      {
         DivElement row = rows.get(userId);
         if (row != null)
         {
            row.removeFromParent();
         }
      }

      public void setColor(String clientId, String color)
      {
         if (rows.containsKey(clientId))
         {
            Element element = rows.get(clientId).getFirstChildElement();
            element.getStyle().setBackgroundColor(color);
            element.removeClassName(css.none());
         }
      }

      public void clearColors()
      {
         rows.iterate(new IterationCallback<DivElement>()
         {
            @Override
            public void onIteration(String key, DivElement value)
            {
               clearColor(key);
            }
         });
      }

      public void clearColor(String clientId)
      {
         if (rows.containsKey(clientId))
         {
            Element element = rows.get(clientId).getFirstChildElement();
            element.getStyle().setBackgroundColor("white");
            element.addClassName(css.none());
         }
      }
   }

   private ParticipantList(View view)
   {
      super(view);
   }

   public void participantAdded(Participant participant)
   {
      addParticipantToView(participant);
   }

   public void participantRemoved(Participant participant)
   {
      getView().removeParticipant(participant.getClientId());
   }


   private void addParticipantToView(Participant participant)
   {
      getView().addParticipant(participant.getClientId(), participant.getDisplayEmail(), participant.getDisplayName(),
         participant.getColor());
   }

   public void setEditParticipant(String clientId, String color)
   {
      getView().setColor(clientId, color);
   }

   public void clearEditParticipants()
   {
      getView().clearColors();
   }

   public void removeEditParticipant(String clientId)
   {
      getView().clearColor(clientId);
   }
}
