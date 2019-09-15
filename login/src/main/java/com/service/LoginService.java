package com.service;

import com.Constant;
import com.dao.UserRepository;
import com.entry.UserEntry;
import com.exception.StatusException;
import com.util.IdCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class LoginService extends BaseService {

    @Autowired
    private UserRepository userRepository;

    @Async(Constant.IO_THREAD_NAME)
    public CompletableFuture<UserEntry> login(String username, String password) throws StatusException {
        //TODO 多点登录判断
        Optional<UserEntry> user = userRepository.findByUserNameAndPassWord(username, password);

        //TODO 当前是没账号，送账号，正式的要请求SDK或者  通过账号密码系统
        return CompletableFuture.completedFuture(user.orElseGet(() -> {
            UserEntry userEntry = new UserEntry(IdCreator.nextId(UserEntry.class));
            userEntry.setUserName(username);
            userEntry.setPassWord(password);
            userRepository.save(userEntry);
            return userEntry;
        }));

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onClose() {

    }


//    public UserEntry login(String username,String password) throws StatusException
//    {
//        //TODO 多点登录判断
//        Optional<UserEntry> user = userRepository.findByUserNameAndPassWord(username, password);
//
//
//        //TODO 当前是没账号，送账号，正式的要请求SDK或者  通过账号密码系统
//        return user.orElseGet(() -> {
//            UserEntry userEntry = new UserEntry(IdCreator.nextId(UserEntry.class));
//            userEntry.setUserName(username);
//            userEntry.setPassWord(password);
//            userRepository.save(userEntry);
//            return userEntry;
//        });
//
//    }
}
