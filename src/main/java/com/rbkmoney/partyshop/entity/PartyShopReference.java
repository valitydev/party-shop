package com.rbkmoney.partyshop.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "party_shop_reference")
public class PartyShopReference implements Serializable {

    @Id
    private String shopId;
    private String partyId;
    private String categoryType;

}
