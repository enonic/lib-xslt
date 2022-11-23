/**
 * XSLT template related functions.
 *
 * @example
 * var xsltLib = require('/lib/xslt');
 *
 * @module xslt
 */

var service = __.newBean('com.enonic.lib.xslt.XsltService');

/**
 * This function renders a view using XSLT. The model is automatically transformed to XML.
 *
 * @example-ref examples/xslt/render.js
 *
 * @param view Location of the view. Use `resolve(..)` to resolve a view.
 * @param {object} model Model that is passed to the view.
 * @returns {string} The rendered output.
 */
exports.render = function (view, model) {
    var processor = service.newProcessor();
    processor.view = view;
    processor.model = __.toScriptValue(model);
    return processor.process();
};
