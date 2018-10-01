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

package nl.basjes.parse.useragent.beam;

import nl.basjes.parse.useragent.analyze.InvalidParserConfigurationException;
import nl.basjes.parse.useragent.annotate.YauaaField;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class TestUserAgentAnalysisDoFnInlineDropPII implements Serializable {

    @Rule
    public final transient TestPipeline pipeline = TestPipeline.create();

    @Rule
    public final transient ExpectedException expectedEx = ExpectedException.none();

    private List<String> useragents = Arrays.asList(
        "Mozilla/5.0 (X11; Linux x86_64) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) " +
            "Chrome/48.0.2564.82 Safari/537.36",

        "Mozilla/5.0 (Linux; Android 7.0; Nexus 6 Build/NBD90Z) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) " +
            "Chrome/53.0.2785.124 Mobile Safari/537.36"
    );

    @Test
    public void testInlineDefinitionDropPII() {
        // Apply Create, passing the list and the coder, to create the PCollection.
        PCollection<String> input = pipeline.apply(Create.of(useragents)).setCoder(StringUtf8Coder.of());

        PCollection<TestRecord> testRecords = input
            .apply("Create testrecords from input",
                ParDo.of(new DoFn<String, TestRecord>() {
                    @ProcessElement
                    public void processElement(ProcessContext c) {
                        c.output(new TestRecord(c.element()));
                    }
                }));

        PCollection<TestRecord> filledTestRecords = testRecords
            .apply("Extract Elements from Useragent",
                ParDo.of(new UserAgentAnalysisDoFn<TestRecord>() {
                    public String getUserAgentString(TestRecord record) {
                        return record.useragent;
                    }

                    @Override
                    public boolean dropPIIFields() {
                        return true;
                    }

                    @YauaaField("DeviceClass")
                    public void setDeviceClass(TestRecord record, String value) {
                        record.deviceClass = value;
                    }

                    @YauaaField("AgentNameVersionMajor")
                    public void setAgentNameVersionMajor(TestRecord record, String value) {
                        record.agentNameVersionMajor = value;
                    }
                }));

        TestRecord expectedRecord1 = new TestRecord(useragents.get(0));
        expectedRecord1.deviceClass = "Desktop";
        expectedRecord1.agentNameVersion = null;
        expectedRecord1.agentNameVersionMajor = "Chrome 48";

        TestRecord expectedRecord2 = new TestRecord(useragents.get(1));
        expectedRecord2.deviceClass = "Phone";
        expectedRecord2.agentNameVersion = null;
        expectedRecord2.agentNameVersionMajor = "Chrome 53";

        PAssert.that(filledTestRecords).containsInAnyOrder(expectedRecord1, expectedRecord2);

        pipeline.run().waitUntilFinish();
    }

    @Test
    public void testDeniedPIIField() throws Throwable {
        expectedEx.expect(InvalidParserConfigurationException.class);
        expectedEx.expectMessage("We cannot provide these fields:[AgentNameVersion]");
        try {
            // Apply Create, passing the list and the coder, to create the PCollection.
            PCollection<String> input = pipeline.apply(Create.of(useragents)).setCoder(StringUtf8Coder.of());

            PCollection<TestRecord> testRecords = input
                .apply("Create testrecords from input",
                    ParDo.of(new DoFn<String, TestRecord>() {
                        @ProcessElement
                        public void processElement(ProcessContext c) {
                            c.output(new TestRecord(c.element()));
                        }
                    }));

            PCollection<TestRecord> filledTestRecords = testRecords
                .apply("Extract Elements from Useragent",
                    ParDo.of(new UserAgentAnalysisDoFn<TestRecord>() {
                        public String getUserAgentString(TestRecord record) {
                            return record.useragent;
                        }

                        @Override
                        public boolean dropPIIFields() {
                            return true;
                        }

                        @YauaaField("DeviceClass")
                        public void setDeviceClass(TestRecord record, String value) {
                            record.deviceClass = value;
                        }

                        // This field is not allowed due to the PII rules.
                        @YauaaField("AgentNameVersion")
                        public void setAgentNameVersion(TestRecord record, String value) {
                            record.agentNameVersion = value;
                        }
                    }));

            pipeline.run().waitUntilFinish();
        } catch (Exception e) {

// The actual class thrown here turns out to be
//   org.apache.beam.repackaged.beam_runners_direct_java.com.google.common.util.concurrent.UncheckedExecutionException
// which is a repackaged version of a Guava class.
// which is the same class that was thrown in Beam 2.4.0 but in a DIFFERENT package.
// Turns out Apache Beam 2.5.0 made a total mess of this because they repackaged this class in at least 5 package names
// in the beam-runners-direct-java and also in the beam-sdks-java-core.
// So to reduce the mess at this end I'm simply taking the underlying cause class ( org.apache.beam.sdk.util.UserCodeException )
// and pulling the real exception my code throws to do the check.
            throw e.getCause().getCause();
        }
    }

}
