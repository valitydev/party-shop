package com.rbkmoney.partyshop.repository;

import com.rbkmoney.partyshop.entity.PartyShopReference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PartyShopReferenceRepository extends JpaRepository<PartyShopReference, String> {

    List<PartyShopReference> findByPartyIdAndCategoryType(String partyId, String type);

}