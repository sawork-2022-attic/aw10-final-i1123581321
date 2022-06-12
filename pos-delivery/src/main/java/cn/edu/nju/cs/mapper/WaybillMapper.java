package cn.edu.nju.cs.mapper;

import cn.edu.nju.cs.dto.WaybillDto;
import cn.edu.nju.cs.model.Waybill;
import org.mapstruct.Mapper;

@Mapper
public interface WaybillMapper {

    Waybill toWaybill(WaybillDto waybillDto);

    WaybillDto toWaybillDto(Waybill waybill);
}
