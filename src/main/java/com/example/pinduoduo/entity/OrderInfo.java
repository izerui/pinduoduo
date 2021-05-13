package com.example.pinduoduo.entity;

import com.example.pinduoduo.support.Description;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "order_info")
public class OrderInfo {

    @Description("订单号")
    @Id
    @Column(unique = true, nullable = false, updatable = false)
    private String orderNo;

    @Description("发货时间")
    private String sendTime;

    @Description("收件人")
    private String receiver;

    @Description("手机号")
    private String phone;

    @Description("省市区")
    private String city;

    @Description("详细地址")
    private String address;

    @Description("商品名称")
    private String productName;

    @Description("规格")
    private String productSpec;

    @Description("sku编码")
    private String sku;

    @Description("商品数量")
    private Integer num;

    @Description("总价")
    private BigDecimal totalAmount;

    @Description("支付金额")
    private BigDecimal payAmount;

    @Description("快递")
    private String courier;

    @Description("快递单号")
    private String courierNo;

    @Description("备注")
    private String remark;

}
