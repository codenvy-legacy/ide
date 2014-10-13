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
package com.codenvy.ide.extension.runner.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

import org.vectomatic.dom.svg.ui.SVGResource;

/**
 * Client resources.
 *
 * @author Artem Zatsarynnyy
 */
public interface RunnerResources extends ClientBundle {

    @Source("run.svg")
    SVGResource launchApp();

    @Source("logs.svg")
    SVGResource getAppLogs();

    @Source("shutdown.svg")
    SVGResource shutdownApp();

    @Source("update.png")
    ImageResource updateApp();

    @Source("run-with.svg")
    SVGResource runWith();

    @Source("edit-custom-environments.svg")
    SVGResource editCustomEnvironments();

    @Source("environment.svg")
    SVGResource environment();

    @Source("clear-logs.svg")
    SVGResource clear();

    @Source("view-recipe.svg")
    SVGResource viewRecipe();

    @Source("in-queue.svg")
    SVGResource inQueue();

    @Source("in-progress.svg")
    SVGResource inProgress();

    @Source("running.svg")
    SVGResource running();

    @Source("done.svg")
    SVGResource done();

    @Source("failed.svg")
    SVGResource failed();

    @Source("timeout.svg")
    SVGResource timeout();

    public interface Css extends CssResource {
        @ClassName("info-panel")
        String infoPanel();

        @ClassName("data-label")
        String dataLabel();

        @ClassName("main-style")
        String mainStyle();

        @ClassName("tab-selected")
        String tabSelected();

        @ClassName("partIcon")
        String partIcon();

        @ClassName("inQueue")
        String inQueue();

        @ClassName("inProgress")
        String inProgress();

        @ClassName("running")
        String running();

        @ClassName("done")
        String done();

        @ClassName("failed")
        String failed();

        @ClassName("timeout")
        String timeout();

        @ClassName("runButton")
        String runButton();

        @ClassName("cancelButton")
        String cancelButton();

        @ClassName("tree-icon")
        String treeIcon();
    }

    @Source({"runner.css", "com/codenvy/ide/api/ui/style.css"})
    Css runner();
}
