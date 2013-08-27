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
package org.exoplatform.ide.editor.ruby.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public interface RubyClientBundle extends ClientBundle {

    RubyClientBundle INSTANCE = GWT.create(RubyClientBundle.class);

    @Source("org/exoplatform/ide/editor/ruby/client/styles/ruby.css")
    RubyCss css();

    @Source("org/exoplatform/ide/editor/ruby/client/images/class.gif")
    ImageResource classItem();

    @Source("org/exoplatform/ide/editor/ruby/client/images/constant-item.png")
    ImageResource rubyConstant();

    @Source("org/exoplatform/ide/editor/ruby/client/images/blank.png")
    ImageResource blankImage();

    @Source("org/exoplatform/ide/editor/ruby/client/images/default-method.png")
    ImageResource defaultMethod();

    @Source("org/exoplatform/ide/editor/ruby/client/images/public-method.png")
    ImageResource publicMethod();

    @Source("org/exoplatform/ide/editor/ruby/client/images/local.png")
    ImageResource variable();

    @Source("org/exoplatform/ide/editor/ruby/client/images/class-variable-item.png")
    ImageResource rubyClassVariable();

    @Source("org/exoplatform/ide/editor/ruby/client/images/global-variable-item.png")
    ImageResource rubyGlobalVariable();

    @Source("org/exoplatform/ide/editor/ruby/client/images/instance-variable-item.png")
    ImageResource rubyObjectVariable();

    @Source("org/exoplatform/ide/editor/ruby/client/images/row-selected.png")
    ImageResource itemSelected();

    @Source("org/exoplatform/ide/editor/ruby/client/images/module-item.png")
    ImageResource module();

    @Source("org/exoplatform/ide/editor/ruby/client/images/ruby-file.png")
    ImageResource ruby();

    @Source("org/exoplatform/ide/editor/ruby/client/images/ruby-file-disabled.png")
    ImageResource rubyDisabled();

}
