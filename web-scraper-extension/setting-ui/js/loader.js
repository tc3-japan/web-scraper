/**
 * Handles initialization of the DevTools extension.
 */

chrome.devtools.panels.create(
  'Scraper',
  '',
  'views/devtools-panel/view.html'
)