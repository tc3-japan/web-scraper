/* global chrome, browser */

const native = chrome || browser;

/**
 * background js
 */
native.runtime.onConnect.addListener((port) => {
  if (port.name === 'devtools-page') {
    native.runtime.onMessage.addListener(request => {
      if (request) {
        if (request.action === 'relay') {
          native.tabs.sendMessage(request.tabId, request.payload);
        } else {
          port.postMessage(request);
        }
      }
    });

    native.tabs.query({ active: true, currentWindow: true }, (tabs) => {
      port.onDisconnect.addListener(() => {
        tabs.forEach((tab) => {
          native.tabs.sendMessage(tab.id, { action: 'stopInspector' });
        });
      });
    });
  }
});
