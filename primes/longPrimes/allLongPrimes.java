package longPrimes;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class allLongPrimes 
{
	//Float.MAX_VALUE: 340,282,346,638,528,860,000,000,000,000,000,000,000.000000
	//double.MAX_VALUE: (2-2-52)·21023.
	//int max: 														2,147,483,647
	//Long.MAX_VALUE: is								9,223,372,036,854,775,807
	//first version actually stuck on 217M
	//next version dont stuck but still slow. too much numbers.
	//long version - keep solving on 32-80 ms most of the time, crashes of memeory on 2741
	/*
	 * goal - reach 140,000,000 primes in 10 minutes
	 * 4.319%
	 * 3241M
	*/	
	
	private final static String fileDir = "G:\\primes";
	
	private final static int Million = 1000000;//1000000	
	private final static int poolSize = Million;

	private final static long ms = Million;
	private final static long sec = 1000*ms;
	private final static long minute = 60*sec;
	private final static long hour = 3600*minute;
	
	//contain all the primes
	private static ArrayListLong primesPool = new ArrayListLong();
	
	//contain the searched numbers
	private final static boolean[] numbersPool = new boolean[poolSize];
	private static long currentLong;
	private static final long poolSizeLong = poolSize;
	private static long ourUpperBound = poolSizeLong;
	private static long sqrtUpperBound = (long) Math.sqrt(ourUpperBound);
	private static long ourLowerBound = 0;
	
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
		currentLong = currentIndex;
		boolean loop = true;
		while(loop)
		{			
			resetTable();
			
			//remove primes multiplies
			removePrevPrimes();
			
			while(currentIndex < poolSize) {
				if(numbersPool[currentIndex])	isPrime(currentLong);
				currentLong = currentLong + 2;
				currentIndex = currentIndex + 2;

				if(currentLong == Long.MAX_VALUE) 
				{
					printLap();
					loop = false;
					currentIndex = poolSize;
				}
			}			

			currentIndex = 1;
			ourUpperBound += poolSizeLong;
			ourLowerBound += poolSizeLong;
			sqrtUpperBound = (int) Math.sqrt(ourUpperBound);
			if(loop & verbose) printLap();	
		}
	}

	private static long upperbound = Million;
	private static void printLap() {
		long temp = System.nanoTime();
		float div = primesPool.size()/((float) upperbound);
		System.out.printf("primes count : %d/%dM\t%f%%\t%dms/%ds\n",
				primesPool.size()/Million, upperbound/Million,
				div, (temp-current)/ms, (temp-time)/sec);			

		upperbound += Million;
		current = System.nanoTime();		
	}

	private static void saveReadable() {
		PrintWriter out = null;
		try {
			primesPool.Itrartor();
			long bg = primesPool.next();
			long oldTitle;
			
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
	private static void isPrime(long prime) {
		primesPool.addLast(prime);		
		/*System.out.print(prime + " ");
		try {Thread.sleep(10);} 
		catch (InterruptedException e) {e.printStackTrace();}*/
		if(isRanRemInBound)//if viewed/all>0.5 we dont need to remove rest. this is what we validating. it is more efficient
			removePrimeFromRest(prime);	
	}

	private static void removePrimeFromRest(long prime) {
		if(sqrtUpperBound < prime) {
			isRanRemInBound = false;
			return;
		}
		int reminder;
		long curr = prime*prime;
		isRanRemInBound = poolSize > curr;
		while(ourUpperBound > curr)
		{
			reminder = ((int) (curr%poolSizeLong));
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
	private static void removePrimeFromRest(long prime, long magnified) {		
		int reminder;
		Long curr = magnified;
		while(ourUpperBound > curr)
		{
			reminder = ((int) (curr%poolSizeLong));
			numbersPool[reminder] = false;
			curr += prime;			
		}		
	}

	//purpose - to make the comparison of bigIntegers just one time
	private static int sqrtIndex = 0;
	private static void removePrevPrimes() {		
		primesPool.Itrartor();
		boolean loop = primesPool.hasNext();
		long prime;
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
				long curr = ((ourLowerBound/prime)*prime);			
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