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

function findScLocator(element) {
    //the element selenium passes is a "safe" XPCNativeWrappers wrapper of the real element. XPCNativeWrappers are used to protect
    //the chrome code working with content objects and there's no way to access the real "underlying" element object.
    //example of an element passed here is [object XPCNativeWrapper [object HTMLInputElement]]

    //see https://developer.mozilla.org/en/wrappedJSObject
    //https://developer.mozilla.org/en/XPCNativeWrapper

    var autWindow = this.window;
    if (autWindow.wrappedJSObject) {
        autWindow = autWindow.wrappedJSObject;
    }

    if(hasSC(autWindow)) {
        var e;
        try {
            var id = element.id;
            if (id == null || id === undefined || id == '') {
                //assign an id to the element if one does not exist so that it can be located by SC
                id = "sel_" + autWindow.isc.ClassFactory.getNextGlobalID();
                element.id = id;
            }

            //The sc classes are loaded in wrappedJSObject window, and not the window reference held by Locators.
            //see https://developer.mozilla.org/en/wrappedJSObject
            e = autWindow.document.getElementById(id);

            var scLocator = autWindow.isc.AutoTest.getLocator(e);

            if(scLocator != null) {
                return "scLocator=" + scLocator;
            } else {
                return null;
            }
        } catch(ex) {
            alert('caught error ' + ex + ' for element ' + e + ' with id' + e.id);
            return null;
        }
    } else {
        return null;
    }
}

LocatorBuilders.add('sc', findScLocator);
// add SC Locator to the head of the priority of builders.
LocatorBuilders.order = ['sc', 'id', 'link', 'name', 'dom:name', 'xpath:link', 'xpath:img', 'xpath:attributes', 'xpath:href', 'dom:index', 'xpath:position'];

//override the default clickLocator so that duplicate click events are not recorded
Recorder.removeEventHandler('clickLocator');
Recorder.addEventHandler('clickLocator', 'click', function(event) {
        if (event.button == 0) {

        // === start sc specific code ===
        var autWindow = this.window;
        if (autWindow.wrappedJSObject) {
            autWindow = autWindow.wrappedJSObject;
        }
        if(hasSC(autWindow)) {
            var element = this.clickedElement;
            var id = element.id;
            if (id == null || id === undefined || id == '') {
                id = "sel_" + autWindow.isc.ClassFactory.getNextGlobalID();
                element.id = id;
            }
            var e = autWindow.document.getElementById(id);
            var scLocator = autWindow.isc.AutoTest.getLocator(e);

            //if an scLocator is found, then this event will be captured by the scClickLocator mousedown event recorder
            // 'return' so that we don't get duplicate records
            if(scLocator != null) {
                return;
            }
        }
        // === end sc specific code ===
            
        var clickable = this.findClickableElement(event.target);
        if (clickable) {
            // prepend any required mouseovers. These are defined as
            // handlers that set the "mouseoverLocator" attribute of the
            // interacted element to the locator that is to be used for the
            // mouseover command. For example:
            //
            // Recorder.addEventHandler('mouseoverLocator', 'mouseover', function(event) {
            //     var target = event.target;
            //     if (target.id == 'mmlink0') {
            //         this.mouseoverLocator = 'img' + target._itemRef;
            //     }
            //     else if (target.id.match(/^mmlink\d+$/)) {
            //         this.mouseoverLocator = 'lnk' + target._itemRef;
            //     }
            // }, { alwaysRecord: true, capture: true });
            //
            if (this.mouseoverLocator) {
                this.record('mouseOver', this.mouseoverLocator, '');
                delete this.mouseoverLocator;
            }
            this.record("click", this.findLocators(event.target), '');
        } else {
            var target = event.target;
            this.callIfMeaningfulEvent(function() {
                    this.record("click", this.findLocators(target), '');
                });
        }
    }
	}, { capture: true });


