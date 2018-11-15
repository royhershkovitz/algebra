
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.LinkedList;

public class InfinityPrimesProj 
{
	//Float.MAX_VALUE: 340,282,346,638,528,860,000,000,000,000,000,000,000.000000
	//double.MAX_VALUE: (2-2-52)·21023.
	//int max: 2147483647
	//Long.MAX_VALUE: is 9,223,372,036,854,775,807.
	//first version actually stuck on 217M
	//next version dont stuck but still slow. too much numbers.
	/*
	 * goal - reach 14000000 primes in 10 minutes
	 * 4.319%
	 * 3241M
	*/
	
	//TODO  work with bigNum bits
	
	private final static String fileListSER = "primeList.ser";
	
	private final static String fileDir = "G:\\primes";
	
	private final static String fileListSERPath = fileDir+"\\"+fileListSER;
	//private final static String readableFileFullPath = filePath+"\\"+readableVersionName;
	
	private final static int poolSize = 1000000;
	
	//contain all the primes
	private static LinkedList<BigInteger> primesPool = new LinkedList<BigInteger>();
	
	//contain the searched numbers
	private final static boolean[] numbersPool = new boolean[poolSize];
	//the current index on the watch list.
	private static int currentIndex;
	private static BigInteger currentBigInteger;
	private static BigInteger poolSizeBigInteger = BigInteger.valueOf(poolSize);
	//upper bound of scanned big Integers
	private static BigInteger ourUpperBound = poolSizeBigInteger.add(BigInteger.ZERO);
	private static BigInteger sqrtUpperBound = ourUpperBound.sqrt();
	//lower bound of scanned big Integers
	private static BigInteger ourLowerBound = BigInteger.valueOf(0);
	private static long time = 0;//start time
	private static long current = 0;
	private static boolean verbose = true;
	public static void main(String[] args) {
		if(verbose) {
			System.out.println("init data| load old primes");
			time = System.nanoTime();
			current = time;
		}
		
		//fetch primes that found before
		File f = new File(fileListSERPath);
		if(f.exists() && !f.isDirectory()) deserObj();
		//no file of our primes.
		else {
			currentIndex = 2;
			currentBigInteger = BigInteger.valueOf(2);
		}
		if(verbose) {
			System.out.println("data loaded in: " + (System.nanoTime()-current) + "ns");
			System.out.println("start searching ");
			current = System.nanoTime();
		}
		
		findPrimes();
	}
	
	
	private static void deserObj() {
		FileInputStream fin = null;
		ObjectInputStream ois = null;
		try {

			fin = new FileInputStream(fileListSERPath);
			ois = new ObjectInputStream(fin);
			primesPool = (LinkedList<BigInteger>) ois.readObject();

		} catch (Exception ex) {ex.printStackTrace();}
		finally {
			if (fin != null) 
				try {fin.close();}
				catch (IOException e) {e.printStackTrace();}			

			if (ois != null) 
				try {ois.close();}
				catch (IOException e) {e.printStackTrace();}	
		}

		
		currentBigInteger = primesPool.removeLast();
		ourLowerBound = currentBigInteger;
		ourUpperBound = ourLowerBound.add(poolSizeBigInteger);
		sqrtUpperBound = ourUpperBound.sqrt();
		if(verbose) {
			float div = primesPool.size()/ourLowerBound.floatValue();
			System.out.println("primes count : " + primesPool.size() + "/" + ourLowerBound.divide(poolSizeBigInteger) +
					"M " + div + "%");
		}
	}


	private static void resetTable() {
		for(int i = 0; i < poolSize; i++) numbersPool[i] = true;		
	}


	private static void findPrimes() {
		primesPool.add(BigInteger.valueOf(2));
		int currentIndex = 3;
		currentBigInteger = BigInteger.valueOf(currentIndex);
		boolean loop = true;
		while(loop)
		{
			resetTable();
			
			//remove primes multiplies
			removePrevPrimes();
			
			while(currentIndex < poolSize) {
				if(numbersPool[currentIndex])	isPrime(currentBigInteger);
				currentBigInteger = currentBigInteger.add(BigInteger.TWO);
				currentIndex = currentIndex + 2;
			}			
			
			currentIndex = 1;
			ourUpperBound = ourUpperBound.add(poolSizeBigInteger);
			ourLowerBound = ourLowerBound.add(poolSizeBigInteger);
			sqrtUpperBound = ourUpperBound.sqrt();
			if(verbose) {
				long temp = System.nanoTime();
				float div = primesPool.size()/ourLowerBound.floatValue();
				System.out.printf("primes count : %d/%sM\t%f%%\t%d/%dms\n",
						primesPool.size(), ourLowerBound.divide(poolSizeBigInteger).toString(),
						div, (temp-current)/1000000, (temp-time)/1000000);			
				
				current = System.nanoTime();				
			}
			
			if(ourLowerBound.divide(poolSizeBigInteger).intValue() > 400) {
				//after this we will stop and save after each run
				//saveReadable();
				loop = false;
			}
		}
	}

