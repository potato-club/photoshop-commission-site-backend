package com.community.site.config;

import com.community.site.jwt.JwtTokenProvider;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

@Component
public class NoAuthArgumentResolver implements HandlerMethodArgumentResolver {

    private JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(NoAuth.class);
    }

    @Override
    public String resolveArgument(final MethodParameter parameter, final ModelAndViewContainer mavContainer,
                                  final NativeWebRequest webRequest, final WebDataBinderFactory binderFactory) {

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String accessToken = request.getHeader("authorization").split("bearer ")[1].substring(7);

//        String refreshToken = jwtTokenProvider.resolveRefreshToken(request);

        return accessToken;
    }
}
