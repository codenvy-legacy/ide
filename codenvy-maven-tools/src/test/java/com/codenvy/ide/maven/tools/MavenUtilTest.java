/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2013] Codenvy, S.A.
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.maven.tools;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/** @author andrew00x */
public class MavenUtilTest {
    @Test
    public void testReadPom() throws IOException {
        URL pom = Thread.currentThread().getContextClassLoader().getResource("test-pom.xml");
        Assert.assertNotNull(pom);
        Model model = MavenUtils.readModel(new File(pom.getFile()));
        Assert.assertEquals("a", model.getArtifactId());
        Parent parent = model.getParent();
        Assert.assertEquals("parent", parent.getGroupId());
        Assert.assertEquals("parent", parent.getArtifactId());
        Assert.assertEquals("x.x.x", parent.getVersion());
        List<Dependency> dependencies = model.getDependencies();
        Assert.assertEquals(dependencies.size(), 1);
        Dependency dependency = dependencies.get(0);
        Assert.assertEquals("x", dependency.getGroupId());
        Assert.assertEquals("y", dependency.getArtifactId());
        Assert.assertEquals("z", dependency.getVersion());
    }

    @Test
    public void testWrite() throws Exception {
        File workDir = new File(System.getProperty("workDir"));
        File pom = new File(workDir, "testWrite-pom.xml");
        List<Dependency> deps = new ArrayList<>(1);
        Dependency dependency = new Dependency();
        dependency.setGroupId("x");
        dependency.setArtifactId("y");
        dependency.setVersion("z");
        dependency.setScope("test");
        deps.add(dependency);
        Model model = new Model();
        model.setGroupId("a");
        model.setArtifactId("b");
        model.setVersion("c");
        model.setDescription("test pom");
        model.setDependencies(deps);
        model.setPomFile(pom);
        MavenUtils.writeModel(model);
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document dom = documentBuilder.parse(pom);
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        Assert.assertEquals("a", xpath.evaluate("/project/groupId", dom, XPathConstants.STRING));
        Assert.assertEquals("b", xpath.evaluate("/project/artifactId", dom, XPathConstants.STRING));
        Assert.assertEquals("c", xpath.evaluate("/project/version", dom, XPathConstants.STRING));
        Assert.assertEquals("test pom", xpath.evaluate("/project/description", dom, XPathConstants.STRING));
        NodeList depsNodeList = (NodeList)xpath.evaluate("/project/dependencies", dom, XPathConstants.NODESET);
        Assert.assertEquals(1, depsNodeList.getLength());
        Node node = depsNodeList.item(0);
        Assert.assertEquals("x", xpath.evaluate("dependency/groupId", node, XPathConstants.STRING));
        Assert.assertEquals("y", xpath.evaluate("dependency/artifactId", node, XPathConstants.STRING));
        Assert.assertEquals("z", xpath.evaluate("dependency/version", node, XPathConstants.STRING));
        Assert.assertEquals("test", xpath.evaluate("dependency/scope", node, XPathConstants.STRING));
    }

