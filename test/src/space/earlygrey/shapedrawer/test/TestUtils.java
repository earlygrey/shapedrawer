package space.earlygrey.shapedrawer.test;

import java.text.DecimalFormat;

public class TestUtils {

    public static class RollingAverage {

        private int size;
        private double total = 0d;
        private int index = 0;
        private double samples[];
        private static final DecimalFormat format = new DecimalFormat("##.##");

        public RollingAverage(int size) {
            this.size = size;
            samples = new double[size];
            for (int i = 0; i < size; i++) samples[i] = 0d;
        }

        public void add(double x) {
            total -= samples[index];
            samples[index] = x;
            total += x;
            if (++index == size) index = 0; // cheaper than modulus
        }

        public double getAverage() {
            return total / size;
        }

        @Override
        public String toString() {
            return format.format(getAverage());
        }
    }
}
