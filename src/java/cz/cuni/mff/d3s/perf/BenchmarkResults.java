/*
 * Copyright 2014 Charles University in Prague
 * Copyright 2014 Vojtech Horky
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

/** Benchmark results storage. */
public interface BenchmarkResults {
    /** Get list of collected events.
     *
     * @return Event names.
     */
    String[] getEventNames();
    
    /** Get data of individual benchmark iterations.
     *
     * @return Actual benchmark scores.
     */
    List<long[]> getData();
}
