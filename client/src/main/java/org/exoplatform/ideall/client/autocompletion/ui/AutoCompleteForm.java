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
package org.exoplatform.ideall.client.autocompletion.ui;


/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class AutoCompleteForm
{

//   private AbsolutePanel lockLayer;
//   
//   private AbsolutePanel keyHandlerLayer;
//   
//   private ListBox listBoxTokens;
//
//   private AutoCompletePresenter presenter;
//   
//   private AbsolutePanel blockMouseEventsPanel;
//   
//   private SelectItem autoCompleteList;
//   
//   private String editorId;
//   
//   private  HandlerManager eventBus;
//   
//   private String textContext;
//      
//   int tt = 0;
//   
//   /**
//    * @param editorId 
//    * @param textContext 
//    * 
//    */
//   public AutoCompleteForm(HandlerManager eventBus, ApplicationContext context, String editorId, int cursorOffsetX,
//      int cursorOffsetY, String textContext, List<String> tokens)
//   {
//
//      this.editorId = editorId;
//      this.eventBus = eventBus;
//      
//      if(textContext.contains(" "))
//      {
//      this.textContext = textContext.substring(textContext.lastIndexOf(" "), textContext.length());
//      }
//      else
//      {
//         this.textContext = textContext;
//      }
////      this.textContext = textContext;
//      // create and draw completion list
//      
//           final DynamicForm form = new DynamicForm();
//            form.setWidth(500);
//            form.setTop(cursorOffsetY);
//            form.setLeft(cursorOffsetX);
//      
//            autoCompleteList = new SelectItem();
//            autoCompleteList.setShowTitle(false);
//            autoCompleteList.setType("comboBox");
//            
//            LinkedHashMap<String, String> token = new LinkedHashMap<String, String>();
//            for (String t : tokens)
//            {
//               token.put(t, t);
//            }
//            autoCompleteList.setValueMap(token);
//            
//            autoCompleteList.addKeyPressHandler(new KeyPressHandler()
//            {
//               
//               public void onKeyPress(KeyPressEvent event)
//               {
//                  if(event.getKeyName().equals("Enter"))
//                  {
//                     onCompletionSelected();
//                     form.destroy();
//                     return;
//                  }
//                  if(event.getKeyName().equals("Escape"))
//                  {
//                     form.destroy();
//                  }
//               }
//            });
//            
//            autoCompleteList.setValue(this.textContext);
//            form.setItems(autoCompleteList);
//            form.show();
//            form.focusInItem(autoCompleteList);
////      listBoxTokens = new ListBox();
////      listBoxTokens.setVisibleItemCount(10);
////
////      lockLayer = new AbsolutePanel();
////      RootPanel.get().add(lockLayer, 0, 0);
////      lockLayer.setWidth("" + Window.getClientWidth() + "px");
////      lockLayer.setHeight("" + Window.getClientHeight()  + "px");
////      DOM.setElementAttribute(lockLayer.getElement(), "id", "auto-complete-lock-layer-id");
////      DOM.setStyleAttribute(lockLayer.getElement(), "zIndex", "" + (Integer.MAX_VALUE));
////      
////      keyHandlerLayer = new AbsolutePanel();
////      lockLayer.add(keyHandlerLayer);
////      
////      lockLayer.add(listBoxTokens, cursorOffsetX, cursorOffsetY);
////      
////      for (String s : tokens)
////      {
////         listBoxTokens.addItem(s);
////      }
////      
////      presenter = new AutoCompletePresenter(eventBus, context, editorId, textContext, tokens);
////      presenter.bindDisplay(this);
//      
////      lockLayer = new AbsolutePanel();
////      RootPanel.get().add(lockLayer, 0, 0);
////      lockLayer.setWidth("" + Window.getClientWidth() + "px");
////      lockLayer.setHeight("" + Window.getClientHeight() + "px");
////      DOM.setElementAttribute(lockLayer.getElement(), "id", "menu-lock-layer-id");
////      DOM.setStyleAttribute(lockLayer.getElement(), "zIndex", "" + (Integer.MAX_VALUE));
////
////      blockMouseEventsPanel = new LockLayer();
////      blockMouseEventsPanel.setStyleName("exo-lockLayer");
////      blockMouseEventsPanel.setWidth("" + Window.getClientWidth() + "px");
////      blockMouseEventsPanel.setHeight("" + Window.getClientHeight() + "px");
////      lockLayer.add(blockMouseEventsPanel, 0, 0);
////
////      
////      final AbsolutePanel p = new AbsolutePanel();
////      p.setWidth("200px");
////      p.setHeight("150px");
////      
////      final Grid g = new Grid(500, 1);
////      for (int i = 0; i < 500; i++) {
////         g.setHTML(i, 0, "item - " + i);
////      }
////      p.add(g);
////      g.setWidth("100%");
////      
////      DOM.setStyleAttribute(p.getElement(), "overflow", "auto");
////      
////      
////      
////      new Timer()
////      {
////         @Override
////         public void run()
////         {
////            tt -= 10;
////            DOM.setStyleAttribute(g.getElement(), "top", "" + tt + "px");
////         }
////      }.scheduleRepeating(1000);
////      
//      
////      TextBox t = new TextBox();
////      t.setWidth("200px");
////      
////      t.addKeyDownHandler(new KeyDownHandler()
////      {
////         
////         public void onKeyDown(KeyDownEvent arg0)
////         {
////            System.out.println("---- key down");
////            
////         }
////      });
////
////      t.addMouseWheelHandler(new MouseWheelHandler()
////      {
////         
////         public void onMouseWheel(MouseWheelEvent event)
////         {
////            mouseWhell(event.getDeltaY());
////            
////         }
////      });
////            
////      lockLayer.add(p, cursorOffsetX, cursorOffsetY);
//      
//   }
//
//   /**
//    * @see org.exoplatform.ideall.client.autocompletion.ui.AutoCompletePresenter.Display#closeForm()
//    */
//   public void closeForm()
//   {
//     
//      presenter.destroy();
//   }
//
//   /**
//    * @see org.exoplatform.ideall.client.autocompletion.ui.AutoCompletePresenter.Display#getAutoCompleteEdit()
//    */
//   public HasKeyPressHandlers getAutoCompleteEdit()
//   {
//      return null;// autoCompleteList;
//   }
//
//   /**
//    * @see org.exoplatform.ideall.client.autocompletion.ui.AutoCompletePresenter.Display#getListBoxTokens()
//    */
//   public HasValue<String> getListBoxTokens()
//   {
//      //return listBoxTokens;
//      return null;
//   }
//   
//   protected void mouseWhell(int velocity) {
//      System.out.println("mouse whell " + System.currentTimeMillis());
//      System.out.println("velocity " + velocity);      
//   }
//   
//   /**
//    *  Lock Layer uses for locking of screen. Uses for hiding popups.
//    */
//   private class LockLayer extends AbsolutePanel
//   {
//
//      public LockLayer()
//      {
//         sinkEvents(Event.ONMOUSEDOWN | Event.ONMOUSEWHEEL);
//      }
//
//      @Override
//      public void onBrowserEvent(Event event)
//      {
//         
//         
//         switch (DOM.eventGetType(event))
//         {
//            case Event.ONMOUSEDOWN :              
//               lockLayer.removeFromParent();
//               break;
//               
//            case Event.ONMOUSEWHEEL:
//               mouseWhell(event.getMouseWheelVelocityY());
//               break;
//         }
//      }
//
//   }   
//   private void onCompletionSelected()
//   {
//      
//      String completion = (String)autoCompleteList.getValue();
//      
//      completion = completion.substring(textContext.length());
//      
//      System.out.println(completion);
//      eventBus.fireEvent(new EditorAutoCompleteEvent(editorId, completion));
//   }
}
