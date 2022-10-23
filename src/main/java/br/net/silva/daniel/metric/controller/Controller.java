package br.net.silva.daniel.metric.controller;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/test")
@Timed
public class Controller {

    private final AtomicInteger testGauge;
    private final Timer findTimeName;

    private final Counter counter;

    public Controller(MeterRegistry meterRegistry) {
        this.testGauge = meterRegistry.gauge("custom_gauge", new AtomicInteger(0));
        this.findTimeName = meterRegistry.timer("http_requests", "method", "GET");
        this.counter = meterRegistry.counter("quantidade_requests");
    }

    @GetMapping("/name")
    public ResponseEntity<String> getName() {
        testGauge.set(getRandomNumberInRange(0, 100));
        return findTimeName.record(() -> {
            counter.increment();
            return ResponseEntity.ok("Hello World");
        });
    }

    private static int getRandomNumberInRange(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
}
