package com.lhoris.msa.account.basic.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    private String custNm;
    private String cardNo;
    private String bankAcntNo;

    private BigDecimal orderProductNo;
    private String orderProductNm;
    private String quantity;

}