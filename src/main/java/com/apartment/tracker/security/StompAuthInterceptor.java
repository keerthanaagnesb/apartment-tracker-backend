package com.apartment.tracker.security;

import com.apartment.tracker.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/** Authenticates WebSocket (STOMP) connections: the browser sends the JWT in the CONNECT frame. */
@Component
@RequiredArgsConstructor
public class StompAuthInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String header = accessor.getFirstNativeHeader("Authorization");
            if (header == null || !header.startsWith("Bearer ")) {
                throw new MessagingException("Missing token");
            }
            String email = jwtService.validateAndGetEmail(header.substring(7))
                    .orElseThrow(() -> new MessagingException("Invalid or expired token"));
            UserDetails details = userDetailsService.loadUserByUsername(email);
            if (!details.isEnabled()) {
                throw new MessagingException("Account not approved");
            }
            accessor.setUser(new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities()));
        }
        return message;
    }
}
