package com.arkaces.btc_ark_channel_service.transfer;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface TransferRepository extends PagingAndSortingRepository<TransferEntity, Long> {
}
