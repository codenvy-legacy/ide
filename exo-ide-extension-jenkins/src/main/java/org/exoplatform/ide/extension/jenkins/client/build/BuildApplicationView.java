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
package org.exoplatform.ide.extension.jenkins.client.build;

import org.exoplatform.ide.client.framework.ui.api.ViewType;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.extension.jenkins.client.JenkinsExtension;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class BuildApplicationView extends ViewImpl implements
   org.exoplatform.ide.extension.jenkins.client.build.BuildApplicationPresenter.Display
{

   private static final String ID = "ide.jenkins.build.view";

   public static final int WIDTH = 450;

   public static final int HEIGHT = 250;

   private boolean animationEnabled = false;

   @UiField
   HTMLPanel buildOutputPanel;

   private static BuildApplicationViewUiBinder uiBinder = GWT.create(BuildApplicationViewUiBinder.class);

   interface BuildApplicationViewUiBinder extends UiBinder<Widget, BuildApplicationView>
   {
   }

   public BuildApplicationView()
   {
      super(ID, ViewType.OPERATION, "Building", new Image(JenkinsExtension.RESOURCES.grey()), WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));
   }

   @Override
   public final native void output(String text) /*-{
      var pre = $doc.getElementById('ide.jenkins.buildOutput');
      if (pre == null || pre == undefined) {
        return;
      }
      
      var curText = pre.textContent;
      if (curText != null && curText != undefined && curText != "") {
         pre.textContent += "\r\n";      
      }
      pre.textContent += text;
   
      this.@org.exoplatform.ide.extension.jenkins.client.build.BuildApplicationView::scrollToBottom()();
   }-*/;

   private void scrollToBottom()
   {
      int scrollHeight = DOM.getElementPropertyInt(buildOutputPanel.getElement(), "scrollHeight");
      DOM.setElementPropertyInt(buildOutputPanel.getElement(), "scrollTop", scrollHeight);
   }

   @Override
   public final native void clearOutput() /*-{
      var pre = $doc.getElementById('ide.jenkins.buildOutput');
      if (pre == null || pre == undefined) {
        return;
      }
      pre.textContent = ""; 
   }-*/;

   @Override
   public void startAnimation()
   {
      animationCharIndex = 1;
      Element animationElement = DOM.getElementById("ide.jenkins.buildingAnimation");
      if (animationElement == null) {
        return;
      }
      animationElement.getStyle().setDisplay(Display.BLOCK);
      animationTimer.scheduleRepeating(150);
   }

   @Override
   public void stopAnimation()
   {
      animationTimer.cancel();
      
      Element animationElement = DOM.getElementById("ide.jenkins.buildingAnimation");
      if (animationElement == null) {
         return;
      }
      
      animationElement.getStyle().setDisplay(Display.NONE);
      animationElement.setInnerHTML("");
   }

   private int animationCharIndex = 1;

   private Timer animationTimer = new Timer()
   {
      @Override
      public void run()
      {
         String c = "";
         switch (animationCharIndex)
         {
            case 1 :
               c = "/";
               break;

            case 2 :
               c = "-";
               break;

            case 3 :
               c = "\\";
               break;

            case 4 :
               c = "|";
               break;
         }

         Element animationElement = DOM.getElementById("ide.jenkins.buildingAnimation");
         if (animationElement != null) {
            animationElement.setInnerHTML(c);
         }

         animationCharIndex++;
         if (animationCharIndex > 4)
         {
            animationCharIndex = 1;
         }
      }
   };

   private Image blinkIcon;

   private Image transparentIcon = new Image(JenkinsExtension.RESOURCES.transparent());

   private boolean iconTransparent = false;

   private boolean blinking;

   @Override
   public void setBlinkIcon(Image blinkIcon, boolean blinking)
   {
      this.blinkIcon = blinkIcon;
      this.blinking = blinking;

      setIcon(blinkIcon);
      if (blinking)
      {
         iconTransparent = false;
         iconBlinkingTimer.scheduleRepeating(700);
      }
      else
      {
         iconBlinkingTimer.cancel();
         iconTransparent = false;
      }
   }

   private Timer iconBlinkingTimer = new Timer()
   {
      @Override
      public void run()
      {
         if (iconTransparent)
         {
            iconTransparent = false;
            setIcon(blinkIcon);
         }
         else
         {
            iconTransparent = true;
            setIcon(transparentIcon);
         }
      }
   };

}
