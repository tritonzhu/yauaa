/*
 * Yet Another UserAgent Analyzer
 * Copyright (C) 2013-2018 Niels Basjes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.basjes.parse.useragent.hive;

import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.apache.hadoop.hive.ql.exec.Description;

/**
 * Hive UDF for parsing the UserAgent string.
 * An example statement
 * would be:
 * <pre>
 *   ADD JAR
 ADD JAR hdfs:///yauaa-hive-5.4-udf.jar;


 USING JAR 'hdfs:/plugins/yauaa-hive-5.4-udf.jar';

 SELECT ParseUserAgentDropPII('Mozilla/5.0 (X11\; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36');
 SELECT ParseUserAgentDropPII(useragent) from useragents;
 *  SELECT ParseUserAgentDropPII(useragent) FROM clickLogs a;
 * </pre>
 *
 *
 */

@Description(
    name = "ParseUserAgentDropPII",
    value = "_FUNC_(str) - Parses the UserAgent into all possible pieces but hides all PII problem fields.",
    extended = "Example:\n" +
        "> SELECT ParseUserAgentDropPII(useragent).DeviceClass, \n" +
        "         ParseUserAgentDropPII(useragent).OperatingsystemNameVersion, \n" +
        "         ParseUserAgentDropPII(useragent).AgentNameVersionMajor \n" +
        "  FROM   clickLogs;\n" +
        "+---------------+-----------------------------+------------------------+\n" +
        "|  deviceclass  | operatingsystemnameversion  | agentnameversionmajor  |\n" +
        "+---------------+-----------------------------+------------------------+\n" +
        "| Phone         | Android 6.0                 | Chrome 46              |\n" +
        "| Tablet        | Android 5.1                 | Chrome 40              |\n" +
        "| Desktop       | Linux Intel x86_64          | Chrome 59              |\n" +
        "| Game Console  | Windows 10.0                | Edge 13                |\n" +
        "+---------------+-----------------------------+------------------------+\n")

public class ParseUserAgentDropPII extends ParseUserAgent {
    @Override
    protected synchronized UserAgentAnalyzer constructAnalyzer() {
        super.constructAnalyzer();
        userAgentAnalyzer.dropPIIFields();
        return userAgentAnalyzer;
    }
}
