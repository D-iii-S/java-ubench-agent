/*
 * Copyright 2017 Charles University in Prague
 * Copyright 2017 Vojtech Horky
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cz.cuni.mff.d3s.perf;

import java.util.List;

import org.junit.*;

public class MeasurementTest {
    @Test
    public void commonEventMustBeAlwaysSupported() {
        Assert.assertTrue(Measurement.isEventSupported("SYS:wallclock-time"));
    }

    @Test
    public void nonsenseEventCannotBeSupported() {
        Assert.assertFalse(Measurement.isEventSupported("THIS:IS:COMPLETELY:NONSENSE:EVENT"));
    }

    @Test
    public void listingEventsWorks() {
        List<String> events = Measurement.getSupportedEvents();
        Assert.assertTrue("SYS:wallclock-time must be present", events.contains("SYS:wallclock-time"));
    }

    @Test
    public void listingPapiEventsWorks() {
        Assume.assumeTrue(Measurement.isEventSupported("PAPI_TOT_INS"));

        List<String> events = Measurement.getSupportedEvents();
        Assert.assertTrue("PAPI_TOT_INS must be present", events.contains("PAPI:PAPI_TOT_INS"));
    }
}
