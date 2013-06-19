function addMenuAddition(html) {
    var tr = document.getElementById("ide-menu-additions-rows");
    var td = document.createElement("td");
    td.innerHTML = html;
    tr.appendChild(td);
}

 var htmlShell = "<a id=\"shell-link\" href=/ide/" + ws + "/_app/shell target=\"_blank\">Shell</a>";
 addMenuAddition(htmlShell);

 var htmlLogout = "<span id=\"logoutButton\" onClick=\"window.location = '/site/logout.jsp';\">Logout</span>";
 addMenuAddition(htmlLogout);

