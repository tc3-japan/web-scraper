package com.topcoder.scraper.command.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.topcoder.common.dao.ProductDAO;
import com.topcoder.common.repository.ProductRepository;
import com.topcoder.common.service.SolrService;

@Component
public class BuildProductIndexCommand {

    private static Logger logger = LoggerFactory.getLogger(BuildProductIndexCommand.class);

    @Autowired
    private SolrService solrService;

    @Autowired
    private ProductRepository productRepository;

    @Value("${solr.load_batch_size:100}")
    private Integer loadBatchSize;

    public void run(ApplicationArguments arguments) {
        logger.info("Start BuildProductIndexCommand.");

        PageRequest pageRequest = new PageRequest(0, loadBatchSize);
        List<ProductDAO> products = null;

        int count = 0;
        do {
            Page<ProductDAO> result = this.productRepository.findAll(pageRequest);
            products = result.getContent();
            logger.info(String.format("%d product(s) fetched.", products.size()));
            count += solrService.load(products);
            logger.info(String.format("%d product(s) loaded.", count));
            pageRequest = new PageRequest(pageRequest.getPageNumber() + 1, pageRequest.getPageSize());
        } while (products != null && products.size() > 0);

        logger.info("Finised loading products in Index. count: " + count);
    }
}
