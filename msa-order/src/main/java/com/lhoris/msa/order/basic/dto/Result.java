package com.lhoris.msa.order.basic.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Result {

    private String accountNo;
    private String custNm;
    private String cardNo;
    private String bankAcntNo;

    private String orderNo;
    private BigDecimal orderProductNo;
    private String orderProductNm;
    private String quantity;

}