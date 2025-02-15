package sparta.barointern.onboarding.global.exception;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.springframework.validation.FieldError;

@Mapper(componentModel = ComponentModel.SPRING) // 빈으로 주입 받을 수 있음
public interface InvalidInputMapper {

    // FieldError 의 defaultMessage -> message 이름 변경
    @Mapping(source = "defaultMessage", target = "message")
    InvalidInputRes toInvalidInputResponseDto(FieldError fieldError);
}
