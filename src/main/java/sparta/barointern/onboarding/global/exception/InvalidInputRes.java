package sparta.barointern.onboarding.global.exception;

public record InvalidInputRes(
    String field,
    String message
) {

}