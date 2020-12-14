/* global window */

import _ from 'lodash';
import React from 'react';
import PT from 'prop-types';
import Swal from 'sweetalert2';

import Button from '../Button';
import ExpandRow from './ExpandRow';
import InputField from '../InputField';
import IconButton, { TYPES as IB_TYPES } from '../IconButton';
import SectionTitle from '../SectionTitle';

import { JSON_DROPDOWN, VALID_SCRAPING_TYPES } from '../../config/dropdown-list';
import getI18T from '../../i18nSetup';
import {
  logInfo,
  sendMessageToPage,
  getCommonParent,
  removeParent,
  getPathParent,
  getCommonClass,
  removeDifferentAndAdditional,
} from '../../services/utils';

import './style.scss';

/**
 * get selector count
 * @param path the path
 * @return {number}
 */
function getTotalOfSelectorTimes(path) {
  if (_.includes(['next_url_element'], path)) {
    return 1;
  }
  return 2;
}

/**
 * editor component
 */
export default class Editor extends React.Component {
  constructor(props, context) {
    super(props, context);
    this.state = {};
    this.toggleSelectorBtn = this.toggleSelectorBtn.bind(this);
    this.promises = {};
    this.t = getI18T();
  }

  componentDidMount() {
    const that = this;
    window.onMessage = (message) => {
      logInfo('got an event from page');
      logInfo(`message = ${JSON.stringify(message)}`);
      if (window.messageListeners) {
        window.messageListeners.forEach((listener) => listener(message));
      }
      if (message.action === 'click') {
        if (!that.props.siteObj.meta.highlight) {
          window.log('ignore message, because of not in highlight mode');
          return;
        }
        that.selectors.push(message);
        logInfo(`current selector length = ${that.selectors.length}`);
        that.calculate()
          .then(() => window.log('calculate done'))
          .catch((e) => {
            // if failed, here need stop inspector
            this.stopInspector('calculate-error');
            Swal.fire({
              html: e.message.replace(/\n/g, '<br />'),
              showConfirmButton: true,
              confirmButtonText: this.t('dialogBtnOK'),
            });
            window.log(e);
          });
      } else if (message.action === 'currentUrl') {
        that.props.onUpdate('url', message.url);
      } else if (message.action === 'getClass') {
        const { promiseId } = message;
        if (this.promises[promiseId]) {
          this.promises[promiseId](message.class);
          delete this.promises[promiseId];
        }
      }
    };
  }

  componentWillUnmount() {
    window.onMessage = _.noop;
    this.stopInspector('componentWillUnmount');
  }

  /**
   * get highlight element by parent
   * @param path the path
   * @return {string|*}
   */
  getHighlight(path) {
    const { siteObj } = this.props;
    const basePath = _.get(siteObj, path);

    const pathParts = path.split('.');
    pathParts.pop();
    pathParts.push('full_path');
    const rootPathValue = _.get(siteObj, pathParts.join('.')); // check full_path checked

    if (_.isNil(basePath)
      || _.isEmpty(basePath)
      || rootPathValue
      || path === 'purchase_order.parent'
      || path === 'next_url_element'
      || path === 'purchase_order.url_element'
    ) {
      return basePath;
    }

    const orderParent = _.get(siteObj, 'purchase_order.parent');
    const productUrlElement = _.get(siteObj, 'purchase_order.purchase_product.url_element');
    const productParent = _.get(siteObj, 'purchase_order.purchase_product.parent');
    const appendPath = (paths) => (paths || []).filter((p) => (p || '').trim() !== '').join(' > ');

    if (path.indexOf('product') > 0) {
      const isParent = path === 'purchase_order.purchase_product.parent'
        || path === 'purchase_order.purchase_product.url_element';
      const haveEle = !(_.isNil(productUrlElement) || _.isEmpty(productUrlElement));
      if (isParent) {
        return haveEle ? basePath : appendPath([orderParent, basePath]);
      }
      return haveEle
        ? appendPath([productParent, basePath])
        : appendPath([orderParent, productParent, basePath]);
    }
    return appendPath([orderParent, basePath]);
  }

  /**
   * get class
   * @param selector the selector
   */
  getClass(selector) {
    const promiseId = Date.now() + Math.random();
    sendMessageToPage({ action: 'getClass', promiseId, selector });
    return new Promise(((resolve) => {
      this.promises[promiseId] = resolve;
    }));
  }

