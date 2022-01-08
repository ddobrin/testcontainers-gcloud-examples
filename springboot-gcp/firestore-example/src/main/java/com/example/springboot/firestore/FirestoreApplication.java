/*
 * Copyright 2020 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.springboot.firestore;

import com.google.cloud.firestore.annotation.DocumentId;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.google.cloud.spring.data.firestore.Document;
import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Repository;

@SpringBootApplication
public class FirestoreApplication {

  public static void main(String[] args) {
    SpringApplication.run(FirestoreApplication.class, args);
  }
}

@Document
class Person {
  @DocumentId
  private String id;
  private String name;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}

@Repository
interface PersonRepsitory extends FirestoreReactiveRepository<Person> {

}

