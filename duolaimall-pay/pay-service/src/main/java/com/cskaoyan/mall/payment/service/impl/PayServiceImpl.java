package com.cskaoyan.mall.payment.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.cskaoyan.mall.common.constant.RedisConst;
import com.cskaoyan.mall.common.constant.ResultCodeEnum;
import com.cskaoyan.mall.common.execption.BusinessException;
import com.cskaoyan.mall.order.constant.OrderStatus;
import com.cskaoyan.mall.order.dto.OrderInfoDTO;
import com.cskaoyan.mall.payment.alipay.AlipayHelper;
import com.cskaoyan.mall.payment.alipay.PayHelper;
import com.cskaoyan.mall.payment.alipay.factory.PayHelperFactory;
import com.cskaoyan.mall.payment.client.OrderApiClient;
import com.cskaoyan.mall.payment.constant.PaymentStatus;
import com.cskaoyan.mall.payment.constant.PaymentType;
import com.cskaoyan.mall.payment.converter.PaymentInfoConverter;
import com.cskaoyan.mall.pay.api.dto.PaymentInfoDTO;
import com.cskaoyan.mall.payment.mapper.PaymentInfoMapper;
import com.cskaoyan.mall.payment.model.PaymentInfo;
import com.cskaoyan.mall.payment.service.PayService;
import kotlin.jvm.internal.Lambda;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;

/**
 * 创建日期: 2023/03/17 14:24
 *
 * @author ciggar
 */
@Service
@SuppressWarnings("all")
@Slf4j
public class PayServiceImpl implements PayService {

    @Autowired
    OrderApiClient orderApiClient;

    @Autowired
    PaymentInfoMapper paymentInfoMapper;


    @Autowired
    PayHelperFactory payHelperFactory;

    @Autowired
    PaymentInfoConverter paymentInfoConverter;

    @Autowired
    RedissonClient redissonClient;


    /**
     * 支付宝支付，获取支付表单
     */
    @Override
    public String createAliPay(Long orderId) {
        //此处需要做如下几件事：
        //1. 校验支付对应的订单状态是否为未支付，如果是未支付或已关闭，直接返回
        OrderInfoDTO orderInfoDTO = orderApiClient.getOrderInfoDTO(orderId);
        String orderStatus = orderInfoDTO.getOrderStatus();
        if (OrderStatus.CLOSED.name().equals(orderStatus)||OrderStatus.PAID.name().equals(orderStatus)){
            return "当前订单已完成或已关闭";
        }
        //2.保存支付记录到支付表
        QueryWrapper<PaymentInfo> queryMapper = new QueryWrapper<>();
        queryMapper.eq("order_id",orderInfoDTO.getId());
        queryMapper.eq("payment_type",PaymentType.ALIPAY.name());
        int count = paymentInfoMapper.selectCount(queryMapper).intValue();
        if (count>0){
            //已经支付，直接返回
            return null;
        }
        //保存交易记录
        //此时需要将order转换为pay
        PaymentInfo paymentInfo = paymentInfoConverter.contvertOrderInfoDTO2PaymentInfo(orderInfoDTO);
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setPaymentType(PaymentType.ALIPAY.name());
        paymentInfo.setPaymentStatus(PaymentStatus.UNPAID.name());
        paymentInfoMapper.insert(paymentInfo);
        //3.调用支付宝SDK, 生成支付表单
        String form = payHelperFactory.getPayHelper(PaymentType.ALIPAY).prePay(orderInfoDTO);
        //4.返回支付表单

        return form;
    }

    @Override
    public void savePaymentInfo(OrderInfoDTO orderInfo, String paymentTypeName) {


    }


    /**
     * 通过外部交易号和交易渠道 查询支付记录
     */
    @Override
    public PaymentInfoDTO queryPaymentInfoByOutTradeNoAndPaymentType(String outTradeNo, String payTypeName) {

        return null;
    }


    /**
     * 修改支付记录表
     * 1. 更改状态
     * 2. 设置回调信息
     * 3. 假如出现异常，删除回调幂等标记
     */
    @Override
    @Transactional
    public Boolean successPay(String outTradeNo, String payTypeName, Map<String, String> paramsMap) {

        return true;
    }

    @Override
    public void updatePaymentStatus(String outTradeNo, String payTypeName, PaymentStatus paymentStatus) {

    }
}
