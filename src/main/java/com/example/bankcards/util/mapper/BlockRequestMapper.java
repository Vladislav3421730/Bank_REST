package com.example.bankcards.util.mapper;

import com.example.bankcards.dto.BlockRequestDto;
import com.example.bankcards.model.BlockRequest;
import com.example.bankcards.model.Card;
import com.example.bankcards.model.User;
import com.example.bankcards.model.enums.BlockStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BlockRequestMapper {

    @Mapping(source = "user", target = "userId")
    @Mapping(source = "card", target = "cardId")
    BlockRequestDto toDto(BlockRequest blockRequest);

    default UUID mapCardToDto(Card card) {
        return card == null ? null : card.getId();
    }

    default UUID mapUserToDto(User user) {
        return user == null ? null : user.getId();
    }

    default String mapFromCardStatusToString(BlockStatus blockStatus) {
        return blockStatus.name();
    }
}
