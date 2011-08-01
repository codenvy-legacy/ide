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
package org.exoplatform.ide.client.samples.twittertrends;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.model.template.FileTemplate;
import org.exoplatform.ide.client.model.template.ProjectTemplate;
import org.exoplatform.ide.client.model.template.Template;
import org.exoplatform.ide.client.model.template.TemplateList;

import java.util.List;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class TwitterTrendsProject
{
   
   private TemplateList templateList = new TemplateList();

   private ProjectTemplate twitterTrendsPrj = new ProjectTemplate("twitter-trends-project");
   
   
   public TwitterTrendsProject()
   {
      createGadget();
      createRestService();
      createReadme();
      twitterTrendsPrj.setDescription("Twitter trend as images sample project");
      twitterTrendsPrj.setDefault(true);
      templateList.getTemplates().add(twitterTrendsPrj);
      
   }
   
   public List<Template> getTemplateList()
   {
      return templateList.getTemplates();
   }
   
   private void createGadget()
   {
      String content = TwitterTrendsSample.INSTANCE.getTwitterTrendsGadget().getText();
      FileTemplate gadgetFileTemplate =
         new FileTemplate(MimeType.GOOGLE_GADGET, "TwitterTrendsGadget.xml", "Twitter Trends Gadget", content, true);
      gadgetFileTemplate.setFileName("TwitterTrendsGadget.xml");
      templateList.getTemplates().add(gadgetFileTemplate);
      twitterTrendsPrj.getChildren().add(gadgetFileTemplate);
   }
   
   private void createRestService() 
   {
      String content = TwitterTrendsSample.INSTANCE.getTwitterTrendsService().getText();
      FileTemplate restServiceTemplate =
         new FileTemplate(MimeType.GROOVY_SERVICE, "TwitterTrendsService.grs", "Twitter Trends Gadget REST service",
            content, true);
      restServiceTemplate.setFileName("TwitterTrendsService.grs");
      templateList.getTemplates().add(restServiceTemplate);
      twitterTrendsPrj.getChildren().add(restServiceTemplate);
   }
   
   private void createReadme() 
   {
      String content = TwitterTrendsSample.INSTANCE.getReadme().getText();
      FileTemplate template =
         new FileTemplate(MimeType.TEXT_PLAIN, "readme_twitter_flicker.txt", "twiter trends readme file",
            content, true);
      template.setFileName("readme_twitter_flicker.txt");
      templateList.getTemplates().add(template);
      twitterTrendsPrj.getChildren().add(template);
   }
   
   public ProjectTemplate getProjectTemplate()
   {
      return twitterTrendsPrj;
   }

}
