/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.ui;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface ButtonResources extends ClientBundle {
    public interface Css extends CssResource{

        @ClassName("btn-link")
        String btnLink();

        @ClassName("btn-info")
        String btnInfo();

        @ClassName("btn-inverse")
        String btnInverse();

        @ClassName("btn-small")
        String btnSmall();

        @ClassName("btn-warning")
        String btnWarning();

        @ClassName("btn-success")
        String btnSuccess();

        @ClassName("btn-mini")
        String btnMini();

        @ClassName("btn-primary")
        String btnPrimary();

        String btn();

        @ClassName("btn-danger")
        String btnDanger();

        @ClassName("hide-text")
        String hideText();

        String active();

        @ClassName("btn-large")
        String btnLarge();

        String clearfix();

        @ClassName("btn-block")
        String btnBlock();

        String disabled();

        @ClassName("input-block-level")
        String inputBlockLevel();
    }

    @Source("Button.css")
    Css buttonCss();
}
