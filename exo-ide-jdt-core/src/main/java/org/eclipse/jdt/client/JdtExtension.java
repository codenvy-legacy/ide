/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.eclipse.jdt.client;

import org.eclipse.jdt.client.event.ShowAstEvent;
import org.eclipse.jdt.client.outline.OutlinePresenter;
import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Jan 20, 2012 1:08:51 PM evgen $
 */
public class JdtExtension extends Extension implements InitializeServicesHandler
{

   static String DOC_CONTEXT;

   static String REST_CONTEXT;

   private static JdtExtension instance;

   /**
    * The code template context type registry for the java editor.
    * 
    * @since 3.0
    */
//   private ContextTypeRegistry fCodeTemplateContextTypeRegistry;

//   private TemplateStore templateStore;

   /** @see org.exoplatform.ide.client.framework.module.Extension#initialize() */
   @Override
   public void initialize()
   {
      instance = this;
      IDE.getInstance().addControl(new Con());
      IDE.addHandler(InitializeServicesEvent.TYPE, this);
      // IDE.getInstance().addControl(new CodeAssistCommand(), Docking.TOOLBAR_RIGHT);
      new AstPresenter(IDE.eventBus());
      new CodeAssistantController();
      new JavaCodeController();
      new OutlinePresenter();
   }

   public static class Con extends SimpleControl implements IDEControl
   {

      /** @param id */
      public Con()
      {
         super("View/Show AST");
         setTitle("Show Ast");
         setPrompt("Show Ast");
         setEvent(new ShowAstEvent());
         setEnabled(true);
         setVisible(true);
      }

      /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
      @Override
      public void initialize()
      {
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler#onInitializeServices(org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent)
    */
   @Override
   public void onInitializeServices(InitializeServicesEvent event)
   {
      REST_CONTEXT = event.getApplicationConfiguration().getContext();
      DOC_CONTEXT = REST_CONTEXT + "/ide/code-assistant/java/class-doc?fqn=";
   }

   public static JdtExtension get()
   {
      return instance;
   }

//   /**
//    * @return
//    */
//   public ContextTypeRegistry getTemplateContextRegistry()
//   {
//      if (fCodeTemplateContextTypeRegistry == null)
//      {
//         fCodeTemplateContextTypeRegistry = new ContextTypeRegistry();
//
//         CodeTemplateContextType.registerContextTypes(fCodeTemplateContextTypeRegistry);
//      }
//
//      return fCodeTemplateContextTypeRegistry;
//   }
//
//   /**
//    * @return
//    */
//   public TemplateStore getTemplateStore()
//   {
//      if (templateStore == null)
//         templateStore = new TemplateStore();
//      return templateStore;
//   }

}
