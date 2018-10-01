package com.topcoder.scraper.service.impl;

import com.topcoder.scraper.dao.PurchaseHistoryDAO;
import com.topcoder.scraper.model.PurchaseHistory;
import com.topcoder.scraper.repository.PurchaseHistoryRepository;
import com.topcoder.scraper.service.PurchaseHistoryService;
import com.topcoder.scraper.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Service
public class DBPurchaseHistoryService implements PurchaseHistoryService {
    private final PurchaseHistoryRepository repository;

    @Autowired
    public DBPurchaseHistoryService(PurchaseHistoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(String site, List<PurchaseHistory> list) {
        for (PurchaseHistory purchaseHistory : list) {
            repository.save(new PurchaseHistoryDAO(purchaseHistory));
        }
    }

    @Override
    public List<PurchaseHistory> listAll(String site) {
        List<PurchaseHistory> results = new ArrayList<>();
        repository.findAll().forEach(
            purchaseHistoryDAO -> results.add(purchaseHistoryDAO.getPurchaseHistory()));

        return results;
    }

    @Override
    public Optional<PurchaseHistory> fetchLast(String site) {
        return listAll(site).stream().max((o1, o2) -> {
            try {
                return DateUtils.fromString(o1.getOrderDate()).compareTo(DateUtils.fromString(o2.getOrderDate()));
            } catch (ParseException e) {
                return -1;
            }
        });
    }
}
