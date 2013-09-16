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
package org.exoplatform.ide.editor.java.client;

import com.google.gwt.i18n.client.Messages;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Dec 1, 2011 5:04:44 PM evgen $
 */
public interface JavaConstants extends Messages {
    @Key("create.java.class.view.title")
    String createJavaClassTitle();

    @Key("create.java.class.view.label.main")
    String createJCLabelMain();

    @Key("create.java.class.view.label.desciption")
    String createJCLabelDesciption();

    @Key("create.java.class.view.label.source")
    String createJCLabelSource();

    @Key("create.java.class.view.label.package")
    String createJCLabelPackage();

    @Key("create.java.class.view.label.name")
    String createJCLabelName();

    @Key("create.java.class.view.label.modifiers")
    String createJCLabelModifiers();

    @Key("create.java.class.view.label.superclass")
    String createJCLabelSuperclass();

    @Key("create.java.class.view.label.interfaces")
    String createJCLabelInterfaces();

    @Key("create.java.class.view.label.subs")
    String createJCLabelStubs();

    @Key("create.java.class.view.label.subs.constructors")
    String createJCLabelConstructors();

    @Key("create.java.class.view.label.subs.methods")
    String createJCLabelMethods();

    @Key("create.java.class.view.button.browse")
    String createJCButtonBrowse();

    @Key("create.java.class.view.button.add")
    String createJCButtonAdd();

    @Key("create.java.class.view.button.create")
    String createJCButtonCreate();

    @Key("create.java.class.view.button.cancel")
    String createJCButtonCancel();

    @Key("create.java.class.view.button.remove")
    String createJCButtonRemove();

    @Key("search.type.button.search")
    String searchTypeButtonSearch();

    @Key("search.type.superclass")
    String searchTypeSuperClass();

    @Key("search.type.interaces")
    String searchTypeInterfaces();

    @Key("search.type.input.text")
    String searchTypeInputText();

    @Key("search.type.result")
    String searchTypeResult();

    @Key("search.type.button.ok")
    String searchTypeButtonOk();

    @Key("create.java.class.view.error.final.abstract")
    String abstractFinalError();

    @Key("create.java.class.view.error.name")
    String nameError();

    @Key("create.java.class.view.error.package")
    String packageError();

    @Key("create.java.type")
    String createJavaType();

    @Key("control.package.title")
    String controlPackageTitle();

    @Key("control.package.prompt")
    String controlPackagePrompt();

    @Key("create.package.label")
    String createPackageLabel();

    @Key("create.package.title")
    String createPackageTitle();

    @Key("create.package.button.create")
    String createPackageButtonCreate();

    @Key("create.package.button.cancel")
    String createPackageButtonCancel();

}
