package com.work.work.converter;

import com.work.work.dto.ReceiptDTO;
import com.work.work.dto.ReceiptFormDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReceiptsConverter {

    @Mapping(target = "conferenceName", ignore = true)
    ReceiptDTO receiptToReceiptDTO(ReceiptFormDTO receiptFormDTO);
}