Recorder.addEventHandler('scClickLocator', 'mousedown', function(event) {
    if (event.button == 0) {
        var autWindow = this.window;
        if (autWindow.wrappedJSObject) {
            autWindow = autWindow.wrappedJSObject;
        }
        if(hasSC(autWindow)) {
            var element = this.clickedElement;
            var id = element.id;
            if (id == null || id === undefined || id == '') {
                id = "sel_" + autWindow.isc.ClassFactory.getNextGlobalID();
                element.id = id;
            }
            var e = autWindow.document.getElementById(id);
            var scLocator = autWindow.isc.AutoTest.getLocator(e);
            
            if(scLocator != null) {
                this.record("click", 'scLocator=' + scLocator, '');
                delete this.click;
            }
        }
    }
}, { capture: true });

Recorder.addEventHandler('scContextMenuLocator', 'mousedown', function(event) {
    if (event.button == 2) {
        var autWindow = this.window;
        if (autWindow.wrappedJSObject) {
            autWindow = autWindow.wrappedJSObject;
        }
        if(hasSC(autWindow)) {
            var element = this.clickedElement;
            var id = element.id;
            if (id == null || id === undefined || id == '') {
                id = "sel_" + autWindow.isc.ClassFactory.getNextGlobalID();
                element.id = id;
            }

            var e = autWindow.document.getElementById(id);
            var scLocator = autWindow.isc.AutoTest.getLocator(e);

            if(scLocator != null) {
                this.record("contextMenu", 'scLocator=' + scLocator, '');
                delete this.click;
            }
        }
    }
}, { capture: true });

CommandBuilders.add('action', function(window) {
    var autWindow = window;
    if (autWindow.wrappedJSObject) {
        autWindow = autWindow.wrappedJSObject;
    }

    if(hasSC(autWindow)) {
        var element = this.getRecorder(window).clickedElement;
        var id = element.id;
        if (id == null || id === undefined || id == '') {
            id = "sel_" + autWindow.isc.ClassFactory.getNextGlobalID();
            element.id = id;
        }

        var e = autWindow.document.getElementById(id);
        var scLocator = autWindow.isc.AutoTest.getLocator(e);

        if(scLocator != null) {
            return {
                command: "click",
                target: "scLocator=" + scLocator
            };
        } else {
            return {
                command: "click",
                disabled : true
            };
        }
    } else {
        return {
                command: "click",
                disabled : true
            };
    }
});


CommandBuilders.add('accessor', function(window) {
    var autWindow = window;
    if (autWindow.wrappedJSObject) {
        autWindow = autWindow.wrappedJSObject;
    }
    var result = { accessor: "table", disabled: true };
    if(hasSC(autWindow)) {
        var element = this.getRecorder(window).clickedElement;

        if (!element) return result;
        var id = element.id;
        if (id == null || id === undefined || id == '') {
            id = "sel_" + autWindow.isc.ClassFactory.getNextGlobalID();
            element.id = id;
        }

        var e = autWindow.document.getElementById(id);
        var listGrid = autWindow.isc.AutoTest.locateCanvasFromDOMElement(e);

        if(listGrid == null || !listGrid.isA("GridRenderer")) return result;

        var cellXY = listGrid.getCellFromDomElement(e);
        if(cellXY == null) return result;
        var row = cellXY[0];
        var col = cellXY[1];
        //the locator can return a GridBody
        if(listGrid.grid) {
            listGrid = listGrid.grid;
        }

        var record = listGrid.getRecord(Number(row));

        var value = listGrid.getCellValue(record, row, col);

        result.target = 'scLocator=' + listGrid.getLocator() + '.' + row + '.' + col;
        result.value = value;
        result.disabled = false;
        return result;
    }

    return result;
});

function hasSC(autWindow) {
    var hasSC = !(autWindow.isc === undefined);
    if(hasSC && autWindow.isc.AutoTest === undefined) {
        //this should never be the case with newer SC versions as AutoTest is part of core
        autWindow.isc.loadAutoTest();
    }
    if(hasSC && autWindow.isc.Canvas.getCanvasLocatorFallbackPath === undefined) {
        autWindow.isc.ApplyAutoTestMethods();
    }
    return hasSC;
}
