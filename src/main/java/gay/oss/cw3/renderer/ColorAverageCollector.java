package gay.oss.cw3.renderer;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class ColorAverageCollector implements Collector<float[], ColorAverageCollector.ResultHolder, float[]> {
    @Override
    public Supplier<ResultHolder> supplier() {
        return () -> new ResultHolder(0, new float[]{0f, 0f, 0f});
    }

    @Override
    public BiConsumer<ResultHolder, float[]> accumulator() {
        return (res, floats) -> {
            res.set(res.count+1, new float[] {
                    res.accumulator[0] + floats[0],
                    res.accumulator[1] + floats[1],
                    res.accumulator[2] + floats[2]
            });
        };
    }

    @Override
    public BinaryOperator<ResultHolder> combiner() {
        return (res1, res2) -> new ResultHolder(res1.count + res2.count, new float[] {
                res1.accumulator[0] + res2.accumulator[0],
                res1.accumulator[1] + res2.accumulator[1],
                res1.accumulator[2] + res2.accumulator[2]
        });
    }

    @Override
    public Function<ResultHolder, float[]> finisher() {
        return res -> new float[]{
                res.accumulator[0] / res.count,
                res.accumulator[1] / res.count,
                res.accumulator[2] / res.count
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of(Characteristics.UNORDERED);
    }

    static class ResultHolder {
        int count;
        float[] accumulator;

        ResultHolder(int count, float[] accumulator) {
            this.set(count, accumulator);
        }

        void set(int count, float[] accumulator) {
            this.count = count;
            this.accumulator = accumulator;
        }
    }
}
