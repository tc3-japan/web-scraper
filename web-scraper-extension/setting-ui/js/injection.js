/**
 * Script injected into all tabs to handle copy SCSS selector.
 */

if (!window.webScraper) {
  window.webScrapper = { posX: undefined, posY: undefined };
  window.onmousedown = (event) => {
    window.webScrapper.posX = event.clientX;
    window.webScrapper.posY = event.clientY;
  }

  chrome.runtime.onMessage.addListener((request) => {
    if (request.type === 'copy-css-selector'
    && window.webScrapper.posX !== undefined
    && window.webScrapper.posY !== undefined) {
      const element = window.document.elementFromPoint(
        window.webScrapper.posX,
        window.webScrapper.posY
      );
      const selector = window.OptimalSelect.select(element);
      navigator.clipboard.writeText(selector);
    }
  });
}
