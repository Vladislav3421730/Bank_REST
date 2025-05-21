package com.example.bankcards.util.mapper;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.request.RegisterUserRequestDto;
import com.example.bankcards.model.Card;
import com.example.bankcards.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    CardMapper cardMapper = Mappers.getMapper(CardMapper.class);

    User toNewEntity(RegisterUserRequestDto registerUserRequestDto);

    @Mapping(source = "cards", target = "cards")
    UserDto toDto(User user);

    default List<CardDto> mapFromCardListToCardDtoList(List<Card> cards) {
        if (cards == null) {
            return null;
        }
        return cards.stream()
                .map(cardMapper::toDto)
                .collect(Collectors.toList());
    }


}
