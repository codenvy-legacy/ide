/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.gwtframework.commons.wadl;

import com.google.gwt.junit.client.GWTTestCase;

import org.exoplatform.gwtframework.commons.xml.QName;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class WadlProcessorTest extends GWTTestCase {

    private String wadlXml =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
            + "<application "
            + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
            + "xsi:schemaLocation=\"https://wadl.dev.java.net/wadl20061109.xsd\" "
            + "xmlns:xssd=\"http://www.w3.org/2001/XMLSchema\" "
            + "xmlns=\"http://wadl.dev.java.net/2009/02\" >"
            +

            "<doc title=\"Hello\" xml:lang=\"us\">"
            + "Text documentation"
            + "</doc> "
            + "<grammars>"
            + "<include "
            + "href=\"NewsSearchResponse.xsd\">"
            + "<doc title=\"Help\" xml:lang=\"en\">"
            + "Doc text!!!"
            + "</doc>"
            + "</include>"
            + "<include "
            + "href=\"Error.xsd\"/>"
            + "</grammars>"
            +

            "<resources base=\"http://localhost:8080/rest\">"
            + "<resource path=\"/jcr-service\">"
            + "<method name=\"OPTIONS\">"
            + "<response>"
            + "<representation mediaType=\"application/vnd.sun.wadl+xml\" />"
            + "</response>"
            + "</method>"
            + "<resource path=\"/update-workspace-config/{repositoryName}/{workspaceName}\">"
            + "<param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\"  style=\"template\" name=\"repositoryName\" >"
            + "<option value=\"json\"/>"
            + "</param>"
            + "<param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\"  style=\"template\" name=\"workspaceName\" />"
            + "<method name=\"POST\" id=\"updateWorkspaceConfiguration\">"
            + "<request>"
            + "<representation mediaType=\"application/json\" />"
            + "<param style=\"query\" />"
            + "</request>"
            + "<response>"
            + "<representation mediaType=\"*/*\" />"
            + "</response>"
            + "</method>"
            + "</resource>"
            + "<resource path=\"/create-workspace/{repositoryName}\">"
            + "<param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\"  style=\"template\" name=\"repositoryName\" />"
            + "<method name=\"POST\" id=\"createWorkspace\">"
            + "<request>"
            + "<representation mediaType=\"application/json\" />"
            + "</request>"
            + "<response>"
            + "<representation mediaType=\"*/*\" />"
            + "</response>"
            + "</method>"
            + "</resource>"
            + "<resource path=\"/repository-service-configuration\">"
            + "<method name=\"GET\" id=\"getRepositoryServiceConfiguration\">"
            + "<response>"
            + "<representation mediaType=\"application/json\" />"
            + "</response>"
            + "</method>"
            + "</resource>"
            + "<resource path=\"/default-ws-config/{repositoryName}\">"
            + "<param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\"  style=\"template\" name=\"repositoryName\" />"
            + "<method name=\"GET\" id=\"getDefaultWorkspaceConfig\">"
            + "<response>"
            + "<representation mediaType=\"application/json\" />"
            + "</response>"
            + "</method>"
            + "</resource>"
            + "<resource path=\"/workspaces/{repositoryName}\">"
            + "<param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\"   style=\"template\" name=\"repositoryName\" />"
            + "<method name=\"GET\" id=\"getWorkspaceNames\">"
            + "<response>"
            + "<representation mediaType=\"application/json\" />"
            + "</response>"
            + "</method>"
            + "</resource>"
            + "<resource path=\"/repositories\">"
            + "<method name=\"GET\" id=\"getRepositoryNames\">"
            + "<response>"
            + "<representation mediaType=\"application/json\" />"
            + "</response>"
            + "</method>"
            + "</resource>"
            + "<resource path=\"/remove-repository/{repositoryName}/{forseSessionClose}\">"
            + "<param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:boolean\" style=\"template\" name=\"forseSessionClose\" />"
            + "<param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\"  style=\"template\" name=\"repositoryName\" />"
            + "<method name=\"POST\" id=\"removeRepository\">"
            + "<response>"
            + "<representation mediaType=\"*/*\" />"
            + "</response>"
            + "</method>"
            + "</resource>"
            + "<resource path=\"/create-repository\">"
            + "<method name=\"POST\" id=\"createRepository\">"
            + "<request>"
            + "<representation mediaType=\"application/json\" />"
            + "</request>"
            + "<response>"
            + "<representation mediaType=\"*/*\" />"
            + "</response>"
            + "</method>"
            + "</resource>"
            + "<resource path=\"/remove-workspace/{repositoryName}/{workspaceName}/{forseSessionClose}/\">"
            + "<param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:boolean\"  style=\"template\" name=\"forseSessionClose\" />"
            + "<param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\"   style=\"template\" name=\"repositoryName\" />"
            + "<param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\"   style=\"template\" name=\"workspaceName\" />"
            + "<method name=\"POST\" id=\"removeWorkspace\">" + "<response>" + "<representation mediaType=\"*/*\" />"
            + "</response>" + "</method>" + "</resource>" + "</resource>" + "</resources>" + "</application>";


    @Override
    public String getModuleName() {
        return "org.exoplatform.gwtframework.commons.CommonsJUnit";
    }

    @Test
    public void testUnmarshaler() {
        WadlApplication application = new WadlApplication();
        try {
            WadlProcessor.unmarshal(application, wadlXml);
            Doc docApp = application.getDoc().get(0);
            assertEquals("Hello", docApp.getTitle());
            assertEquals("us", docApp.getLang());
            assertEquals("Text documentation", (String)docApp.getContent().get(0));
            Grammars gram = application.getGrammars();
            assertEquals(2, gram.getInclude().size());
            assertEquals("NewsSearchResponse.xsd", gram.getInclude().get(0).href);
        } catch (IllegalWADLException e) {

            System.err.println(e.getMessage());
            fail("IllegalWADL");
        }
    }

    @Test
    public void testUnmarshalerResources() {
        WadlApplication application = new WadlApplication();
        try {
            WadlProcessor.unmarshal(application, wadlXml);
            Resources res = application.getResources();
            assertEquals("http://localhost:8080/rest", res.getBase());

            assertEquals(1, res.getResource().size());

            assertEquals(10, res.getResource().get(0).getMethodOrResource().size());

            Resource resource = res.getResource().get(0);
            assertEquals("/jcr-service", resource.getPath());

            Method method = (Method)resource.getMethodOrResource().get(0);
            assertEquals("OPTIONS", method.getName());

            RepresentationType rep = method.getResponse().getRepresentationOrFault().get(0);
            assertEquals("application/vnd.sun.wadl+xml", rep.getMediaType());

        } catch (IllegalWADLException e) {
            System.err.println(e.getMessage());
            fail("IllegalWADL");
        }
    }

    @Test
    public void testUnmarshalerParam() {
        WadlApplication application = new WadlApplication();
        try {
            WadlProcessor.unmarshal(application, wadlXml);
            Resource res = (Resource)application.getResources().getResource().get(0).getMethodOrResource().get(1);
            Param param = res.getParam().get(0);
            QName name = new QName("xmlns:xs", "http://www.w3.org/2000/xmlns/");
            assertEquals("http://www.w3.org/2001/XMLSchema", param.getOtherAttributes().get(name));
            assertEquals("xs:string", param.getType().getPrefix() + ":" + param.getType().getLocalName());
            assertEquals("template", param.getStyle().value());
            assertEquals("repositoryName", param.getName());
        } catch (IllegalWADLException e) {
            System.err.println(e.getMessage());
            fail("IllegalWADL");
        }

    }

    @Test
    public void testUnmarshalerMethod() {
        try {
            WadlApplication application = new WadlApplication();
            WadlProcessor.unmarshal(application, wadlXml);
            Resource res = (Resource)application.getResources().getResource().get(0).getMethodOrResource().get(1);
            Method method = (Method)res.getMethodOrResource().get(0);
            assertEquals("POST", method.getName());
            assertEquals("updateWorkspaceConfiguration", method.getId());
        } catch (IllegalWADLException e) {
            System.err.println(e.getMessage());
            fail("IllegalWADL");
        }
    }
}
