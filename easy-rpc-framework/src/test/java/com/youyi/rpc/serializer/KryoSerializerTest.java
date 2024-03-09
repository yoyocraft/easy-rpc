package com.youyi.rpc.serializer;

import java.io.IOException;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
class KryoSerializerTest {

    KryoSerializer serializer = new KryoSerializer();

    @Test
    void serialize() throws IOException {
        Person person = new Person("John", 30);
        byte[] bytes = serializer.serialize(person);
        Person deserialize = serializer.deserialize(bytes, Person.class);
        System.out.println(deserialize.getAge());
    }

    @Test
    void deserialize() {
    }

    @Getter
    @Setter
    public static class Person {

        private String name;
        private int age;

        public Person() {
        }

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }
}