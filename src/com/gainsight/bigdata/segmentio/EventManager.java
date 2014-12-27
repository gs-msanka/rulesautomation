package com.gainsight.bigdata.segmentio;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.gainsight.testdriver.Log;

import us.monoid.web.Resty;

public class EventManager {
	private static final int NUMBER_OF_THREADS = 10;

	public static int submitEvents(Resty resty, String url,
			List<String> payloads) throws ExecutionException {
		ExecutorService executor = Executors
				.newFixedThreadPool(NUMBER_OF_THREADS);
		List<Future<Boolean>> list = new ArrayList<Future<Boolean>>();
		for (String payload : payloads) {
			Callable<Boolean> worker = new EventSubmitter(resty, url, payload);
			Future<Boolean> submit = executor.submit(worker);
			list.add(submit);
		}
		int successfulEvents = 0;
		for (Future<Boolean> future : list) {
			try {
				if (future.get()) {
					successfulEvents++;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			executor.awaitTermination(180L, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Log.info("Error while waiting for threads to complete event submissions (timeout is 180 seconds)");
			e.printStackTrace();
		}
		return successfulEvents;
	}
}
