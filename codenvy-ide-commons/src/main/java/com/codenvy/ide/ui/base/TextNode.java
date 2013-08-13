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

package com.codenvy.ide.ui.base;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Text;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

public class TextNode extends Widget implements HasText {

    private Text    baseNode;
    private boolean attached;

    public TextNode() {
    }

    public TextNode(String text) {
        setText(text);
    }


    @Override
    public String getText() {

        return baseNode != null ? baseNode.getData() : null;
    }


    @Override
    public void setText(String text) {
        assert baseNode == null : "TextNode can be set once";
        baseNode = Document.get().createTextNode(text);
        setElement(baseNode.<Element>cast());
    }
    
    @Override
    public boolean isAttached() {
        return attached;
    }
    
    @Override
    protected void onAttach() {
        
        if(isAttached()) {
            throw new IllegalStateException("already added");
        }
        
        this.attached = true;
        
        onLoad();
        
        AttachEvent.fire(this, attached);
    }
    
    @Override
    protected void onDetach() {
        
        if(!isAttached()) {
            throw new IllegalStateException("is not attached");
        }
        
        this.attached = false;
        
        AttachEvent.fire(this, attached);
    }
    
    
}
