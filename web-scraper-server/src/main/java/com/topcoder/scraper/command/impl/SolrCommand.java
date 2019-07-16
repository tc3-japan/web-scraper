package com.topcoder.scraper.command.impl;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.MapSolrParams;
import org.apache.solr.client.solrj.impl.HttpSolrClient.*;
import org.apache.solr.client.solrj.response.QueryResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;



/**
 * This will group product information of all products where group_status is null or uninitialized,
 * to the appropriate product_group table.
 */
@Component
@Transactional
public class SolrCommand {


  private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

  public void run(ApplicationArguments arguments) {
    final SolrClient client = new HttpSolrClient("http://localhost:8983/solr/collection1");

    final Map<String, String> queryParamMap = new HashMap<String, String>();
    queryParamMap.put("q", "*:*");
    queryParamMap.put("fl", "id, name");
    MapSolrParams queryParams = new MapSolrParams(queryParamMap);
    
    QueryResponse response = null;
    try {
      response = client.query("techproducts", queryParams);
    } catch (SolrServerException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    final SolrDocumentList documents = response.getResults();
    
    
    //assertEquals(NUM_INDEXED_DOCUMENTS, documents.getNumFound());
    for(SolrDocument document : documents) {
      //assertTrue(document.getFieldNames().contains("id"));
      //assertTrue(document.getFieldNames().contains("name"));
      System.out.println("Document is here!");
    }

  }
}
