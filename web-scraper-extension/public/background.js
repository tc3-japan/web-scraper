const native = chrome || browser;

/**
 * background js
 */
native.runtime.onConnect.addListener(function (port) {
  if (port.name === "devtools-page") {
    native.tabs.query({active: true, currentWindow: true}, (tabs) => {
      port.onDisconnect.addListener(function () {
        tabs.forEach(tab => {
          native.tabs.sendMessage(tab.id, {action: 'stopInspector'});
        });
      });
    });
  }
});