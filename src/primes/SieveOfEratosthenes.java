package primes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class SieveOfEratosthenes {

	/**
	 * Returns a list of all primes from 2 to n 
	 * @param n
	 * limit of primes
	 * @return
	 * List of primes 
	 */
	public static List<Integer> getPrimes(int n){
		//create list of numbers from 0-n
		boolean[] flags = new boolean[n+1];
		Arrays.fill(flags, Boolean.TRUE);
		List<Integer> primes = new ArrayList<>();
		
		for(int p = 2; p*p <=n;p++) { //p*p because when we reach Sqrt(n) we will have covered everytrhing up to n
			if(flags[p] == true) {
				//is prime
				primes.add(p);
				for(int i = p*p; i<=n; i+=p) {//start i at p, add p ech time?
					flags[i] = false;
				}
			}
		}
		return primes;
	}
}
