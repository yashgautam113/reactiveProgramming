package com.reactivespring.controller;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.EqualToJsonPattern;
import com.reactivespring.domain.Movie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Objects;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.get;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 8084)
@TestPropertySource(
        properties = {
                "restClient.moviesInfoUrl: http://localhost:8084/v1/moviesInfos",
                "restClient.reviewsUrl: http://localhost:8084/v1/reviews"
        }
)
public class MoviesControllerIntgTest {

    @Autowired
    private WebTestClient webTestClient;
    @Test
    void retrieveMovieById(){
        var movieID = "abc";

//        it will pick response from the file at location
        stubFor(get(urlEqualTo("/v1/moviesInfos" + "/" + movieID))
                .willReturn(aResponse()
                .withHeader("Content-Type",  "application/json")
                .withBodyFile("movieinfo.json"))
        );
        stubFor(get(urlPathEqualTo("/v1/reviews"))
                .willReturn(aResponse()
                        .withHeader("Content-Type",  "application/json")
                        .withBodyFile("reviews.json"))
        );
        webTestClient.get()
                .uri("/v1/movies/{id}", movieID)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Movie.class)
                .consumeWith(movieEntityExchangeResult -> {
                    var movie = movieEntityExchangeResult.getResponseBody();
                    assert Objects.requireNonNull(movie).getReviewList().size() == 2;
                });

//        for creating a 4xxError

//        stubFor(get(urlEqualTo("/v1/moviesInfos" + "/" + movieID))
//                .willReturn(aResponse()
//                        .withStatus(404)
//                ));

//         webTestClient.get()
//                .uri("/v1/movies/{id}", movieID)
//                .exchange()
//                .expectStatus()
//                 .is4xxClientError();

//        retry testing unit teST
        WireMock.verify(1,getRequestedFor(urlEqualTo("/v1/moviesInfos" + "/" + movieID)));
    }
}
