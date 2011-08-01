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
package org.exoplatform.ide.client.samples.sc;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.model.template.FileTemplate;
import org.exoplatform.ide.client.model.template.FolderTemplate;
import org.exoplatform.ide.client.model.template.ProjectTemplate;
import org.exoplatform.ide.client.model.template.Template;
import org.exoplatform.ide.client.model.template.TemplateList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class ShoppingCardProject
{

   private TemplateList templateList = new TemplateList();

   private ProjectTemplate shoppingCartPrj = new ProjectTemplate("shopping-cart-project");

   private FolderTemplate uiFolder = new FolderTemplate("UI");

   private FolderTemplate dataFolder = new FolderTemplate("data");

   private FolderTemplate businessLogicFolder = new FolderTemplate("logic");

   public ShoppingCardProject()
   {
      businessLogicFolder.setChildren(new ArrayList<Template>());
      uiFolder.setChildren(new ArrayList<Template>());
      dataFolder.setChildren(new ArrayList<Template>());
      shoppingCartPrj.getChildren().add(dataFolder);
      shoppingCartPrj.getChildren().add(businessLogicFolder);
      shoppingCartPrj.getChildren().add(uiFolder);
      createItemToPurchase();
      createProduct();
      createShoppingCart();
      createShop();
      createShoppingCartGadget();
      createShoppingCartRestService();
      createCss();
      createReadme();
      shoppingCartPrj.setDescription("Shopping Cart Demo Project");
      shoppingCartPrj.setDefault(true);
      templateList.getTemplates().add(shoppingCartPrj);

   }
   
   private void createReadme() 
   {
      String content = ShoppingCartSample.INSTANCE.getReadme().getText();
      FileTemplate template =
         new FileTemplate(MimeType.TEXT_PLAIN, "readme_shopping_cart.txt", "shopping cart readme file",
            content, true);
      template.setFileName("readme_shopping_cart.txt");
      templateList.getTemplates().add(template);
      shoppingCartPrj.getChildren().add(template);
   }

   
   private void createCss() 
   {
      String content = ShoppingCartSample.INSTANCE.getCssSource().getText();
      FileTemplate cssTemplate =
         new FileTemplate(MimeType.TEXT_CSS, "StyleSheet.css", "Shopping Cart CSS", content, true);
      cssTemplate.setFileName("StyleSheet.css");
      templateList.getTemplates().add(cssTemplate);
      uiFolder.getChildren().add(cssTemplate);
   }

   private void createShoppingCartGadget() 
   {
      String content = ShoppingCartSample.INSTANCE.getShoppingCartGadgetSource().getText();
      FileTemplate gadgetFileTemplate =
         new FileTemplate(MimeType.GOOGLE_GADGET, "ShoppingCartGadget.xml", "Shopping Cart Gadget", content, true);
      gadgetFileTemplate.setFileName("ShoppingCartGadget.xml");
      templateList.getTemplates().add(gadgetFileTemplate);
      uiFolder.getChildren().add(gadgetFileTemplate);
   }

   private void createShoppingCartRestService() 
   {
      String content = ShoppingCartSample.INSTANCE.getShoppingCartRestServiceSource().getText();
      FileTemplate restServiceTemplate =
         new FileTemplate(MimeType.GROOVY_SERVICE, "ShoppingCartRestService.grs", "Shopping Cart REST service",
            content, true);
      restServiceTemplate.setFileName("ShoppingCartRestService.grs");
      templateList.getTemplates().add(restServiceTemplate);
      businessLogicFolder.getChildren().add(restServiceTemplate);
   }

   private void createShoppingCart() 
   {
      String content = ShoppingCartSample.INSTANCE.getShoppingCartSource().getText();
      FileTemplate template =
         new FileTemplate(MimeType.CHROMATTIC_DATA_OBJECT, "ShoppingCart.groovy", "ShoppingCart Data Object", content,
            true);
      template.setFileName("ShoppingCart.groovy");
      templateList.getTemplates().add(template);
      dataFolder.getChildren().add(template);
   }
   
   private void createShop() 
   {
      String content = ShoppingCartSample.INSTANCE.getShopSource().getText();
      FileTemplate template =
         new FileTemplate(MimeType.CHROMATTIC_DATA_OBJECT, "Shop.groovy", "Shop Data Object", content,
            true);
      template.setFileName("Shop.groovy");
      templateList.getTemplates().add(template);
      dataFolder.getChildren().add(template);
   }

   private void createItemToPurchase() 
   {
      String content = ShoppingCartSample.INSTANCE.getItemToPurchaseSource().getText();
      FileTemplate template =
         new FileTemplate(MimeType.CHROMATTIC_DATA_OBJECT, "ItemToPurchase.groovy", "ItemToPurchase Data Object",
            content, true);
      template.setFileName("ItemToPurchase.groovy");
      templateList.getTemplates().add(template);
      dataFolder.getChildren().add(template);
   }

   private void createProduct() 
   {
      String content = ShoppingCartSample.INSTANCE.getProductSource().getText();
      FileTemplate template =
         new FileTemplate(MimeType.CHROMATTIC_DATA_OBJECT, "Product.groovy", "Product Data Object", content, true);
      template.setFileName("Product.groovy");
      templateList.getTemplates().add(template);
      dataFolder.getChildren().add(template);
   }

   public List<Template> getTemplateList()
   {
      return templateList.getTemplates();
   }
   
   public ProjectTemplate getProjectTemplate()
   {
      return shoppingCartPrj;
   }

}
