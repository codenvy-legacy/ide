/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.jseditor.client.debug;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Resources interface for the breakpoints marks.
 */
public interface BreakpointResources extends ClientBundle {

    /**
     * Image for the breakpoint mark.
     * 
     * @return the image for a breakpoint
     */
    @Source("org/eclipse/che/ide/texteditor/renderer/breakpoint.png")
    ImageResource breakpoint();

    /**
     * Image for the active/current breakpoint mark.
     * 
     * @return the image for the current breakpoint
     */
    @Source("org/eclipse/che/ide/texteditor/renderer/breakpoint-current.gif")
    ImageResource currentBreakpoint();
}
