package com.demohot.solr.test;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.junit.Before;
import org.junit.Test;

import com.demohot.solr.model.Item;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ItemDataImportTest {
	private HttpSolrServer httpSolrServer;

	private static final ObjectMapper MAPPER = new ObjectMapper();

	@Before
	public void setUp() throws Exception {
		
		String url = "https://www.jd.com/"; 
		HttpSolrServer httpSolrServer = new HttpSolrServer(url); 
		httpSolrServer.setParser(new XMLResponseParser()); 
		httpSolrServer.setMaxRetries(1);
		httpSolrServer.setConnectionTimeout(500); 

		this.httpSolrServer = httpSolrServer;
	}

	@Test
	public void testData() throws Exception {
		
		String url = "http://manage.enjoyshop.com/rest/item?page={page}&rows=100";
		int page = 1;
		int pageSzie = 0;
		do {
			String u = StringUtils.replace(url, "{page}", "" + page);
			System.out.println(u);
			String jsonData = doGet(u);
			JsonNode jsonNode = MAPPER.readTree(jsonData);
			String rowsStr = jsonNode.get("rows").toString();
			List<Item> items = MAPPER.readValue(rowsStr,
					MAPPER.getTypeFactory().constructCollectionType(List.class, Item.class));
			pageSzie = items.size();
			this.httpSolrServer.addBeans(items);
			this.httpSolrServer.commit();

			page++;
		} while (pageSzie == 100);

	}

	private String doGet(String url) throws Exception {
		
		CloseableHttpClient httpclient = HttpClients.createDefault();

		
		HttpGet httpGet = new HttpGet(url);

		CloseableHttpResponse response = null;
		try {
		
			response = httpclient.execute(httpGet);
			
			if (response.getStatusLine().getStatusCode() == 200) {
				return EntityUtils.toString(response.getEntity(), "UTF-8");
			}
		} finally {
			if (response != null) {
				response.close();
			}
			httpclient.close();
		}
		return null;
	}
}