    @Test
    public void testWriteToFile() throws Exception {
        File workDir = new File(System.getProperty("workDir"));
        File pom = new File(workDir, "testWrite-pom.xml");
        File pom2 = new File(workDir, "testWriteToFile-pom.xml");
        List<Dependency> deps = new ArrayList<>(1);
        Dependency dependency = new Dependency();
        dependency.setGroupId("x");
        dependency.setArtifactId("y");
        dependency.setVersion("z");
        dependency.setScope("test");
        deps.add(dependency);
        Model model = new Model();
        model.setGroupId("a");
        model.setArtifactId("b");
        model.setVersion("c");
        model.setDescription("test pom");
        model.setDependencies(deps);
        model.setPomFile(pom);
        MavenUtils.writeModel(model, pom2);
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document dom = documentBuilder.parse(pom2);
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        Assert.assertEquals("a", xpath.evaluate("/project/groupId", dom, XPathConstants.STRING));
        Assert.assertEquals("b", xpath.evaluate("/project/artifactId", dom, XPathConstants.STRING));
        Assert.assertEquals("c", xpath.evaluate("/project/version", dom, XPathConstants.STRING));
        Assert.assertEquals("test pom", xpath.evaluate("/project/description", dom, XPathConstants.STRING));
        NodeList depsNodeList = (NodeList)xpath.evaluate("/project/dependencies", dom, XPathConstants.NODESET);
        Assert.assertEquals(depsNodeList.getLength(), 1);
        Node node = depsNodeList.item(0);
        Assert.assertEquals("x", xpath.evaluate("dependency/groupId", node, XPathConstants.STRING));
        Assert.assertEquals("y", xpath.evaluate("dependency/artifactId", node, XPathConstants.STRING));
        Assert.assertEquals("z", xpath.evaluate("dependency/version", node, XPathConstants.STRING));
        Assert.assertEquals("test", xpath.evaluate("dependency/scope", node, XPathConstants.STRING));
    }

    @Test
    public void testAddDependency() throws Exception {
        File workDir = new File(System.getProperty("workDir"));
        File pom = new File(workDir, "testAddDependency-pom.xml");
        Model model = new Model();
        model.setGroupId("a");
        model.setArtifactId("b");
        model.setVersion("c");
        model.setDescription("test pom");
        model.setPomFile(pom);
        MavenUtils.writeModel(model);
        Dependency dependency = new Dependency();
        dependency.setGroupId("x");
        dependency.setArtifactId("y");
        dependency.setVersion("z");
        dependency.setScope("test");
        MavenUtils.addDependency(pom, dependency);
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document dom = documentBuilder.parse(pom);
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        NodeList depsNodeList = (NodeList)xpath.evaluate("/project/dependencies", dom, XPathConstants.NODESET);
        Assert.assertEquals(depsNodeList.getLength(), 1);
        Node node = depsNodeList.item(0);
        Assert.assertEquals("x", xpath.evaluate("dependency/groupId", node, XPathConstants.STRING));
        Assert.assertEquals("y", xpath.evaluate("dependency/artifactId", node, XPathConstants.STRING));
        Assert.assertEquals("z", xpath.evaluate("dependency/version", node, XPathConstants.STRING));
        Assert.assertEquals("test", xpath.evaluate("dependency/scope", node, XPathConstants.STRING));
    }

    @Test
    public void testAddDependencyWithModel() throws Exception {
        File workDir = new File(System.getProperty("workDir"));
        File pom = new File(workDir, "testAddDependencyWithModel-pom.xml");
        Model model = new Model();
        model.setGroupId("a");
        model.setArtifactId("b");
        model.setVersion("c");
        model.setDescription("test pom");
        model.setPomFile(pom);
        MavenUtils.writeModel(model);
        Model dependency = new Model();
        dependency.setGroupId("x");
        dependency.setArtifactId("y");
        dependency.setVersion("z");
        dependency.setDescription("test dependency pom");
        dependency.setPomFile(pom);
        MavenUtils.addDependency(pom, dependency);
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document dom = documentBuilder.parse(pom);
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        NodeList depsNodeList = (NodeList)xpath.evaluate("/project/dependencies", dom, XPathConstants.NODESET);
        Assert.assertEquals(depsNodeList.getLength(), 1);
        Node node = depsNodeList.item(0);
        Assert.assertEquals("x", xpath.evaluate("dependency/groupId", node, XPathConstants.STRING));
        Assert.assertEquals("y", xpath.evaluate("dependency/artifactId", node, XPathConstants.STRING));
        Assert.assertEquals("z", xpath.evaluate("dependency/version", node, XPathConstants.STRING));
        // there is no 'scope' in this case
        Assert.assertEquals("", xpath.evaluate("dependency/scope", node, XPathConstants.STRING));
    }
}
