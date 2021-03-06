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

import java.io.PrintStream;

/** Helper class for printing measurement results. */
public final class BenchmarkResultsPrinter {
    /** Prevent instantiation. */
    private BenchmarkResultsPrinter() {}
    
    /** Prints results as CSV with tab separator and header.
     *
     * @param results Benchmark results.
     * @param stream Stream to print to.
     */
    public static void toCsv(final BenchmarkResults results, final PrintStream stream) {
        toCsv(results, stream, "\t", true);
    }

    /** Prints results as CSV.
     *
     * @param results Benchmark results.
     * @param stream Stream to print to.
     * @param separator Field separator.
     * @param printHeader Whether to print header fields.
     */
    public static void toCsv(final BenchmarkResults results, final PrintStream stream,
            final String separator, final boolean printHeader) {
        if (printHeader) {
            String[] eventNames = results.getEventNames();
            for (int i = 0; i < eventNames.length; i++) {
                if (i > 0) {
                    stream.append(separator);
                }
                stream.append(eventNames[i]);
            }
            stream.append("\n");
        }

        for (long[] row : results.getData()) {
            for (int i = 0; i < row.length; i++) {
                if (i > 0) {
                    stream.append(separator);
                }
                stream.append(String.format("%d", row[i]));
            }
            stream.append("\n");
        }
    }

    /** Print results as a table with adaptive column widths.
     *
     * @param results Benchmark results.
     * @param stream Stream to print to.
     */
    public static void table(final BenchmarkResults results, final PrintStream stream) {
        table(results, stream, getOptimalColumnWidths(results), true);
    }

    /** Print results as a table.
     *
     * @param results Benchmark results.
     * @param stream Stream to print to.
     * @param columnWidths Column widths (recycled if shorter than needed).
     * @param printHeader Whether to print header fields.
     */
    public static void table(final BenchmarkResults results, final PrintStream stream,
            final int[] columnWidths, final boolean printHeader) {
        if (printHeader) {
            int index = 0;
            for (String name : results.getEventNames()) {
                int width = columnWidths[index % columnWidths.length];
                String format = String.format("%%%ds", width);
                String nameBeginning = name.substring(0, Math.min(name.length(), width - 1));
                stream.append(String.format(format, nameBeginning));
                index++;
            }
            stream.append("\n");
        }

        String[] formats = new String[columnWidths.length];
        for (int i = 0; i < formats.length; i++) {
            formats[i] = String.format("%%%dd", columnWidths[i]);
        }

        for (long[] row : results.getData()) {
            int index = 0;
            for (long r : row) {
                stream.append(String.format(formats[index % formats.length], r));
                index++;
            }
            stream.append("\n");
        }
    }

    /** Computes optimal width for each column of results.
     *
     * <p>
     * Takes into account both biggest value and column header (event name).
     * 
     * @param results Benchmark results.
     * @return Array of optimal column widths (counting one character for separator).
     */
    public static int[] getOptimalColumnWidths(final BenchmarkResults results) {
        long maxValue = 0;
        for (long[] row : results.getData()) {
            for (long r : row) {
                if (r > maxValue) {
                    maxValue = r;
                }
            }
        }

        int minWidth = String.format("%d", maxValue).length();

        String[] names = results.getEventNames();

        int[] widths = new int[names.length];
        for (int i = 0; i < widths.length; i++) {
            widths[i] = Math.max(names[i].length(), minWidth) + 1;
        }

        return widths;
    }

}
