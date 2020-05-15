// Create a tab in the devtools area
chrome.devtools.panels.create("Scraper Helper", "", "index.html", function (panel) {

});
// DevTools page -- devtools.js
// Create a connection to the background page
var backgroundPageConnection = chrome.runtime.connect({
  name: "devtools-page"
});

backgroundPageConnection.onMessage.addListener(function (message) {
  // Handle responses from the background page, if any
});