	private static void finish() {
		primesPool.addLast(ourLowerBound);//for later use
		
		FileOutputStream fout = null;
		ObjectOutputStream oos = null;
		try {
			File f = new File(fileListSERPath);
			if(!f.exists())
			{
				File dir = new File(fileDir);
				if(!dir.exists())	dir.mkdirs();
				f.createNewFile();
			}
			fout = new FileOutputStream(fileListSERPath);
			oos = new ObjectOutputStream(fout);			
			oos.writeObject(primesPool);
		} catch (IOException e1) {e1.printStackTrace();}
		finally {
			if (fout != null) 
				try {fout.close();}
				catch (IOException e) {e.printStackTrace();}			

			if (oos != null) 
				try {oos.close();}
				catch (IOException e) {e.printStackTrace();}	
		}
		
		saveReadable();
	}


	private static void saveReadable() {
		PrintWriter out = null;
		try {
			Iterator<BigInteger> iter = primesPool.iterator();
			BigInteger bg = iter.next();
			int oldTitle;
			
			while(iter.hasNext())
			{			
				oldTitle = bg.divide(poolSizeBigInteger).intValue();
				File f = new File(fileDir+"\\"+ oldTitle + "M.txt");
				
				if(!f.exists()) {
					out = new PrintWriter(f);
					while(iter.hasNext() & oldTitle == bg.divide(poolSizeBigInteger).intValue())
					{
						out.print(bg + " ");
						bg = iter.next();
					}
					out.close();
					out = null;
				}
				else {
					while(iter.hasNext() & oldTitle == bg.divide(poolSizeBigInteger).intValue())	bg = iter.next();					
				}				
			}
		} catch (IOException e1) {e1.printStackTrace();}
		finally {if (out != null)	out.close();}
	}


	private static void isPrime(BigInteger prime) {
		primesPool.addLast(prime);
		/*System.out.print(prime + " ");
		try {Thread.sleep(10);} 
		catch (InterruptedException e) {e.printStackTrace();}*/
		if(isRanRemInBound)//if viewed/all>0.5 we dont need to remove rest. this is what we validating. it is more efficient
			removePrimeFromRest(prime);	
	}

	private static boolean isRanRemInBound = true;
	private static void removePrimeFromRest(BigInteger prime) {		
		BigInteger reminder;
		BigInteger curr = prime;
		isRanRemInBound = false;
		while(ourUpperBound.compareTo(curr) > 0)
		{
			reminder = curr.remainder(poolSizeBigInteger);
			numbersPool[reminder.intValue()] = false;
			curr = curr.add(prime);		
			isRanRemInBound = true;
		}
	}
	
	/**
	 * 
	 * @param prime the original prime
	 * @param magnified use it to adapt the prime to the new area
	 */
	private static void removePrimeFromRest(BigInteger prime, BigInteger magnified) {		
		BigInteger reminder;
		BigInteger curr = magnified;
		while(ourUpperBound.compareTo(curr) > 0)
		{
			reminder = curr.remainder(poolSizeBigInteger);
			numbersPool[reminder.intValue()] = false;
			curr = curr.add(prime);			
		}		
	}
	
	//purpose - to make the comparison of bigIntegers just one time
	private static int sqrtIndex = 0;
	private static void removePrevPrimes() {		
		Iterator<BigInteger> iter = primesPool.iterator();
		boolean loop = iter.hasNext();
		BigInteger prime;
		int primeIndex = 0;
		while(loop) 
		{
			prime = iter.next();			
			if(primeIndex > sqrtIndex && sqrtUpperBound.compareTo(prime) < 0) 
			{
				//System.out.println(sqrtUpperBound + " " + prime);
				sqrtIndex = primeIndex-1;
				loop = false;			
			}
			else {
				BigInteger curr = ourLowerBound.divide(prime).multiply(prime);			
				if(curr.compareTo(ourLowerBound) < 0)
					curr = curr.add(prime);
				//System.out.println(prime + " " + curr + " " + ourLowerBound);			
				removePrimeFromRest(prime, curr);
				loop = iter.hasNext();
				primeIndex = primeIndex + 1;
			}
		}
	}

}