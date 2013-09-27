/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
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
package org.eclipse.jdt.client.core;

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
