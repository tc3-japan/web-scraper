/**
 * Handles context menu for CSS copying.
 */

const MENU_ITEM_ID = 'copy-css-selectors';

const injected = new Set();

chrome.tabs.onActiveChanged.addListener((tabId) => {
  chrome.tabs.executeScript(tabId, {
    file: '/node_modules/optimal-select/dist/optimal-select.min.js'
  });
  chrome.tabs.executeScript(tabId, {
    file: '/js/injection.js'
  });
});

chrome.contextMenus.create({
  id: MENU_ITEM_ID,
  title: 'Copy CSS Selector',
  contexts: ['all']
});

chrome.contextMenus.onClicked.addListener((info) => {
  if (info.menuItemId === MENU_ITEM_ID) {
    chrome.tabs.query({ active: true, currentWindow: true }, (tabs) => {
      chrome.tabs.sendMessage(tabs[0].id, { type: 'copy-css-selector' });
    });
  }
});
