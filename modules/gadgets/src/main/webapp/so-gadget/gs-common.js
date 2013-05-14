 
function getUserPreferenceValue(name) {
   	var queryString, theQueryName, vars, prefs;
   	if (window != "undefined" && window != undefined && window != null && window != " ") {
	   	queryString = window.location.search;
	   	if (queryString != " " && queryString != "undefined") {
	   		queryString = queryString.substring(1);
	   		vars = queryString.split("&");
	   		for (var i = 0; i < vars.length; i++) {
	   			var pair = vars[i].split("=");
	   			if (pair[0] == name) {
	   				return pair[1];
	   			}
	   		}
	   	}
   	}
   	prefs = new gadgets.Prefs();
   	return prefs.getString(name);
}