/**
 * Copyright (C) 2010 eXo Platform SAS.
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
 *
 */

package org.exoplatform.gwtframework.ui.client.component;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.util.ImageFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ImageButton extends Composite implements HasClickHandlers, HasText, MouseOverHandler, MouseOutHandler,
                                                      MouseDownHandler, MouseUpHandler, ClickHandler {

    private static ImageButtonUiBinder uiBinder = GWT.create(ImageButtonUiBinder.class);

    interface ImageButtonUiBinder extends UiBinder<Widget, ImageButton> {
    }

    interface Style extends CssResource {

//        String HIDDEN = "imageButtonHidden";
//
//        String OVER = "imageButtonOver";
//
//        String DOWN = "imageButtonDown";
//
//        String DISABLED = "imageButtonDisabled";

        String imageButtonTitleTable();

        String imageButtonDown();

        String imageButtonHidden();

        String imageButtonRight();

        String imageButtonLeft();

        String imageButtonDisabled();

        String imageButtonPanel();

        String imageButtonOver();

        String imageButtonText();

        String imageButtonStretch();

        String imageButtonTable();

        String imageButtonIconPanel();
    }

    interface Resources extends ClientBundle {
        @Source("org/exoplatform/gwtframework/ui/client/component/image-button/image-button-22.css")
        Style css();

        @Source("org/exoplatform/gwtframework/ui/client/component/image-button/image-button-22-left.png")
        ImageResource left();

        @Source("org/exoplatform/gwtframework/ui/client/component/image-button/image-button-22-right.png")
        ImageResource right();

        @Source("org/exoplatform/gwtframework/ui/client/component/image-button/image-button-22-stretch.png")
        @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.Horizontal)
        ImageResource stretch();
    }

    private static final Resources RESOURCES = GWT.create(Resources.class);

    static {
        RESOURCES.css().ensureInjected();
    }

    @UiField(provided = true)
    Resources    res;
    @UiField
    TableElement table;

    @UiField
    TableCellElement imageElement;

    @UiField
    TableCellElement delimiterElement;

    @UiField
    TableCellElement textElement;

    @UiField
    SimplePanel imagePanel;

    private String id;

    private String text;

    private Image image;

    private Image disabledImage;

    private boolean enabled = true;

    private List<ClickHandler> clickHandlers = new ArrayList<ClickHandler>();

    public ImageButton() {
        this(null, null, null);
    }

    public ImageButton(String text) {
        this(text, null, null);
    }

    public ImageButton(String text, String imageName) {
        this(text, ImageFactory.getImage(imageName), ImageFactory.getDisabledImage(imageName));
    }

    public ImageButton(String text, Image image) {
        this(text, image, null);
    }

    public ImageButton(String text, Image image, Image disabledImage) {
        res = RESOURCES;
        this.image = image;
        this.disabledImage = disabledImage;
        this.text = text;

        initWidget(uiBinder.createAndBindUi(this));
        getElement().setAttribute("button-enabled", enabled + "");

        addDomHandler(this, MouseOverEvent.getType());
        addDomHandler(this, MouseOutEvent.getType());
        addDomHandler(this, MouseDownEvent.getType());
        addDomHandler(this, MouseUpEvent.getType());
        addDomHandler(this, ClickEvent.getType());

        render();
    }

    private void showElement(Element element) {
        element.removeClassName(RESOURCES.css().imageButtonHidden());
    }

    private void hideElement(Element element) {
        element.addClassName(RESOURCES.css().imageButtonHidden());
    }

    public void setText(String text) {
        this.text = text;
        render();
    }

    public void setImage(Image image) {
        this.image = image;
        render();
    }

    public void setDisabledImage(Image disabledImage) {
        this.disabledImage = disabledImage;
        render();
    }

    /**
     * @param image
     * @param disabledImage
     */
    public void setImages(Image image, Image disabledImage) {
        setImage(image);
        setDisabledImage(disabledImage);
    }

    /**
     * Set button image resource.<br>
     * (uses for UiBinder)
     *
     * @param image
     */
    public void setImageResource(ImageResource image) {
        setImage(new Image(image));
    }

    /**
     * Set disabled image resource.<br>
     * (uses for UiBinder)
     *
     * @param disabledImage
     */
    public void setDisabledImageResource(ImageResource disabledImage) {
        setDisabledImage(new Image(disabledImage));
    }

    private void render() {
        Image img = null;
        if (enabled) {
            img = image;
        } else {
            img = disabledImage != null ? disabledImage : image;
        }

        imagePanel.clear();
        if (img != null) {
            showElement(imageElement);
            imagePanel.add(img);
        } else {
            hideElement(imageElement);
        }

        if (text != null && !text.isEmpty()) {
            showElement(textElement);
            textElement.setInnerHTML(text);
        } else {
            hideElement(textElement);
            textElement.setInnerHTML(null);
        }

        if (img != null && text != null && !text.isEmpty()) {
            showElement(delimiterElement);
        } else {
            hideElement(delimiterElement);
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

        table.removeClassName(RESOURCES.css().imageButtonDown());
        table.removeClassName(RESOURCES.css().imageButtonOver());

        if (enabled) {
            table.removeClassName(RESOURCES.css().imageButtonDisabled());
        } else {
            table.addClassName(RESOURCES.css().imageButtonDisabled());
        }

        getElement().setAttribute("button-enabled", enabled + "");

        render();
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void onMouseOver(MouseOverEvent event) {
        table.removeClassName(RESOURCES.css().imageButtonDown());
        table.addClassName(RESOURCES.css().imageButtonOver());
    }

    @Override
    public void onMouseOut(MouseOutEvent event) {
        table.removeClassName(RESOURCES.css().imageButtonOver());
    }

    @Override
    public void onMouseDown(MouseDownEvent event) {
        table.removeClassName(RESOURCES.css().imageButtonOver());
        table.addClassName(RESOURCES.css().imageButtonDown());
    }

    @Override
    public void onMouseUp(MouseUpEvent event) {
        table.removeClassName(RESOURCES.css().imageButtonDown());
        table.addClassName(RESOURCES.css().imageButtonOver());
    }

    @Override
    public String getText() {
        return text;
    }

    public void setImageName(String imageName) {
        if (imageName == null || imageName.isEmpty()) {
            image = null;
            disabledImage = null;
        } else {
            image = ImageFactory.getImage(imageName);
            disabledImage = ImageFactory.getDisabledImage(imageName);
        }

        render();
    }

    /**
     * Get button's ID
     *
     * @return button's ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets new ID for this button
     *
     * @param id
     *         new ID
     */
    public void setId(String id) {
        this.id = id;
        getElement().setId(id);
    }

    /**
     * Sets new ID for this button ( like setId )
     *
     * @param id
     *         new ID
     */
    public void setButtonId(String id) {
        setId(id);
    }

    @Override
    public void onClick(ClickEvent event) {
        if (!enabled) {
            return;
        }

        List<ClickHandler> oneCycleClickHandlers = new ArrayList<ClickHandler>(clickHandlers);
        for (ClickHandler handler : oneCycleClickHandlers) {
            handler.onClick(event);
        }
    }

    /** @see com.google.gwt.event.dom.client.HasClickHandlers#addClickHandler(com.google.gwt.event.dom.client.ClickHandler) */
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        clickHandlers.add(handler);
        return new ClickHandlerRegistration(handler);
    }

    private class ClickHandlerRegistration implements HandlerRegistration {

        private ClickHandler clickHandler;

        public ClickHandlerRegistration(ClickHandler clickHandler) {
            this.clickHandler = clickHandler;
        }

        @Override
        public void removeHandler() {
            clickHandlers.remove(clickHandler);
        }

    }

    /** @see com.google.gwt.user.client.ui.UIObject#setTitle(java.lang.String) */
    @Override
    public void setTitle(String title) {
        super.setTitle(title);
    }

}
