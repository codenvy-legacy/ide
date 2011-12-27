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
package org.exoplatform.ide.core;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
public class Preview extends AbstractTestModule
{
   
   private static final String GROOVY_TEMPLATE_PREVIEW = "//div[@view-id='Preview']";

   private static final String GADGET_PREVIEW = "//div[@view-id='gadgetpreview']";

   private static final String HTML_PREVIEW = "//div[@view-id='idePreviewHTMLView']";

   //XXX: only for groovy template and netvibes preview
   private static final String PREVIEW_FRAME_ID = "eXo-IDE-preview-frame";
   
   private static final String GADGET_PREVIEW_IFRAME = GADGET_PREVIEW + "//iframe";

   private static final String VIEW_TITLE = "Preview";
   
   @FindBy(xpath = HTML_PREVIEW)
   private WebElement htmlPreview;

   @FindBy(xpath = GADGET_PREVIEW)
   private WebElement gadgetPreview;
   
   @FindBy(xpath = GADGET_PREVIEW_IFRAME)
   private WebElement gadgetIframe;

   @FindBy(xpath = GROOVY_TEMPLATE_PREVIEW)
   private WebElement gtmplPreview;

   /**
    * Wait for HTML preview view opened.
    * 
    * @throws Exception
    */
   public void waitHtmlPreviewOpened() throws Exception
   {
      new WebDriverWait(driver(), 3).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               return (htmlPreview != null && htmlPreview.isDisplayed());
            }
            catch (Exception e)
            {
               return false;
            }
         }
      });
   }

   /**
    * Wait for HTML preview view closed.
    * 
    * @throws Exception
    */
   public void waitHtmlPreviewClosed() throws Exception
   {
      new WebDriverWait(driver(), 3).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               return htmlPreview == null || !htmlPreview.isDisplayed();
            }
            catch (NoSuchElementException e)
            {
               return true;
            }
         }
      });
   }

   /**
    * Wait for Gadget preview view opened.
    * 
    * @throws Exception
    */
   public void waitGadgetPreviewOpened() throws Exception
   {
      new WebDriverWait(driver(), 3).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               return (gadgetPreview != null && gadgetPreview.isDisplayed());
            }
            catch (Exception e)
            {
               return false;
            }
         }
      });
   }

   /**
    * Wait for Gadget preview view closed.
    * 
    * @throws Exception
    */
   public void waitGadgetPreviewClosed() throws Exception
   {
      new WebDriverWait(driver(), 3).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               return gadgetPreview == null || !gadgetPreview.isDisplayed();
            }
            catch (NoSuchElementException e)
            {
               return true;
            }
         }
      });
   }

   /**
    * Wait for GTMPL preview view opened.
    * 
    * @throws Exception
    */
   public void waitGtmplPreviewOpened() throws Exception
   {
      new WebDriverWait(driver(), 3).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               return (gtmplPreview != null && gtmplPreview.isDisplayed());
            }
            catch (Exception e)
            {
               return false;
            }
         }
      });
   }

   /**
    * Wait for GTMPL preview view closed.
    * 
    * @throws Exception
    */
   public void waitGtmplPreviewClosed() throws Exception
   {
      new WebDriverWait(driver(), 3).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               return gtmplPreview == null || !gtmplPreview.isDisplayed();
            }
            catch (NoSuchElementException e)
            {
               return true;
            }
         }
      });
   }

   /**
    * Returns opened state of HTML preview view.
    * 
    * @return {@link Boolean} opened state of HTML preview
    */
   public boolean isHtmlPreviewOpened()
   {
      try
      {
         return htmlPreview != null && htmlPreview.isDisplayed();
      }
      catch (NoSuchElementException e)
      {
         return false;
      }
   }
   
   /**
    * Returns opened state of Gadget preview view.
    * 
    * @return {@link Boolean} opened state of Gadget preview
    */
   public boolean isGadgetPreviewOpened()
   {
      try
      {
         return gadgetPreview != null && gadgetPreview.isDisplayed();
      }
      catch (NoSuchElementException e)
      {
         return false;
      }
   }
   
   /**
    * Returns opened state of GTMPL preview view.
    * 
    * @return {@link Boolean} opened state of GTMPL preview
    */
   public boolean isGtmplPreviewOpened()
   {
      try
      {
         return gtmplPreview != null && gtmplPreview.isDisplayed();
      }
      catch (NoSuchElementException e)
      {
         return false;
      }
   }

   /**
    * Returns active state of HTML preview view.
    * 
    * @return {@link Boolean} active state of HTML preview
    */
   public boolean isHtmlPreviewActive()
   {
      return IDE().PERSPECTIVE.isViewActive(htmlPreview);
   }
   
   /**
    * Returns active state of GTMPL preview view.
    * 
    * @return {@link Boolean} active state of GTMPL preview
    */
   public boolean isGtmplPreviewActive()
   {
      return IDE().PERSPECTIVE.isViewActive(gtmplPreview);
   }
   
   /**
    * Returns active state of Gadget preview view.
    * 
    * @return {@link Boolean} active state of Gadget preview
    */
   public boolean isGadgetPreviewActive()
   {
      return IDE().PERSPECTIVE.isViewActive(gadgetPreview);
   }

   /**
    * Select preview frame.
    */
   public void selectPreviewIFrame()
   {
      driver().switchTo().frame(PREVIEW_FRAME_ID);
   }
   
   public void selectGadgetPreviewIframe()
   {
      driver().switchTo().frame(gadgetIframe);
   }

   public void closeView()
   {
      IDE().PERSPECTIVE.getCloseViewButton(VIEW_TITLE).click();
   }

}
