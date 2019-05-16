package com.example.webfluxsessiondemo.controller;

import com.example.webfluxsessiondemo.model.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

/**
 * @author wangxing
 * @create 2019/5/15
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private String nickname;

    @PutMapping("/update")
    public Mono<String> update(@RequestBody User user, WebSession session) {
        return Mono.justOrEmpty(user)
                .doOnNext(u -> {
                    System.out.println(Thread.currentThread().getName() + " " + session.getId() + " Setting to " + user.getNickName());
                    nickname = user.getNickName();
                    session.getAttributes().put("nickname", user.getNickName());
                })
                .flatMap(u -> session.changeSessionId())
                .thenReturn("success");
    }

    @GetMapping("/me")
    public User me(@SessionAttribute("nickname") String nickname, WebSession session) {
        System.out.println(Thread.currentThread().getName() + " " + session.getId()  + " Got " + nickname);
        return  new User("test", "this", nickname);
    }

    public String getNickname() {
        return this.nickname;
    }
}