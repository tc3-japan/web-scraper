import React from 'react';
import Button from "../Button";
import './style.scss'
import ExpandRow from "./ExpandRow";
import PT from "prop-types";
import _ from 'lodash'
import {JSON_DROPDOWN} from "../../config/config";
import {getI18T} from "../../i18nSetup";
import {
  logInfo,
  sendMessageToPage,
  getCommonParent,
  removeParent,
  getPathParent,
  cleanNOfTh
} from "../../services/utils";

const iconP = require('../../assets/icon_+.png');


/**
 * editor component
 */
class Editor extends React.Component {
  constructor(props, context) {
    super(props, context);
    this.state = {}
    this.toggleSelectorBtn = this.toggleSelectorBtn.bind(this);
    this.promises = {};
  }

  /**
   * expand left items, like order, product
   * @param key the key
   */
  toggle(key) {
    this.props.onUpdate('meta.expanded.' + key, !_.get(this.props.siteObj, 'meta.expanded.' + key));
  }

  /**
   * get selector count
   * @param path the path
   * @return {number}
   */
  getTotalOfSelectorTimes(path) {
    if (_.includes(['next_url_element', 'purchase_order.url_element'], path)) {
      return 1;
    }
    return 2;
  }

  /**
   * get class
   * @param selector the selector
   */
  getClass(selector) {
    const promiseId = Date.now() + Math.random();
    sendMessageToPage({action: 'getClass', promiseId: promiseId, selector: selector})
    return new Promise((resolve => {
      this.promises[promiseId] = resolve;
    }))
  }

  /**
   * toggle selector button
   * @param path the path
   * @param action the action
   */
  toggleSelectorBtn(path, action) {
    const {siteObj} = this.props;
    if (action === 'start') {
      this.props.onUpdate('meta.highlight', path);
      sendMessageToPage({action: 'startInspector', selector: _.get(siteObj, path)});
      this.selectors = [];
      this.totalSelectorTimes = this.getTotalOfSelectorTimes(path);
    } else {
      this.stopInspector();
    }
  }

  /**
   * stop inspector
   */
  stopInspector() {
    setTimeout(() => {
      sendMessageToPage({action: 'stopInspector'});
      this.props.onUpdate('meta.highlight', null)
    }, 200)
  }

  /**
   * calculate and set path for json
   */
  calculate() {

    const {siteObj} = this.props;
    const selectors = this.selectors;
    const path = siteObj.meta.highlight;

    // check it is root path
    const pathParts = path.split('.')
    pathParts.pop();
    pathParts.push('full_path')
    const rootPathValue = _.get(siteObj, pathParts.join('.'))
    if (rootPathValue) {
      this.props.onUpdate(path, selectors[selectors.length - 1].path);
      this.stopInspector();
      return;
    }

    if (selectors.length < this.totalSelectorTimes) {
      return;
    }

    logInfo('start calculate path for ' + path + ', length = ' + selectors.length);
    if (selectors.length < 2) { // only one, so just return and set it
      this.props.onUpdate(path, selectors[0].path)
    } else {
      let sp1 = selectors[0].path
      let sp2 = selectors[1].path
      const orderParent = _.get(siteObj, 'purchase_order.parent')
      // product items
      if (path.indexOf('product') > 0) {
        const urlElement = _.get(siteObj, 'purchase_order.purchase_product.url_element')
        const productParent = _.get(siteObj, 'purchase_order.purchase_product.parent')
        // same page, need clean order parent
        if (_.isNil(urlElement) || _.isEmpty(urlElement)) {
          sp1 = removeParent(orderParent, sp1)
          sp2 = removeParent(orderParent, sp2)
        }
        const parent = getCommonParent(sp1, sp2)
        const selector = removeParent(parent, sp1)
        // update parent
        if (path.indexOf('parent') > 0 || _.isEmpty(productParent) || _.isNil(productParent)) {
          this.getClass(getPathParent(selectors[0].path, selectors[1].path)).then(classStr => {
            this.props.onUpdate('purchase_order.purchase_product.parent', cleanNOfTh(parent) + classStr)
          })
        }
        // update other product fields
        if (path.indexOf('parent') < 0) {
          this.props.onUpdate(path, selector + selectors[0].class);
        }
      } else { // order items
        const parent = getCommonParent(sp1, sp2)
        const selector = removeParent(parent, sp1)
        if (path.indexOf('parent') > 0 || _.isNil(orderParent) || _.isEmpty(orderParent)) {
          this.getClass(getPathParent(sp1, sp2)).then(classStr => {
            this.props.onUpdate('purchase_order.parent', parent + classStr)
          })
        }
        if (path.indexOf('parent') < 0) {
          this.props.onUpdate(path, selector + selectors[0].class);
        }
      }
    }
    this.stopInspector();
  }

  componentDidMount() {
    const that = this;
    window.onMessage = (message) => {
      logInfo('got an event from page')
      logInfo('message = ' + JSON.stringify(message));
      if (message.action === 'click') {
        that.selectors.push(message);
        logInfo('current selector length = ' + that.selectors.length);
        that.calculate();
      } else if (message.action === 'currentUrl') {
        that.props.onUpdate('url', message.url)
      } else if (message.action === 'getClass') {
        const promiseId = message.promiseId
        if (this.promises[promiseId]) {
          this.promises[promiseId](message.class)
          delete this.promises[promiseId]
        }
      }
    }
  }

  componentWillUnmount() {
    window.onMessage = _.noop
    sendMessageToPage({action: 'stopInspector'});
  }

