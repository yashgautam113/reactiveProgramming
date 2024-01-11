package com.reactivespring.controller;

import com.reactivespring.service.MovieInfoService;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.*;

@WebFluxTest(controllers = MoviesInfoController.class)
class MoviesInfoControllerUnitTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private MovieInfoService movieInfoService;
}