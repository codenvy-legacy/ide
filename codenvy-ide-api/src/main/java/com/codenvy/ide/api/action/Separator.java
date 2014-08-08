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
package com.codenvy.ide.api.action;

/**
 * Represents a separator.
 *
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class Separator extends Action {
    private static final Separator ourInstance = new Separator();

    private String myText;

    public Separator() {
    }

    public Separator(final String text) {
        myText = text;
    }

    public String getText() {
        return myText;
    }

    public static Separator getInstance() {
        return ourInstance;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException();
    }
}
