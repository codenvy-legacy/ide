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
package org.exoplatform.ide.chromattic;

import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.ext.app.SessionProviderService;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.resource.JcrURLConnection;
import org.exoplatform.services.jcr.ext.resource.NodeRepresentationService;
import org.exoplatform.services.jcr.ext.resource.UnifiedNodeReference;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;

import java.net.URL;
import java.util.Calendar;

import javax.jcr.Node;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class MimeTypeResolverTest extends BaseTest
{
   /** . */
   private static final String dataObjectGroovy =
      "@org.chromattic.api.annotations.PrimaryType(name=\"nt:unstructured\")\n" + "class DataObject {\n"
         + "@org.chromattic.api.annotations.Property(name = \"a\") def String a;\n" + "}";
   
   private SessionProviderService sessionProviderService; 
   
   private NodeRepresentationService representationService;

   @Override
   public void setUp() throws Exception
   {
      super.setUp();
      Node groovyRepo = root.addNode("dependencies", "nt:folder");
      Node test1 = groovyRepo.addNode("DataObject.groovy", "nt:file");
      test1 = test1.addNode("jcr:content", "nt:resource");
      test1.setProperty("jcr:mimeType", "script/groovy");
      test1.setProperty("jcr:lastModified", Calendar.getInstance());
      test1.setProperty("jcr:data", dataObjectGroovy);
      session.save();
      
      sessionProviderService = (ThreadLocalSessionProviderService)container.getComponentInstanceOfType(SessionProviderService.class);
      ConversationState state = new ConversationState(new Identity("root"));
      SessionProvider sessionProvider = new SessionProvider(state);
      ConversationState.setCurrent(state);
      sessionProvider.setCurrentRepository(repository);
      sessionProviderService.setSessionProvider(null, sessionProvider);
      representationService = (NodeRepresentationService)container.getComponentInstanceOfType(NodeRepresentationService.class);
   }
   
   public void testResolver() 
   {
      try {
      UnifiedNodeReference url1 = new UnifiedNodeReference("db1", "ws", "/dependencies/DataObject.groovy");
//      URL url = new URL("jcr://db1/ws/#/dependencies/DataObject.groovy");
//      url1.getURL().
//      JcrURLConnection conn = (JcrURLConnection)url1.getURL().openConnection();
      assertTrue(MimeTypeResolver.resolve(url1, "script/groovy"));//, sessionProviderService.getSessionProvider(null), representationService));
      } catch (Exception e) {
         e.printStackTrace();
      }
     
   }

}
