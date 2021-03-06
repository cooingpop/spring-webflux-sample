package com.heowc.webflux.service;

import com.heowc.webflux.doamin.Study;
import com.heowc.webflux.repository.StudyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
public class DefaultStudyQueryService implements StudyQueryService {

    private final StudyRepository repository;

    @Autowired
    public DefaultStudyQueryService(StudyRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<Study> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public Mono<Study> add(Mono<Study> studyMono) {
        return studyMono.flatMap(repository::save);
    }

    @Override
    public Mono<Study> modify(Mono<Study> studyMono) {
        return studyMono
                .flatMap(s -> repository.findById(s.getId())
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "not exist")))
                        .map(s1 -> {
                            s1.setTitle(s.getTitle());
                            s1.setContent(s.getContent());
                            return s1;
                        }))
                .flatMap(repository::save);
    }

    @Override
    public Mono<Void> remove(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "incorrect param data")))
                .flatMap(repository::delete);
    }
}
