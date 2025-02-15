package sparta.barointern.onboarding.global.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class CommonResponse<T> {

    private Integer code; // 커스텀 응답 코드
    private String message; // 응답에 대한 설명
    private T data; // 응답에 필요한 데이터

    /**
     * 에러 발생 시 특정 에러에 맞는 응답하는 메서드 - data 필드가 필요 없는 경우
     */
    public static CommonResponse<CommonEmptyRes> error(ErrorCase errorCase) {

        return CommonResponse.<CommonEmptyRes>builder()
            .code(errorCase.getCode())
            .message(errorCase.getMessage())
            .data(new CommonEmptyRes())
            .build();
    }

    /**
     * 에러 발생 시 특정 에러에 맞는 응답하는 메서드 - data 필드가 필요한 경우
     */
    public static <T> CommonResponse<T> error(ErrorCase errorCase, T data) {
        return CommonResponse.<T>builder()
            .code(errorCase.getCode())
            .message(errorCase.getMessage())
            .data(data)
            .build();
    }
}
