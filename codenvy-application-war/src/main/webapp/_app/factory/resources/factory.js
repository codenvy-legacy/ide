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

/*

The main rule is given below to understand how to use this script for embedding Code button on your page. (prefered)
	
<script type="text/javascript" 

	src="factory.js"
			// URL to this script

	factory="http://url.to/your/factory"
			// Specify URL to your factory:
			//		factory="http://url.to/your/factory" will lead to open 
			//		"http://url.to/your/factory" in new window after clicking the button;
			//		if factory attribute is not set, clicking the button will be ignored.

	style="white"
			// Specify style of Code button:
			// 		attribute not set or style="dark" shows dark button;
			//		style="white" shows white button;
			//		style="advanced" shows advanced button with user defined Logo.

	counter="vertical"
			// Appearance and orientation of Counter to see how many projects were created by your factory:
			//		not set or counter="none" shows button without counter;
			//		counter="horizontal" shows counter on the right near the button;
			//		counter="vertical" shows counter above the button.

></script>

Examples:

- default button

		<script type="text/javascript" src="factory.js" factory="http://codenvy.com/api/factory/factoryixak9964p942mikq"></script>

- white button
		
		<script type="text/javascript" src="factory.js" style="white"></script>

- default button with vertical counter
		
		<script type="text/javascript" src="factory.js" counter="vertical"></script>

- white button with horizontal counter
		
		<script type="text/javascript" src="factory.js" style="white" counter="horizontal"></script>

- advanced button with User defined Logo

		<script type="text/javascript" src="factory.js" style="advanced"></script>

*/

