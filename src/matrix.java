import java.util.*;
public class matrix {
	Scanner scan = new Scanner(System.in);
	private double[][] arr;
	private int x;
	private int y;
	private boolean opposite = false;
	private boolean det_val = false;
	private double[][] arr_op;
	
	//building function
	public matrix(double[][] matrix) {
		this.arr = matrix;
		this.x = matrix.length;
		this.y = matrix[0].length;
	}
	
	public matrix(int x, int y) {
		this.x = x;
		this.y = y;
		x--;y--;
		while(x>=0) {
			while(y>=0) {
				arr[x][y] = scan.nextDouble();
				y--;
			}
			x--;
		}		
	}
	
	
	//medareg et hamatriza
	public void dareg() {
		int i = 0;
		int i2 = 1;		
		while(i<this.x & i<this.y) {
			if(arr[i][i]==0) {
				int j = i+1;
				while(j<this.x && arr[j][i]==0)
					j++;
				if(j<this.x)
					replace(i, j);
			}
			if(arr[i][i]!=0) {
				System.out.println(i+" "+i2+ " "+this.x+"\n"+this.toString());
				while(i2<this.x) {
					if(i!=i2)
						sub_row(i, i, i2);
					i2++;
				}
			}
		if(!det_val)
			one_heading(i);
		i++;
		i2 = 0;
		}
	}
	
	
	//geters
	public int get_row() {
		return this.x;
	}	
	public int get_col() {
		return this.y;
	}
	public double get(int row, int col){
		return arr[row][col];
	}
	
	// replace row i with j
	public void replace(int i, int j) {
		double temp;
		for(int x = 0; x < this.y; x++) {
			temp = arr[i][x];
			arr[i][x] = arr[j][x];
			arr[j][x] = temp;
		}
	}
	
	
	//MULTIPLY two different matrix
	public void mat_mul(matrix mat) {
		if(this.y != mat.get_row())
			throw new IllegalArgumentException("can't be multiplied!!");
		this.y = mat.get_col();
		double[][] mult = new double[this.x][this.y];				
		double sum;		
		for(int row = 0; row < this.x; row++)
			for(int col = 0; col < this.y; col++) {
				sum = 0;
				for(int k = 0; k < mat.get_row(); k++)			
					sum = sum + this.arr[row][k]*mat.get(k,col);					
				
				mult[row][col] = sum;
				}
		this.arr = mult;
	}
			
	//SUM two different matrix
	public void mat_sum(matrix mat) {
		if(this.y != mat.get_col() & this.x != mat.get_row())
			throw new IllegalArgumentException("can't be sumed!!");
		double sum;
		for(int row = 0; row < this.x; row++)
			for(int col = 0; col < this.y; col++) 
				arr[row][col] = arr[row][col] + mat.get(row,col);				
	}

	//multiply the matrix with a scalar
	public void scalar(double scalar) {
			for(int i = this.y-1; i>=0; i--)
				for(int j = this.x-1; j>=0; j--)
					arr[j][i] = arr[j][i]*scalar;			
		}
	
	//multiply a line with a scalar
	public void scalar(double scalar, int row) {
		int i = 0;
		while(i<this.y) {
			arr[row][i] = scalar*arr[row][i];
			i++;
		}
	}
	
	
	//multiply a line with a scalar
	private void one_heading(int row) {
			int i = 0;
			while(i<this.y && arr[row][i] == 0)
				i++;
			if(i<this.y) {
				double scalar = 1/arr[row][i];
				arr[row][i] = 1; i++;
				while(i<this.y) {
					arr[row][i] = scalar*arr[row][i];
					i++;
				}
			}
		}
	
	//subs one row from another 
	private void sub_row(int col, int row1, int row2) {
		int i = col;
		if(arr[row1][col] != 0) {
			double scalar = arr[row2][col]/arr[row1][col];		
			arr[row2][i] = 0; i++;
			while(i<this.y) {
				arr[row2][i] = arr[row2][i]-scalar*arr[row1][i];
				i++;			
			}
		}
	}
	
	
	//minor
	public static double[][] minor(double[][] array, int Min_row, int Min_col) {
		double[][] output = new double[array.length-1][array[0].length-1];
		for(int row = 0; row < output.length; row++)
			for(int col = 0; col < output[0].length; col++) {
				if(row < Min_row){
					if(col < Min_col)
						output[row][col] = array[row][col];
					else
						output[row][col] = array[row][col+1];
				}
				else {
					if(col < Min_col)
						output[row][col] = array[row+1][col];
					else
						output[row][col] = array[row+1][col+1];
				}
			}
					
		return output;
	}
	
	
	//CALCULATE adj
	public void adj(double scalar, int row) {
		int i = 0;
		while(i<this.y) {
			arr[row][i] = scalar*arr[row][i];
			i++;
		}
	}
	
	
	//CALCULATE DET
	public double fast_det() {
		det_val = true;
		if(this.x != this.y)
			throw new IllegalArgumentException("Is'nt an square matrix");
		dareg();
		double det = 1;
		for(int i = 0; i < this.x; i++)
			det = det*this.arr[i][i];
		det_val = false;
		return det;
	}
	
	//CALCULATE DET
	public static double det(double[][] arr) {
		//System.out.println(arr.length + " " + arr[0].length);
		double output = 0;
		if(arr.length < 2 | arr[0].length < 2)
			output = arr[0][0];
		else {
			if(arr.length == 2 & arr[0].length == 2)
				output = det_2_2_cal(arr);
			else {
				int i = 0;
				int j = 0;
				while(i < arr.length) {
					if((i+j)%2 == 0)
						output = output + arr[i][j]*det(minor(arr,i,j));
					else
						output = output - arr[i][j]*det(minor(arr,i,j));
					i++;
				}
			}
		}
		return output;
	}
	
	// calc det of 2*2 mat
	private static double det_2_2_cal(double[][] arr) {
		return arr[0][0]*arr[1][1] - arr[1][0]*arr[0][1];
	}	
	
	
	//find A*op = I
	public void opposite_r() {
		
	}
	
	//find op*A = I
	public void opposite_l() {
			
	}

	// returns the class string
	public String toString(){
		String st = "[";
		for(int i = 0; i<this.x; i++){
			for(int j = 0; j<this.y; j++){
				st = st+arr[i][j]+" ";
			}
		st= st+"\n ";
		}
	return st.substring(0, st.length()-3)+"]\n";
	}
}
