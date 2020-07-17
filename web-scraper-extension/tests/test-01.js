const assert = require('assert');
const selector = require('../src/services/selector-helper');

const orderParent = 'html > body > div > div:nth-of-type(2) > div:nth-of-type(1) > div:nth-of-type(5) > div.a-box-group.a-spacing-base.order';
const productParent = 'div > div > div > div > div:nth-of-type(1) > div > div > div.a-fixed-left-grid-inner';

const p1 = 'html > body > div:nth-of-type(1) > div:nth-of-type(2) > div:nth-of-type(1) > div:nth-of-type(5) > div:nth-of-type(5) > div:nth-of-type(3) > div > div:nth-of-type(2) > div > div:nth-of-type(1) > div > div:nth-of-type(1) > div > div:nth-of-type(2) > div:nth-of-type(1) > a';
const p2 = 'html > body > div:nth-of-type(1) > div:nth-of-type(2) > div:nth-of-type(1) > div:nth-of-type(5) > div:nth-of-type(5) > div:nth-of-type(3) > div > div:nth-of-type(2) > div > div:nth-of-type(1) > div > div:nth-of-type(2) > div > div:nth-of-type(2) > div:nth-of-type(1) > a';

// Remove Purchase Order Parent without class from each Product Name by common calculation C:
const pp1 = selector.removeParent(orderParent, p1);
const pp2 = selector.removeParent(orderParent, p2);

assert.strictEqual(pp1, 'div:nth-of-type(3) > div > div:nth-of-type(2) > div > div:nth-of-type(1) > div > div:nth-of-type(1) > div > div:nth-of-type(2) > div:nth-of-type(1) > a');
assert.strictEqual(pp2, 'div:nth-of-type(3) > div > div:nth-of-type(2) > div > div:nth-of-type(1) > div > div:nth-of-type(2) > div > div:nth-of-type(2) > div:nth-of-type(1) > a');

// Remove Purchase Product Parent without class from each Product Name by common calculation C:

const pro1 = selector.removeParent(productParent, pp1);
const pro2 = selector.removeParent(productParent, pp2);

assert.strictEqual(pro1, 'div:nth-of-type(2) > div:nth-of-type(1) > a');
assert.strictEqual(pro2, 'div:nth-of-type(2) > div:nth-of-type(1) > a');

console.log(selector.removeParent(productParent, 'div:nth-of-type(3) > div > div:nth-of-type(2) > div > div:nth-of-type(1) > div > div > div > div:nth-of-type(2) > div:nth-of-type(1) > a'));
