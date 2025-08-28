package com.chanbinme.springconcurrency.repository;

import com.chanbinme.springconcurrency.redisentity.SampleObject;
import org.springframework.data.repository.CrudRepository;

public interface SampleObjectRepository extends CrudRepository<SampleObject, Long> {

}
