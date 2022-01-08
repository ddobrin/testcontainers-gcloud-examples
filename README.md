Examples using the [Testcontainers GCloud module](https://www.testcontainers.org/modules/gcloud/) to test applications locally.

All examples leverage the Google Cloud SDK and emulators provided by Google for several services.

The repository provides examples for:
1. Cloud Datastore
2. Cloud Firestore
3. Cloud PubSub
4. Cloud spanner

All examples are provided in 3 flavors:
1. No framework in use - directly leverage Testcontainers
2. Spring Cloud GCP 1.x - as maintained by the Spring Team
3. Spring Cloud GCP 2.x - latest released version, as maintained at this time by Google Cloud Teams

To run all the tests: `mvn verify`
