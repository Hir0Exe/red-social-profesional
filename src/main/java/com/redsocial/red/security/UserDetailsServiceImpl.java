package com.redsocial.red.security;

import com.redsocial.red.entity.Usuario;
import com.redsocial.red.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.debug("Intentando cargar usuario con email: {}", email);
        
        Usuario usuario = usuarioRepository.findByEmailWithRoles(email)
                .orElseThrow(() -> {
                    logger.error("Usuario no encontrado con email: {}", email);
                    return new UsernameNotFoundException("Usuario no encontrado con email: " + email);
                });

        logger.debug("Usuario encontrado: {} - Activo: {} - Email verificado: {}", 
                usuario.getEmail(), usuario.isActivo(), usuario.isEmailVerificado());

        UserDetails userDetails = UserDetailsImpl.build(usuario);
        logger.debug("UserDetails construido - Enabled: {} - AccountNonLocked: {}", 
                userDetails.isEnabled(), userDetails.isAccountNonLocked());

        return userDetails;
    }
}