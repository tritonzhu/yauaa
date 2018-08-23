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

package nl.basjes.parse.useragent.analyze;

import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

import static nl.basjes.parse.useragent.UserAgent.DROP_PII;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

// CHECKSTYLE.OFF: ParenPad
public class TestBuilderPII {

    @Test
    public void getAllFields(){
        List<String> fieldsNoPII = UserAgentAnalyzer
            .newBuilder()
            .dropPIIFields()
            .build()
            .getAllPossibleFieldNamesSorted();

        assertTrue(fieldsNoPII.contains("DeviceClass"));
        assertTrue(fieldsNoPII.contains("AgentName"));
        assertFalse(fieldsNoPII.contains("AgentNameVersion"));
    }

    @Test
    public void testLimitedFields() {
        UserAgentAnalyzer userAgentAnalyzer =
            UserAgentAnalyzer
                .newBuilder()
                .withoutCache()
                .withAllFields()
                .withField("DeviceClass")
                .withField("AgentNameVersionMajor")
                .withUserAgentMaxLength(1234)
                .dropPIIFields()
                .build();

        assertEquals(1234, userAgentAnalyzer.getUserAgentMaxLength());

        UserAgent parsedAgent = userAgentAnalyzer.parse("Mozilla/5.0 (Linux; Android 7.0; Nexus 6 Build/NBD90Z) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.124 Mobile Safari/537.36");

        // The requested fields
        assertEquals("Phone",                    parsedAgent.getValue("DeviceClass"              )); // Phone
        assertEquals("Chrome 53",                parsedAgent.getValue("AgentNameVersionMajor"    )); // Chrome 53

        // The fields that are internally needed to build the requested fields
        assertEquals("Chrome",                   parsedAgent.getValue("AgentName"                )); // Chrome
        assertEquals("??",                       parsedAgent.getValue("AgentVersion"             )); // 53.0.2785.124
        assertEquals("53",                       parsedAgent.getValue("AgentVersionMajor"        )); // 53
        assertEquals("Unknown",                  parsedAgent.getValue("AgentNameVersion"         )); // Chrome 53.0.2785.124

        // The rest must be at confidence -1 (i.e. no rules fired)
        assertEquals(-1, (long)parsedAgent.getConfidence("DeviceName"                   )); // Nexus 6
        assertEquals(-1, (long)parsedAgent.getConfidence("DeviceBrand"                  )); // Google
        assertEquals(-1, (long)parsedAgent.getConfidence("OperatingSystemClass"         )); // Mobile
        assertEquals(-1, (long)parsedAgent.getConfidence("OperatingSystemName"          )); // Android
        assertEquals(-1, (long)parsedAgent.getConfidence("OperatingSystemVersion"       )); // 7.0
        assertEquals(-1, (long)parsedAgent.getConfidence("OperatingSystemNameVersion"   )); // Android 7.0
        assertEquals(-1, (long)parsedAgent.getConfidence("OperatingSystemVersionBuild"  )); // NBD90Z
        assertEquals(-1, (long)parsedAgent.getConfidence("LayoutEngineClass"            )); // Browser
        assertEquals(-1, (long)parsedAgent.getConfidence("LayoutEngineName"             )); // Blink
        assertEquals(-1, (long)parsedAgent.getConfidence("LayoutEngineVersion"          )); // 53.0
        assertEquals(-1, (long)parsedAgent.getConfidence("LayoutEngineVersionMajor"     )); // 53
        assertEquals(-1, (long)parsedAgent.getConfidence("LayoutEngineNameVersion"      )); // Blink 53.0
        assertEquals(-1, (long)parsedAgent.getConfidence("LayoutEngineNameVersionMajor" )); // Blink 53
        assertEquals(-1, (long)parsedAgent.getConfidence("AgentClass"                   )); // Browser
    }


    @Test
    public void testAllPIISafeFields() {
        UserAgentAnalyzer userAgentAnalyzer =
            UserAgentAnalyzer
                .newBuilder()
                .withoutCache()
                .withAllFields()
                .withField(DROP_PII) // This way we ask for all PII safe fields.
                .withUserAgentMaxLength(1234)
//                .dropPIIFields()
                .build();

        assertEquals(1234, userAgentAnalyzer.getUserAgentMaxLength());

        UserAgent parsedAgent = userAgentAnalyzer.parse("Mozilla/5.0 (Linux; Android 7.0; Nexus 6 Build/NBD90Z) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.124 Mobile Safari/537.36");

        // The requested fields
        assertEquals("Phone",               parsedAgent.getValue("DeviceClass"                     )); // Phone
        assertEquals("Chrome",              parsedAgent.getValue("AgentName"                       )); // Chrome
        assertEquals("53",                  parsedAgent.getValue("AgentVersionMajor"               )); // 53
        assertEquals("Chrome 53",           parsedAgent.getValue("AgentNameVersionMajor"           )); // Chrome 53

        assertEquals("Google Nexus 6",      parsedAgent.getValue("DeviceName"                      )); // Google Nexus 6
        assertEquals("Google",              parsedAgent.getValue("DeviceBrand"                     )); // Google
        assertEquals("Mobile",              parsedAgent.getValue("OperatingSystemClass"            )); // Mobile
        assertEquals("Android",             parsedAgent.getValue("OperatingSystemName"             )); // Android
        assertEquals("7.0",                 parsedAgent.getValue("OperatingSystemVersion"          )); // 7.0
        assertEquals("Android 7.0",         parsedAgent.getValue("OperatingSystemNameVersion"      )); // Android 7.0
        assertEquals("Browser",             parsedAgent.getValue("LayoutEngineClass"               )); // Browser
        assertEquals("Blink",               parsedAgent.getValue("LayoutEngineName"                )); // Blink
        assertEquals("53",                  parsedAgent.getValue("LayoutEngineVersionMajor"        )); // 53
        assertEquals("Blink 53",            parsedAgent.getValue("LayoutEngineNameVersionMajor"    )); // Blink 53
        assertEquals("Browser",             parsedAgent.getValue("AgentClass"                      )); // Browser

        // NOT PII Safe
        // The fields that are internally needed to build the requested fields, but are not PII safe so they must be empty
        assertEquals("??",                  parsedAgent.getValue("AgentVersion"                    )); // 53.0.2785.124
        assertEquals("Unknown",             parsedAgent.getValue("AgentNameVersion"                )); // Chrome 53.0.2785.124
        assertEquals("??",                  parsedAgent.getValue("LayoutEngineVersion"             )); // 53.0
        assertEquals("Unknown",             parsedAgent.getValue("LayoutEngineNameVersion"         )); // Blink 53.0
        assertEquals("Unknown",             parsedAgent.getValue("OperatingSystemVersionBuild"     )); // NBD90Z

        // Check the special field we actually do not want to see.
        assertEquals(Long.valueOf(-1),      parsedAgent.getConfidence(DROP_PII));
        assertEquals("Unknown", parsedAgent.getValue(DROP_PII));

    }

    @Rule
    public final ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void testAskingForImpossibleField() {
        expectedEx.expect(InvalidParserConfigurationException.class);
        expectedEx.expectMessage("We cannot provide these fields:[FirstNonexistentField, SecondNonexistentField]");

        UserAgentAnalyzer
            .newBuilder()
            .withoutCache()
            .hideMatcherLoadStats()
            .delayInitialization()
            .withField("FirstNonexistentField")
            .withField("DeviceClass")
            .withField("SecondNonexistentField")
            .dropPIIFields()
            .build();
    }


}
