/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.client.template;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Window;

import org.exoplatform.gwtframework.commons.loader.EmptyLoader;
import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.model.template.FileTemplate;
import org.exoplatform.ide.client.model.template.Template;
import org.exoplatform.ide.client.model.template.TemplateCreatedCallback;
import org.exoplatform.ide.client.model.template.TemplateDeletedCallback;
import org.exoplatform.ide.client.model.template.TemplateList;
import org.exoplatform.ide.client.model.template.TemplateService;
import org.exoplatform.ide.client.model.template.TemplateServiceImpl;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class GwtTestTemplateService extends GWTTestCase
{
   private HandlerManager eventBus;

   private Loader loader = new EmptyLoader();

   private final int DELAY_TEST = 6000;

   private String url;

   private String wrongUrl;

   /**
    * @see com.google.gwt.junit.client.GWTTestCase#gwtSetUp()
    */
   @Override
   protected void gwtSetUp() throws Exception
   {
      super.gwtSetUp();
      eventBus = new HandlerManager(null);
      url = "http://" + Window.Location.getHost() + "/ideall/rest/private/registry/repository/exo:applications/IDEall";
      wrongUrl = "http://" + Window.Location.getHost() + "/ideall/private/registry/repository/exo:applications/IDEall";
   }

   /**
    * @see com.google.gwt.junit.client.GWTTestCase#getModuleName()
    */
   @Override
   public String getModuleName()
   {
      return "org.exoplatform.ideall.IDEGwtTest";
   }

   /**
    * Test getting base application's templates.
    */
   public void testGetBaseTemplates()
   {
      new TemplateServiceImpl(eventBus, loader, url, null);

      TemplateService.getInstance().getTemplates(new AsyncRequestCallback<TemplateList>()
      {
         
         @Override
         protected void onSuccess(TemplateList result)
         {
            //Six base templates.
            assertEquals(6, result.getTemplates().size());
            finishTest();            
         }
         
         @Override
         protected void onFailure(Throwable exception)
         {
            fail();
         }
      });
      delayTestFinish(DELAY_TEST);
   }

   /**
    * Test getting saved templates plus base ones.
    */
   public void testGetTemplates()
   {
      new TemplateServiceImpl(eventBus, loader, url, null);

      String content = "var hello = 'Hello all!';";
      String contentType = MimeType.APPLICATION_JAVASCRIPT;
      String name = "templateJS";
      String description = "This is text js hello template.";
      Template template = new FileTemplate(contentType, name, description, content, null);

      TemplateService.getInstance().createTemplate(template, new TemplateCreatedCallback()
      {
         @Override
         protected void onSuccess(Template result)
         {
            TemplateService.getInstance().getTemplates(new AsyncRequestCallback<TemplateList>()
            {
               
               @Override
               protected void onSuccess(TemplateList result)
               {
                  assertEquals(7, result.getTemplates().size());
                  finishTest();
               }
               
               @Override
               protected void onFailure(Throwable exception)
               {
                  fail();
               }
            });
         }
      });
      delayTestFinish(DELAY_TEST);
   }

   /**
    * Test creation template.
    */
   public void testCreateTemplate()
   {
      new TemplateServiceImpl(eventBus, loader, url, null);

      String content = "New empty file.";
      String contentType = MimeType.TEXT_PLAIN;
      String name = "template1";
      String description = "This is text file template.";
      final Template template = new FileTemplate(contentType, name, description, content, null);

      TemplateService.getInstance().createTemplate(template, new TemplateCreatedCallback()
      {
         @Override
         protected void onSuccess(Template result)
         {
            assertEquals(result.getName(), template.getName());
            assertEquals(((FileTemplate)result).getContent(), ((FileTemplate)template).getContent());
            assertEquals(result.getDescription(), template.getDescription());
            assertEquals(((FileTemplate)result).getMimeType(), ((FileTemplate)template).getMimeType());
            finishTest();
         }
         
         @Override
         protected void onFailure(Throwable exception)
         {
            fail(exception.getMessage());
         }
      });
      delayTestFinish(DELAY_TEST);
   }

   /**
    * Test creation templates with non latin name and content.
    */
   public void testCreateTemplateWithNonLatinSymbols()
   {
      new TemplateServiceImpl(eventBus, loader, url, null);
      String content = "Это новый пустой файл.";
      String contentType = MimeType.TEXT_PLAIN;
      String name = "Новый тестовый файл";
      String description = "New test file.";

      final Template template = new FileTemplate(contentType, name, description, content, null);
      TemplateService.getInstance().createTemplate(template, new TemplateCreatedCallback()
      {
         @Override
         protected void onSuccess(Template result)
         {
            assertEquals(result.getName(), template.getName());
            assertEquals(((FileTemplate)result).getContent(), ((FileTemplate)template).getContent());
            assertEquals(result.getDescription(), template.getDescription());
            assertEquals(((FileTemplate)result).getMimeType(), ((FileTemplate)template).getMimeType());
            finishTest();
         }
         
         @Override
         protected void onFailure(Throwable exception)
         {
            fail(exception.getMessage());
         }
      });
      delayTestFinish(DELAY_TEST);
   }

   /**
    * Create template with incorrect URL pointed.
    */
   public void testCreateTemplateWithFail()
   {
      new TemplateServiceImpl(eventBus, loader, wrongUrl, null);

      String content = "New empty file.";
      String contentType = MimeType.TEXT_CSS;
      String name = "templateCss";
      String description = "This is css file template.";
      final Template template = new FileTemplate(contentType, name, description, content, null);
      TemplateService.getInstance().createTemplate(template, new TemplateCreatedCallback()
      {
         @Override
         protected void onSuccess(Template result)
         {
            fail("Wrong template was created");
         }
         
         @Override
         protected void onFailure(Throwable exception)
         {
            finishTest();
         }
      });

      delayTestFinish(DELAY_TEST);
   }

   /**
    * Test deleting created template.
    */
   public void testDeleteTemplate()
   {
      new TemplateServiceImpl(eventBus, loader, url, null);
     
      String content = "New empty file.";
      String contentType = MimeType.TEXT_PLAIN;
      String name = "templateForDelete";
      String description = "This is text file template.";
      final Template templateToDelete = new FileTemplate(contentType, name, description, content, null);

      TemplateService.getInstance().createTemplate(templateToDelete, new TemplateCreatedCallback()
      {
         @Override
         protected void onSuccess(Template result)
         {
            TemplateService.getInstance().getTemplates(new AsyncRequestCallback<TemplateList>()
            {
               
               @Override
               protected void onSuccess(TemplateList result)
               {
                  int templateListSize = result.getTemplates().size();
                  Template template = result.getTemplates().get(templateListSize - 1);
                  TemplateService.getInstance().deleteTemplate(template, new TemplateDeletedCallback()
                  {
                     
                     @Override
                     protected void onSuccess(Template result)
                     {
                        assertEquals(templateToDelete.getName(), result.getName());
                        finishTest();
                     }
                     
                     @Override
                     protected void onFailure(Throwable exception)
                     {
                        fail(exception.getMessage());
                     }
                  });
               }
               
               @Override
               protected void onFailure(Throwable exception)
               {
                  fail(exception.getMessage());
               }
            });
         }
         
         @Override
         protected void onFailure(Throwable exception)
         {
            finishTest();
         }
      });
      delayTestFinish(DELAY_TEST);
   }

   /**
    * Test deletion of not existed template.
    */
   public void testDeleteNotExistTemplate()
   {
      new TemplateServiceImpl(eventBus, loader, url, null);
      
      String contentType = MimeType.TEXT_PLAIN;
      String name = "no such";
      String description = "This is text file template.";
      final Template template = new FileTemplate(contentType, name, description, "", null);

      TemplateService.getInstance().deleteTemplate(template, new TemplateDeletedCallback()
      {
         @Override
         protected void onSuccess(Template result)
         {
            fail("Can't delete not existing template");
         }
         
         @Override
         protected void onFailure(Throwable exception)
         {
            finishTest();
         }
      });
      delayTestFinish(DELAY_TEST);
   }
}
