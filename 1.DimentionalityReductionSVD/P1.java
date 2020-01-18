/*******************************************************
* Project 1: Dimentionality Reduction using SVD
* Shweta Kharat
********************************************************/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import thirdPartyLibraryJama.EigenvalueDecomposition;
import thirdPartyLibraryJama.Matrix;

public class P1 extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static double aa1, bb1, cc1, dd1;
	static double cx1=0;
	static double cx2=0;
	static double cx3=0;
	static double sx1=0;
	static double sx2=0;
	static double sx3=0;
	static double nRoots;
	private static final double TWO_PI = 2.0 * Math.PI; 
	private static final double FOUR_PI = 4.0 * Math.PI;
	
	public P1(){
		
	}
	
	public P1(String s){
		super(s);
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		P1 ddsvd = new P1();
		double[][] matrixA = new double[100][100] ;
		double fn;
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String input = null;
		String[] inputRow = null;
		int rowNum = 0;
		int colNum = 0;
		int colNumPrev = 0;
		
		System.out.println("***************************************");
        System.out.println("Program to calculate SVD.\nSCUID: 1384227");
        System.out.println("***************************************");
		//Get matrix A from user
		System.out.println("Please enter matrix A : ");
		input = in.readLine();
		int comment = 0;
		while( (input != null)&& (!(input.isEmpty()))){
			inputRow = input.trim().split("\\s+");
			colNum = inputRow.length;
			for(int i=0; i<colNum;i++){
				if(inputRow[i].equals("#") ){
					comment = 1;
					break;
				}
			}
			
			
			if(colNum > 3 && comment !=1){
				try {
					throw new P1("Invalid Input: Column size greater than 3");
				} catch (P1 e) {
					// TODO Auto-generated catch block
					System.out.println("Invalid Input: Column size greater than 3");
					System.exit(0);
				}
			}
			if(colNumPrev!=0)
			{
				if(comment != 1 && colNumPrev !=colNum){
					try {
						throw new P1("Invalid Input: Column size inconsistent");
					} catch (P1 e) {
						// TODO Auto-generated catch block
						System.out.println("Invalid Input: Column size is inconsistent");
						System.exit(0);
					}
				}
			}
			for(int i=0; i<colNum;i++){
					if(inputRow[i].equals("#")){
							colNum = colNumPrev;
							if(i==0){
								rowNum--;
							}
							break;
					}
					try{
						double val = Double.parseDouble(inputRow[i]);
						matrixA[rowNum][i] = val;
					}catch(NumberFormatException e){
						System.out.println("Invalid input: Data type is not integer");
						System.exit(0);
					}
			}
			
			colNumPrev = colNum;
			rowNum++;
			input = in.readLine();
		}
		System.out.println("rows = " + rowNum);
		System.out.println("columns = " + colNum);
		
		//Display matrix A
		ddsvd.display(matrixA, rowNum, colNum, "A");
		
		//Find A Transpose (AT)
		double[][] matrixAT = new double[colNum][rowNum] ;
		for(int i=0;i<rowNum;i++){
			for(int j=0;j<colNum;j++){
				matrixAT[j][i]=matrixA[i][j];
			}
		}
		ddsvd.display(matrixAT, colNum, rowNum, "AT");
		
		//Find AT * A
		double[][] matrixATA = new double[colNum][colNum] ;
		for(int i=0;i<colNum;i++){
			for(int j=0;j<colNum;j++){
				for(int k=0;k<rowNum;k++){
					matrixATA[i][j] = matrixATA[i][j] + matrixAT[i][k] * matrixA[k][j];
				}
			}
		}
		ddsvd.display(matrixATA, colNum, colNum, "AT * A");
		
		//Find |A'A - cI|, eigen values c1,c2 and singular values s1,s2
		if(colNum == 2){
			//System.out.println("\n2*2 or 3*2 matrix");
			//Solve equation ax^2 - bx -c
			double c1=0, c2=0, d, s1=0, s2=0;
			double b, c;
			b = - (matrixATA[0][0] + matrixATA[1][1]);   //Find b
			c = (matrixATA[0][0] * matrixATA[1][1]) - (matrixATA[0][1] * matrixATA[1][0]);
			d = (b * b) - (4 * c);                   //b^2 - 4ac where a=1
			if(d > 0){
				c1 = (- b + Math.sqrt(d)) / 2;
				c2 = (- b - Math.sqrt(d)) / 2;
			}
			else if(d == 0){
				c1 = c2 = (b + Math.sqrt(d)) / 2;
			}
			else{
				System.out.println("Roots are imaginary");
			}
			if(c1 < c2){
				double temp;
				temp = c1;
				c1 = c2;
				c2 = temp;
			}
			s1 = Math.sqrt(c1);
			s2 = Math.sqrt(c2);
			
			System.out.println("\nc1 = " + c1);
			System.out.println("c2 = " + c2);
			System.out.println("s1 = " + s1);
			System.out.println("s2 = " + s2);
			
			//************************************************
			//Find Matrix S and S Inverse
			double[][] matrixS = new double[colNum][colNum] ;
			double[][] matrixSInv = new double[colNum][colNum] ;
			matrixS[0][0] = s1;
			matrixS[0][1] = matrixS[1][0] = 0;
			matrixS[1][1] = s2;
			ddsvd.display(matrixS, colNum, colNum, "S");
			
			matrixSInv[0][0] = 1/s1;
			matrixSInv[0][1] = matrixSInv[1][0] = 0;
			matrixSInv[1][1] = 1/s2;
			ddsvd.display(matrixSInv, colNum, colNum, "SInv");
			
			//Find A'A - cI by using c1, c2 and matrix V
			//Using c1
			double[][] matrixATAC1 = new double[colNum][colNum] ;
			double[][] matrixV = new double[colNum][colNum] ;
			matrixATAC1[0][0] = matrixATA[0][0] - c1;
			matrixATAC1[0][1] = matrixATA[0][1];
			matrixATAC1[1][0] = matrixATA[1][0];
			matrixATAC1[1][1] = matrixATA[1][1] - c1;	
			ddsvd.display(matrixATAC1, colNum, colNum, "ATAC1");
		
			if(matrixATAC1[0][0] != matrixATAC1[0][1]){
				matrixV[0][0] = matrixV[1][0] = 1 / Math.sqrt(2);
			}
			else{
				matrixV[0][0] = 1 / Math.sqrt(2);
				matrixV[1][0] = -(1 / Math.sqrt(2));
			}
			//Using c2
			double[][] matrixATAC2 = new double[colNum][colNum] ;
			matrixATAC2[0][0] = matrixATA[0][0] - c2;
			matrixATAC2[0][1] = matrixATA[0][1];
			matrixATAC2[1][0] = matrixATA[1][0];
			matrixATAC2[1][1] = matrixATA[1][1] - c2;
			ddsvd.display(matrixATAC2, colNum, colNum, "ATAC2");
			
			if(matrixATAC2[0][0] != matrixATAC2[0][1]){
				matrixV[0][1] = matrixV[1][1] = 1 / Math.sqrt(2);
			}
			else{
				matrixV[0][1] = 1 / Math.sqrt(2);
				matrixV[1][1] = -(1 / Math.sqrt(2));
			}
			ddsvd.display(matrixV, colNum, colNum, "V");
			
			//Find V Transpose (VT)
			double[][] matrixVT = new double[colNum][colNum] ;
			for(int i=0;i<colNum;i++){
				for(int j=0;j<colNum;j++){
					matrixVT[j][i]=matrixV[i][j];
				}
			}
			ddsvd.display(matrixVT, colNum, colNum, "VT");	
			
			//Compute U = A*V*SInv
			double[][] matrixU = new double[rowNum][colNum] ;
			double[][] matrixAV = new double[rowNum][colNum] ;
			//A * V
			for(int i=0;i<rowNum;i++){
				for(int j=0;j<colNum;j++){
					for(int k=0;k<colNum;k++){
						matrixAV[i][j] += matrixA[i][k] * matrixV[k][j];
					}
				}
			}
			ddsvd.display(matrixAV, rowNum, colNum, "A * V");
			//U = AV * SInv
			for(int i=0;i<rowNum;i++){
				for(int j=0;j<colNum;j++){
					for(int k=0;k<colNum;k++){
						matrixU[i][j] += matrixAV[i][k] * matrixSInv[k][j];
					}
				}
			}
			ddsvd.display(matrixU, rowNum, colNum, "U");
			
			//Compute B = U*S*VT
			double[][] matrixB = new double[rowNum][colNum] ;
			double[][] matrixUS = new double[rowNum][colNum] ;
			//U*S
			for(int i=0;i<rowNum;i++){
				for(int j=0;j<colNum;j++){
					for(int k=0;k<colNum;k++){
						matrixUS[i][j] += matrixU[i][k] * matrixS[k][j];
					}
				}
			}
			ddsvd.display(matrixUS, rowNum, colNum, "US");

			//US * VT
			for(int i=0;i<rowNum;i++){
				for(int j=0;j<colNum;j++){
					for(int k=0;k<colNum;k++){
						matrixB[i][j] += matrixUS[i][k] * matrixVT[k][j];
					}
				}
			}
			ddsvd.display(matrixB, rowNum, colNum, "B");
			
			//Frobenius norm = sqrt(summation(matrixA-matrixB)^2)
			double sqOfDiff = 0;
			for(int i=0;i<rowNum;i++){
				for(int j=0;j<colNum;j++){
					double temp = (matrixA[i][j] - matrixB[i][j]) * (matrixA[i][j] - matrixB[i][j]);
					sqOfDiff = sqOfDiff + temp;
				}
			}
			fn = Math.sqrt(sqOfDiff);
			System.out.println("Frobenious Norm : " + fn);
		//***************************************************
			//if s2 = 0
			//New S
			double[][] matrixSNew = new double[colNum-1][colNum-1] ;
			matrixSNew[0][0] = matrixS[0][0];
			ddsvd.display(matrixSNew, colNum-1, colNum-1, "New S");
			//New U
			double[][] matrixUNew = new double[rowNum][colNum-1] ;
			for(int i=0;i<rowNum;i++){
				for(int j=0;j<colNum-1;j++){
					matrixUNew[i][j] = matrixU[i][j];
				}
			}
			ddsvd.display(matrixUNew, rowNum, colNum-1, "New U");
			//New VT
			double[][] matrixVTNew = new double[colNum-1][colNum] ;
			for(int i=0;i<colNum - 1;i++){
				for(int j=0;j<colNum;j++){
					matrixVTNew[i][j] = matrixVT[i][j];
				}
			}
			ddsvd.display(matrixVTNew, colNum-1, colNum, "New VT");
			//New B = U*S*VT
			double[][] matrixBNew = new double[rowNum][colNum] ;
			double[][] matrixUSNew = new double[rowNum][colNum-1] ;
			//U*S
			for(int i=0;i<rowNum;i++){
				for(int j=0;j<colNum - 1;j++){
					for(int k=0;k<colNum -1;k++){
						matrixUSNew[i][j] += matrixUNew[i][k] * matrixSNew[k][j];
					}
				}
			}
			ddsvd.display(matrixUSNew, rowNum, colNum-1, "US New");

			//US * VT
			for(int i=0;i<rowNum;i++){
				for(int j=0;j<colNum-1;j++){
					for(int k=0;k<colNum-1;k++){
						matrixBNew[i][j] += matrixUSNew[i][k] * matrixVTNew[k][j];
					}
				}
			}
			ddsvd.display(matrixBNew, rowNum, colNum, "B New");
			
			//Frobenius norm = sqrt(summation(matrixA-matrixB)^2)
			double sqOfDiff1 = 0;
			for(int i=0;i<rowNum;i++){
				for(int j=0;j<colNum;j++){
					double tempN = (matrixA[i][j] - matrixBNew[i][j]) * (matrixA[i][j] - matrixBNew[i][j]);
					sqOfDiff1 = sqOfDiff1 + tempN;
				}
			}
			double fn1 = Math.sqrt(sqOfDiff1);
			System.out.println("New Frobenious Norm : " + fn1);
			
		//****************************************************	
			
		}
		else if(colNum == 3){
			//System.out.println("\n3*3 or 2*3 matrix");
			//Find |A'A - cI|, eigen values c1,c2,c2 and singular values s1,s2,s3
			aa1=1;
			double roots[] = new double[3];
			bb1 = (-matrixATA[0][0] - matrixATA[1][1] - matrixATA[2][2]);
			cc1 = + (matrixATA[0][0] * matrixATA[1][1]) 
					+ (matrixATA[0][0] * matrixATA[2][2])
					+ (matrixATA[1][1] * matrixATA[2][2])
					- (matrixATA[2][1] * matrixATA[1][2])
					- (matrixATA[0][1] * matrixATA[1][0])
					- (matrixATA[0][2] * matrixATA[2][0]);
			dd1 = -(matrixATA[0][0] * matrixATA[1][1] * matrixATA[2][2])
					+ (matrixATA[0][0] * matrixATA[2][1] * matrixATA[1][2])
					+ (matrixATA[0][1] * matrixATA[1][0] * matrixATA[2][2])
					- (matrixATA[0][1] * matrixATA[2][0] * matrixATA[1][2])
					- (matrixATA[0][2] * matrixATA[1][0] * matrixATA[2][1])
					+ (matrixATA[0][2] * matrixATA[2][0] * matrixATA[1][1]);
			
			roots = ddsvd.solve(aa1, bb1, cc1, dd1);
			
			cx1 = roots[0];
			cx2 = roots[1];
			cx3 = roots[2];
			sx1 = Math.sqrt(cx1);
			sx2 = Math.sqrt(cx2);
			sx3 = Math.sqrt(cx3);
			
			//Find Matrix S and S Inverse
			double[][] matrixS = new double[colNum][colNum] ;
			double[][] matrixSInv = new double[colNum][colNum] ;
			matrixS[0][0] = sx1;
			matrixS[0][1] = matrixS[1][0] = matrixS[2][0] = matrixS[0][2] =  0;
			matrixS[1][1] = sx2;
			matrixS[2][2] = sx3;
			ddsvd.display(matrixS, colNum, colNum, "S");
			
			matrixSInv[0][0] = 1/sx1;
			matrixSInv[0][1] = matrixSInv[1][0] = matrixSInv[2][0] = matrixSInv[2][0] = 0;
			matrixSInv[1][1] = 1/sx2;
			matrixSInv[2][2] = 1/sx3;
			ddsvd.display(matrixSInv, colNum, colNum, "SInv");
			
			//Find A'A - cI by using c1, c2 and matrix V
			//Using c1
			double[][] matrixATAC1 = new double[colNum][colNum] ;
			double[][] matrixV = new double[colNum][colNum] ;
			matrixATAC1[0][0] = matrixATA[0][0] - cx1;
			matrixATAC1[0][1] = matrixATA[0][1];
			matrixATAC1[0][2] = matrixATA[0][2];
			matrixATAC1[1][0] = matrixATA[1][0];
			matrixATAC1[1][1] = matrixATA[1][1] - cx1;
			matrixATAC1[1][2] = matrixATA[1][2];
			matrixATAC1[2][0] = matrixATA[2][0];
			matrixATAC1[2][1] = matrixATA[2][1];
			matrixATAC1[2][2] = matrixATA[2][2] - cx1;
			ddsvd.display(matrixATAC1, colNum, colNum, "ATAC1");
			
			//Using c2
			double[][] matrixATAC2 = new double[colNum][colNum] ;
			matrixATAC2[0][0] = matrixATA[0][0] - cx2;
			matrixATAC2[0][1] = matrixATA[0][1];
			matrixATAC2[1][0] = matrixATA[1][0];
			matrixATAC2[1][1] = matrixATA[1][1] - cx2;
			matrixATAC2[1][2] = matrixATA[1][2];
			matrixATAC2[2][1] = matrixATA[2][1];
			matrixATAC2[0][2] = matrixATA[0][2];
			matrixATAC2[2][0] = matrixATA[2][0];
			matrixATAC2[2][2] = matrixATA[2][2] - cx2;
			ddsvd.display(matrixATAC2, colNum, colNum, "ATAC2");
			
			//Using c3
			double[][] matrixATAC3 = new double[colNum][colNum] ;
			matrixATAC3[0][0] = matrixATA[0][0] - cx3;
			matrixATAC3[0][1] = matrixATA[0][1];
			matrixATAC3[1][0] = matrixATA[1][0];
			matrixATAC3[1][1] = matrixATA[1][1] - cx3;
			matrixATAC3[1][2] = matrixATA[1][2];
			matrixATAC3[2][1] = matrixATA[2][1];
			matrixATAC3[0][2] = matrixATA[0][2];
			matrixATAC3[2][0] = matrixATA[2][0];
			matrixATAC3[2][2] = matrixATA[2][2] - cx3;
			ddsvd.display(matrixATAC3, colNum, colNum, "ATAC3");
			
			//Find Matrix V
			Matrix A = new Matrix(matrixA);
			EigenvalueDecomposition e = A.eig();
			Matrix V = e.getV();
			matrixV = V.getArray();
			ddsvd.display(matrixV, colNum, colNum, "V");
			
			//Find V Transpose (VT)
			double[][] matrixVT = new double[colNum][colNum] ;
			for(int i=0;i<colNum;i++){
				for(int j=0;j<colNum;j++){
					matrixVT[j][i]=matrixV[i][j];
				}
			}
			ddsvd.display(matrixVT, colNum, colNum, "VT");	
			
			//Compute U = A*V*SInv
			double[][] matrixU = new double[rowNum][colNum] ;
			double[][] matrixAV = new double[rowNum][colNum] ;
			//A * V
			for(int i=0;i<rowNum;i++){
				for(int j=0;j<colNum;j++){
					for(int k=0;k<colNum;k++){
						matrixAV[i][j] += matrixA[i][k] * matrixV[k][j];
					}
				}
			}
			//U = AV * SInv
			for(int i=0;i<rowNum;i++){
				for(int j=0;j<colNum;j++){
					for(int k=0;k<colNum;k++){
						matrixU[i][j] += matrixAV[i][k] * matrixSInv[k][j];
					}
				}
			}
			ddsvd.display(matrixU, rowNum, colNum, "U");
			
			//Compute B = U*S*VT
			double[][] matrixB = new double[rowNum][colNum] ;
			double[][] matrixUS = new double[rowNum][colNum] ;
			//U*S
			for(int i=0;i<rowNum;i++){
				for(int j=0;j<colNum;j++){
					for(int k=0;k<colNum;k++){
						matrixUS[i][j] += matrixU[i][k] * matrixS[k][j];
					}
				}
			}
			ddsvd.display(matrixUS, rowNum, colNum, "US");

			//US * VT
			for(int i=0;i<rowNum;i++){
				for(int j=0;j<colNum;j++){
					for(int k=0;k<colNum;k++){
						matrixB[i][j] += matrixUS[i][k] * matrixVT[k][j];
					}
				}
			}
			ddsvd.display(matrixB, rowNum, colNum, "B");
			
			//Frobenius norm = sqrt(summation(matrixA-matrixB)^2)
			double sqOfDiff = 0;
			for(int i=0;i<rowNum;i++){
				for(int j=0;j<colNum;j++){
					double temp = (matrixA[i][j] - matrixB[i][j]) * (matrixA[i][j] - matrixB[i][j]);
					sqOfDiff = sqOfDiff + temp;
				}
			}
			fn = Math.sqrt(sqOfDiff);
			System.out.println("Frobenious Norm : " + fn);
			
			//Remove s3 from matrixS,column 3 from U and row 3 from V
			double[][] matrixNewS = new double[colNum-1][colNum-1] ;
			double[][] matrixNewU = new double[rowNum][colNum-1] ;
			double[][] matrixNewVT = new double[colNum-1][colNum] ;
			matrixNewS[0][0] = matrixS[0][0];
			matrixNewS[1][1] = matrixS[1][1];
			ddsvd.display(matrixNewS, colNum-1, colNum-1, "New S");
			
			for(int i=0; i<rowNum; i++){
				for(int j=0; j<colNum-1;j++){
					matrixNewU[i][j] = matrixU[i][j];
				}
			}
			ddsvd.display(matrixNewU, rowNum, colNum-1, "New U");
			
			for(int i=0;i<colNum-1;i++){
				for(int j=0;j<colNum;j++){
					matrixNewVT[i][j]=matrixVT[i][j];
				}
			}
			ddsvd.display(matrixNewVT, colNum-1, colNum, "New VT");
			//Compute newB = newU*newS*newVT
			double[][] matrixNewB = new double[rowNum][colNum] ;
			double[][] matrixNewUS = new double[rowNum][colNum-1] ;
			//New U*S
			for(int i=0;i<rowNum;i++){
				for(int j=0;j<colNum-1;j++){
					for(int k=0;k<colNum-1;k++){
						matrixNewUS[i][j] += matrixNewU[i][k] * matrixNewS[k][j];
					}
				}
			}
			//NewUS * NewVT
			for(int i=0;i<rowNum;i++){
				for(int j=0;j<colNum-1;j++){
					for(int k=0;k<colNum-1;k++){
						matrixNewB[i][j] += matrixNewUS[i][k] * matrixNewVT[k][j];
					}
				}
			}
			ddsvd.display(matrixNewB, rowNum, colNum, "New B");
			
			//New Frobenius norm = sqrt(summation(matrixA-matrixB)^2)
			double sqOfDiffNew = 0, fnNew = 0;
			for(int i=0;i<rowNum;i++){
				for(int j=0;j<colNum;j++){
					double temp1 = (matrixA[i][j] - matrixNewB[i][j]) * (matrixA[i][j] - matrixNewB[i][j]);
					sqOfDiffNew = sqOfDiffNew + temp1;
				}
			}
			fnNew = Math.sqrt(sqOfDiffNew);
			System.out.println("New Frobenious Norm : " + fnNew);
			
			
			
		}
		else{
			System.out.println("\nCannot be solved");
		}
		
		
		
		System.out.println("\nDone!");
	}
	
	//Display Matrix 
	public void display(double M[][],int rN,int cN,String matrixName){
		System.out.println("\nMatrix " + matrixName + ":");
		for(int i=0;i<rN;i++){
			for(int j=0;j<cN;j++){
				System.out.print(M[i][j]);
				System.out.print("\t\t\t");
			}
			System.out.println("");
		}
	}
	
	//**********************************
	public double[] solve (double aa1, double bb1, double cc1, double dd1) 
	 { 
		double [] ans = new double [4];
	 // Verify preconditions. 
	 if (aa1 == 0.0) 
	  { 
	  throw new RuntimeException ("Cubic.solve(): a = 0"); 
	  } 
	 
	 // Normalize coefficients. 
	 double denom = aa1; 
	 aa1 = bb1/denom; 
	 bb1 = cc1/denom; 
	 cc1 = dd1/denom; 
	 
	 // Commence solution. 
	 double a_over_3 = aa1 / 3.0; 
	 double Q = (3*bb1 - aa1*aa1) / 9.0; 
	 double Q_CUBE = Q*Q*Q; 
	 double R = (9*aa1*bb1 - 27*cc1 - 2*aa1*aa1*aa1) / 54.0; 
	 double R_SQR = R*R; 
	 double D = Q_CUBE + R_SQR; 
	 
	 if (D < 0.0) 
	  { 
	  // Three unequal real roots. 
	  nRoots = 3; 
	  double theta = Math.acos (R / Math.sqrt (-Q_CUBE)); 
	  double SQRT_Q = Math.sqrt (-Q); 
	  cx1 = 2.0 * SQRT_Q * Math.cos (theta/3.0) - a_over_3; 
	  cx2 = 2.0 * SQRT_Q * Math.cos ((theta+TWO_PI)/3.0) - a_over_3; 
	  cx3 = 2.0 * SQRT_Q * Math.cos ((theta+FOUR_PI)/3.0) - a_over_3; 
	  sortRoots(); 
	  } 
	 else if (D > 0.0) 
	  { 
	  // One real root. 
	  nRoots = 1; 
	  double SQRT_D = Math.sqrt (D); 
	  double S = Math.cbrt (R + SQRT_D); 
	  double T = Math.cbrt (R - SQRT_D); 
	  cx1 = (S + T) - a_over_3; 
	  cx2 = Double.NaN; 
	  cx3 = Double.NaN; 
	  } 
	 else 
	  { 
	  // Three real roots, at least two equal. 
	  nRoots = 3; 
	  double CBRT_R = Math.cbrt (R); 
	  cx1 = 2*CBRT_R - a_over_3; 
	  cx2 = cx3 = CBRT_R - a_over_3; 
	  sortRoots(); 
	  }
	 ans[0] = cx1;
	 ans[1] = cx2;
	 ans[2] = cx3;
	return ans; 
	 }
	
	private void sortRoots() 
	 { 
	 if (cx1 < cx2) 
	  { 
	  double tmp = cx1; cx1 = cx2; cx2 = tmp; 
	  } 
	 if (cx2 < cx3) 
	  { 
	  double tmp = cx2; cx2 = cx3; cx3 = tmp; 
	  } 
	 if (cx1 < cx2) 
	  { 
	  double tmp = cx1; cx1 = cx2; cx2 = tmp; 
	  } 
	 } 
	

}
