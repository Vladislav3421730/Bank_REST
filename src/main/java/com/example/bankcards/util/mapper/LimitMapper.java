package com.example.bankcards.util.mapper;

import com.example.bankcards.dto.LimitDto;
import com.example.bankcards.model.Card;
import com.example.bankcards.model.Limit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LimitMapper {

    CardMapper cardMapper = Mappers.getMapper(CardMapper.class);

    @Mapping(source = "card", target = "cardId")
    @Mapping(source = "card", target = "number")
    LimitDto toDto(Limit limit);

    default UUID mapFromCardToUUID(Card card) {
        return card == null ? null : card.getId();
    }

    default String mapFromCardToString(Card card) {
        return card == null ? null : cardMapper.mapNumberFromCardToCardDto(card.getNumber());
    }


}
