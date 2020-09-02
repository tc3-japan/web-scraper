package com.topcoder.scraper.service.impl;

import com.topcoder.common.dao.ProductDAO;
import com.topcoder.common.dao.PurchaseHistoryDAO;
import com.topcoder.common.dao.PurchaseProductDAO;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.model.PurchaseHistory;
import com.topcoder.common.repository.ProductRepository;
import com.topcoder.common.repository.PurchaseHistoryRepository;
import com.topcoder.common.repository.PurchaseProductRepository;
import com.topcoder.common.util.CipherUtils;
import com.topcoder.scraper.service.PurchaseHistoryService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DBPurchaseHistoryService implements PurchaseHistoryService {
    private final PurchaseHistoryRepository historyRepository;
    private final PurchaseProductRepository purchaseProductRepository;
    private final ProductRepository productRepository;

    @Autowired
    public DBPurchaseHistoryService(PurchaseHistoryRepository historyRepository,
                                    PurchaseProductRepository purchaseProductRepository,
                                    ProductRepository productRepository) {
        this.historyRepository = historyRepository;
        this.purchaseProductRepository = purchaseProductRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void save(String site, List<PurchaseHistory> list) {
        for (PurchaseHistory purchaseHistory : list) {
            if (purchaseHistory == null) continue;
            if (purchaseHistory.getOrderNumber() == null && purchaseHistory.getOrderDate() == null) continue;

            // Save PurchaseHistoryDAO
            PurchaseHistoryDAO dao = historyRepository.save(new PurchaseHistoryDAO(site, purchaseHistory));

            if (purchaseHistory.getProducts() == null) continue;
            for (ProductInfo productInfo : purchaseHistory.getProducts()) {
                if (productInfo.getName() == null && productInfo.getCode() == null) continue;

                // Save PurchaseProductDAO
                PurchaseProductDAO purchaseProductDAO = new PurchaseProductDAO(productInfo, dao);
                purchaseProductRepository.save(purchaseProductDAO);

                // Save ProductDAO, if product is not in DB
                ProductDAO existingProductDao = null;
                if (productInfo.getCode() != null) {
                    existingProductDao = productRepository.findByProductCode(productInfo.getCode());
                } else {
                    existingProductDao = productRepository.findByECSiteAndProductName(site, productInfo.getName());
                }
                if (existingProductDao == null) {
                    ProductDAO productDao = new ProductDAO(site, productInfo);
                    productRepository.save(productDao);
                } else {
                    existingProductDao.setUpdateAt(new Date());
                    productRepository.save(existingProductDao);
                }
            }
        }
    }

    @Override
    public List<PurchaseHistory> listAll(String site) {
        List<PurchaseHistory> results = new ArrayList<>();
        historyRepository.findAll().forEach(
                purchaseHistoryDAO -> results.add(purchaseHistoryDAO.getPurchaseHistory()));

        return results;
    }

    @Override
    public Optional<PurchaseHistory> fetchLast(int accountId) {
        List<PurchaseHistoryDAO> histories = historyRepository.getPurchaseHistoriesByAccountIdOrderByOrderDateDesc(CipherUtils.md5(Integer.toString(accountId)));
        return Optional.ofNullable(histories != null && histories.size() > 0 ? histories.get(0).getPurchaseHistory() : null);
    }

}
