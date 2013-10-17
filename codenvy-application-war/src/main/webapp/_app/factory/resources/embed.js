/* 
    CODENVY CONFIDENTIAL
    __________________

    [2012] - [2013] Codenvy, S.A.
    All Rights Reserved.

    NOTICE:  All information contained herein is, and remains
    the property of Codenvy S.A. and its suppliers,
    if any.  The intellectual and technical concepts contained
    herein are proprietary to Codenvy S.A.
    and its suppliers and may be covered by U.S. and Foreign Patents,
    patents in process, and are protected by trade secret or copyright law.
    Dissemination of this information or reproduction of this material
    is strictly forbidden unless prior written permission is obtained
    from Codenvy S.A..
*/

(function Embed() {
	
	// This script element
	var _script;
	
	// Parent of this script element. Uses as a parent for injecting IFrame.
	var _parent;
	
	// Factory ID.
	var _factory;
	
	/*
	 * Fetches this script element, it's parent, factory ID and builds URL for IFrame.
	 */
	function makeIFrameURL() {
		try {
			// Take this script element and it's parent
			var scripts = document.getElementsByTagName('script');
			_script = scripts[scripts.length - 1];
			_parent = _script.parentNode;

			// Validation of Factory ID.
			// Predicted position of '?' in script's URL must be bigger 30..35.
			// Also, at least 5 characters must be after '?'.
			if (_script.src.indexOf('?') < 30 || _script.src.indexOf('?') >= (_script.src.length - 6)) {
				console.log("Factory ID has been set incorrectly");
				return null;
			}
			
			// Take Factory ID.
			_factory = _script.src.substring(_script.src.indexOf('?') + 1);
			
			// Checking for Factory ID. It must be non empty.
			if (_factory == null || _factory == undefined || _factory.trim() == "") {
				console.log("Factory ID has been set incorrectly");
				return null;
			}
			
			// Make URL for IFrame.
			_frameURL = _script.src;
			_frameURL = _frameURL.substring(0, _frameURL.lastIndexOf("/")) + "/embed.html?factory=" + _factory;
			return _frameURL;
		} catch (e) {
			console.log(e.message);
		}
		
		return null;
	}
	
	/*
	 * Add IFrame to page.
	 */
	function addIFrame(url) {
		var _iframe = document.createElement("iframe");
		_iframe.id = "factory-" + _factory;
		_iframe.src = url;
		
		// Dimensions. 0px * 0px by default
		_iframe.style.width = "0px";
		_iframe.style.height = "0px";

		// Style attributes
		_iframe.style.background = "transparent";
		_iframe.style.border = "0px none transparent";
		_iframe.style.padding = "0px";
		_iframe.style.overflow = "hidden";
		
		// Properties
		_iframe.scrolling = "no";
		_iframe.frameborder = "0";
		_iframe.allowtransparency = "true"		

	    _parent.appendChild(_iframe);
	}
	
	/*
	 * Adds handler to window object to handle "message" events.
	 */
	function handleWindowMessages() {
		window.addEventListener("message", function(event) {
			try {
				// Get message.
				var message = event.data;
				
				// It must be the request for resize Factory button.
				if (message.indexOf("resize-factory-button:") != 0) {
					return;				
				}
				
				// Parse message and resize Factory Button.
				var parts = message.split(':');			
				var iframe = document.getElementById("factory-" + parts[1]);
				iframe.style.width = "" + parts[2] + "px";
				iframe.style.height = "" + parts[3] + "px";
			} catch (e) {
				console.log(e.message);
			}			
		}, false);
	}
	
	// Takes IFrame's URL
	var iframeURL = makeIFrameURL();
	
	// Remove current script element from Document
	_parent.removeChild(_script);

	// If IFrame URL is Ok, register handler and add IFrame.
	if (!(iframeURL == null || iframeURL == undefined)) {
		handleWindowMessages();
		addIFrame(iframeURL);
	}
})();
