package com.chanbinme.springbatch.batchprocessing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

/**
 * ItemProcessor 인터페이스를 구현한 클래스
 * ItemProcessor는 입력된 데이터를 가공하는 역할을 한다.
 * 입력된 데이터를 가공하여 새로운 데이터를 생성하는 메서드인 process()를 구현해야 한다.
 * process() 메서드는 입력된 데이터를 인자로 받아서 가공된 데이터를 반환한다.
 * 가공된 데이터는 다음 단계로 전달된다.
 */
@Slf4j
public class PersonItemProcessor implements ItemProcessor<Person, Person> {

    @Override
    public Person process(Person person) throws Exception {
        String firstName = person.firstName().toUpperCase();
        String lastName = person.lastName().toUpperCase();

        Person transformedPerson = new Person(firstName, lastName);

        log.info("Converting ({}) into ({})", person, transformedPerson);

        return transformedPerson;
    }

}
