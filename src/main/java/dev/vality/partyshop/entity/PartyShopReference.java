package dev.vality.partyshop.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "party_shop_reference")
public class PartyShopReference implements Serializable {

    @EmbeddedId
    private PartyShopPK partyShopPK;
    private String categoryType;

}
