const fakeI18N = () => (key) => key
const selector = require('../src/services/selector-helper')
const assert = require('assert')

const p1 = 'html > body > div:nth-of-type(1) > div:nth-of-type(2) > div:nth-of-type(1) > div:nth-of-type(5) > div:nth-of-type(2) > div:nth-of-type(1) > div > div > div > div:nth-of-type(2) > div:nth-of-type(2) > ul > a'
const p2 = 'html > body > div:nth-of-type(1) > div:nth-of-type(2) > div:nth-of-type(1) > div:nth-of-type(5) > div:nth-of-type(3) > div:nth-of-type(1) > div > div > div > div:nth-of-type(2) > div:nth-of-type(2) > ul > a'


assert.strictEqual(selector.removeDifferentAndAdditional(p1, p2), 'html > body > div:nth-of-type(1) > div:nth-of-type(2) > div:nth-of-type(1) > div:nth-of-type(5) > div > div:nth-of-type(1) > div > div > div > div:nth-of-type(2) > div:nth-of-type(2) > ul > a')