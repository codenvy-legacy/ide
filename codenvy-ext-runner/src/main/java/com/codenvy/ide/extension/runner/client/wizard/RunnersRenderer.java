/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/

package com.codenvy.ide.extension.runner.client.wizard;

import elemental.dom.Element;
import elemental.html.SpanElement;

import com.codenvy.api.project.shared.dto.RunnerEnvironment;
import com.codenvy.api.project.shared.dto.RunnerEnvironmentLeaf;
import com.codenvy.api.project.shared.dto.RunnerEnvironmentTree;
import com.codenvy.ide.extension.runner.client.RunnerResources;
import com.codenvy.ide.ui.tree.NodeRenderer;
import com.codenvy.ide.ui.tree.TreeNodeElement;
import com.codenvy.ide.util.dom.Elements;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.ui.SVGResource;

/**
* @author Evgen Vidolob
*/
@Singleton
public class RunnersRenderer implements NodeRenderer<Object> {

    @Inject
    private RunnerResources resources;


    @Override
    public Element getNodeKeyTextContainer(SpanElement treeNodeLabel) {
        return null;
    }

    @Override
    public SpanElement renderNodeContents(Object data) {
        SpanElement rootElement = Elements.createSpanElement();
        if (data instanceof RunnerEnvironmentTree) {
            rootElement.setInnerHTML(((RunnerEnvironmentTree)data).getDisplayName());
        } else if (data instanceof RunnerEnvironmentLeaf) {
            SVGResource environment = resources.environment();
            SVGImage image = new SVGImage(environment);
            image.getElement().setAttribute("class", resources.runner().treeIcon());
            rootElement.appendChild((elemental.dom.Node)image.getElement());
            rootElement.setInnerHTML(rootElement.getInnerHTML() + "&nbsp;" + ((RunnerEnvironmentLeaf)data).getDisplayName());
        }

        return rootElement;
    }

    @Override
    public void updateNodeContents(TreeNodeElement<Object> treeNode) {

    }
}
