package com.github.shangtanlin.service;

import com.github.shangtanlin.model.dto.CodeLoginDTO;
import com.github.shangtanlin.model.dto.PasswordLoginDTO;
import com.github.shangtanlin.model.dto.RegisterDTO;
import com.github.shangtanlin.result.Result;

public interface UserService {
    Result<?> sendCode(String phoneNumber, String type);

    Result<?> register(RegisterDTO registerDTO);

    Result<?> loginByPassword(PasswordLoginDTO passwordLoginDTO);

    Result<?> loginByCode(CodeLoginDTO codeLoginDTO);


    Result<?> getProfile();
}
