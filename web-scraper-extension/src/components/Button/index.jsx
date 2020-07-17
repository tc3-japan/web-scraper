import React from 'react';
import PT from 'prop-types';
import './styles.scss';

const selectorImg = require('../../assets/selector.png');

/**
 * Button component
 */
export default function Button({
  title,
  children,
  onClick,
  disabled,
  type,
  highlight,
  path,
}) {
  const h = highlight === path && type === 'selector' && highlight;
  return (
    <button
      disabled={disabled}
      onClick={() => {
        onClick(path, h ? 'stop' : 'start');
      }}
      className={`btn-container ${type} ${h ? 'high-light' : ''}`}
      type="button"
    >
      {title}
      {children}
      <img src={selectorImg} alt="btn" />
    </button>
  );
}

Button.propTypes = {
  children: PT.node,
  onClick: PT.func,
  title: PT.string,
  disabled: PT.bool,
  type: PT.string,
  path: PT.string,
  highlight: PT.string,
};

Button.defaultProps = {
  children: null,
  onClick: undefined,
  title: undefined,
  disabled: undefined,
  type: undefined,
  path: undefined,
  highlight: undefined,
};
