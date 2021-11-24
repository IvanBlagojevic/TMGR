package org.gs4tr.termmanager.webmvc.security;

import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.modules.entities.model.StringConstants;
import org.gs4tr.foundation.modules.webmvc.httpsecurity.configurers.securityHeaders.CspPolicyProvider;
import org.springframework.beans.factory.annotation.Value;

public class DefaultCspConfigurationProvider implements CspPolicyProvider {

    @Value("${csp.contentTemplate.child.src:child-src 'self';}")
    private String _childSrc;

    @Value("${csp.contentTemplate.connect.src:connect-src 'self'%s;}")
    private String _connectSrc;

    private String _cspPolicyTemplate;

    @Value("${csp.contentTemplate.default.src:default-src 'none';}")
    private String _defaultSrc;

    @Value("${csp.contentTemplate.font.src:font-src 'self';}")
    private String _fontSrc;

    @Value("${csp.contentTemplate.form.action:form-action 'self';}")
    private String _formAction;

    @Value("${csp.contentTemplate.frame.ancestors:frame-ancestors 'self'%s;}")
    private String _frameAncestors;

    @Value("${csp.contentTemplate.frame.src:frame-src 'self'%s;}")
    private String _frameSrc;

    @Value("${csp.contentTemplate.img.src:img-src 'self' data: blob:;}")
    private String _imgSrc;

    @Value("${csp.contentTemplate.media.src:media-src 'self';}")
    private String _mediaSrc;

    @Value("${csp.contentTemplate.object.src:object-src 'self';}")
    private String _objectSrc;

    @Value("${csp.contentTemplate.script.src:script-src 'self' 'unsafe-eval' 'nonce-%s';}")
    private String _scriptSrc;

    @Value("${csp.contentTemplate.style.src:style-src 'self' 'unsafe-inline';}")
    private String _styleSrc;

    @Value("${security.trusted.internal.app.domains:#{null}}")
    private String _trustedInternalDomains;

    @Override
    public String getCspPolicy(String nonce) {
	if (_cspPolicyTemplate == null) {
	    buildCspPolicy();
	}
	return String.format(_cspPolicyTemplate, nonce);
    }

    private void buildCspPolicy() {
	StringBuilder builder = new StringBuilder();
	builder.append(getDefaultSrc());
	builder.append(getImgSrc());
	builder.append(getStyleSrc());
	builder.append(getFontSrc());
	builder.append(getMediaSrc());
	builder.append(getFormAction());
	builder.append(getChildSrc());
	builder.append(getObjectSrc());
	builder.append(getScriptSrc());
	builder.append(getFrameSrc());
	builder.append(getConnectSrc());
	builder.append(getFrameAncestors());
	_cspPolicyTemplate = builder.toString();
    }

    private String getChildSrc() {
	return _childSrc;
    }

    private String getConnectSrc() {
	return String.format(_connectSrc, getTrustedApplicationUrlsPart());
    }

    private String getDefaultSrc() {
	return _defaultSrc;
    }

    private String getFontSrc() {
	return _fontSrc;
    }

    private String getFormAction() {
	return _formAction;
    }

    private String getFrameAncestors() {
	return String.format(_frameAncestors, getTrustedApplicationUrlsPart());
    }

    private String getFrameSrc() {
	return String.format(_frameSrc, getTrustedApplicationUrlsPart());
    }

    private String getImgSrc() {
	return _imgSrc;
    }

    private String getMediaSrc() {
	return _mediaSrc;
    }

    private String getObjectSrc() {
	return _objectSrc;
    }

    private String getScriptSrc() {
	return _scriptSrc;
    }

    private String getStyleSrc() {
	return _styleSrc;
    }

    private String getTrustedApplicationUrlsPart() {
	String urlsJoined = StringConstants.EMPTY;
	if (StringUtils.isNotEmpty(_trustedInternalDomains)) {
	    String urls = _trustedInternalDomains.trim().replace(",", " ");
	    urlsJoined = StringConstants.SPACE.concat(urls);
	}
	return urlsJoined;
    }
}
