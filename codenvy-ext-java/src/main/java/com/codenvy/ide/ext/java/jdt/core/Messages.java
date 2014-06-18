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
package com.codenvy.ide.ext.java.jdt.core;

import com.google.gwt.core.client.GWT;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface Messages extends com.google.gwt.i18n.client.Messages {

    Messages INSTANCE = GWT.create(Messages.class);

    /** @return  */
    String convention_package_nullName();

    /** @return  */
    String convention_package_emptyName();

    /** @return  */
    String convention_package_dotName();

    /** @return  */
    String convention_package_nameWithBlanks();

    /** @return  */
    String convention_package_consecutiveDotsName();

    /**
     * @param typeName
     * @return
     */
    String convention_illegalIdentifier(String typeName);

    /** @return  */
    String convention_package_uppercaseName();

    String GetterSetterCompletionProposal_getter_label(String str);

    String GetterSetterCompletionProposal_setter_label(String str);

    String MethodCompletionProposal_constructor_label();

    String MethodCompletionProposal_method_label();

    @Key("formatter.title")
    String formatterTitle();
}
