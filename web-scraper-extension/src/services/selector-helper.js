/**
 * no any depend selector calculate helper method
 * used for extension and tests
 */

/**
 * get tag
 * @param element the element
 * @returns {string} the tag
 */
function getTag(element) {
  const e = (element || '').trim();
  if (e.indexOf(':') > 0) {
    return e.split(':').shift().trim();
  }
  if (e.indexOf('.') > 0) {
    return e.split('.').shift().trim();
  }
  return e;
}

/**
 * get common parent
 * @param p1 path 1
 * @param p2 path 2
 * @param getI18T get i18t
 */
function getCommonParent(p1, p2, getI18T) {
  const parts1 = p1.split('>');
  const parts2 = p2.split('>');
  const minLength = Math.min(parts1.length, parts2.length);
  const commonParts = [];

  if (parts1.length !== parts2.length) {
    throw new Error(getI18T()('editor.differentType'));
  }
  for (let i = 0; i < minLength; i++) {
    const tag1 = getTag(parts1[i]);
    const tag2 = getTag(parts2[i]);
    if (tag1 !== tag2) {
      throw new Error(getI18T()('editor.differentType'));
    }
  }
  for (let i = 0; i < minLength; i++) {
    if (parts1[i] === parts2[i]) {
      commonParts.push(parts1[i]);
    } else {
      commonParts.push(getTag((parts1[i] || parts2[i])));
      break;
    }
  }
  return commonParts.map((p) => p.trim()).join(' > ');
}

/**
 * get p1 parent (common part + p1 part, common part + p2 part)
 * @param p1 the path 1
 * @param p2 the path 2
 */
function getPathParent(p1, p2) {
  const parts1 = p1.split('>');
  const parts2 = p2.split('>');
  const minLength = Math.min(parts1.length, parts2.length);
  const commonParts = [];
  const results = [];
  for (let i = 0; i < minLength; i++) {
    if (parts1[i] === parts2[i]) {
      commonParts.push(parts1[i]);
    } else {
      results[0] = commonParts.concat([parts1[i]]).map((p) => p.trim()).join(' > ');
      results[1] = commonParts.concat([parts2[i]]).map((p) => p.trim()).join(' > ');
      break;
    }
  }
  return results;
}

/**
 * remove parent
 * @param parent the parent path
 * @param path the current path
 */
function removeParent(parent, path) {
  const parentParts = parent.split('>');
  const parts = path.split('>');
  for (let i = 0; i < parentParts.length; i++) {
    const tag1 = getTag(parts[0]);
    const tag2 = getTag(parentParts[i]);
    if (tag1 === tag2) {
      parts.shift();
    }
  }
  return parts.map((p) => p.trim()).join(' > ');
}

/**
 * get common class
 * @param classes the classes
 * @return {string}
 */
function getCommonClass(classes) {
  if (!classes || classes.length <= 0) {
    return '';
  }
  let common = (classes[0] || '').split('.');
  for (let i = 1; i < classes.length; i++) {
    const parts = (classes[i] || '').split('.');
    const newCommon = [];
    for (let ii = 0; ii < common.length; ii++) {
      if (common[ii] === parts[ii]) {
        newCommon.push(common[ii]);
      }
    }
    common = newCommon;
  }
  return common.join('.');
}

/**
 * Remove different and additional ‘:nth-of-type()’ array number in pair of selectors
 * @param p1 the path 1
 * @param p2 the path 2
 * @return {string}
 */
function removeDifferentAndAdditional(p1, p2) {
  const parts1 = p1.split('>');
  const parts2 = p2.split('>');
  // part2 length should = part2 length
  for (let i = 0; i < parts1.length; i++) {
    const tag1 = getTag(parts1[i]);
    const tag2 = getTag(parts2[i]);
    if (tag1 === tag2 && parts1[i] !== parts2[i]) {
      // Remove different and additional `:nth-of-type()` array number in pair of selectors
      parts1[i] = tag1;
      parts2[i] = tag2;
    }
  }
  return parts2.map((p) => p.trim()).join(' > ');
}

module.exports = {
  getCommonParent, getCommonClass, getPathParent, removeDifferentAndAdditional, removeParent,
};
