package com.youyi.rpc.serial;

import java.io.IOException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://github.com/yoyocraft">youyi</a>
 */
@Slf4j
class KryoSerializerTest {

    private final KryoSerializer serializer = new KryoSerializer();

    @Test
    void serialize() throws IOException {
        Person person = new Person("John", 30);
        byte[] bytes = serializer.serialize(person);
        Person deserialize = serializer.deserialize(bytes, Person.class);
        log.info("age: {}", deserialize.getAge());
    }

    @Test
    void deserialize() {
    }

    @Getter
    @Setter
    public static class Person {

        private String name;
        private int age;

        Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }
}