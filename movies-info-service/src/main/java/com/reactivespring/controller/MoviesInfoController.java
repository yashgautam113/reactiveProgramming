package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MovieInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1")
public class MoviesInfoController {

    private MovieInfoService movieInfoService;

    public MoviesInfoController(MovieInfoService movieInfoService){
        this.movieInfoService = movieInfoService;
    }
    @PostMapping("/moviesInfos")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<MovieInfo>> addMovieInfo (@RequestBody @Valid MovieInfo movieInfo){
        return movieInfoService.adMovieInfo(movieInfo)
                .map(response -> ResponseEntity.ok().body(response))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build())).log();
    }

    @GetMapping ("/movieInfo/{id}")
    public Mono<MovieInfo> getMovieById(@PathVariable String id) {
         return movieInfoService.getMovieInfoById(id);
    }
}
