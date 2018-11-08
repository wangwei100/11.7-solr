package com.demohot.solr.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.demohot.solr.model.Foo;

public class SolrjService {
	private HttpSolrServer httpSolrServer;

	public SolrjService(HttpSolrServer httpSolrServer) {
		this.httpSolrServer = httpSolrServer;
	}

	public void add(Foo foo) throws Exception {
		this.httpSolrServer.addBean(foo); 
		this.httpSolrServer.commit(); 	}

	public void delete(List<String> ids) throws Exception {
		this.httpSolrServer.deleteById(ids);
		this.httpSolrServer.commit(); 
	}

	public List<Foo> search(String keywords, Integer page, Integer rows) throws Exception {
		SolrQuery solrQuery = new SolrQuery(); 
		solrQuery.setQuery("title:" + keywords); 
		
		solrQuery.setStart((Math.max(page, 1) - 1) * rows);
		solrQuery.setRows(rows);

		
		boolean isHighlighting = !StringUtils.equals("*", keywords) && StringUtils.isNotEmpty(keywords);

		if (isHighlighting) {
			
			solrQuery.setHighlight(true); 
			solrQuery.addHighlightField("title");
			solrQuery.setHighlightSimplePre("<em>");
			solrQuery.setHighlightSimplePost("</em>");
		}

	
		QueryResponse queryResponse = this.httpSolrServer.query(solrQuery);
		List<Foo> foos = queryResponse.getBeans(Foo.class);
		if (isHighlighting) {
			
			Map<String, Map<String, List<String>>> map = queryResponse.getHighlighting();
			for (Map.Entry<String, Map<String, List<String>>> highlighting : map.entrySet()) {
				for (Foo foo : foos) {
					if (!highlighting.getKey().equals(foo.getId().toString())) {
						continue;
					}
					foo.setTitle(StringUtils.join(highlighting.getValue().get("title"), ""));
					break;
				}
			}
		}

		return foos;
	}
}
