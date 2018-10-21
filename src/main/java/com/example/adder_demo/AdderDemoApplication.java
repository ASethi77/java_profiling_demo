package com.example.adder_demo;

import java.util.Random;
import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@SpringBootApplication
public class AdderDemoApplication {

	private static ForkJoinPool pool;

	AdderDemoApplication() {
		pool = new ForkJoinPool(8);
	}

	@RequestMapping(value = "/add")
	public String add(@RequestParam("array_len") int arrayLen, @RequestParam("balanced") boolean balanced) {
		final int MAX_INT_VALUE = 100;

        // Initialize random array to test under parallel adder
        int[] input_array = new int[arrayLen];
        Random int_generator = new Random();
		System.out.println("Setting up list...");
        for (int i = 0; i < input_array.length; i++) {
            input_array[i] = int_generator.nextInt(MAX_INT_VALUE);
        }
        
        ArrayAdder adder = new ArrayAdder(input_array, 0, input_array.length, balanced, false);
		long startTime = System.nanoTime();
		System.out.println("starting computation");
        int sum = pool.invoke(adder);
		long endTime = System.nanoTime();
		long elapsed = (endTime - startTime) / 1000000;
		System.out.println("summation took " + elapsed + " milliseconds");
		return String.format("<p>Sum is: %d</p>", sum);
	}

	@RequestMapping(value = "/leaky_add")
	public String leakyAdd(@RequestParam("array_len") int arrayLen, @RequestParam("balanced") boolean balanced) {
		final int MAX_INT_VALUE = 100;

        // Initialize random array to test under parallel adder
        int[] input_array = new int[arrayLen];
        Random int_generator = new Random();
		System.out.println("Setting up list...");
        for (int i = 0; i < input_array.length; i++) {
            input_array[i] = int_generator.nextInt(MAX_INT_VALUE);
        }
        
        LeakyArrayAdder adder = new LeakyArrayAdder(input_array, 0, input_array.length, balanced, true);
		ForkJoinPool p = new ForkJoinPool(8);
		long startTime = System.nanoTime();
		System.out.println("starting computation");
        int sum = p.invoke(adder);
		long endTime = System.nanoTime();
		long elapsed = (endTime - startTime) / 1000000;
		System.out.println("summation took " + elapsed + " milliseconds");
		return String.format("<p>Sum is: %d</p>", sum);
	}

	public static void main(String[] args) {
		SpringApplication.run(AdderDemoApplication.class, args);
	}
}
