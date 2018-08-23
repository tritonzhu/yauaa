/*
 * Yet Another UserAgent Analyzer
 * Copyright (C) 2013-2018 Niels Basjes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.basjes.parse.useragent.pii;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import static nl.basjes.parse.useragent.UserAgent.DROP_PII;
import static nl.basjes.parse.useragent.UserAgent.SYNTAX_ERROR;

public final class PIIFieldList {
    private PIIFieldList() {}

    // Important here is this is what I believe to be PII safe, like I said: I am NOT a lawyer so I may very well be wrong about this.

    private static final String[] FIELDS_CONSIDERED_TO_BE_PII_SAFE = {
        // Basic idea:
        // If a field is likely to identify a consumer device very uniquely it is removed.
        // Things specific to robots and hackers and such are always retained.
        // For most things we kick the exact build versions and minor versions and retain only the major version numbers

        "DeviceClass",
        "DeviceName",
        "DeviceBrand",
        "DeviceCpu",
        "DeviceCpuBits",
        // "DeviceFirmwareVersion",                 // Very specific
        "DeviceVersion",
        "OperatingSystemClass",
        "OperatingSystemName",
        "OperatingSystemVersion",
        "OperatingSystemNameVersion",
        // "OperatingSystemVersionBuild",           // Very specific
        "LayoutEngineClass",
        "LayoutEngineName",
        // "LayoutEngineVersion",                   // A bit too specific
        "LayoutEngineVersionMajor",
        // "LayoutEngineNameVersion",               // A bit too specific
        "LayoutEngineNameVersionMajor",
        // "LayoutEngineBuild",                     // Too specific
        "AgentClass",
        "AgentName",
        // "AgentVersion",                          // Too specific
        "AgentVersionMajor",
        // "AgentNameVersion",                      // Too specific
        "AgentNameVersionMajor",
        // "AgentBuild",                            // Too specific
        "AgentLanguage",
        "AgentLanguageCode",
        "AgentInformationEmail",                    // NOT PII Because I have only seen this on Robots/Spiders/Crawlers.
        "AgentInformationUrl",                      // NOT PII Because I have only seen this on Robots/Spiders/Crawlers.
        "AgentSecurity",
        // "AgentUuid",                             // Very PII as this is used by spammers to inject a unique and stable "browser id"
        "WebviewAppName",
        // "WebviewAppVersion",                     // Too specific
        "WebviewAppVersionMajor",
        "WebviewAppNameVersionMajor",
        "FacebookCarrier",
        "FacebookDeviceClass",
        "FacebookDeviceName",
        "FacebookDeviceVersion",
        // "FacebookFBOP",                          // I do not fully understand this field --> unsafe for now
        // "FacebookFBSS",                          // I do not fully understand this field --> unsafe for now
        "FacebookOperatingSystemName",
        "FacebookOperatingSystemVersion",
        "Anonymized",
        "HackerAttackVector",
        "HackerToolkit",
        // "KoboAffiliate",                         // Too specific
        // "KoboPlatformId",                        // Too specific
        // "IECompatibilityVersion",                // Too specific
        "IECompatibilityVersionMajor",
        // "IECompatibilityNameVersion",            // Too specific
        "IECompatibilityNameVersionMajor",
        "Carrier",
        // "GSAInstallationID",                     // Way too specific

        SYNTAX_ERROR,                               // System variable: true/false
        DROP_PII,                         // System variable: Drop or do not drop PII unsafe fields
    };

    private static final ImmutableList<String> FIELDS_CONSIDERED_TO_BE_PII_SAFE_LIST =
        new ImmutableList.Builder<String>().add(FIELDS_CONSIDERED_TO_BE_PII_SAFE).build();

    public static boolean isDropPIIRequestField(String fieldName) {
        return DROP_PII.equals(fieldName);
    }

    public static ImmutableList<String> getSafeFields() {
        return FIELDS_CONSIDERED_TO_BE_PII_SAFE_LIST;
    }

    public static boolean isPIISafeField(String fieldName) {
        return FIELDS_CONSIDERED_TO_BE_PII_SAFE_LIST.contains(fieldName);
    }

    public static boolean areAllPIISafeFields(Collection<String> fieldNames) {
        return FIELDS_CONSIDERED_TO_BE_PII_SAFE_LIST.containsAll(fieldNames);
    }
}
