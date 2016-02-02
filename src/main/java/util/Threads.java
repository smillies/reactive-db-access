package util;

import java.util.concurrent.ThreadFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class Threads {

    public static ThreadFactory newThreadFactory(String nameFormat, boolean createDaemons) {
        return new ThreadFactoryBuilder()
                    .setNameFormat(nameFormat)
                    .setDaemon(createDaemons)
                    .build();
    }

}
