package com.demohot.solr.test;

import java.util.Arrays;
import java.util.List;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.junit.Before;
import org.junit.Test;

import com.demohot.solr.model.Foo;
import com.demohot.solr.service.SolrjService;

public class SolrjServiceTest {
	private SolrjService solrjService;

	private HttpSolrServer httpSolrServer;

	@Before
	public void setUp() throws Exception {
		
		String url = "http://solr.enjoyshop.com/enjoyshop";
		HttpSolrServer httpSolrServer = new HttpSolrServer(url);
		httpSolrServer.setParser(new XMLResponseParser()); 
		httpSolrServer.setMaxRetries(1); 
		httpSolrServer.setConnectionTimeout(500); 

		this.httpSolrServer = httpSolrServer;
		solrjService = new SolrjService(httpSolrServer);
	}

	@Test
	public void testAdd() throws Exception {
		Foo foo = new Foo();
		foo.setId(System.currentTimeMillis() + "");
		foo.setTitle("轻量级Java EE企业应用实战（第3版）：Struts2＋Spring3＋Hibernate整合开发（附CD光盘）");

		this.solrjService.add(foo);
	}

	@Test
	public void testDelete() throws Exception {
		this.solrjService.delete(Arrays.asList("1416537175446"));
	}

	@Test
	public void testSearch() throws Exception {
		List<Foo> foos = this.solrjService.search("linux", 1, 10);
		for (Foo foo : foos) {
			System.out.println(foo);
		}
	}

	@Test
	public void testDeleteByQuery() throws Exception {
		httpSolrServer.deleteByQuery("*:*");
		httpSolrServer.commit();
	}
}
