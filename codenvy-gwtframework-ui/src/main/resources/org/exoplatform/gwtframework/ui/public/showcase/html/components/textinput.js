/**
 * 
 */

//var TEXTINPUT_TEMPLATE = document.getElementById("textInputTemplate").innerHTML;

var TEXTINPUT_TEMPLATE = "" +
"		<div id=\"{textInputId}\" class=\"exoTextInputPanel\" style=\"width: 300px;\">" + 
"		" + 
"				<table id=\"{textInputTableId}\" class=\"exoTextInputTable\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">" + 
"				" + 
"						<tbody><tr>" + 
"								<td class=\"exoTextInputTableLeft\">" + 
"										<div style=\"width: 3px; height: 1px;\"></div>" + 
"								</td>" + 
"						" + 
"								<td class=\"exoTextInputTableText\"><input class=\"exoTextInputField\" value=\"left docking\" type=\"text\">" + 
"								</td>" + 
"								" + 
"								<td class=\"exoTextInputTableDelimiter\">" + 
"									<div style=\"width: 4px; height: 1px;\"></div>" + 
"								</td>" + 
"						" + 
"								<td class=\"exoTextInputTableButton\">" + 
"									<div class=\"exoTextInputButtonPanel\"><img class=\"exoTextInputButtonIcon\" src=\"../../images/bundled/search.png\">" + 
"									</div>" + 
"								</td>" + 
"						" + 
"								<td class=\"exoTextInputTableRight\">" + 
"									<div style=\"width: 3px; height: 1px;\"></div>" + 
"								</td>" + 
"						</tr>" + 
"				" + 
"				</tbody></table>" + 
"		" + 
"		</div>"; 

function TextInput() {
	
	var textInputId;
	
	var isStyleDown = false;
	
	var html;
	
	this.create = function(id) {
		this.textInputId = id;
		
		var html = TEXTINPUT_TEMPLATE;
		html = html.replace("{textInputId}", id);
		html = html.replace("{textInputTableId}", id + "-table");
		this.html = html;
	}
	
	this.addTo = function(placeHolder) {
		document.getElementById(placeHolder).innerHTML = this.html;
	}

	this.setNormal = function() {
		this.isStyleDown = false;		
		var i = this.textInputId + "-table";
		document.getElementById(i).className = "exoTextInputTable";
	}
	
	this.setPressed = function() {
		this.isStyleDown = true;		
		var i = this.textInputId + "-table";
		document.getElementById(i).className = "exoTextInputTable exoTextInputTableDown";
	}
	
	this.setWidth = function(width) {
		document.getElementById(this.textInputId).style.width = width;
	}
	
	this.getHTML = function() {
		return this.html;
	}
	
}
