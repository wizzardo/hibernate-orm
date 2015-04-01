package org.hibernate.util;

import java.lang.ref.SoftReference;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by wizzardo on 01.04.15.
 */
public class StringBuilderCache {

    private static Queue<SoftReference<StringBuilder>> queue = new ConcurrentLinkedQueue<SoftReference<StringBuilder>>();

    public static StringBuilder get() {
        SoftReference<StringBuilder> reference = queue.poll();
        StringBuilder sb = reference == null ? new StringBuilder() : reference.get();
        if (sb == null)
            sb = new StringBuilder();
        return sb;
    }

    public static void release(StringBuilder sb) {
        sb.setLength(0);
        queue.add(new SoftReference<StringBuilder>(sb));
    }
}
