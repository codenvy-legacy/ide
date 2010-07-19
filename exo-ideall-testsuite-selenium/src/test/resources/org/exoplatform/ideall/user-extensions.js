/*
 * Isomorphic SmartClient
 * Version 8.0
 * Copyright(c) 1998 and beyond Isomorphic Software, Inc. All rights reserved.
 * "SmartClient" is a trademark of Isomorphic Software, Inc.
 *
 * licensing@smartclient.com
 *
 * http://smartclient.com/license
 */

PageBot.prototype.getAutWindow = function() {
    var autWindow = this.browserbot.getUserWindow();
    // if the user window is the dev console, redirect to the actual app window
    if (autWindow.targetWindow != null) autWindow = autWindow.targetWindow;

    if (autWindow.isc.AutoTest === undefined) {
        //this should never be the case with newer SC versions as AutoTest is part of core
        autWindow.isc.loadAutoTest();
    } else if (autWindow.isc.Canvas.getCanvasLocatorFallbackPath === undefined) {
        autWindow.isc.ApplyAutoTestMethods();
    }
    return autWindow;
};

Selenium.prototype.getAutWindow = PageBot.prototype.getAutWindow;

// All locateElementBy* methods are added as locator-strategies.
PageBot.prototype.locateElementByScID = function(idLocator, inDocument, inWindow) {
    LOG.debug("Locate Element with SC ID=" + idLocator + ", inDocument=" + inDocument + ", inWindow=" + inWindow.location.href);
    var autWindow = this.getAutWindow();

    idLocator = idLocator.replace(/'/g, "");
    idLocator = idLocator.replace(/"/g, "");

    var scObj = autWindow[idLocator];
    if(scObj == null || scObj === undefined) {
        LOG.info("Unable to locate SC element with ID " + idLocator);
        return null;
    } else {
        LOG.debug('Found SC object ' + scObj);
    }

    var scLocator = "//" + scObj.getClassName() + "[ID=\"" + idLocator + "\"]";
    LOG.debug("Using SC Locator " + scLocator);
    var elem = autWindow.isc.AutoTest.getElement(scLocator);
    LOG.info("Returning element :: " + elem + " for SC locator " + scLocator);
    return elem;
};

PageBot.prototype.locateElementByScLocator = function(scLocator, inDocument, inWindow) {
    LOG.debug("Locate Element with SC Locator=" + scLocator + ", inDocument=" + inDocument + ", inWindow=" + inWindow.location.href);

    //support scLocators with the direct ID of the widget specified
    if(scLocator.indexOf("/") == -1) {
        LOG.debug("Using ID locator");
        return this.locateElementByScID(scLocator, inDocument, inWindow);
    }
    var autWindow = this.getAutWindow();
    var elem = autWindow.isc.AutoTest.getElement(scLocator);
    LOG.debug("Returning element :: " + elem + " for SC locator " + scLocator);
    return elem;
};

Selenium.prototype.orig_doType = Selenium.prototype.doType;

Selenium.prototype.doType = function(locator, value) {
    /**
   * Sets the value of an input field, as though you typed it in.
   *
   * <p>Can also be used to set the value of combo boxes, check boxes, etc. In these cases,
   * value should be the value of the option selected, not the visible text.</p>
   *
   * @param locator an <a href="#locators">element locator</a>
   * @param value the value to type
   */

   Selenium.prototype.orig_doType.call(this, locator, value);

    //Selenium doesn't actually simulate a user typing into an input box so for SmartClient FormItem's manually register the change.
    if(this.hasSC()) {
        var autWindow = this.getAutWindow();
        var formItem = autWindow.isc.AutoTest.getLocatorFormItem(locator);
        if(formItem != null) {
            formItem.updateValue();
        }
    }
};


Selenium.prototype.orig_doClick = Selenium.prototype.doClick;

Selenium.prototype.doClick = function(locator, eventParams)
{
    LOG.info("Located in doScClick : " + locator);
    var element = this.page().findElement(locator);

    if(this.hasSC()) {

        var autWindow = this.getAutWindow();
        var canvas = autWindow.isc.AutoTest.locateCanvasFromDOMElement(element);
        //if the clicked element does not correspond to a SmartClient widget, then perform the default SmartClient click operation
        if(canvas == null) {
            Selenium.prototype.orig_doClick.call(this, locator, eventParams);
            return;
        }
        LOG.debug("Located canvas " + canvas + " for locator " + locator);

        var rect = autWindow.isc.Element.getElementRect(element);
        var clientX = rect[0];
        var clientY = rect[1];

        LOG.debug("clientX = " + clientX + ", clientY=" + clientY);

        //fire a sequence of mousedown, mouseup and click operation to trigger a SmartClient click event
        this.browserbot.triggerMouseEvent(element, "mousedown", true, clientX, clientY);
        this.browserbot.triggerMouseEvent(element, "mouseup", true, clientX, clientY);
        this.browserbot.clickElement(element);
    } else {
        Selenium.prototype.orig_doClick.call(this, locator, eventParams);
    }
};

Selenium.prototype.orig_doDoubleClick = Selenium.prototype.doDoubleClick;

Selenium.prototype.doDoubleClick = function(locator, eventParams)
{
    LOG.info("Locator in doDoubleClick : " + locator);
    var element = this.page().findElement(locator);
    
    if(this.hasSC()) {
        var autWindow = this.getAutWindow();
        var canvas = autWindow.isc.AutoTest.locateCanvasFromDOMElement(element);
        //if the clicked element does not correspond to a SmartClient widget, then perform the default SmartClient doubleclick operation
        if(canvas == null) {
            Selenium.prototype.orig_doDoubleClick.call(this, locator, eventParams);
            return;
        }
        LOG.debug("Located canvas " + canvas + " for locator " + locator);

        var rect = autWindow.isc.Element.getElementRect(element);
        var clientX = rect[0];
        var clientY = rect[1];

        LOG.debug("clientX = " + clientX + ", clientY=" + clientY);

        //fire a sequence of events to trigger a SmartClient doubleclick event
        this.browserbot.triggerMouseEvent(element, "mouseover", true, clientX, clientY);
        this.browserbot.triggerMouseEvent(element, "mousedown", true, clientX, clientY);
        this.browserbot.triggerMouseEvent(element, "mouseup", true, clientX, clientY);
        this.browserbot.clickElement(element);
        this.browserbot.triggerMouseEvent(element, "mousedown", true, clientX, clientY);
        this.browserbot.triggerMouseEvent(element, "mouseup", true, clientX, clientY);
        this.browserbot.clickElement(element);

    } else {
        Selenium.prototype.orig_doDoubleClick.call(this, locator, eventParams);
    }
};

Selenium.prototype.orig_doContextMenu = Selenium.prototype.doContextMenu;

Selenium.prototype.doContextMenu = function(locator, eventParams)
{
    LOG.info("Locator in doContextMenu : " + locator);
    var element = this.page().findElement(locator);
    if(this.hasSC()) {
        var autWindow = this.getAutWindow();
        var canvas = autWindow.isc.AutoTest.locateCanvasFromDOMElement(element);
        if(canvas == null) {
            Selenium.prototype.orig_doContextMenu.call(this, locator, eventParams);
            return;
        }
        LOG.debug("Located canvas " + canvas + " for locator " + locator);

        var rect = autWindow.isc.Element.getElementRect(element);
        var clientX = rect[0];
        var clientY = rect[1];

        LOG.debug("clientX = " + clientX + ", clientY=" + clientY);
        this.browserbot.triggerMouseEvent(element, "contextmenu", true, clientX, clientY);
    } else {
        Selenium.prototype.orig_doContextMenu.call(this, locator, eventParams);
    }
};


Selenium.prototype.hasSC = function() {
    var autWindow = this.browserbot.getUserWindow();
    if (autWindow.targetWindow != null) autWindow = autWindow.targetWindow;
    return !(autWindow.isc === undefined);
};


Selenium.prototype.orig_getTable = Selenium.prototype.getTable;

Selenium.prototype.getTable = function(tableCellAddress) {
/**
 * Gets the text from a cell of a table. The cellAddress syntax
 * tableLocator.row.column, where row and column start at 0.
 *
 * @param tableCellAddress a cell address, e.g. "foo.1.4"
 * @return string the text from the specified cell
 */

    if(this.hasSC()) {
        // This regular expression matches "tableName.row.column"
        // For example, "mytable.3.4"
        var pattern = /(.*)\.(\d+)\.(\d+)/;

        if(!pattern.test(tableCellAddress)) {
            throw new SeleniumError("Invalid target format. Correct format is tableLocator.rowNum.columnNum");
        }

        var pieces = tableCellAddress.match(pattern);

        var tableName = pieces[1];
        var row = pieces[2];
        var col = pieces[3];

        var element = this.browserbot.findElement(tableName);

        var autWindow = this.getAutWindow();

        var listGrid = autWindow.isc.AutoTest.locateCanvasFromDOMElement(element);
        if(listGrid == null) {
            return Selenium.prototype.orig_getTable.call(this, tableCellAddress);
        }
        //the locator can return a GridBody
        if(listGrid.grid) {
            listGrid = listGrid.grid;
        }
        
        LOG.debug("Found ListGrid " + listGrid.getClassName());
        
        var record = listGrid.getRecord(Number(row));
        LOG.debug("Record for row " + row + " is " + record);
        return listGrid.getCellValue(record, row, col);
    } else {
        Selenium.prototype.orig_getTable.call(this, tableCellAddress);
    }    
};

Selenium.prototype.orig_doMouseOver = Selenium.prototype.doMouseOver;

Selenium.prototype.doMouseOver = function(locator) {
    /**
   * Simulates a user hovering a mouse over the specified element.
   *
   * @param locator an <a href="#locators">element locator</a>
   */

    LOG.info("Locator in doMouseOver : " + locator);
    var element = this.page().findElement(locator);
    if(this.hasSC()) {
        var autWindow = this.getAutWindow();
        var canvas = autWindow.isc.AutoTest.locateCanvasFromDOMElement(element);
        if(canvas == null) {
            Selenium.prototype.orig_doMouseOver.call(this, locator);
            return;
        }
        LOG.debug("Located canvas " + canvas + " for locator " + locator);

        var rect = autWindow.isc.Element.getElementRect(element);
        var clientX = rect[0];
        var clientY = rect[1];

        LOG.debug("clientX = " + clientX + ", clientY=" + clientY);
        this.browserbot.triggerMouseEvent(element, "mouseover", true, clientX, clientY);
    } else {
        Selenium.prototype.orig_doMouseOver.call(this, locator);
    }

};


