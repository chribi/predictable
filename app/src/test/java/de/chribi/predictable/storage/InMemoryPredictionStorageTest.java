package de.chribi.predictable.storage;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class InMemoryPredictionStorageTest extends PredictionStorageTest {
    @Override
    PredictionStorage createStorage() {
        return new InMemoryPredictionStorage();
    }
}
