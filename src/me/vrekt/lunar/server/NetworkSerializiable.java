package me.vrekt.lunar.server;

import java.io.IOException;

/**
 * Created by Rick on 3/18/2017.
 */
public interface NetworkSerializiable {
    byte[] serialze() throws IOException;
    void deserialize(byte[] bytes) throws IOException;
}
