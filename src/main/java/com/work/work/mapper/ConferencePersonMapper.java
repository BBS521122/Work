package com.work.work.mapper;

import com.work.work.dto.ReceiptFormDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ConferencePersonMapper {

    @Insert("insert into receipt(unit, name, gender, phone, email, roomType, arrivalMethod, " +
            "arrivalTrain, arrivalTime, returnMethod, returnTrain, returnTime, remarks, conferenceId) " +
            "values (#{unit},#{name},#{gender}," +
            "#{phone},#{email},#{roomType},#{arrivalMethod},#{arrivalTrain}," +
            "#{arrivalTime},#{returnMethod},#{returnTrain},#{returnTime},#{remarks},#{id})")
    int submit(ReceiptFormDTO receiptFormDTO);

    @Select("select * from receipt where conferenceId=#{conferenceId}")
    List<ReceiptFormDTO> getReceipts(int conferenceId);
}
