/**
 * Flexible Modal.
 */
/* global document */

import _ from 'lodash';
import React from 'react';
import ReactDom from 'react-dom';
import PT from 'prop-types';

import './style.scss';

export default function Modal({
  children,
  onCancel,
}) {
  const overlayRef = React.useRef();
  const [portal, setPortal] = React.useState();

  React.useEffect(() => {
    const p = document.createElement('div');
    document.body.classList.add('scrolling-disabled-by-modal');
    document.body.appendChild(p);
    setPortal(p);
    return () => {
      document.body.classList.remove('scrolling-disabled-by-modal');
      document.body.removeChild(p);
    };
  }, []);

  return portal ? ReactDom.createPortal(
    (
      <>
        <div
          aria-modal="true"
          className="ModalContainer"
          onWheel={(event) => event.stopPropagation()}
          role="dialog"
        >
          {children}
        </div>
        <div
          aria-label="Cancel"
          className="ModalOverlay"
          onClick={() => onCancel()}
          onKeyDown={(e) => {
            if (e.key === 'Escape') onCancel();
          }}
          ref={(node) => {
            if (node && node !== overlayRef.current) {
              overlayRef.current = node;
              node.focus();
            }
          }}
          role="button"
          tabIndex="-1"
        />
      </>
    ),
    portal,
  ) : null;
}

Modal.propTypes = {
  onCancel: PT.func,
  children: PT.node,
};

Modal.defaultProps = {
  onCancel: _.noop,
  children: null,
};
