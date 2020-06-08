package org.egov.assets.service;

import org.egov.assets.common.DomainService;
import org.egov.assets.common.Pagination;
import org.egov.assets.model.PurchaseOrderDetail;
import org.egov.assets.model.PurchaseOrderDetailSearch;
import org.egov.assets.repository.PurchaseOrderDetailJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PurchaseOrderDetailService extends DomainService {


    @Autowired
    private PurchaseOrderDetailJdbcRepository purchaseOrderDetailJdbcRepository;

    public Pagination<PurchaseOrderDetail> search(PurchaseOrderDetailSearch purchaseOrderDetailSearch) {

        return purchaseOrderDetailJdbcRepository.search(purchaseOrderDetailSearch);

    }

}