import React from 'react';
import PT from 'prop-types';

export default function InputField({
  disabled,
  onChange,
  onBlur,
  onFocus,
  title,
  value,
}) {
  return (
    <div className="input-container">
      <span>{title}</span>
      <input
        disabled={disabled}
        onBlur={onBlur}
        onChange={onChange && ((e) => onChange(e.target.value))}
        onFocus={onFocus}
        value={value}
      />
    </div>
  );
}

InputField.propTypes = {
  disabled: PT.bool,
  onBlur: PT.func,
  onChange: PT.func,
  onFocus: PT.func,
  title: PT.string,
  value: PT.string,
};

InputField.defaultProps = {
  disabled: false,
  onBlur: undefined,
  onChange: undefined,
  onFocus: undefined,
  title: '',
  value: undefined,
};
