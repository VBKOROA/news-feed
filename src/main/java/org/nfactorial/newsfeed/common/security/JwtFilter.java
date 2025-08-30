package org.nfactorial.newsfeed.common.security;

import java.io.IOException;
import java.util.Optional;

import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtFilter implements Filter {
	public static final String AUTH_HEADER = "Authorization";
	public static final String BEARER_PREFIX = "Bearer ";
	public static final String ACCOUNT_ID_ATTR = "accountId";

	private final JwtUtil jwtUtil;
	private final TokenBlacklist tokenBlacklist;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
		ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest)request;
		getHeader(httpServletRequest)
			.flatMap(this::getBearerToken)
			.filter(token -> tokenBlacklist.hasToken(token) == false)
			.flatMap(jwtUtil::getAccountId)
			.ifPresent(accountId -> {
				request.setAttribute(ACCOUNT_ID_ATTR, accountId);
			});
		chain.doFilter(request, response);
	}

	private Optional<String> getHeader(HttpServletRequest httpServletRequest) {
		return Optional.ofNullable(httpServletRequest.getHeader(AUTH_HEADER));
	}

	private Optional<String> getBearerToken(String header) {
		if (header.startsWith(BEARER_PREFIX)) {
			return Optional.of(header.substring(BEARER_PREFIX.length()));
		}
		return Optional.empty();
	}
}