  render() {
    const {site, type, siteObj, loadType, onUpdate} = this.props;
    const t = getI18T();
    if (!site || !type || loadType === 'pending') {
      return <div className='tip'>{t('loadJsonTip')}</div>
    }

    if (loadType === 'loading') {
      return <div className='tip'>{t('loadingJson')}</div>
    }

    if (siteObj == null && loadType === 'loaded') {
      return <div className='tip'>{t('loadJsonFailed')}</div>
    }
    if (siteObj == null) {
      return <div/>
    }

    const isExpanded = key => siteObj.meta.expanded[key];
    const getEText = e => e ? '▼' : '▲';
    const orderRows = _.get(siteObj, 'purchase_order.rows') || []
    const productRows = _.get(siteObj, 'purchase_order.purchase_product.rows') || []
    const renderInputRow = (path, title) => <div className='input-container'>
      <span>{title || t('editor.urlSelector')}</span>
      <input value={_.get(siteObj, path)} onChange={e => onUpdate(path, e.target.value)}/>
    </div>

    return <div className='editor-container'>
      <div className={`editor-row ${!!isExpanded('history')}`}>
        <div className='title' onClick={() => this.toggle('history')}>
          {getEText(isExpanded('history'))}<span>Purchase History Page</span>
        </div>
        <div className='input-container'>
          <span>{t('editor.url')}</span>
          <input value={siteObj.url} onChange={e => onUpdate('url', e.target.value)}/>
        </div>
        <Button title={'Current URL'} onClick={() => sendMessageToPage({action: 'currentUrl'})}/>
      </div>
      {isExpanded('history') && <div className='indent'>
        <div className={`editor-row ${!!isExpanded('order')}`}>
          <div className='title' onClick={() => this.toggle('order')}>{getEText(isExpanded('order'))}<span>Purchase Order</span>
          </div>
          {renderInputRow('purchase_order.url_element')}
          <Button type={'selector'}
                  path={'purchase_order.url_element'}
                  highlight={siteObj.meta.highlight}
                  onClick={this.toggleSelectorBtn}/>
        </div>
        {isExpanded('order') && <div className={`editor-row`}>
          <div className='parent-selector'/>
          {renderInputRow('purchase_order.parent', t('editor.parentSelector'))}
          <Button type={'selector'}
                  path={'purchase_order.parent'}
                  highlight={siteObj.meta.highlight}
                  onClick={this.toggleSelectorBtn}/>
        </div>
        }
        {isExpanded('order') && <div className='indent'>
          {_.map(orderRows, (key, i) =>
            <ExpandRow row={orderRows[i]}
                       rows={orderRows}
                       path={`purchase_order.rows.${i}`}
                       toggleSelectorBtn={this.toggleSelectorBtn}
                       highlight={siteObj.meta.highlight}
                       advancedExpanded={siteObj.meta.advancedExpanded}
                       onUpdate={onUpdate}
                       key={`order-${i}`}/>)}
          {orderRows.length < JSON_DROPDOWN.length && <div
            className='icon-btn icon-btn-line'
            onClick={() => onUpdate(`purchase_order.rows.${orderRows.length}`, {})}
          ><img alt={'btn'} src={iconP}/><span>{t('editor.addItem')}</span></div>}

          <div className={`editor-row ${!!isExpanded('product')}`}>
            <div className='title' onClick={() => this.toggle('product')}>{getEText(isExpanded('product'))}<span>Purchase Product</span>
            </div>
            {renderInputRow('purchase_order.purchase_product.url_element')}
            <Button type={'selector'}
                    path={'purchase_order.purchase_product.url_element'}
                    highlight={siteObj.meta.highlight}
                    onClick={this.toggleSelectorBtn}/>
          </div>


          {isExpanded('product') && <div className='editor-row'>
            <div className='parent-selector'/>
            {renderInputRow('purchase_order.purchase_product.parent', t('editor.parentSelector'))}
            <Button type={'selector'}
                    path={'purchase_order.purchase_product.parent'}
                    highlight={siteObj.meta.highlight}
                    onClick={this.toggleSelectorBtn}/>
          </div>}

          {isExpanded('product') && <div className='indent'>
            {_.map(productRows, (key, i) =>
              <ExpandRow row={productRows[i]}
                         rows={productRows}
                         path={`purchase_order.purchase_product.rows.${i}`}
                         toggleSelectorBtn={this.toggleSelectorBtn}
                         highlight={siteObj.meta.highlight}
                         advancedExpanded={siteObj.meta.advancedExpanded}
                         onUpdate={onUpdate}
                         key={`product-${i}`}/>)}
            {productRows.length < JSON_DROPDOWN.length && <div
              className='icon-btn icon-btn-line'
              onClick={() => onUpdate(`purchase_order.purchase_product.rows.${productRows.length}`, {})}
            >
              <img alt={'btn'} src={iconP}/><span>{t('editor.addItem')}</span>
            </div>}
          </div>
          }
        </div>}

        <div className={`editor-row ${!!isExpanded('next')}`}>
          <div className='title'
               onClick={() => this.toggle('next')}>{getEText(isExpanded('next'))}<span>Next Page</span></div>
          {renderInputRow('next_url_element', t('editor.selector'))}
          <Button type={'selector'} onClick={this.toggleSelectorBtn} path={'next_url_element'}
                  highlight={siteObj.meta.highlight}/>
        </div>
      </div>}
    </div>
  }
}

Editor.propTypes = {
  site: PT.any,
  type: PT.any,
  siteObj: PT.object,
  onUpdate: PT.func,
}
export default Editor;