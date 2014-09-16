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

#define  _POSIX_C_SOURCE 200809L

#include "ubench.h"

#pragma warning(push, 0)
#include <stdlib.h>
#include <stddef.h>
#include <time.h>
#include <string.h>
#pragma warning(pop)

#ifdef HAS_PAPI
/*
 * Include <sys/types.h> and define __USE_BSD because of caddr_t.
 * This is not needed if we would compile with GCC and -std=c99.
 */
#define __USE_BSD
#include <sys/types.h>
#include <papi.h>
#endif

#ifdef HAS_QUERY_PERFORMANCE_COUNTER
#pragma warning(push, 0)
#include <windows.h>
#pragma warning(pop)
#endif

static void store_wallclock(timestamp_t *ts) {
#ifdef HAS_TIMESPEC
	clock_gettime(CLOCK_MONOTONIC, ts);
#elif defined(HAS_QUERY_PERFORMANCE_COUNTER)
	QueryPerformanceCounter(ts);
#else
	*ts = -1;
#endif
}

void ubench_measure_start(const benchmark_configuration_t const *config,
		ubench_events_snapshot_t *snapshot) {
#ifdef HAS_GETRUSAGE
	if ((config->used_backends & UBENCH_EVENT_BACKEND_RESOURCE_USAGE) > 0) {
		getrusage(RUSAGE_SELF, &(snapshot->resource_usage));
	}
#endif

	if ((config->used_backends & UBENCH_EVENT_BACKEND_JVM_COMPILATIONS) > 0) {
		snapshot->compilations = ubench_atomic_get(&counter_compilation_total);
	}

	snapshot->garbage_collections = ubench_atomic_get(&counter_gc_total);

#ifdef HAS_PAPI
	if ((config->used_backends & UBENCH_EVENT_BACKEND_PAPI) > 0) {
		// TODO: check for errors
		snapshot->papi_rc1 = PAPI_start_counters((int *) config->used_papi_events, config->used_papi_events_count);
		snapshot->papi_rc2 = PAPI_read_counters(snapshot->papi_events, config->used_papi_events_count);
	}
#endif

	if ((config->used_backends & UBENCH_EVENT_BACKEND_SYS_WALLCLOCK) > 0) {
		store_wallclock(&(snapshot->timestamp));
	}
}

void ubench_measure_stop(const benchmark_configuration_t const *config,
		ubench_events_snapshot_t *snapshot) {
	if ((config->used_backends & UBENCH_EVENT_BACKEND_SYS_WALLCLOCK) > 0) {
		store_wallclock(&(snapshot->timestamp));
	}

#ifdef HAS_PAPI
	// TODO: check for errors
	if ((config->used_backends & UBENCH_EVENT_BACKEND_PAPI) > 0) {
		snapshot->papi_rc1 = PAPI_stop_counters(snapshot->papi_events, config->used_papi_events_count);
	}
#endif

	if ((config->used_backends & UBENCH_EVENT_BACKEND_JVM_COMPILATIONS) > 0) {
		snapshot->compilations = ubench_atomic_get(&counter_compilation_total);
	}

	snapshot->garbage_collections = ubench_atomic_get(&counter_gc_total);

#ifdef HAS_GETRUSAGE
	if ((config->used_backends & UBENCH_EVENT_BACKEND_RESOURCE_USAGE) > 0) {
		getrusage(RUSAGE_SELF, &(snapshot->resource_usage));
	}
#endif
}