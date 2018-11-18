const React    = require('react');
const ReactDOM = require('react-dom');
const Rx       = require('rxjs');
const _getType = function(type){return module.exports[type]};
module.exports = {React:React, ReactDOM:ReactDOM, Rx:Rx, getType:_getType};
