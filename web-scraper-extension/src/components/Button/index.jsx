import React from 'react';
import PT from 'prop-types';
import './styles.scss';

const selectorImg = require('../../assets/selector.png');

/**
 * Button component
 */
export default function Button({
  className,
  title,
  children,
  onClick,
  disabled,
  type,
  highlight,
  path,
}) {
  let buttonClass = `btn-container ${type}`;
  if (className) buttonClass += ` ${className}`;
  const h = highlight === path && type === 'selector' && highlight;
  if (h) buttonClass += ' high-light';
  return (
    <button
      disabled={disabled}
      onClick={() => onClick && onClick(path, h ? 'stop' : 'start')}
      className={buttonClass}
      type="button"
    >
      {title}
      {children}
      <img src={selectorImg} alt="btn" />
    </button>
  );
}

Button.propTypes = {
  className: PT.string,
  children: PT.node,
  onClick: PT.func,
  title: PT.string,
  disabled: PT.bool,
  type: PT.string,
  path: PT.string,
  highlight: PT.string,
};

Button.defaultProps = {
  className: undefined,
  children: null,
  onClick: undefined,
  title: undefined,
  disabled: undefined,
  type: undefined,
  path: undefined,
  highlight: undefined,
};
