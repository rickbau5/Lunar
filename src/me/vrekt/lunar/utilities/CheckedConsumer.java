package me.vrekt.lunar.utilities;

import java.io.IOException;

/**
 * Created by Rick on 3/18/2017.
 */
@FunctionalInterface
public interface CheckedConsumer<T> {
    void accept(T t) throws IOException;
}
