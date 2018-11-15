
public class AljebCheck 
{
public static void main(String[]args){
	double[][] matr = {{1,2,3},{4,5,6},{7,8,9}};
	//double[][] matr2 = {{1,2,3},{4,5,6},{7,8,9}};
	double[][] matr2 = {{0,1},{3,9},{10,2}};
	double[][] matr3 = {{0,1,4,6},{3,9,1,2},{10,2,5,7}};
	matrix mat = new matrix(matr); 
	matrix mat2 = new matrix(matr2); 
	matrix mat3 = new matrix(matr3); 
	matr2 = matrix.minor(matr, 0, 0);
	System.out.println(new matrix(matr2));
	System.out.println(new matrix(matrix.minor(matr, 0, 2)));
	System.out.println(new matrix(matrix.minor(matr, 1, 1)));
	System.out.println(new matrix(matrix.minor(matr, 2, 2)));
	System.out.println(new matrix(matrix.minor(matr2, 0, 0)));
	System.out.println(mat);
	System.out.println(mat2);
	//System.out.println(mat2.get(0,0)+" "+mat2.get(1,0)+" "+mat2.get(2,0)+" "+mat2.get(0,1)+" "+mat2.get(1,1)+" "+mat2.get(2,1) );
	//mat2.scalar(0.5);mat.mat_sum(mat2);
	mat.mat_mul(mat2);
	System.out.println(mat.toString());
	mat.dareg();
	System.out.println(mat.toString());
	System.out.println(mat3.toString());
	mat3.dareg();
	System.out.println(mat3.toString());
	double[][] matr4 = {{5,2,4},{10,3,10},{2.5,1,6}};
	matrix mat4 = new matrix(matr4); 
	System.out.println("mat4\n"+mat4);
	System.out.println(matrix.det(matr4));
	System.out.println(mat4.fast_det());
	System.out.println("end"+mat4);
	}
}
