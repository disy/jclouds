package org.jclouds.imagestore.benchmarks.reedsolomon;

import java.util.Random;

import org.jclouds.imagestore.imagegenerator.reedsolomon.GenericGF;
import org.jclouds.imagestore.imagegenerator.reedsolomon.GenericGF.GenericGFs;
import org.jclouds.imagestore.imagegenerator.reedsolomon.ReedSolomon;
import org.jclouds.imagestore.imagegenerator.reedsolomon.ReedSolomonDecoder;
import org.jclouds.imagestore.imagegenerator.reedsolomon.ReedSolomonEncoder;
import org.jclouds.imagestore.imagegenerator.reedsolomon.ReedSolomonException;
import org.perfidix.Benchmark;
import org.perfidix.annotation.Bench;
import org.perfidix.ouput.TabularSummaryOutput;
import org.perfidix.result.BenchmarkResult;

public class ReedSolomonBenchmark {

    static final int size = 10;

    static final int[][] completeData = new int[size][];
    static final int[][] onlyData = new int[size][];
    static {
        Random ran = new Random();
        for (int i = 0; i < size; i++) {
            byte[] data = new byte[1 << i + 3];
            ran.nextBytes(data);
            onlyData[i] = ReedSolomon.castToInt(data);
            completeData[i] = new int[(1 << i + 3) + data.length / 2];
            System.arraycopy(onlyData[i], 0, completeData[i], i, onlyData[i].length);

        }
    }
    static GenericGF field = GenericGFs.AZTEC_DATA_12.mGf;

    @Bench
    public void aztec12encode8() {
        int index = 0;
        ReedSolomonEncoder encoder = new ReedSolomonEncoder(field);
        encoder.encode(completeData[index], completeData[index].length - onlyData[index].length);
    }

    @Bench
    public void aztec12decode8() throws ReedSolomonException {
        int index = 0;
        ReedSolomonDecoder decoder = new ReedSolomonDecoder(field);
        decoder.decode(completeData[index], completeData[index].length - onlyData[index].length);
    }

    @Bench
    public void aztec12encode16() {
        int index = 1;
        ReedSolomonEncoder encoder = new ReedSolomonEncoder(field);
        encoder.encode(completeData[index], completeData[index].length - onlyData[index].length);
    }

    @Bench
    public void aztec12decode16() throws ReedSolomonException {
        int index = 1;
        ReedSolomonDecoder decoder = new ReedSolomonDecoder(field);
        decoder.decode(completeData[index], completeData[index].length - onlyData[index].length);
    }

    @Bench
    public void aztec12encode32() {
        int index = 2;
        ReedSolomonEncoder encoder = new ReedSolomonEncoder(field);
        encoder.encode(completeData[index], completeData[index].length - onlyData[index].length);
    }

    @Bench
    public void aztec12decode32() throws ReedSolomonException {
        int index = 2;
        ReedSolomonDecoder decoder = new ReedSolomonDecoder(field);
        decoder.decode(completeData[index], completeData[index].length - onlyData[index].length);
    }

    @Bench
    public void aztec12encode64() {
        int index = 3;
        ReedSolomonEncoder encoder = new ReedSolomonEncoder(field);
        encoder.encode(completeData[index], completeData[index].length - onlyData[index].length);
    }

    @Bench
    public void aztec12decode64() throws ReedSolomonException {
        int index = 3;
        ReedSolomonDecoder decoder = new ReedSolomonDecoder(field);
        decoder.decode(completeData[index], completeData[index].length - onlyData[index].length);
    }

    @Bench
    public void aztec12encode128() {
        int index = 4;
        ReedSolomonEncoder encoder = new ReedSolomonEncoder(field);
        encoder.encode(completeData[index], completeData[index].length - onlyData[index].length);
    }

    @Bench
    public void aztec12decode128() throws ReedSolomonException {
        int index = 4;
        ReedSolomonDecoder decoder = new ReedSolomonDecoder(field);
        decoder.decode(completeData[index], completeData[index].length - onlyData[index].length);
    }

    @Bench
    public void aztec12encode256() {
        int index = 5;
        ReedSolomonEncoder encoder = new ReedSolomonEncoder(field);
        encoder.encode(completeData[index], completeData[index].length - onlyData[index].length);
    }

    @Bench
    public void aztec12decode256() throws ReedSolomonException {
        int index = 5;
        ReedSolomonDecoder decoder = new ReedSolomonDecoder(field);
        decoder.decode(completeData[index], completeData[index].length - onlyData[index].length);
    }

    @Bench
    public void aztec12encode512() {
        int index = 6;
        ReedSolomonEncoder encoder = new ReedSolomonEncoder(field);
        encoder.encode(completeData[index], completeData[index].length - onlyData[index].length);
    }

    @Bench
    public void aztec12decode512() throws ReedSolomonException {
        int index = 6;
        ReedSolomonDecoder decoder = new ReedSolomonDecoder(field);
        decoder.decode(completeData[index], completeData[index].length - onlyData[index].length);
    }

    @Bench
    public void aztec12encode1024() {
        int index = 7;
        ReedSolomonEncoder encoder = new ReedSolomonEncoder(field);
        encoder.encode(completeData[index], completeData[index].length - onlyData[index].length);
    }

    @Bench
    public void aztec12decode1024() throws ReedSolomonException {
        int index = 7;
        ReedSolomonDecoder decoder = new ReedSolomonDecoder(field);
        decoder.decode(completeData[index], completeData[index].length - onlyData[index].length);
    }

    @Bench
    public void aztec12encode2048() {
        int index = 8;
        ReedSolomonEncoder encoder = new ReedSolomonEncoder(field);
        encoder.encode(completeData[index], completeData[index].length - onlyData[index].length);
    }

    @Bench
    public void aztec12decode2048() throws ReedSolomonException {
        int index = 8;
        ReedSolomonDecoder decoder = new ReedSolomonDecoder(field);
        decoder.decode(completeData[index], completeData[index].length - onlyData[index].length);
    }

    @Bench
    public void aztec12encode4096() {
        int index = 9;
        ReedSolomonEncoder encoder = new ReedSolomonEncoder(field);
        encoder.encode(completeData[index], completeData[index].length - onlyData[index].length);
    }

    @Bench
    public void aztec12decode4096() throws ReedSolomonException {
        int index = 9;
        ReedSolomonDecoder decoder = new ReedSolomonDecoder(field);
        decoder.decode(completeData[index], completeData[index].length - onlyData[index].length);
    }

    public static void main(String[] args) {
        Benchmark bench = new Benchmark();
        bench.add(ReedSolomonBenchmark.class);
        BenchmarkResult res = bench.run();

        TabularSummaryOutput output = new TabularSummaryOutput();
        output.visitBenchmark(res);

    }

}
