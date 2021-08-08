import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testng.Assert;
import org.testng.annotations.DataProvider;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class ZomatoTest {

	private static final String BASE_ENDPOINT = "https://developers.zomato.com/api/v2.1/";
	private static final String API_KEY = "b1010e01b6fe484b1a0e7cc2a677153f";
	static CloseableHttpClient client;
	static CloseableHttpResponse response;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// setUpBeforeClass()
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		// tearDownAfterClass()
	}

	@Before
	public void setUp() throws Exception {
		client = HttpClientBuilder.create().build();
	}

	@After
	public void tearDown() throws Exception {
		client.close();
		response.close();
	}
	
	@Test
	public void AssertCategoriesResponseCode() throws ClientProtocolException, IOException {
		HttpGet get = new HttpGet(BASE_ENDPOINT + "categories");
		get.addHeader("user-key", API_KEY);
		response = client.execute(get);
		int actualStatus = response.getStatusLine().getStatusCode();
		
		// Assert status code is 200
		Assert.assertEquals(actualStatus, 200);		
	}
	
	@Test
	public void AssertCategoriesContentType() throws ClientProtocolException, IOException {
		HttpGet get = new HttpGet(BASE_ENDPOINT + "categories");
		get.addHeader("user-key", API_KEY);
		response = client.execute(get);
		Header content = response.getEntity().getContentType();
		
		// Assert content type is json
		Assert.assertEquals(content.getValue(), "application/json");
	}
	
	@Test
	public void AssertCategoriesNumber() throws ClientProtocolException, IOException {
		HttpGet get = new HttpGet(BASE_ENDPOINT + "categories");
		get.addHeader("user-key", API_KEY);
		response = client.execute(get);
		String jsonStr = EntityUtils.toString(response.getEntity());
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.readTree(jsonStr);
		ArrayNode arrayNode = (ArrayNode) node.get("categories");
		List<Category> categories = mapper.readValue(arrayNode.toString(), new TypeReference<List<Category>>() {});
		
		// Assertion, expecting 13 categories
		Assert.assertEquals(categories.size(), 13);
	}
	
	@Test
	public void AssertCategoriesJson() throws ClientProtocolException, IOException {
		HttpGet get = new HttpGet(BASE_ENDPOINT + "categories");
		get.addHeader("user-key", API_KEY);
		response = client.execute(get);
		String jsonStr = EntityUtils.toString(response.getEntity());
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.readTree(jsonStr);
		ArrayNode arrayNode = (ArrayNode) node.get("categories");
		List<Category> categories = mapper.readValue(arrayNode.toString(), new TypeReference<List<Category>>() {});
		
		// Assert the category where id = 8 equals "Breakfast"
		Assert.assertEquals(categories.get(7).getCategories().getName(), "Breakfast");
	}
	
	@Test
	public void AssertCitiesResponseCode() throws ClientProtocolException, IOException {
		String city = "dublin";
		HttpGet get = new HttpGet(BASE_ENDPOINT + "cities?q=" + city);
		get.addHeader("user-key", API_KEY);
		response = client.execute(get);
		int actualStatus = response.getStatusLine().getStatusCode();
		
		// Assert status code is 200
		Assert.assertEquals(actualStatus, 200);
	}
	
	@Test
	public void AssertCitiesSearchQuery() throws ClientProtocolException, IOException {
		String city = "dublin";
		HttpGet get = new HttpGet(BASE_ENDPOINT + "cities?q=" + city);
		get.addHeader("user-key", API_KEY);
		response = client.execute(get);
		String jsonStr = EntityUtils.toString(response.getEntity());
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.readTree(jsonStr);
		ArrayNode arrayNode = (ArrayNode) node.get("location_suggestions");
		List<LocationSuggestion> locations = mapper.readValue(arrayNode.toString(), new TypeReference<List<LocationSuggestion>>() {});
		
		// Assert first search result for dublin returns dublin, ireland
		LocationSuggestion dublin = locations.get(0);
		System.out.println(dublin.getName());
		Assert.assertEquals(dublin.getName(), "Dublin");
		Assert.assertEquals(dublin.getCountryName(), "Ireland");
	}
}