  /**
   * expand left items, like order, product
   * @param key the key
   */
  toggle(key) {
    const { onUpdate, siteObj } = this.props;
    onUpdate(`meta.expanded.${key}`, !_.get(siteObj, `meta.expanded.${key}`));
  }

  /**
   * stop inspector
   */
  stopInspector(reason) {
    const { onUpdate } = this.props;
    this.selectors = [];
    setTimeout(() => {
      sendMessageToPage({ action: 'stopInspector', reason });
      window.log(`stopInspector,reason=${reason}`);
      onUpdate('meta.highlight', null);
    }, 200);
  }

  /**
   * calculate and set path for json
   */
  async calculate() {
    const { onUpdate, siteObj } = this.props;
    const { selectors } = this;
    const path = siteObj.meta.highlight;

    // check it is root path
    const pathParts = path.split('.');
    window.log(`calculate path = ${path}`);
    pathParts.pop();
    pathParts.push('full_path');
    const rootPathValue = _.get(siteObj, pathParts.join('.'));
    if (rootPathValue) {
      onUpdate(path, selectors[selectors.length - 1].path);
      this.stopInspector('rootPathValue');
      return;
    }

    if (selectors.length < this.totalSelectorTimes) {
      return;
    }

    logInfo(`start calculate path for ${path}, length = ${selectors.length}`);
    if (selectors.length < 2) { // only one, so just return and set it
      onUpdate(path, selectors[0].path);
    } else {
      let sp1 = selectors[0].path;
      let sp2 = selectors[1].path;
      const orderParent = _.get(siteObj, 'purchase_order.parent');
      // product items
      if (path.indexOf('product') > 0) {
        const urlElement = _.get(siteObj, 'purchase_order.purchase_product.url_element');
        let productParent = _.get(siteObj, 'purchase_order.purchase_product.parent');
        const isElement = path.indexOf('url_element') >= 0;
        // same page, need clean order parent
        // and purchase_product.url_element always need calculate from order parent
        if (_.isNil(urlElement)
          || _.isEmpty(urlElement)
          || isElement) {
          sp1 = removeParent(orderParent, sp1);
          sp2 = removeParent(orderParent, sp2);
        }
        window.log(`sp1 = ${sp1}`);
        window.log(`sp2 = ${sp2}`);
        const isProductParentNull = _.isNil(productParent) || _.isEmpty(productParent);
        // calculate new product parent
        productParent = isProductParentNull ? getCommonParent(sp1, sp2) : productParent;

        // update parent, only if product parent is null/selector is parent
        // and not in url_element selector
        if ((path.indexOf('parent') > 0
          || isProductParentNull
        ) && !isElement) {
          const pPath = getPathParent(selectors[0].path, selectors[1].path);
          const c1 = await this.getClass(pPath[0]);
          const c2 = await this.getClass(pPath[1]);
          window.log(`parent path1 = ${pPath[0]}`);
          window.log(`parent path2 = ${pPath[1]}`);
          const classStr = getCommonClass([c1, c2]);
          window.log(`${c1} , ${c2} , ${classStr}`);
          onUpdate('purchase_order.purchase_product.parent', (productParent) + classStr);
        }

        let s1 = sp1;
        let s2 = sp2;
        if (isElement) {
          window.log('skip remove product parent, because of this is purchase_product.url_element');
        } else {
          s1 = removeParent(productParent, sp1);
          s2 = removeParent(productParent, sp2);
        }
        window.log(`s1 = ${s1}`);
        window.log(`s2 = ${s2}`);
        const selector = removeDifferentAndAdditional(s1, s2);
        window.log(`after removeDifferentAndAdditional, selector = ${selector}`);
        // update other product fields
        if (path.indexOf('parent') < 0) {
          onUpdate(path, selector + (getCommonClass([selectors[0].class, selectors[1].class])));
        }
      } else { // order items
        const newParent = getCommonParent(sp1, sp2);
        const isElement = path.indexOf('url_element') >= 0;
        let orderParent2 = _.get(siteObj, 'purchase_order.parent');
        const isOrderParentNull = _.isNil(orderParent2) || _.isEmpty(orderParent2);
        orderParent2 = isOrderParentNull ? newParent : orderParent2;

        let selector = null;
        // order.url_element not need use parent
        // only need removeDifferentAndAdditional
        let s1 = sp1;
        let s2 = sp2;
        if (isElement) {
          orderParent2 = newParent;
        } else {
          s1 = removeParent(orderParent2, sp1);
          s2 = removeParent(orderParent2, sp2);
        }

        window.log(`s1 = ${s1}`);
        window.log(`s2 = ${s2}`);
        selector = removeDifferentAndAdditional(s1, s2);
        window.log(`final selector = ${selector}`);
        // update parent if needed
        if ((path.indexOf('parent') > 0
          || isOrderParentNull
        ) && !isElement // element not need update parent
        ) {
          const pPath = getPathParent(sp1, sp2);
          const c1 = await this.getClass(pPath[0]);
          const c2 = await this.getClass(pPath[1]);
          const classStr = getCommonClass([c1, c2]);
          window.log(`${c1} , ${c2} , ${classStr}`);
          onUpdate('purchase_order.parent', orderParent2 + classStr);
        }

        // update property
        if (path.indexOf('parent') < 0) {
          onUpdate(path, selector + (getCommonClass([selectors[0].class, selectors[1].class])));
        }
      }
    }
    this.stopInspector('calculate-finished');
  }

