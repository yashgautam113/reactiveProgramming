package com.reactivespring.handler;

import com.reactivespring.domain.Review;
import com.reactivespring.exception.ReviewNotFoundException;
import com.reactivespring.repository.ReviewReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.xml.validation.Validator;
import java.util.Collections;

@Component
public class ReviewHandler {

    private ReviewReactiveRepository reviewReactiveRepository;
    

    public ReviewHandler(ReviewReactiveRepository reviewReactiveRepository){
        this.reviewReactiveRepository = reviewReactiveRepository;
    }

    public Mono<ServerResponse> addReview(ServerRequest request) {
        return request.bodyToMono(Review.class)
//                adding bean validation
                .flatMap(review -> {
                    return reviewReactiveRepository.save(review);
                }).flatMap(savedReview -> {
                    return ServerResponse.status(HttpStatus.CREATED)
                            .bodyValue(savedReview);
                });
    }

    public Mono<ServerResponse> getReviews(ServerRequest request) {
//        say for getting a review based on a query Param
        var movieInfoId = request.queryParam("movieInfoId");

//        var reviewFlux = reviewReactiveRepository.findAll();
        if(movieInfoId.isPresent()){
            var reviewFlux = reviewReactiveRepository.findReviewsByMovieInfoId(Long.valueOf(movieInfoId.get()));
            return ServerResponse.ok().body(reviewFlux, Review.class);
        }else{
            var reviewFlux = reviewReactiveRepository.findAll();
            return ServerResponse.ok().body(reviewFlux, Review.class);
        }
//        return ServerResponse.ok().body(reviewFlux, Review.class);
    }

    public Mono<ServerResponse> updateReviews(ServerRequest request) {
        var reviewId = request.pathVariable("id");

        var existingReview = reviewReactiveRepository.findById(reviewId)
                .switchIfEmpty(Mono.error(new ReviewNotFoundException("Review not found for the given Id" + reviewId )));

        return existingReview.flatMap(review -> request.bodyToMono((Review.class))
                .map(reqReview -> {
                    review.setComment(reqReview.getComment());
                    review.setRating(reqReview.getRating());
                    return review;
                })
                .flatMap(finalreview -> reviewReactiveRepository.save(finalreview))
                .flatMap(savedReview -> ServerResponse.ok().bodyValue(savedReview))

//                for delete operation add .then(ServerResponse.noContent().build());
        );
    }
}
