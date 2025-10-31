package org.csbf.ecomie.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Base64Util;
import org.csbf.ecomie.config.AuthContext;
import org.csbf.ecomie.mapper.UserTokenMapper;
import org.csbf.ecomie.repository.UserTokenRepository;
import org.csbf.ecomie.service.UserTokenService;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.UUID;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserTokenServiceImpl implements UserTokenService {

    private final UserTokenRepository repo;

    private final AuthContext authContext;

    private final Environment environment;

    private final UserTokenMapper mapper;


    @Override
    public String createToken() {
        return Base64Util.encode(UUID.randomUUID().toString());
    }



}
