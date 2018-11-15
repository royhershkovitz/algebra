package intPrimes;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class allIntPrimes 
{
	//Float.MAX_VALUE: 340,282,346,638,528,860,000,000,000,000,000,000,000.000000
	//double.MAX_VALUE: (2-2-52)·21023.
	//int max: 2147483647
	//Long.MAX_VALUE: is 9,223,372,036,854,775,807.
	//first version actually stuck on 217M
	//next version dont stuck but still slow. too much numbers.
	//int version is amazing 10-12 ms calculate for each!
	//primes count : 104/2145M	0.048943%	10/27231ms the total is less then half an minute!
	/*
	 * goal - reach 14000000 primes in 10 minutes
	 * 4.319%
	 * 3241M
	*/	
	
	private final static String fileDir = "G:\\primes";
	
	private final static int Million = 1000000;//1000000	
	private final static int poolSize = Million;//Integer.MAX_VALUE;
	
	//contain all the primes
	private static ArrayListInt primesPool = new ArrayListInt();
	
	//contain the searched numbers
	private final static boolean[] numbersPool = new boolean[poolSize];
	private static int currentIntger;
	private static int poolSizeInteger = poolSize;
	private static int ourUpperBound = poolSizeInteger;
	private static int sqrtUpperBound = (int) Math.sqrt(ourUpperBound);
	private static int ourLowerBound = 0;
	
	private static long time = 0;//start time
	private static long current = 0;
	private static boolean verbose = true;
	public static void main(String[] args) {
		if(verbose) {
			System.out.println("init data| load old primes");
			time = System.nanoTime();
			current = time;
			System.out.println("start searching ");
		}
		
		findPrimes();
	}	

	private static void resetTable() {
		for(int i = 0; i < poolSize; i++) numbersPool[i] = true;		
	}


	private static void findPrimes() {
		primesPool.addLast(2);
		int currentIndex = 3;
		currentIntger = currentIndex;
		boolean loop = true;
		while(loop)
		{			
			resetTable();
			
			//remove primes multiplies
			removePrevPrimes();
			
			while(currentIndex < poolSize) {
				if(numbersPool[currentIndex])	isPrime(currentIntger);
				currentIntger = currentIntger + 2;
				currentIndex = currentIndex + 2;

				if(currentIntger == Integer.MAX_VALUE) 
				{
					printLap();
					loop = false;
					currentIndex = poolSize;
				}
			}			

			currentIndex = 1;
			ourUpperBound += poolSizeInteger;
			ourLowerBound += poolSizeInteger;
			sqrtUpperBound = (int) Math.sqrt(ourUpperBound);
			if(loop & verbose) printLap();	
		}
	}

	private static void printLap() {
		long temp = System.nanoTime();
		float div = primesPool.size()/((float) upperbound);
		System.out.printf("primes count : %d/%dM\t%f%%\t%d/%dms\n",
				primesPool.size()/Million, upperbound/Million,
				div, (temp-current)/Million, (temp-time)/Million);			

		upperbound += Million;
		current = System.nanoTime();		
	}

	private static void saveReadable() {
		PrintWriter out = null;
		try {
			primesPool.Itrartor();
			int bg = primesPool.next();
			int oldTitle;
			
			while(primesPool.hasNext())
			{			
				oldTitle = bg/poolSize;
				File f = new File(fileDir+"\\"+ oldTitle + "M.txt");
				
				if(!f.exists()) {
					out = new PrintWriter(f);
					while(primesPool.hasNext() & oldTitle == bg/poolSize)
					{
						out.print(bg + " ");
						bg = primesPool.next();
					}
					out.close();
					out = null;
				}
				else {
					while(primesPool.hasNext() & oldTitle == bg/poolSize)	bg = primesPool.next();					
				}				
			}
		} catch (IOException e1) {e1.printStackTrace();}
		finally {if (out != null)	out.close();}
	}

	private static boolean isRanRemInBound = true;
	private static int upperbound = Million;
	private static void isPrime(int prime) {
		primesPool.addLast(prime);		
		/*System.out.print(prime + " ");
		try {Thread.sleep(10);} 
		catch (InterruptedException e) {e.printStackTrace();}*/
		if(isRanRemInBound)//if viewed/all>0.5 we dont need to remove rest. this is what we validating. it is more efficient
			removePrimeFromRest(prime);	
	}

	private static void removePrimeFromRest(int prime) {
		if(sqrtUpperBound < prime) {
			isRanRemInBound = false;
			return;
		}
		int reminder;
		int curr = prime*prime;
		//isRanRemInBound = poolSize > curr;
		while(ourUpperBound > curr)
		{
			reminder = curr%poolSizeInteger;
			//System.out.println(reminder + " ");
			numbersPool[reminder] = false;
			curr += prime;
		}
	}
	
	/**
	 * 
	 * @param prime the original prime
	 * @param magnified use it to adapt the prime to the new area
	 */
	private static void removePrimeFromRest(int prime, int magnified) {		
		int reminder;
		int curr = magnified;
		while(ourUpperBound > curr)
		{
			reminder = curr%poolSizeInteger;
			numbersPool[reminder] = false;
			curr += prime;			
		}		
	}

	//purpose - to make the comparison of bigIntegers just one time
	private static int sqrtIndex = 0;
	private static void removePrevPrimes() {		
		primesPool.Itrartor();
		boolean loop = primesPool.hasNext();
		int prime;
		int primeIndex = 0;
		while(loop) 
		{
			prime = primesPool.next();			
			if(primeIndex > sqrtIndex && sqrtUpperBound < prime) 
			{
				//System.out.println(sqrtUpperBound + " " + prime);
				sqrtIndex = primeIndex-1;
				loop = false;			
			}
			else {
				int curr = (ourLowerBound/prime)*prime;			
				if(curr < ourLowerBound)
					curr += prime;
				//System.out.println(prime + " " + curr + " " + ourLowerBound);			
				removePrimeFromRest(prime, curr);
				loop = primesPool.hasNext();
				primeIndex = primeIndex + 1;
			}
		}
	}
}