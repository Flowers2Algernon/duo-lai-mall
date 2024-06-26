package com.cskaoyan.mall.product.converter.dto;

import com.cskaoyan.mall.product.dto.PlatformAttributeInfoDTO;
import com.cskaoyan.mall.product.dto.PlatformAttributeValueDTO;
import com.cskaoyan.mall.product.model.PlatformAttributeInfo;
import com.cskaoyan.mall.product.model.PlatformAttributeValue;
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
public class PlatformAttributeInfoConverterImpl implements PlatformAttributeInfoConverter {

    @Override
    public PlatformAttributeInfoDTO platformAttributeInfoPO2DTO(PlatformAttributeInfo platformAttributeInfo) {
        if ( platformAttributeInfo == null ) {
            return null;
        }

        PlatformAttributeInfoDTO platformAttributeInfoDTO = new PlatformAttributeInfoDTO();

        platformAttributeInfoDTO.setId( platformAttributeInfo.getId() );
        platformAttributeInfoDTO.setAttrName( platformAttributeInfo.getAttrName() );
        platformAttributeInfoDTO.setCategoryId( platformAttributeInfo.getCategoryId() );
        platformAttributeInfoDTO.setCategoryLevel( platformAttributeInfo.getCategoryLevel() );
        platformAttributeInfoDTO.setAttrValueList( platformAttributeValueListToPlatformAttributeValueDTOList( platformAttributeInfo.getAttrValueList() ) );

        return platformAttributeInfoDTO;
    }

    @Override
    public List<PlatformAttributeInfoDTO> platformAttributeInfoPOs2DTOs(List<PlatformAttributeInfo> platformAttributeInfos) {
        if ( platformAttributeInfos == null ) {
            return null;
        }

        List<PlatformAttributeInfoDTO> list = new ArrayList<PlatformAttributeInfoDTO>( platformAttributeInfos.size() );
        for ( PlatformAttributeInfo platformAttributeInfo : platformAttributeInfos ) {
            list.add( platformAttributeInfoPO2DTO( platformAttributeInfo ) );
        }

        return list;
    }

    @Override
    public PlatformAttributeValueDTO platformAttributeValuePO2DTO(PlatformAttributeValue platformAttributeValue) {
        if ( platformAttributeValue == null ) {
            return null;
        }

        PlatformAttributeValueDTO platformAttributeValueDTO = new PlatformAttributeValueDTO();

        platformAttributeValueDTO.setId( platformAttributeValue.getId() );
        platformAttributeValueDTO.setValueName( platformAttributeValue.getValueName() );
        platformAttributeValueDTO.setAttrId( platformAttributeValue.getAttrId() );

        return platformAttributeValueDTO;
    }

    protected List<PlatformAttributeValueDTO> platformAttributeValueListToPlatformAttributeValueDTOList(List<PlatformAttributeValue> list) {
        if ( list == null ) {
            return null;
        }

        List<PlatformAttributeValueDTO> list1 = new ArrayList<PlatformAttributeValueDTO>( list.size() );
        for ( PlatformAttributeValue platformAttributeValue : list ) {
            list1.add( platformAttributeValuePO2DTO( platformAttributeValue ) );
        }

        return list1;
    }
}
