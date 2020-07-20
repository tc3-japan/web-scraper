/**
 * A blue title of editor subsection, prefixed by an arrow.
 */

import React from 'react';
import PT from 'prop-types';

import './style.scss';

export default function SectionTitle({
  arrowUp,
  onClick,
  title,
}) {
  const arrow = arrowUp ? '▲' : '▼';
  return (
    <div
      className="SectionTitle"
      onClick={onClick}
      onKeyPress={onClick}
      role="button"
      tabIndex={0}
    >
      { arrow }
      <span className="title">{title}</span>
    </div>
  );
}

SectionTitle.propTypes = {
  arrowUp: PT.bool,
  onClick: PT.func,
  title: PT.string,
};

SectionTitle.defaultProps = {
  arrowUp: false,
  onClick: undefined,
  title: '',
};
