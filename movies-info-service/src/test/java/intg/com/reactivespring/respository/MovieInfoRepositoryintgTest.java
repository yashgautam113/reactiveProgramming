package com.reactivespring.respository;

import com.reactivespring.domain.MovieInfo;
import org.hamcrest.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")
class MovieInfoRepositoryintgTest {

    @Autowired
    MovieInfoRepository movieInfoRepository;

    @BeforeEach
    void setUp() {
        var movieinfos = List.of(new MovieInfo(null, "Batman Begins",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight",
                        2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

        movieInfoRepository.saveAll(movieinfos)
                .blockLast();
//        blockList will ensure that due to asynchronous operations taking place this saveAll occur before the
//        actual testCase execution
    }

    void saveMovieInfo(){
        var movieInfo1 = new MovieInfo(null, "XBatman Begins",
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));
        var movieInfoMono = movieInfoRepository.save(movieInfo1).log();
        StepVerifier.create(movieInfoMono)
                .assertNext(xMovieInfo -> {
                    assertNotNull(xMovieInfo.getMovieInfoId());
                    assertEquals(xMovieInfo.getName(), "XBatman Begins");
                })
                .verifyComplete();
    }

    @Test
    void findAll() {
//        Similar function for find by ID
        var movieInfoFlux = movieInfoRepository.findAll().log();
        StepVerifier.create(movieInfoFlux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void findByYear() {
//        Similar function for find by ID

        var movieInfoFlux = movieInfoRepository.findByYear(2005).log();
        StepVerifier.create(movieInfoFlux)
                .expectNextCount(1)
                .verifyComplete();
    }


}