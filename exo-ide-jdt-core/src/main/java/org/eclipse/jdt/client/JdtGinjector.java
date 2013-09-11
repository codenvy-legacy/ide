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
package org.eclipse.jdt.client;

import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

import org.eclipse.jdt.client.internal.corext.codemanipulation.AddGetterSetterPresenter;
import org.eclipse.jdt.client.internal.corext.codemanipulation.CodemanipulationModule;
import org.eclipse.jdt.client.internal.corext.codemanipulation.GenerateNewConstructorUsingFieldsPresenter;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
@GinModules({JdtModule.class, CodemanipulationModule.class})
public interface JdtGinjector extends Ginjector {

    AddGetterSetterPresenter getSetterGetterPresenter();

    GenerateNewConstructorUsingFieldsPresenter getNewConstructorUsingFields();
}
