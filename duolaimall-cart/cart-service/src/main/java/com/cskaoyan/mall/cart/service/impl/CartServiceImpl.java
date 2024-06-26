package com.cskaoyan.mall.cart.service.impl;

import com.cskaoyan.mall.cart.api.dto.CartInfoDTO;
import com.cskaoyan.mall.cart.converter.SkuInfoConverter;
import com.cskaoyan.mall.cart.client.ProductApiClient;
import com.cskaoyan.mall.cart.service.CartService;
import com.cskaoyan.mall.common.constant.RedisConst;
import com.cskaoyan.mall.common.util.DateUtil;
import com.cskaoyan.mall.product.dto.SkuInfoDTO;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("all")
public class CartServiceImpl implements CartService {

    @Autowired
    private ProductApiClient productApiClient;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    SkuInfoConverter skuInfoConverter;

    @Override
    public void addToCart(Long skuId, String userId, Integer skuNum) {

        //将商品加入到购物车中，不用考虑是临时用户还是已经登录的用户--用户Id已经在controller方法中进行了考虑
        //传进来的skuNum为1表示新增一个，为-1表示decrease一个
        //购物车中数据是依靠hash表来存储的
        String hashGoodsTableSingleUser = RedisConst.USER_KEY_PREFIX + userId + RedisConst.USER_CART_KEY_SUFFIX;
        //从redis中获取购物车的hash表
        RMap<String, CartInfoDTO> cartInfoDTORMap = redissonClient.getMap(hashGoodsTableSingleUser);
        CartInfoDTO cartInfoDTO = null;
        if (cartInfoDTORMap.containsKey(skuId.toString())) {
            cartInfoDTO = cartInfoDTORMap.get(skuId.toString());
            //此处的数量是每次加减的数量
            cartInfoDTO.setSkuNum(cartInfoDTO.getSkuNum()+skuNum);
            cartInfoDTO.setIsChecked(1);
            cartInfoDTO.setSkuPrice(productApiClient.getSkuPrice(skuId));
            cartInfoDTO.setUpdateTime(new Date());
        }else {
            //给cartInfo赋值
            SkuInfoDTO skuInfo = productApiClient.getSkuInfo(skuId);
            cartInfoDTO = skuInfoConverter.skuInfoToCartInfo(skuInfo, skuNum, skuId, userId);
        }
        cartInfoDTORMap.put(skuId.toString(),cartInfoDTO);
    }

    private String getCartKey(String userId) {
        //定义key user:1:cart
        return RedisConst.USER_KEY_PREFIX + userId + RedisConst.USER_CART_KEY_SUFFIX;
    }

    private List<CartInfoDTO> sortList(List<CartInfoDTO> list){
        Collections.sort(list, (first, second) ->{
            // 只比较到秒即可
            return DateUtil.truncatedCompareTo(second.getCreateTime(), first.getCreateTime(), Calendar.SECOND);
        });
        return list;
    }

    /**
     * 通过用户Id 查询购物车列表
     * 合并userId与userTempId的购物车、删除userTempId的购物车
     */
    @Override
    public List<CartInfoDTO> getCartList(String userId, String userTempId) {
        //如果userId为空，表示用户此时没有登录，直接返回临时用户id的购物车数据即可
        if (userId==null){
            String hashGoodsTableSingleUser = RedisConst.USER_KEY_PREFIX + userTempId + RedisConst.USER_CART_KEY_SUFFIX;
            ArrayList<CartInfoDTO> cartInfoDTOS = new ArrayList<>();
            RMap<Long, CartInfoDTO> goodsMap = redissonClient.getMap(hashGoodsTableSingleUser);
            if (goodsMap!=null){
                for (Map.Entry<Long, CartInfoDTO> longCartInfoDTOEntry : goodsMap.entrySet()) {
                    cartInfoDTOS.add(longCartInfoDTOEntry.getValue());
                }
            }
//            return sortList(cartInfoDTOS);
            return (cartInfoDTOS);
        }
        //此时userId不为空，表示用户已经登录，此时需要合并购物车
        String hashGoodsTableLoginUser = getCartKey(userId);
        RMap<Long, CartInfoDTO> longinGoodsMap = redissonClient.getMap(hashGoodsTableLoginUser);
        String hashGoodsTableUnLogin = getCartKey(userTempId);
        RMap<Long, CartInfoDTO> unLoginGoodsMap = redissonClient.getMap(hashGoodsTableUnLogin);
        if (unLoginGoodsMap==null){
            //未登录时的购物车中数据为空，直接返回已经登录的购物车中数据即可
            ArrayList<CartInfoDTO> cartInfoDTOS = new ArrayList<>();
            if (longinGoodsMap!=null){
                for (Map.Entry<Long, CartInfoDTO> objectObjectEntry : longinGoodsMap.entrySet()) {
                    cartInfoDTOS.add(objectObjectEntry.getValue());
                }
            }
//            return sortList(cartInfoDTOS);
            return (cartInfoDTOS);

        }
        //代码走到这表示未登录时的购物车中数据不为空，此时需要合并购物车
        //此时不能保证longinCart不为空
        if (longinGoodsMap!=null){
            for (Map.Entry<Long, CartInfoDTO> longCartInfoDTOEntryUnLogin : unLoginGoodsMap.entrySet()) {
                if (longinGoodsMap.containsKey(longCartInfoDTOEntryUnLogin.getKey())){
                    CartInfoDTO cartInfoDTO = longinGoodsMap.get(longCartInfoDTOEntryUnLogin.getKey());
                    cartInfoDTO.setSkuNum(cartInfoDTO.getSkuNum()+longCartInfoDTOEntryUnLogin.getValue().getSkuNum());
                    //将商品数量更新到登录后的购物车中
                    longinGoodsMap.fastPut(cartInfoDTO.getSkuId(),cartInfoDTO);
                }else {
                    //此时登录后的购物车中不包含当前未登录时购物车中的商品
                    CartInfoDTO cartInfoDTO = longCartInfoDTOEntryUnLogin.getValue();
                    cartInfoDTO.setUserId(userId);
                    longinGoodsMap.put(longCartInfoDTOEntryUnLogin.getKey(),cartInfoDTO);
                }
            }
            redissonClient.getMap(hashGoodsTableUnLogin).delete();
//            return sortList(longinGoodsMap.values().stream().toList());
            return (longinGoodsMap.values().stream().toList());
        }else {
            //此时未登录时的购物车有商品，而已登录的购物车中没有商品--loginGoodsMap为Null
            for (Map.Entry<Long, CartInfoDTO> longCartInfoDTOEntry : unLoginGoodsMap.entrySet()) {
                longCartInfoDTOEntry.getValue().setUserId(userId);
            }
            //将商品中的用户id改为已登录用户后，删除临时购物车后直接返回
            redissonClient.getMap(hashGoodsTableUnLogin).delete();
//            return sortList(unLoginGoodsMap.values().stream().toList());
            return (unLoginGoodsMap.values().stream().toList());
        }

    }


