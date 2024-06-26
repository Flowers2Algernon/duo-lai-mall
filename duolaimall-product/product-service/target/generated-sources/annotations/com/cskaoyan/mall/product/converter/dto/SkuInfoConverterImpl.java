package com.cskaoyan.mall.product.converter.dto;

import com.cskaoyan.mall.product.dto.SkuImageDTO;
import com.cskaoyan.mall.product.dto.SkuInfoDTO;
import com.cskaoyan.mall.product.dto.SkuPlatformAttributeValueDTO;
import com.cskaoyan.mall.product.dto.SkuSaleAttributeValueDTO;
import com.cskaoyan.mall.product.model.SkuImage;
import com.cskaoyan.mall.product.model.SkuInfo;
import com.cskaoyan.mall.product.model.SkuPlatformAttributeValue;
import com.cskaoyan.mall.product.model.SkuSaleAttributeValue;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-06-25T22:28:58+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.9 (Oracle Corporation)"
)
@Component
public class SkuInfoConverterImpl implements SkuInfoConverter {

    @Override
    public SkuInfoDTO skuInfoPO2DTO(SkuInfo skuInfo) {
        if ( skuInfo == null ) {
            return null;
        }

        SkuInfoDTO skuInfoDTO = new SkuInfoDTO();

        skuInfoDTO.setId( skuInfo.getId() );
        skuInfoDTO.setSpuId( skuInfo.getSpuId() );
        skuInfoDTO.setPrice( skuInfo.getPrice() );
        skuInfoDTO.setSkuName( skuInfo.getSkuName() );
        skuInfoDTO.setSkuDesc( skuInfo.getSkuDesc() );
        skuInfoDTO.setWeight( skuInfo.getWeight() );
        skuInfoDTO.setTmId( skuInfo.getTmId() );
        skuInfoDTO.setThirdLevelCategoryId( skuInfo.getThirdLevelCategoryId() );
        skuInfoDTO.setSkuDefaultImg( skuInfo.getSkuDefaultImg() );
        skuInfoDTO.setIsSale( skuInfo.getIsSale() );
        skuInfoDTO.setSkuImageList( skuImageListToSkuImageDTOList( skuInfo.getSkuImageList() ) );
        skuInfoDTO.setSkuPlatformAttributeValueList( skuPlatformAttributeValueListToSkuPlatformAttributeValueDTOList( skuInfo.getSkuPlatformAttributeValueList() ) );
        skuInfoDTO.setSkuSaleAttributeValueList( skuSaleAttributeValueListToSkuSaleAttributeValueDTOList( skuInfo.getSkuSaleAttributeValueList() ) );

        return skuInfoDTO;
    }

    @Override
    public SkuImageDTO skuImagePO2DTO(SkuImage skuImage) {
        if ( skuImage == null ) {
            return null;
        }

        SkuImageDTO skuImageDTO = new SkuImageDTO();

        skuImageDTO.setId( skuImage.getId() );
        skuImageDTO.setSkuId( skuImage.getSkuId() );
        skuImageDTO.setImgName( skuImage.getImgName() );
        skuImageDTO.setImgUrl( skuImage.getImgUrl() );
        skuImageDTO.setSpuImgId( skuImage.getSpuImgId() );
        skuImageDTO.setIsDefault( skuImage.getIsDefault() );

        return skuImageDTO;
    }

    @Override
    public SkuPlatformAttributeValueDTO skuPlatformAttributeValuePO2DTO(SkuPlatformAttributeValue skuPlatformAttributeValue) {
        if ( skuPlatformAttributeValue == null ) {
            return null;
        }

        SkuPlatformAttributeValueDTO skuPlatformAttributeValueDTO = new SkuPlatformAttributeValueDTO();

        skuPlatformAttributeValueDTO.setId( skuPlatformAttributeValue.getId() );
        skuPlatformAttributeValueDTO.setAttrId( skuPlatformAttributeValue.getAttrId() );
        skuPlatformAttributeValueDTO.setValueId( skuPlatformAttributeValue.getValueId() );
        skuPlatformAttributeValueDTO.setSkuId( skuPlatformAttributeValue.getSkuId() );

        return skuPlatformAttributeValueDTO;
    }

    @Override
    public SkuSaleAttributeValueDTO skuSaleAttributeValuePOs2DTOs(SkuSaleAttributeValue skuSaleAttributeValue) {
        if ( skuSaleAttributeValue == null ) {
            return null;
        }

        SkuSaleAttributeValueDTO skuSaleAttributeValueDTO = new SkuSaleAttributeValueDTO();

        skuSaleAttributeValueDTO.setId( skuSaleAttributeValue.getId() );
        skuSaleAttributeValueDTO.setSkuId( skuSaleAttributeValue.getSkuId() );
        skuSaleAttributeValueDTO.setSpuId( skuSaleAttributeValue.getSpuId() );

        return skuSaleAttributeValueDTO;
    }

    protected List<SkuImageDTO> skuImageListToSkuImageDTOList(List<SkuImage> list) {
        if ( list == null ) {
            return null;
        }

        List<SkuImageDTO> list1 = new ArrayList<SkuImageDTO>( list.size() );
        for ( SkuImage skuImage : list ) {
            list1.add( skuImagePO2DTO( skuImage ) );
        }

        return list1;
    }

    protected List<SkuPlatformAttributeValueDTO> skuPlatformAttributeValueListToSkuPlatformAttributeValueDTOList(List<SkuPlatformAttributeValue> list) {
        if ( list == null ) {
            return null;
        }

        List<SkuPlatformAttributeValueDTO> list1 = new ArrayList<SkuPlatformAttributeValueDTO>( list.size() );
        for ( SkuPlatformAttributeValue skuPlatformAttributeValue : list ) {
            list1.add( skuPlatformAttributeValuePO2DTO( skuPlatformAttributeValue ) );
        }

        return list1;
    }

    protected List<SkuSaleAttributeValueDTO> skuSaleAttributeValueListToSkuSaleAttributeValueDTOList(List<SkuSaleAttributeValue> list) {
        if ( list == null ) {
            return null;
        }

        List<SkuSaleAttributeValueDTO> list1 = new ArrayList<SkuSaleAttributeValueDTO>( list.size() );
        for ( SkuSaleAttributeValue skuSaleAttributeValue : list ) {
            list1.add( skuSaleAttributeValuePOs2DTOs( skuSaleAttributeValue ) );
        }

        return list1;
    }
}
