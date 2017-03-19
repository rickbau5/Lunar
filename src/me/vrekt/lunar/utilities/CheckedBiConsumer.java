package me.vrekt.lunar.utilities;

import java.io.IOException;

/**
 * Created by Rick on 3/18/2017.
 */
@FunctionalInterface
public interface CheckedBiConsumer<T, U> {
    void accept(T t, U u) throws IOException;
}
