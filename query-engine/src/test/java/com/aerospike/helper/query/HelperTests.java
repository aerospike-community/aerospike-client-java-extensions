package com.aerospike.helper.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;

import com.aerospike.client.AerospikeException;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.ResultCode;
import com.aerospike.client.query.IndexCollectionType;
import com.aerospike.client.query.IndexType;
import com.aerospike.client.task.IndexTask;

//@RunWith(Parameterized.class)
public abstract class HelperTests extends AerospikeAwareTests {

	// These values are added to Lists and Maps to avoid single item collections.
	// Tests should ignore them for assertion purposes
	protected static final long skipLongValue = Long.MAX_VALUE;
	protected static final String skipColorValue = "SKIP_THIS_COLOR";

	protected int[] ages = new int[]{25,26,27,28,29};
	protected String[] colours = new String[]{"blue","red","yellow","green","orange"};
	protected String[] animals = new String[]{"cat","dog","mouse","snake","lion"};

	protected String geoSet = "geo-set", geoBinName = "querygeobin";
	protected String specialCharBin = "scBin";
	private static final String keyPrefix = "querykey";

	protected Map<Integer, Long> recordsWithAgeCounts;
	protected Map<String, Long> recordsWithColourCounts;
	protected Map<String, Long> recordsWithAnimalCounts;
	protected Map<Long, Long> recordsModTenCounts;

	@Before
	public void setUp() {
		super.setUp();
		int i = 0;
		Key key = new Key(TestQueryEngine.NAMESPACE, TestQueryEngine.SET_NAME, "selector-test:"+ 10);

		initializeMaps();
		for (int x = 1; x <= TestQueryEngine.RECORD_COUNT; x++){
			Map<Long, String>ageColorMap = new HashMap<>();
			ageColorMap.put((long) ages[i], colours[i]);
			ageColorMap.put(skipLongValue, skipColorValue);

			Map<String, Long>colorAgeMap = new HashMap<>();
			colorAgeMap.put(colours[i], (long) ages[i]);
			colorAgeMap.put(skipColorValue, skipLongValue);

			List<String>colorList = new ArrayList<>();
			colorList.add(colours[i]);
			colorList.add(skipColorValue);

			List<Long>longList = new ArrayList<>();
			longList.add((long)ages[i]);
			longList.add(skipLongValue);

			key = new Key(TestQueryEngine.NAMESPACE, TestQueryEngine.SET_NAME, "selector-test:"+ x);
			Bin name = new Bin("name", "name:" + x);
			Bin age = new Bin("age", ages[i]);
			Bin colour = new Bin("color", colours[i]);
			Bin animal = new Bin("animal", animals[i]);
			Bin modTen = new Bin("modten", i % 10);

			Bin ageColorMapBin = new Bin("ageColorMap", ageColorMap);
			Bin colorAgeMapBin = new Bin("colorAgeMap", colorAgeMap);
			Bin colorListBin = new Bin("colorList", colorList);
			Bin longListBin = new Bin("longList", longList);

			this.client.put(null, key, name, age, colour, animal, modTen, ageColorMapBin,
					colorAgeMapBin, colorListBin, longListBin);
			// Add to our counts of records written for each bin value
			recordsWithAgeCounts.put(ages[i], recordsWithAgeCounts.get(ages[i]) + 1);
			recordsWithColourCounts.put(colours[i], recordsWithColourCounts.get(colours[i]) + 1);
			recordsWithAnimalCounts.put(animals[i], recordsWithAnimalCounts.get(animals[i]) + 1);
			recordsModTenCounts.put((long) (i % 10), recordsModTenCounts.get((long)(i % 10)) + 1);

			i++;
			if ( i == 5)
				i = 0;
		}

		//GEO Test setup
		for (i = 0; i < TestQueryEngine.RECORD_COUNT; i++) {
			double lng = -122 + (0.1 * i);
			double lat = 37.5 + (0.1 * i);
			key = new Key(TestQueryEngine.NAMESPACE, geoSet, keyPrefix + i);
			Bin bin = Bin.asGeoJSON(geoBinName, buildGeoValue(lng, lat));
			client.put(null, key, bin);
		}

	}

	@After
	public void tearDown() throws Exception {
		client.truncate(null, TestQueryEngine.NAMESPACE, TestQueryEngine.SET_NAME, null);
		client.truncate(null, TestQueryEngine.NAMESPACE, geoSet, null);
		super.tearDown();
	}

	private static String buildGeoValue(double lg, double lat) {
		StringBuilder ptsb = new StringBuilder();
		ptsb.append("{ \"type\": \"Point\", \"coordinates\": [");
		ptsb.append(String.valueOf(lg));
		ptsb.append(", ");
		ptsb.append(String.valueOf(lat));
		ptsb.append("] }");
		return ptsb.toString();
	}

	private void initializeMaps() {
		recordsWithAgeCounts = new HashMap<>();
		for (int age: ages) {
			recordsWithAgeCounts.put(age, 0l);
		}
		recordsWithColourCounts = new HashMap<>();
		for (String colour: colours) {
			recordsWithColourCounts.put(colour, 0l);
		}
		recordsWithAnimalCounts = new HashMap<>();
		for (String animal: animals) {
			recordsWithAnimalCounts.put(animal, 0l);
		}
		recordsModTenCounts = new HashMap<>();
		for (long i = 0; i < 10; i++) {
			recordsModTenCounts.put(i, 0l);
		}
	}
	
	protected void tryDropIndex(String namespace, String setName, String indexName, int waitTime) {
		try {
			IndexTask indexDropTask = client.dropIndex(null, namespace, null, indexName);
			indexDropTask.waitTillComplete(waitTime);
		}  catch (AerospikeException e) {
			if (e.getResultCode() != ResultCode.INDEX_NOTFOUND) {
				throw e;
			}
		}
		queryEngine.refreshCluster();

	}

	protected void tryCreateIndex(String namespace, String setName, String indexName, String binName, IndexType indexType) {
		try {
			IndexTask task = this.client.createIndex(null, namespace, setName, indexName, binName, indexType);
			task.waitTillComplete(50);
		} catch (AerospikeException e) {
			if (e.getResultCode() != ResultCode.INDEX_FOUND) {
				throw(e);
			}
		}
		queryEngine.refreshCluster();

	}

	protected void tryCreateIndex(String namespace, String setName, String indexName, String binName, IndexType indexType, 
			IndexCollectionType collectionType) {
		try {
			IndexTask task = this.client.createIndex(null, namespace, setName, indexName, binName, indexType, collectionType);
			task.waitTillComplete(50);
		} catch (AerospikeException e) {
			if (e.getResultCode() != ResultCode.INDEX_FOUND) {
				throw(e);
			}
		}
		queryEngine.refreshCluster();
	}
}
