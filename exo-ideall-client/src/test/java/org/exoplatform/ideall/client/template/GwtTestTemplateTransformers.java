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
package org.exoplatform.ideall.client.template;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ideall.client.AbstractGwtTest;
import org.exoplatform.ideall.client.TestResponse;
import org.exoplatform.ideall.client.model.template.Template;
import org.exoplatform.ideall.client.model.template.TemplateList;
import org.exoplatform.ideall.client.model.template.marshal.TemplateListUnmarshaller;
import org.exoplatform.ideall.client.model.template.marshal.TemplateMarshaller;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class GwtTestTemplateTransformers extends AbstractGwtTest
{
   private String templateListString =
      "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
         + "<templates xmlns:app=\"http://www.gatein.org/jcr/application-registry/1.0/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:exo=\"http://www.exoplatform.com/jcr/exo/1.0\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:fn_old=\"http://www.w3.org/2004/10/xpath-functions\" xmlns:gtn=\"http://www.gatein.org/jcr/gatein/1.0/\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:mop=\"http://www.gatein.org/jcr/mop/1.0/\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:pc=\"http://www.gatein.org/jcr/pc/1.0/\" xmlns:rep=\"internal\" xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\" xmlns:tkn=\"http://www.gatein.org/jcr/token/1.0/\" xmlns:wsrp=\"http://www.gatein.org/jcr/wsrp/1.0/\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" jcr:primaryType=\"exo:registryGroup\">"
         + "<template-1277882702980 jcr:primaryType=\"exo:registryGroup\">"
         + "<template jcr:primaryType=\"exo:registryEntry\">"
         + "<name jcr:primaryType=\"nt:unstructured\">xml%20template</name>"
         + "<description jcr:primaryType=\"nt:unstructured\">new%20xml%20template</description>"
         + "<mime-type jcr:primaryType=\"nt:unstructured\">text%2Fxml</mime-type><content jcr:primaryType=\"nt:unstructured\">%3C%3Fxml%20version%3D'1.0'%20encoding%3D'UTF-8'%3F%3E%0A</content></template></template-1277882702980><template-1277882805372 jcr:primaryType=\"exo:registryGroup\"><template jcr:primaryType=\"exo:registryEntry\"><name jcr:primaryType=\"nt:unstructured\">css%20template</name><description jcr:primaryType=\"nt:unstructured\">title%20in%20css</description><mime-type jcr:primaryType=\"nt:unstructured\">text%2Fcss</mime-type>"
         + "<content jcr:primaryType=\"nt:unstructured\">.title%20%7B%0A%20%20%20%20background%3A%20blue%3B%0A%7D%0A</content>"
         + "</template>" + "</template-1277882805372>" + "</templates>";

   /**
    * Test marshaller for template request. 
    */
   public void testTemplateMarshaler()
   {
      String mimeType = MimeType.TEXT_CSS;
      String name = "new css template";
      String description = "temaplte for css";
      String content = ".header {height : 30px;}";
      Template template = new Template(mimeType, name, description, content, null);
      TemplateMarshaller marshaller = new TemplateMarshaller(template);
      try
      {
         String request = marshaller.marshal();
         assertTrue(request.length() > 0);
      }
      catch (Exception e)
      {
         fail();
      }
   }

   /**
    * Test unmarshaller of templates list response.
    */
   public void testTemplateListUnmarshaler()
   {
      TemplateList templateList = new TemplateList();
      TemplateListUnmarshaller unmarshaller = new TemplateListUnmarshaller(null, templateList);
      TestResponse testResponse = new TestResponse(templateListString);
      try
      {
         unmarshaller.unmarshal(testResponse);
         assertEquals(2, templateList.getTemplates().size());
         Template template = templateList.getTemplates().get(0);
         assertEquals("xml template", template.getName());
         assertEquals("new xml template", template.getDescription());
         assertEquals("text/xml", template.getMimeType());
         template = templateList.getTemplates().get(1);
         assertEquals("css template", template.getName());
         assertEquals("title in css", template.getDescription());
         assertEquals("text/css", template.getMimeType());
      }
      catch (UnmarshallerException e)
      {
         fail();
      }
   }
}
