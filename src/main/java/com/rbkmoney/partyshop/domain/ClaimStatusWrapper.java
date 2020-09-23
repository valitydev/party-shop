package com.rbkmoney.partyshop.domain;

import com.rbkmoney.damsel.payment_processing.ClaimStatus;
import lombok.Data;

@Data
public class ClaimStatusWrapper {

    private long revision;
    private ClaimStatus claimStatus;

}
