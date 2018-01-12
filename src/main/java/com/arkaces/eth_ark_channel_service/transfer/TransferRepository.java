package com.arkaces.eth_ark_channel_service.transfer;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface TransferRepository extends PagingAndSortingRepository<TransferEntity, Long> {
}
