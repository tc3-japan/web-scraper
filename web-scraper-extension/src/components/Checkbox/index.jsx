import React from 'react';
import PT from 'prop-types';
import './styles.scss'


/**
 * check box component
 */
class Checkbox extends React.Component {

  render() {
    const {onChange, value} = this.props;
    return <div className='checkbox-container' onClick={() => onChange(!value)}>
      {value && <div className='checked'/>}
    </div>
  }
}

Checkbox.defaultProps = {
  onChange: () => {
  }
}
Checkbox.propTypes = {
  onChange: PT.func,
  value: PT.bool,
}

export default Checkbox;