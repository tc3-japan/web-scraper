import React from 'react';
import PT from 'prop-types';
import './styles.scss'

const selectorImg = require('../../assets/selector.png');

/**
 * Button component
 */
class Button extends React.Component {

  render() {
    const {title, children, onClick, disabled, type, highlight, path} = this.props;
    const h = highlight === path && type === 'selector' && highlight;
    return <button disabled={disabled}
                   onClick={() => {
                     onClick(path, h ? 'stop' : 'start')
                   }}
                   className={`btn-container ${type} ${h ? 'high-light' : ''}`}>{title}{children}
      <img src={selectorImg} alt={'btn'}/>
    </button>
  }
}

Button.propTypes = {
  onClick: PT.func,
  title: PT.string,
  disabled: PT.bool,
  type: PT.string,
  path: PT.string,
  highlight: PT.string,
}

export default Button;