  /**
   * toggle selector button
   * @param path the path
   * @param action the action
   */
  toggleSelectorBtn(path, action) {
    if (action === 'start') {
      const { onUpdate } = this.props;
      onUpdate('meta.highlight', path);
      setTimeout(() => {
        sendMessageToPage({
          action: 'startInspector',
          selector: this.getHighlight(path),
          path,
        });
        this.selectors = [];
        this.totalSelectorTimes = getTotalOfSelectorTimes(path);
      }); // next tick
    } else {
      this.stopInspector('toggleSelectorBtn');
    }
  }

  render() {
    const {
      siteObj,
      onUpdate,
    } = this.props;
    const t = getI18T();

    if (
      !siteObj
      || siteObj.dataType !== VALID_SCRAPING_TYPES.PURCHASE_HISTORY
    ) return null;

    const isExpanded = (key) => siteObj.meta.expanded[key];

    const orderRows = _.get(siteObj, 'purchase_order.rows') || [];
    const productRows = _.get(siteObj, 'purchase_order.purchase_product.rows') || [];

    const renderInputRow = (path, title) => (
      <InputField
        onChange={(value) => onUpdate(path, value)}
        title={title || t('editor.urlSelector')}
        value={_.get(siteObj, path)}
      />
    );

    return (
      <div className="editor-container">
        <div className={`editor-row ${!!isExpanded('history')}`}>
          <SectionTitle
            arrowUp={!isExpanded('history')}
            onClick={() => this.toggle('history')}
            title={t('editor.purchaseHistoryPage')}
          />
          <InputField
            onChange={(value) => onUpdate('url', value)}
            title={t('editor.url')}
            value={siteObj.url}
          />
          <Button
            title={t('editor.currentUrl')}
            onClick={() => sendMessageToPage({ action: 'currentUrl' })}
          />
        </div>
        <div className="currentUrlWarning">
          {t('editor.currentUrlYearPlaceholder')}
        </div>
        {isExpanded('history') && (
        <div className="indent">
          <div className={`editor-row ${!!isExpanded('order')}`}>
            <SectionTitle
              arrowUp={!isExpanded('order')}
              onClick={() => this.toggle('order')}
              title={t('editor.purchaseOrder')}
            />
            {renderInputRow('purchase_order.url_element')}
            <Button
              type="selector"
              path="purchase_order.url_element"
              highlight={siteObj.meta.highlight}
              onClick={this.toggleSelectorBtn}
            />
          </div>
          {isExpanded('order') && (
          <div className="editor-row">
            <div className="parent-selector" />
            {renderInputRow('purchase_order.parent', t('editor.parentSelector'))}
            <Button
              type="selector"
              path="purchase_order.parent"
              highlight={siteObj.meta.highlight}
              onClick={this.toggleSelectorBtn}
            />
          </div>
          )}
          {isExpanded('order') && (
          <div className="indent">
            {_.map(orderRows, (key, i) => (
              <ExpandRow
                row={orderRows[i]}
                rows={orderRows}
                path={`purchase_order.rows.${i}`}
                toggleSelectorBtn={this.toggleSelectorBtn}
                highlight={siteObj.meta.highlight}
                advancedExpanded={siteObj.meta.advancedExpanded}
                selectorPrefix={_.get(siteObj, 'purchase_order.parent')}
                onUpdate={onUpdate}
                key={`order-${i}`}
              />
            ))}
            {
              orderRows.length < JSON_DROPDOWN.length && (
                <IconButton
                  onClick={
                    () => onUpdate(
                      `purchase_order.rows.${orderRows.length}`,
                      {},
                    )
                  }
                  topMargin
                  title={t('editor.addItem')}
                  type={IB_TYPES.PLUS}
                />
              )
            }
            <div className={`editor-row ${!!isExpanded('product')}`}>
              <SectionTitle
                arrowUp={!isExpanded('product')}
                onClick={() => this.toggle('product')}
                title={t('editor.purchaseProduct')}
              />
              {renderInputRow('purchase_order.purchase_product.url_element')}
              <Button
                type="selector"
                path="purchase_order.purchase_product.url_element"
                highlight={siteObj.meta.highlight}
                onClick={this.toggleSelectorBtn}
              />
            </div>

            {isExpanded('product') && (
            <div className="editor-row">
              <div className="parent-selector" />
              {renderInputRow('purchase_order.purchase_product.parent', t('editor.parentSelector'))}
              <Button
                type="selector"
                path="purchase_order.purchase_product.parent"
                highlight={siteObj.meta.highlight}
                onClick={this.toggleSelectorBtn}
              />
            </div>
            )}

            {isExpanded('product') && (
            <div className="indent">
              {
                _.map(productRows, (key, i) => {
                  let prefix = [];

                  const productUrlSelector = _.get(
                    siteObj,
                    'purchase_order.purchase_product.url_element',
                  );

                  if (!productUrlSelector) {
                    const orderParentSelector = _.get(
                      siteObj,
                      'purchase_order.parent',
                    );
                    if (orderParentSelector) prefix.push(orderParentSelector);
                  }

                  const purchaseParentSelector = _.get(
                    siteObj,
                    'purchase_order.purchase_product.parent',
                  );
                  if (purchaseParentSelector) {
                    prefix.push(purchaseParentSelector);
                  }

                  prefix = prefix.join(' > ');

                  return (
                    <ExpandRow
                      row={productRows[i]}
                      rows={productRows}
                      path={`purchase_order.purchase_product.rows.${i}`}
                      toggleSelectorBtn={this.toggleSelectorBtn}
                      highlight={siteObj.meta.highlight}
                      selectorPrefix={prefix}
                      advancedExpanded={siteObj.meta.advancedExpanded}
                      onUpdate={onUpdate}
                      key={`product-${i}`}
                    />
                  );
                })
              }
              {
                productRows.length < JSON_DROPDOWN.length && (
                  <IconButton
                    onClick={
                      () => onUpdate(
                        `purchase_order.purchase_product.rows.${productRows.length}`,
                        {},
                      )
                    }
                    topMargin
                    title={t('editor.addItem')}
                    type={IB_TYPES.PLUS}
                  />
                )
              }
            </div>
            )}
          </div>
          )}

          <div className={`editor-row ${!!isExpanded('next')}`}>
            <SectionTitle
              arrowUp={!isExpanded('next')}
              onClick={() => this.toggle('next')}
              title={t('editor.nextPage')}
            />
            {renderInputRow('next_url_element', t('editor.selector'))}
            <Button
              type="selector"
              onClick={this.toggleSelectorBtn}
              path="next_url_element"
              highlight={siteObj.meta.highlight}
            />
          </div>
        </div>
        )}
      </div>
    );
  }
}

Editor.propTypes = {
  siteObj: PT.shape({
    dataType: PT.string.isRequired,
    meta: PT.shape({
      advancedExpanded: PT.shape({}),
      expanded: PT.shape({}),
      highlight: PT.string,
    }),
    url: PT.string,
  }),
  onUpdate: PT.func,
};

Editor.defaultProps = {
  siteObj: undefined,
  onUpdate: undefined,
};
