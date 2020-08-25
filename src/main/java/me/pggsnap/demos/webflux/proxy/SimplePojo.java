package me.pggsnap.demos.webflux.proxy;

/**
 * @author pggsnap
 * @date 2020/4/22
 */
public class SimplePojo implements Pojo {
    @Override
    public int add(int x, int y) {
        return nadd(x, y);
    }

    @Override
    public int nadd(int x, int y) {
        return x + y;
    }
}
