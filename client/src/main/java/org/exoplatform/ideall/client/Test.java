/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */
package org.exoplatform.ideall.client;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class Test
{

   private VLayout mainLayout;

   private HLayout hLayout;

   private VLayout vLayout;

   private Layout navigator;

   private Layout editor;

   private Layout output;

   public Test()
   {
      mainLayout = new VLayout();
      mainLayout.setWidth100();
      mainLayout.setHeight100();
      mainLayout.setBackgroundColor("#FFEEAA");
      mainLayout.draw();

      createButtons();

      hLayout = new HLayout();
      mainLayout.addMember(hLayout);

      navigator = new Layout();
      navigator.setShowResizeBar(true);
      navigator.setWidth("35%");
      navigator.setBackgroundColor("#7FC1FF");
      hLayout.addMember(navigator);

      vLayout = new VLayout();
      hLayout.addMember(vLayout);

      editor = new Layout();
      editor.setShowResizeBar(true);
      editor.setResizeBarTarget("next");
      editor.setBackgroundColor("#C6FFF2");
      vLayout.addMember(editor);

      output = new Layout();
      output.setOverflow(Overflow.SCROLL);
      output.setBackgroundColor("#FFDFD1");
      output.hide();
      vLayout.addMember(output);

      Label label1 = new Label("Navigator");
      label1.setWidth100();
      label1.setHeight100();
      label1.setAlign(Alignment.CENTER);
      navigator.addMember(label1);

      Label label2 = new Label("Editor");
      label2.setWidth100();
      label2.setHeight100();
      label2.setAlign(Alignment.CENTER);
      editor.addMember(label2);

      label3 = new Label("Output");
      label3.setWidth100();
      label3.setHeight100();
      label3.setAlign(Alignment.CENTER);
      output.addMember(label3);
   }
   
   private Label label3;

   private IButton fillOutputButton;

   private IButton maximizeOutputButton;

   private IButton restoreOutputButton;

   private void createButtons()
   {
      HLayout layout = new HLayout();
      layout.setWidth100();
      layout.setMargin(10);
      layout.setHeight(20);
      layout.setMembersMargin(10);
      mainLayout.addMember(layout);

      fillOutputButton = new IButton("Fill Output");
      fillOutputButton.setWidth(150);
      layout.addMember(fillOutputButton);
      fillOutputButton.addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            fillOutput();
         }
      });

      maximizeOutputButton = new IButton("Maximize Output");
      maximizeOutputButton.setWidth(150);
      maximizeOutputButton.disable();
      layout.addMember(maximizeOutputButton);
      maximizeOutputButton.addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            maximizeOutput();
         }
      });

      restoreOutputButton = new IButton("Restore Output");
      restoreOutputButton.setWidth(150);
      restoreOutputButton.disable();
      layout.addMember(restoreOutputButton);
      restoreOutputButton.addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            restoreOutput();
         }
      });

   }

   protected void fillOutput()
   {
      fillOutputButton.disable();
      maximizeOutputButton.enable();

      String html = "<div style=\"background:#FFAAEE;\">o_O";
      for (int i = 0; i < 100; i++)
      {
         html += "<br>" + i;
      }
      html += "</div>";
      output.removeMember(label3);
      output.addMember(new Label(html));
      output.show();
   }

   protected void maximizeOutput()
   {
      navigator.hide();
      editor.hide();
      
      hLayout.setResizeBarSize(0);
      vLayout.setResizeBarSize(0);
   }

   protected void restoreOutput()
   {
   }

}
