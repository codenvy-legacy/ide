/*
 * Appends DIV element to placing additional menu items.
 */
(function addMenuAdditionsPlaceHolder() {
	var html = "<div id='ide-menu-additions' align='right' class='ideMenuAdditions'>" +
					"<table cellspacing='0' cellpadding='0' border='0' class='ideMenuAdditionsTable'>" +
						"<tr id='ide-menu-additions-rows'></tr>" +
					"</table>" +
				"</div>";

	var div = document.createElement('div');
	div.innerHTML = html;
	document.body.appendChild(div.childNodes[0]);
	
	var head = document.getElementsByTagName('head')[0];
	var links = head.getElementsByTagName('link');
	
	for (i = 0; i < links.length; i++) {
		if ("menu-additions.css" == links[i].href) {
			return;
		}
	}

	var link = document.createElement('link');
	link.rel = "stylesheet";
	link.type = "text/css";
	link.href = "menu-additions.css";
	head.appendChild(link);
})();


/*
 * Adds additional menu item.
 */
function addMenuAddition(html) {
    var tr = document.getElementById("ide-menu-additions-rows");
    if (tr == null) {
    	return;
    }
    
    var td = document.createElement("td");
    td.innerHTML = html;
    tr.appendChild(td);
}


var htmlShell = "<a id=\"shell-link\" href=/ide/" + ws + "/_app/shell target=\"_blank\">Shell</a>";
addMenuAddition(htmlShell);

var htmlLogin = "<span id=\"loginButton\" onClick=\"window.location = '/ide/login';\">Login</span>";
addMenuAddition(htmlLogin);

var htmlLogout = "<span id=\"logoutButton\" onClick=\"window.location = '/ide/_app/logout.jsp';\">Logout</span>";
addMenuAddition(htmlLogout);