function Factory() {
	
	// This script element in Document
	var _script = null;

	var _parent = null;
	
	// URL to CSS file
	var _cssURL = null;
	
	// Factory button type.
	// Allowable values: "simple-dark", "simple-white" or "advanced".
	//
	// 		After refactoring: remove counter attribute. 
	//		Use only one type attribute: 
	//			"dark", "dark-counter-right", "dark-counter-top", "white", "white-counter-right", "white-counter-top", "advanced".
	var _type = true;
	
    this.getType = function() {
        return _type;
    }
	
	// Indicates visibility and orientation of the Counter.
	// Allowable values: null, "horizontal", "vertical".
	// Null means counter is not visible.
	var _counter = null;
	
	// Deprecated. This variable and "target" attribute must be deleted when Factory 1.1 was fully used.
	var _target = null;
	
	// Deprecated. This variable and "img" attribute must be deleted when Factory 1.1 was fully used.
	var _logoURL = null;
	
	var _logoImage = null;
	
	this.getLogoImage = function() {
		return _logoImage;
	}
	
	function fetchButtonOptions() {
		// Fetch this script HTML element
		var scripts = document.getElementsByTagName('script');
		_script = scripts[scripts.length - 1];
		_parent = _script.parentNode;
		
		// Fetch URL to factory-1.1.css file
		_cssURL = _script.src;
		_cssURL = _cssURL.substring(0, _cssURL.lastIndexOf("/")) + "/factory.css";
		
		// Fetch button type
		if ("advanced" == _script.getAttribute("style")) {
			_type = "advanced";
		} else if ("white" == _script.getAttribute("style")) {
			_type = "simple-white";
		} else {
			_type = "simple-dark";
		}
		
		// Fetch counter appearance and orientation
		if ("vertical" == _script.getAttribute("counter")) {
			_counter = "vertical";
		} else if ("horizontal" == _script.getAttribute("counter")) {
			_counter = "horizontal";
		}
		
		// Deprecated. Must be deleted when Factory 1.1 was fully used.
		if (_script.hasAttribute("target")) {
			_target = _script.getAttribute("target");
		}
		
		// Deprecated. Must be deleted when Factory 1.1 was fully used.
		if (_script.hasAttribute("img")) {
			_logoURL = _script.getAttribute("img");
		}		
	}
	
	// Adds a link to factory.css styles as a child of HEAD element 
	function injectStyles() {
		var head = document.getElementsByTagName('head')[0];
		var links = head.getElementsByTagName('link');
		
		for (i = 0; i < links.length; i++) {
			if (_cssURL == links[i].href) {
				return;
			}
		}	

		// Add a link to factory-1.1.css to HEAD element
		var link = document.createElement('link');
		link.rel = "stylesheet";
		link.type = "text/css";
		link.href = _cssURL;
		head.appendChild(link);
	}	
	
	function factoryButtonClickHandler() {
		if (_target == null || "" == _target) {
			return;
		}
		
		window.open(_target, "_blank");
	}	
	
	function embedDark() {
		/*
			<div class="codenow-dark" onclick="window.open('http://codenvy.com', '_blank');"></div>
		*/
		
		var _embed = document.createElement("div");
	    _embed.classList.add("codenow-dark");
	    _embed.onclick = factoryButtonClickHandler;
	    _parent.appendChild(_embed);
	}
	
	function embedDarkCounterHorizontal() {
		/*
			<div class="codenow-horizontal">
				<div class="codenow-dark codenow-bottom" onclick="window.open('http://codenvy.com', '_blank');"></div>
				<div class="codenow-counter-horizontal">&nbsp;33</div>
			</div>
		*/
		
		var _embed = document.createElement("div");
	    _embed.classList.add("codenow-horizontal");
	    _parent.appendChild(_embed);

	    var _button = document.createElement("div");
	    _button.classList.add("codenow-dark");
	    _button.classList.add("codenow-bottom");
	    _button.onclick = factoryButtonClickHandler;
	    _embed.appendChild(_button);

	    var _counter = document.createElement("div");
	    _counter.classList.add("codenow-counter-horizontal");
	    _counter.innerHTML = "&nbsp;33";
	    _embed.appendChild(_counter);		
	}

	function embedDarkCounterVertical() {
		/*
			<div class="codenow-vertical">
				<div class="codenow-counter-vertical">33</div>
				<div class="codenow-dark codenow-bottom" onclick="window.open('http://codenvy.com', '_blank');"></div>
			</div>		
		*/

		var _embed = document.createElement("div");
	    _embed.classList.add("codenow-vertical");
	    _parent.appendChild(_embed);

	    var _counter = document.createElement("div");
	    _counter.classList.add("codenow-counter-vertical");
	    _counter.innerHTML = "33";
	    _embed.appendChild(_counter);		
	    
	    var _button = document.createElement("div");
	    _button.classList.add("codenow-dark");
	    _button.classList.add("codenow-bottom");
	    _button.onclick = factoryButtonClickHandler;
	    _embed.appendChild(_button);
	}
	
	function embedWhite() {
		/*
			<div class="codenow-white" onclick="window.open('http://codenvy.com', '_blank');"></div>
		*/

		var _embed = document.createElement("div");
	    _embed.classList.add("codenow-white");
	    _embed.onclick = factoryButtonClickHandler;
	    _parent.appendChild(_embed);
	}
	
	function embedWhiteCounterHorizontal() {
		/*
			<div class="codenow-horizontal">
				<div class="codenow-white codenow-bottom" onclick="window.open('http://codenvy.com', '_blank');"></div>
				<div class="codenow-counter-horizontal">&nbsp;33</div>
			</div>	
		*/

		var _embed = document.createElement("div");
	    _embed.classList.add("codenow-horizontal");
	    _parent.appendChild(_embed);
		
	    var _button = document.createElement("div");
	    _button.classList.add("codenow-white");
	    _button.classList.add("codenow-bottom");
	    _button.onclick = factoryButtonClickHandler;
	    _embed.appendChild(_button);
	    
	    var _counter = document.createElement("div");
	    _counter.classList.add("codenow-counter-horizontal");
	    _counter.innerHTML = "&nbsp;33";
	    _embed.appendChild(_counter);
	}

	function embedWhiteCounterVertical() {
		/*
			<div class="codenow-vertical">
				<div class="codenow-counter-vertical">33</div>
				<div class="codenow-white codenow-bottom" onclick="window.open('http://codenvy.com', '_blank');"></div>
			</div>
		*/
		
		var _embed = document.createElement("div");
	    _embed.classList.add("codenow-vertical");
	    _parent.appendChild(_embed);
	    
	    var _counter = document.createElement("div");
	    _counter.classList.add("codenow-counter-vertical");
	    _counter.innerHTML = "33";
	    _embed.appendChild(_counter);
	    
	    var _button = document.createElement("div");
	    _button.classList.add("codenow-white");
	    _button.classList.add("codenow-bottom");
	    _button.onclick = factoryButtonClickHandler;
	    _embed.appendChild(_button);		
	}

	function logoLoadComplete() {
		_logoImage.style.opacity = 1;
	}
	
	function logoLoadError() {
		_logoImage.style.opacity = 1;
		_logoImage.classList.add("advanced-factory-no-logo-available");
	}
		
	function embedAdvanced() {
		/*
			<div class="advanced-factory" style="position:absolute; left: 50px; top:420px;">
				<img alt="" src="..." />
				<div></div>
			</div>
		*/

		var _embed = document.createElement("div");
	    _embed.classList.add("advanced-factory");
	    _parent.appendChild(_embed);
	    
	    _logoImage = document.createElement("img");
	    _logoImage.src = _logoURL;
	    _logoImage.onload = logoLoadComplete;
	    _logoImage.onabort = logoLoadError;
	    _logoImage.onerror = logoLoadError;
	    _embed.appendChild(_logoImage);
	    
	    _logoImage.style.opacity = 0;
	    	    
	    var _button = document.createElement("div");
	    _button.onclick = factoryButtonClickHandler;
	    _embed.appendChild(_button);

	    if (_logoURL == null || "" == _logoURL) {	    	
	    	logoLoadError();
	    }	    
	}
	
	// Prepare button's options
	fetchButtonOptions();

	// Inject styles
	injectStyles();
	
	// Remove this script element from Document
	_parent.removeChild(_script);

	// Place Factory button
	if (_type == "simple-dark" && _counter == null) {
		embedDark();
	} else if (_type == "simple-dark" && _counter == "horizontal") {
		embedDarkCounterHorizontal();
	} else if (_type == "simple-dark" && _counter == "vertical") {
		embedDarkCounterVertical();
	} else if (_type == "simple-white" && _counter == null) {
		embedWhite();
	} else if (_type == "simple-white" && _counter == "horizontal") {
		embedWhiteCounterHorizontal();
	} else if (_type == "simple-white" && _counter == "vertical") {
		embedWhiteCounterVertical();
	} else if (_type == "advanced") {
		embedAdvanced();
	}
}

var factory = new Factory();
