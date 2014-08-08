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
package com.codenvy.ide.api.wizard;

/**
 * Wizard dialog manages wizard pages. It's responsible for communication user with the wizard page. In typical usage, the client
 * instantiates this class with a particular wizard. The wizard dialog orchestrates the presentation of wizard pages.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface WizardDialog {
    /** Show wizard dialog. */
    void show();
}