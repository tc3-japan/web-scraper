/* global browser, chrome, document, inspector, window */

(function () {
  /**
   * https://github.com/ilyashubin/hover-inspect
   * @constructor
   */
  const native = chrome || browser;
  const blockEvent = 'block-event';
  const Inspector = function () {
    this.log = this.log.bind(this);
    this.layout = this.layout.bind(this);
    this.mousedown = this.mousedown.bind(this);

    this.handleResize = this.handleResize.bind(this);
    this.$target = null;
    this.$cacheEl = null;
    this.forbidden = [this.$cacheEl, document.body, document.documentElement];
    this.highlightNodes = [];
    this.messages = {};
  };

  const templateHtml = `<template class='tl-template'>
<style>
.tl-wrap {
  pointer-events: none;
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  z-index: 10000001;
}

.tl-wrap.-out .tl-canvas {
  transition: opacity 0.3s;
  opacity: 0;
}
.block-event{
  pointer-events: none !important;
}
</style>
<div class="tl-wrap">
  <canvas width='100' height='100' id='tl-canvas' class='tl-canvas'></canvas>
</div>
</template>
`;
  Inspector.prototype = {
    getNodes() {
      this.template = templateHtml;
      this.createNodes();
      this.registerEvents();
    },

    createNodes() {
      this.$host = document.createElement('div');
      this.$host.className = 'tl-host';
      this.$host.style.cssText = 'all: initial;';

      const shadow = this.$host;
      document.body.appendChild(this.$host);

      const templateMarkup = document.createElement('div');
      templateMarkup.innerHTML = this.template;
      shadow.innerHTML = templateMarkup.querySelector('template').innerHTML;

      this.$wrap = shadow.querySelector('.tl-wrap');
      this.$canvas = shadow.querySelector('#tl-canvas');
      this.c = this.$canvas.getContext('2d');
      this.$canvas.width = window.innerWidth;
      this.$canvas.height = window.innerHeight;
      this.width = this.$canvas.width;
      this.height = this.$canvas.height;
    },

    getFullPath(ele) {
      function getNthOfType(parent, current) {
        if (!parent) {
          return '';
        }
        const childNodes = (parent.childNodes || []);
        const nodes = [];
        for (let i = 0; i < childNodes.length; i++) {
          const node = childNodes[i];
          if (node.tagName === current.tagName) {
            nodes.push(node);
          }
        }

        if (nodes.length === 1) {
          return '';
        }

        for (let i = 0; i < nodes.length; i++) {
          if (nodes[i] === current) {
            return `:nth-of-type(${i + 1})`;
          }
        }
        return '';
      }

      let next = ele;
      let path = '';
      while (next) {
        const p = next.parentNode;
        const i = getNthOfType(p, next);
        if (next.tagName) {
          path = next.tagName.toLowerCase() + i + (path === '' ? '' : ' > ') + path;
        }
        next = next.parentNode;
      }
      return path;
    },

    getClass(e, first) {
      let classStr = '';
      if (!e) {
        return classStr;
      }
      const classList = e.classList || [];
      for (let i = 0; i < classList.length; i++) {
        if (classList[i] !== blockEvent) {
          classStr += `.${classList[i]}`;
          if (first) {
            break; // only fetch the first element
          }
        }
      }
      return classStr;
    },

    blockEvent(target) {
      let next = target;
      const elements = [];
      while (next) {
        if (next.classList && !next.classList.contains(blockEvent)) {
          if (next === target) {
            next.classList.add(blockEvent);
          }
          const context = { e: next };
          if (next.tagName.toLowerCase() === 'a') {
            context.href = next.href;
            next.href = 'javascript:;'; // eslint-disable-line no-script-url
          }
          elements.push(context);
        }
        next = next.parentNode;
      }
      setTimeout(() => {
        for (let i = 0; i < elements.length; i++) {
          const ele = elements[i].e;
          const context = elements[i];
          ele.classList.remove(blockEvent);
          if (context.href) {
            ele.href = context.href;
          }
        }
      }, 2000);
    },
    mousedown(e) {
      e.preventDefault();
      e.stopPropagation();
      e.stopImmediatePropagation();

      this.blockEvent(e.target);
      const classStr = this.getClass(this.$target);
      const fullPath = this.getFullPath(this.$target);
      console.log(`selector path = ${fullPath}`);
      try {
        native.runtime.sendMessage({
          action: 'click',
          path: fullPath,
          class: classStr,
        });
      } catch (error) {
        console.log(error);
      }
      return false;
    },

    log(e) {
      this.$target = e.target;
      // check if element cached
      if (this.forbidden.indexOf(this.$target) !== -1) return;
      this.$cacheEl = this.$target;
      this.layout();
    },

    // redraw overlay
    layout() {
      const { c } = this;
      const that = this;
      c.clearRect(0, 0, this.width, this.height);

      function drawElement(target, highlight) {
        const rect = target.getBoundingClientRect();
        const computedStyle = window.getComputedStyle(target);
        const box = {
          width: rect.width,
          height: rect.height,
          top: rect.top,
          left: rect.left,
          margin: {
            top: computedStyle.marginTop,
            right: computedStyle.marginRight,
            bottom: computedStyle.marginBottom,
            left: computedStyle.marginLeft,
          },
          padding: {
            top: computedStyle.paddingTop,
            right: computedStyle.paddingRight,
            bottom: computedStyle.paddingBottom,
            left: computedStyle.paddingLeft,
          },
        };
        // pluck negatives
        ['margin', 'padding'].forEach((property) => {
          /* eslint-disable guard-for-in, no-restricted-syntax */
          for (const el in box[property]) {
            const val = parseInt(box[property][el], 10);
            box[property][el] = Math.max(0, val);
          }
          /* eslint-enable guard-for-in, no-restricted-syntax */
        });

        box.left = Math.floor(box.left) + 1.5;
        box.width = Math.floor(box.width) - 1;
        let x; let y; let width; let
          height;
        // margin
        x = box.left - box.margin.left;
        y = box.top - box.margin.top;
        width = box.width + box.margin.left + box.margin.right;
        height = box.height + box.margin.top + box.margin.bottom;

        c.fillStyle = 'rgba(255,165,0,0.5)';
        c.fillRect(x, y, width, height);

        // padding
        x = box.left;
        y = box.top;
        width = box.width;
        height = box.height;

        c.fillStyle = 'rgba(158,113,221,0.5)';
        c.clearRect(x, y, width, height);
        c.fillRect(x, y, width, height);

        // content
        x = box.left + box.padding.left;
        y = box.top + box.padding.top;
        width = box.width - box.padding.right - box.padding.left;
        height = box.height - box.padding.bottom - box.padding.top;

        if (highlight) {
          c.fillStyle = 'rgba(0,0,255,0.25)';
        } else {
          c.fillStyle = 'rgba(255,165,85,0.25)';
        }
        c.clearRect(x, y, width, height);
        c.fillRect(x, y, width, height);

        if (!highlight) {
          // rulers (horizontal - =)
          x = -10;
          y = Math.floor(box.top) + 0.5;
          width = that.width + 10;
          height = box.height - 1;

          c.beginPath();
          c.setLineDash([10, 3]);
          c.fillStyle = 'rgba(0,0,0,0.02)';
          c.strokeStyle = 'rgba(13, 139, 201, 0.45)';
          c.lineWidth = 1;
          c.rect(x, y, width, height);
          c.stroke();
          c.fill();

          // // rulers (vertical - ||)
          x = box.left;
          y = -10;
          width = box.width;
          height = that.height + 10;

          c.beginPath();
          c.setLineDash([10, 3]);
          c.fillStyle = 'rgba(0,0,0,0.02)';
          c.strokeStyle = 'rgba(13, 139, 201, 0.45)';
          c.lineWidth = 1;
          c.rect(x, y, width, height);
          c.stroke();
          c.fill();
        }
      }

      const hNodes = this.highlightNodes || [];
      for (let i = 0; i < hNodes.length; i++) {
        drawElement(hNodes[i], true);
      }

      if (this.$target) {
        drawElement(this.$target);
      }
    },

    highlight(selector) {
      if (!selector || selector.trim() === '') {
        return;
      }
      this.highlightNodes = document.querySelectorAll(selector);
      this.layout();
    },
    handleResize() {
      this.$canvas.width = window.innerWidth;
      this.$canvas.height = window.innerHeight;
      this.width = this.$canvas.width;
      this.height = this.$canvas.height;
    },

    activate() {
      this.deactivate();
      this.getNodes();
    },

    registerEvents() {
      document.addEventListener('mousemove', this.log);
      window.addEventListener('mouseup', this.mousedown);
      document.addEventListener('scroll', this.layout);
      window.addEventListener('resize', () => {
        this.handleResize();
        this.layout();
      });
    },
    deactivate() {
      if (this.$host) {
        this.$wrap.classList.add('-out');
        document.removeEventListener('mousemove', this.log);
        window.removeEventListener('mouseup', this.mousedown);
        document.body.removeChild(this.$host);
        this.highlightNodes = [];
        this.$target = null;
        this.$cacheEl = null;
        this.$host = null;
      }
    },
  };

  function onMessage(request) {
    console.log(request);
    const { messageId } = request;
    if (messageId && inspector.messages[messageId]) {
      console.log(`skip same message, id = ${messageId}`);
      return;
    }
    inspector.messages[messageId] = true;
    if (request.action === 'startInspector') {
      inspector.activate();
      inspector.highlight(request.selector);
    } else if (request.action === 'stopInspector') {
      inspector.deactivate();
    } else if (request.action === 'currentUrl') {
      native.runtime.sendMessage({
        action: 'currentUrl',
        url: document.location.href,
      });
    } else if (request.action === 'getClass') {
      const element = document.querySelector(request.selector);
      let classStr = '';
      if (element) {
        classStr = inspector.getClass(element);
      }
      native.runtime.sendMessage({
        action: 'getClass',
        promiseId: request.promiseId,
        class: classStr,
      });
    }
  }

  window.inspector = window.inspector || new Inspector();
  if (native.runtime.onMessage && native.runtime.onMessage.addListener) {
    native.runtime.onMessage.addListener(onMessage);
  }
}());
