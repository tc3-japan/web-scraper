/**
 * The round button with plus or minus sign, and optional label.
 */

import React from 'react';
import PT from 'prop-types';
import './style.scss';

const iconP = require('../../assets/icon_+.png');
const iconM = require('../../assets/icon_-.png');

export const TYPES = {
  MINUS: 'MINUS',
  PLUS: 'PLUS',
};

const VALID_TYPES = Object.values(TYPES);

export default function IconButton({
  className,
  onClick,
  title,
  topMargin,
  type,
}) {
  let iconSrc;
  switch (type) {
    case TYPES.MINUS: iconSrc = iconM; break;
    case TYPES.PLUS: iconSrc = iconP; break;
    default:
  }

  let containerClassName = 'IconButton';
  if (topMargin) containerClassName += ' topMargin';
  if (className) containerClassName += ` ${className}`;

  return (
    <div
      className={containerClassName}
      onClick={() => onClick()}
      onKeyPress={() => onClick()}
      role="button"
      tabIndex={0}
    >
      <img alt="button" src={iconSrc} />
      { title ? <span>{title}</span> : null }
    </div>
  );
}

IconButton.propTypes = {
  className: PT.string,
  onClick: PT.func,
  title: PT.string,
  topMargin: PT.bool,
  type: PT.oneOf(VALID_TYPES).isRequired,
};

IconButton.defaultProps = {
  className: '',
  onClick: undefined,
  title: '',
  topMargin: false,
};
