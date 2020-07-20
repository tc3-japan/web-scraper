/* global chrome */

// Create a tab in the devtools area
chrome.devtools.panels.create('Scraper Helper', '', 'index.html', () => {

});

// DevTools page -- devtools.js
// Create a connection to the background page
const backgroundPageConnection = chrome.runtime.connect({
  name: 'devtools-page',
});

backgroundPageConnection.onMessage.addListener(() => {
  // Handle responses from the background page, if any
});
