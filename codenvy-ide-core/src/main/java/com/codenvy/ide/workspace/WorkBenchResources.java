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
package com.codenvy.ide.workspace;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface WorkBenchResources extends ClientBundle {
    public interface WorkBenchCss extends CssResource {

        @ClassName("ide-work-bench-tool-panel-bottom")
        String ideWorkBenchToolPanelBottom();

        @ClassName("ide-work-bench-tool-panel-left")
        String ideWorkBenchToolPanelLeft();

        @ClassName("ide-work-bench-tool-panel-right")
        String ideWorkBenchToolPanelRight();

        @ClassName("ide-work-bench-split-panel-left")
        String ideWorkBenchSplitPanelLeft();

        @ClassName("ide-work-bench-split-panel-bottom")
        String ideWorkBenchSplitPanelBottom();

        @ClassName("ide-work-bench-parent-panel")
        String ideWorkBenchParentPanel();
    }

    @Source({"WorkBench.css", "com/codenvy/ide/api/ui/style.css"})
    WorkBenchCss workBenchCss();
}
