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
package org.eclipse.jdt.client.internal.corext.codemanipulation;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class CodemanipulationModule extends AbstractGinModule {

    /** @see com.google.gwt.inject.client.AbstractGinModule#configure() */
    @Override
    protected void configure() {
        bind(AddGetterSetterPresenter.Display.class).to(AddGetterSetterView.class);
        bind(new TypeLiteral<MultiSelectionModel<Object>>() {
        });
        bind(GetterSetterTreeModel.class);
        bind(GenerateNewConstructorUsingFieldsPresenter.Display.class).to(GenerateNewConstructorUsingFieldsView.class);
    }

    @Provides
    @Singleton
    GetterSetterEntryProvider provide() {
        return AddGetterSetterPresenter.get();
    }
}