    @Override
    public void checkCart(String userId, Integer isChecked, Long skuId) {
        //所需做的是改变当前选中的商品的id
        //修改对应user的购物车--中某个skuId商品对应的选中状态，并返回即可
        //第一步是获取当前用户的购物车，登录与否已经在controller方法中提前规避了
        String cartKey = getCartKey(userId);
        //上述cartKey即为对应用户的购物车的key，购物车数据是存储在Redis中的
        RMap<Long, CartInfoDTO> redissonClientMap = redissonClient.getMap(cartKey);
        CartInfoDTO goods = redissonClientMap.get(skuId);
        goods.setIsChecked(isChecked);
        redissonClientMap.put(skuId,goods);
    }



    /**
     * @param skuId         商品id
     * @param userId        用户id
     * @return: void
     * 功能描述: 删除购物车指定的商品
     */
    @Override
    public void deleteCart(Long skuId, String userId) {
        String cartKey = getCartKey(userId);
        RMap<Long, CartInfoDTO> redissonClientMap = redissonClient.getMap(cartKey);
        redissonClientMap.remove(skuId);
    }


    /**
     * @param userId
     * @return: void
     * 功能描述: 删除购物车中所有选中的商品
     */
    @Override
    public void deleteChecked(String userId) {
        //利用userId获取到购物车，遍历其中选中的，remove即可
        String cartKey = getCartKey(userId);
        RMap<Long, CartInfoDTO> redissonClientMap = redissonClient.getMap(cartKey);
        for (Map.Entry<Long, CartInfoDTO> goods : redissonClientMap.entrySet()) {
            if (goods.getValue().getIsChecked()==1){
                redissonClientMap.remove(goods.getKey());
            }
        }
    }



    @Override
    public List<CartInfoDTO> getCartCheckedList(String userId) {
        //根据用户id，获取当前用户购物车中所有已选中的商品id
        String cartKey = getCartKey(userId);
        RMap<Long, CartInfoDTO> redissonClientMap = redissonClient.getMap(cartKey);
        List<CartInfoDTO> cartInfoDTOS = new ArrayList<>();
        for (Map.Entry<Long, CartInfoDTO> goods : redissonClientMap.entrySet()) {
            if (goods.getValue().getIsChecked()==1){
                cartInfoDTOS.add(goods.getValue());
            }
        }
        //此时还没有下单，不用删除购物车中选中的商品
        return cartInfoDTOS;
    }

    @Override
    public void delete(String userId, List<Long> skuIds) {
        String cartKey = getCartKey(userId);
        RMap<Object, Object> redissonClientMap = redissonClient.getMap(cartKey);
        skuIds.stream().forEach(skuId->{
            if (redissonClientMap.containsKey(skuId.toString())){
                redissonClientMap.remove(skuId.toString());
            }
        });
    }

    @Override
    public void refreshCartPrice(String userId, Long skuId) {
        //首先获取用户的购物车
        String cartKey = getCartKey(userId);
        RMap<String, CartInfoDTO> cartMap = redissonClient.getMap(cartKey);
        CartInfoDTO cartInfoDTO = cartMap.get(cartKey);
        //获取商品的实际价格
        BigDecimal skuPrice = productApiClient.getSkuPrice(skuId);
        cartInfoDTO.setSkuPrice(skuPrice);
        cartMap.put(skuId.toString(),cartInfoDTO);
    }
}
