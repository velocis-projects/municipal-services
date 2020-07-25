package org.egov.assets.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.egov.assets.common.DomainService;
import org.egov.assets.common.Pagination;
import org.egov.assets.model.MaterialBalanceRate;
import org.egov.assets.model.MaterialReceipt;
import org.egov.assets.model.MaterialReceiptDetail;
import org.egov.assets.model.MaterialReceiptDetailSearch;
import org.egov.assets.model.MaterialReceiptSearch;
import org.egov.assets.repository.MaterialReceiptJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MaterialReceiptService extends DomainService {

	@Autowired
	private MaterialReceiptJdbcRepository materialReceiptJdbcRepository;

	@Autowired
	private MaterialReceiptDetailService materialReceiptDetailService;

	public Pagination<MaterialReceipt> search(MaterialReceiptSearch materialReceiptSearch) {
		Pagination<MaterialReceipt> materialReceiptPagination = materialReceiptJdbcRepository
				.search(materialReceiptSearch);

		if (materialReceiptPagination.getPagedData().size() > 0) {
			for (MaterialReceipt materialReceipt : materialReceiptPagination.getPagedData()) {
				List<MaterialReceiptDetail> materialReceiptDetail = getMaterialReceiptDetail(
						materialReceipt.getMrnNumber(), materialReceiptSearch.getTenantId());
				materialReceipt.setReceiptDetails(materialReceiptDetail);
			}
		}

		return materialReceiptPagination;
	}

	public Pagination<MaterialBalanceRate> searchBalanceRate(MaterialReceiptSearch materialReceiptSearch) {
		Pagination<MaterialBalanceRate> materialBalanceRatePagination = materialReceiptJdbcRepository
				.searchBalanceRate(materialReceiptSearch);
		return materialBalanceRatePagination;
	}

	private List<MaterialReceiptDetail> getMaterialReceiptDetail(String mrnNumber, String tenantId) {
		MaterialReceiptDetailSearch materialReceiptDetailSearch = MaterialReceiptDetailSearch.builder()
				.mrnNumber(Arrays.asList(mrnNumber)).tenantId(tenantId).build();
		Pagination<MaterialReceiptDetail> materialReceiptDetails = materialReceiptDetailService
				.search(materialReceiptDetailSearch);
		return materialReceiptDetails.getPagedData().size() > 0 ? materialReceiptDetails.getPagedData()
				: Collections.EMPTY_LIST;
	}
}