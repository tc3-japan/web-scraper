import React from 'react';
import PT from 'prop-types';
import './styles.scss';

/**
 * check box component
 */
export default function Checkbox({
  onChange,
  value,
}) {
  return (
    <div
      className="checkbox-container"
      onClick={() => onChange(!value)}
      onKeyPress={() => onChange(!value)}
      role="button"
      tabIndex={0}
    >
      {value && <div className="checked" />}
    </div>
  );
}

Checkbox.propTypes = {
  onChange: PT.func,
  value: PT.bool,
};

Checkbox.defaultProps = {
  onChange: () => {},
  value: false,
};
