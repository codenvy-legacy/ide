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
package org.exoplatform.ide.java.client.projectmodel;

import com.google.gwt.http.client.Response;

import org.apache.commons.io.IOUtils;
import org.exoplatform.ide.commons.exception.UnmarshallerException;
import org.exoplatform.ide.java.client.BaseTest;
import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.resources.model.Resource;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import static org.fest.assertions.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class ModelUnmarshallerTest extends BaseTest
{

   private static String projectJs;

   @Mock
   private JavaProject project;

   @Mock
   private JavaProjectDesctiprion projectDescription;

   @Mock
   private Response response;

   @BeforeClass
   public static void init()
   {
      InputStream stream =
         Thread.currentThread().getContextClassLoader()
            .getResourceAsStream("org/exoplatform/ide/java/client/projectmodel/project.js");
      try
      {
         projectJs = IOUtils.toString(stream);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      finally
      {
         if (stream != null)
         {
            try
            {
               stream.close();
            }
            catch (IOException e)
            {
               e.printStackTrace();
            }
         }
      }
   }

   @Before
   public void setUp()
   {
      when(project.getDescription()).thenReturn(projectDescription);
      when(project.getPath()).thenReturn("/SpringProject");
      when(projectDescription.getSourceFolders()).thenReturn(
         JsonCollections.createStringSet("src/main/java", "src/main/resources", "src/test/java", "src/test/resources"));
      when(response.getText()).thenReturn(projectJs);
   }

   @Test
   public void sourceFoldersParse() throws UnmarshallerException
   {
      JavaModelUnmarshaller unmarshaller = new JavaModelUnmarshaller(project);
      unmarshaller.unmarshal(response);
      ArgumentCaptor<Resource> childrens = ArgumentCaptor.forClass(Resource.class);
      verify(project, times(7)).addChild(childrens.capture());
      List<Resource> allValues = childrens.getAllValues();
      assertThat(allValues).onProperty("name").containsOnly("src/main/java", "src/test/java", "src/main/resources",
         "src/test/java", "src/test/resources", "src", "pom.xml", ".project");
   }
}
