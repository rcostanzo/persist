package net.sf.persist;

import java.io.IOException;

public interface SizeAware {
    int size() throws IOException;
}
