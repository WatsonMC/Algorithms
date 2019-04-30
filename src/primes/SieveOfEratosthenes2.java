package primes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class SieveOfEratosthenes2 {

	/**
	 * Returns a list of all primes from 2 to n 
	 * @param n
	 * limit of primes
	 * @return
	 * List of primes 
	 */
	public static List<Integer> getPrimes(int n){
		//create list of numbers from 0-n
		int[] integers = IntStream.range(0, n+1).toArray(); 
		boolean[] flags = new boolean[integers.length];
		Arrays.fill(flags, Boolean.TRUE);
		List<Integer> primes = new ArrayList<>();
		
		int p = 1;
		boolean nextPrimeFound = true;
		
		while(nextPrimeFound) {
			nextPrimeFound =false;
			for(int i = p+1; i<=n;i++) {
				if(flags[i] == true) {
					p = i;
					primes.add(p);
					nextPrimeFound = true;
					break;
				}
			}
			if(nextPrimeFound) {
				for(int i =2; i* p <=n;i++ ) {
					//mark flags
					flags[i*p] =false;
				}
			}
		}
		for(int i = primes.get(primes.size()-1)+1;i<=n;i++) {
			if(flags[i] == true) {primes.add(i);}
		}
		return primes;
	}
}
