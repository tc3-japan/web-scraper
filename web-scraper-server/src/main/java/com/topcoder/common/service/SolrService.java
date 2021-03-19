package com.topcoder.common.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.topcoder.common.util.Common;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.topcoder.common.dao.ProductDAO;

@Service
public class SolrService {

    private static final Logger logger = LoggerFactory.getLogger(SolrService.class);

    /**
     * the http solr client
     */
    private HttpSolrClient httpSolrClient;

    /**
     * create new solr service
     *
     * @param serverURI the solr server uri
     */
    public SolrService(@Value("${solr.uri}") String serverURI) {
        httpSolrClient = new HttpSolrClient.Builder(serverURI).build();
        httpSolrClient.setParser(new XMLResponseParser());
    }

    public String findByProductId(Integer id) throws IOException, SolrServerException {
        SolrQuery query = new SolrQuery();
        query.set("q", "product_id:" + id + "");
        QueryResponse response = httpSolrClient.query(query);
        if (response.getResults().getNumFound() <= 0) {
            return null;
        }
        return response.getResults().get(0).get("id").toString();
    }

    public List<SolrPorduct> searchSimilarProducts(ProductDAO product) throws IOException, SolrServerException {
        if (product == null) {
            throw new IllegalArgumentException("product must be specified.");
        }
        String id = findByProductId(product.getId());
        if (id == null) {
            return new ArrayList<SolrPorduct>(0);
        }
        /* q=id:a61d769e-a38b-4a84-a585-6d02a3d356d5&indent=on&mlt=true&mlt.fl=product_name
         * &mlt.mindf=1&mlt.mintf=1&fl=id,product_id,ec_site,product_name,unit_price,group_id,score
         */
        SolrQuery query = new SolrQuery("id:" + id);
        query.set("mlt", true);
        query.set("mlt.fl", "product_name");
        query.set("mlt.mindf", 1);
        query.set("mlt.mintf", 1);
        query.set("fl", "id,product_id,ec_site,product_name,unit_price,group_id,score");

        QueryResponse response = this.httpSolrClient.query(query);
        return response.getMoreLikeThis().get(id).stream().map(doc -> {
            return toProduct(doc);
        }).collect(Collectors.toList());
    }

    /**
     * create or update solr entity
     *
     * @param page the page enitty
     * @throws IOException         if network exception happened
     * @throws SolrServerException if solr server exception happened
     */
    public void createOrUpdate(ProductDAO product) throws IOException, SolrServerException {
        SolrInputDocument document = toDocument(product);
        httpSolrClient.add(document);
        httpSolrClient.commit();
    }

    public int load(List<ProductDAO> products) {
        if (products == null || products.size() == 0) {
            return 0;
        }
        int count = 0;
        for (Iterator<ProductDAO> iter = products.iterator(); iter.hasNext(); ) {
            try {
                ProductDAO p = iter.next();
                logger.info(
                        String.format("Indexing Product {id:%d, name:%s, site:%s, group:%d}", p.getId(), p.getEcSite(),
                                p.getProductName(), p.getProductGroupId()));
                SolrInputDocument document = toDocument(p);
                UpdateResponse response = httpSolrClient.add(document);
                if (response.getStatus() >= 300) { //TODO
                    String message = "Received an error response in updating record in Index. Response: " + response.getResponse().jsonStr();
                    Common.ZabbixLog(logger, message);
                    continue;
                }
                count++;
            } catch (IOException | SolrServerException e) {
                Common.ZabbixLog(logger, "Failed to update record in Index. ", e);
            }
        }
        try {
            UpdateResponse response = httpSolrClient.commit(true, false);
            logger.info("Commit updates in Index. Response: " + response.getResponse().jsonStr());
            return count;
        } catch (IOException | SolrServerException e) {
            Common.ZabbixLog(logger, "Failed to commit updates in Index. ", e);
            return 0;
        }
    }

    SolrInputDocument toDocument(ProductDAO product) throws IOException, SolrServerException {
        String id = findByProductId(product.getId());
        // set id if exist
        SolrInputDocument document = new SolrInputDocument();
        if (id != null) {
            document.addField("id", id);
        }
        document.addField("product_id", product.getId());
        document.addField("ec_site", product.getEcSite());
        document.addField("unit_price", product.getUnitPrice());
        document.addField("product_name", product.getProductName());
        document.addField("group_id", product.getProductGroupId());
        return document;
    }

    String getString(SolrDocument doc, String key) {
        if (key == null || key.length() == 0) {
            throw new IllegalArgumentException("key must be specified.");
        }
        if (doc == null) {
            return null;
        }
        if (!doc.containsKey(key) || doc.get(key) == null) {
            return null;
        }
        return doc.get(key).toString();
    }

    SolrPorduct toProduct(SolrDocument doc) {
        if (doc == null) {
            return null;
        }
        SolrPorduct prod = new SolrPorduct();
        prod.setDocumentId(getString(doc, "id"));

        if (doc.containsKey("product_id")) {
            prod.setId((Integer) doc.getFieldValue("product_id"));
        }
        prod.setEcSite(getString(doc, "ec_site"));
        prod.setProductName(getString(doc, "product_name"));
        prod.setUnitPrice(getString(doc, "unit_price"));

        if (doc.containsKey("group_id")) {
            prod.setProductGroupId((Integer) doc.getFieldValue("group_id"));
        }
        if (doc.containsKey("score")) {
            prod.setScore((Float) doc.getFieldValue("score"));
        }
        return prod;
    }

    public static class SolrPorduct extends ProductDAO {
        private String documentId;
        private Float score;

        public String getDocumentId() {
            return documentId;
        }

        public void setDocumentId(String documentId) {
            this.documentId = documentId;
        }

        public Float getScore() {
            return score;
        }

        public void setScore(Float score) {
            this.score = score;
        }

        public ProductDAO toProductDAO() {
            ProductDAO prod = new ProductDAO();
            BeanUtils.copyProperties(this, prod);
            return prod;
        }
    }
}
