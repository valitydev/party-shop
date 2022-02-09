package dev.vality.partyshop.utils;

import dev.vality.damsel.domain.*;
import dev.vality.damsel.payment_processing.*;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.List;

public class BeanUtils {

    public static final String SHOP_ID = "shop_id";

    @NotNull
    public static PartyChange createPartyChange() {
        return PartyChange.claim_created(new Claim()
                .setCreatedAt(Instant.now().toString())
                .setChangeset(List.of(PartyModification.shop_modification(new ShopModificationUnit()
                        .setId("s")
                        .setModification(ShopModification.category_modification(
                                new CategoryRef()
                                        .setId(123)))))
                )
                .setStatus(ClaimStatus.accepted(new ClaimAccepted().setEffects(
                        List.of(ClaimEffect.shop_effect(new ShopEffectUnit(SHOP_ID, ShopEffect.created(new Shop()
                                .setBlocking(Blocking.unblocked(new Unblocked()
                                        .setReason("123")
                                        .setSince("123"))
                                )
                                .setSuspension(Suspension.active(new Active().setSince("1")))
                                .setDetails(new ShopDetails()
                                        .setName("name"))
                                .setLocation(ShopLocation.url("wer"))
                                .setCategory(new CategoryRef().setId(1))
                                .setContractId("123")
                                .setCreatedAt(Instant.now().toString())
                                .setId(SHOP_ID)))))))

                ));
    }

}
