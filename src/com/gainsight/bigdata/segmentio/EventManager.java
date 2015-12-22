package com.gainsight.bigdata.segmentio;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.gainsight.http.Header;
import com.gainsight.http.WebAction;
import com.gainsight.testdriver.Log;

import org.apache.http.HttpEntity;
import us.monoid.web.Resty;

public class EventManager {
	private static final int NUMBER_OF_THREADS = 10;

	public static int submitEvents(List<Header> headers, String url,
			List<HttpEntity> payloads) throws ExecutionException {
		ExecutorService executor = Executors
				.newFixedThreadPool(NUMBER_OF_THREADS);
		List<Future<Boolean>> list = new ArrayList<Future<Boolean>>();
		for (HttpEntity payload : payloads) {
			Callable<Boolean> worker = new EventSubmitter(headers, url, payload);
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
			executor.shutdown();
			executor.awaitTermination(180L, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Log.info("Error while waiting for threads to complete event submissions (timeout is 180 seconds)");
			e.printStackTrace();
		}
		return successfulEvents;
	}
}
