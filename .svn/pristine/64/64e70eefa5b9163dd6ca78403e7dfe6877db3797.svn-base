package org.gs4tr.termmanager.model.feature;

import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.gs4tr.foundation.modules.entities.model.StringConstants;

public class TmgrFeatureConstants {
    public static final String DEFAULT = "default";
    public static final String ENABLED_FEATURES_KEY = "enabled.feature";
    public static final String GRID_FEATURE = "grid";

    /*
     ****************************************************
     * Note: tomcat/bin/setenv.sh Command Example:
     * -Denabled.feature=enableUIFeature,someOtherFeature
     *
     * Expected features that can be enabled or disabled: {enableUIFeature(feature
     * for UI)}
     ****************************************************
     */

    public static String[] getEnabledFeatures() {
	String propVal = StringUtils.trimToEmpty(System.getProperty(ENABLED_FEATURES_KEY, DEFAULT));
	if (DEFAULT.equals(propVal)) {
	    return new String[] { propVal };
	}
	String[] parsedValues = propVal.split(StringConstants.COMMA);
	return Arrays.stream(parsedValues).filter(StringUtils::isNotBlank).map(StringUtils::trimToEmpty)
		.toArray(String[]::new);

    }

    public static boolean isEnabled(String featureName) {
	if (StringUtils.isBlank(featureName)) {
	    return false;
	}
	String[] enabledFeatures = getEnabledFeatures();
	return ArrayUtils.isNotEmpty(enabledFeatures) && ArrayUtils.contains(enabledFeatures, featureName);
    }
}
