package com.example.webfluxsessiondemo;

import com.example.webfluxsessiondemo.controller.UserController;
import com.example.webfluxsessiondemo.model.User;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebfluxSessionDemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebfluxSessionDemoApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserController userController;

    @Before
    public void setUp() throws Exception {
        webTestClient = webTestClient.mutate()
                .filter(new CookieExchangeFilterFunction())
                .build();
    }

    private WebTestClient.BodySpec<User, ?> me() {
        return webTestClient.get()
                .uri("/user/me")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(User.class)
                ;
    }

    private WebTestClient.BodySpec<String, ?> updateUser(String nickName) {
        User request = new User();
        request.setNickName(nickName);
        request.setId("id");
        request.setUserName("test");
        return webTestClient.put()
                .uri("/user/update")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(request), User.class)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(String.class)
                ;
    }


    @Test
    @SneakyThrows
    public void contextLoads() {
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            System.out.println("\nTest\n");
            String nickName = String.valueOf(random.nextLong());
            updateUser(nickName);
            String currentNickName = userController.getNickname();
            assert nickName.equals(currentNickName);
            me().value(Matchers.hasProperty("nickName", Matchers.equalTo(currentNickName)))
            ;
        }
    }

    static class CookieExchangeFilterFunction implements ExchangeFilterFunction {
        private Map<String, List<ResponseCookie>> cookieStore = new ConcurrentHashMap<>();

        @Override
        public Mono<ClientResponse> filter(ClientRequest clientRequest,
                ExchangeFunction exchange) {
            return exchange
                .exchange(withCookies(clientRequest))
                .doOnNext(clientResponse -> cookieStore.putAll(clientResponse.cookies()));
        }

        private ClientRequest withCookies(ClientRequest clientRequest) {
            return ClientRequest.from(clientRequest).cookies(cookies -> {
                        cookieStore.forEach((k, v) -> {
                            List<String> values = v.stream().map(c -> c.getValue()).collect(Collectors.toList());
                            cookies.put(k, values);
                        });
                    })
                    .build();
        }
    }
}