import React from 'react';
import PT from 'prop-types';
import './styles.scss';

/**
 * check box component
 */
export default function Checkbox({
  disabled,
  label,
  onChange,
  value,
}) {
  let className = 'checkbox-container';
  if (disabled) className += ' disabled';

  let res = (
    <div
      className={className}
      onClick={() => disabled || onChange(!value)}
      onKeyPress={() => disabled || onChange(!value)}
      role="button"
      tabIndex={0}
    >
      {value && <div className="checked" />}
    </div>
  );
  if (label) {
    res = (
      <div className="checkbox-outer">
        {res}
        <div className="label">{label}</div>
      </div>
    );
  }
  return res;
}

Checkbox.propTypes = {
  disabled: PT.bool,
  label: PT.string,
  onChange: PT.func,
  value: PT.bool,
};

Checkbox.defaultProps = {
  disabled: false,
  label: '',
  onChange: () => {},
  value: false,
};
