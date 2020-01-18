/******************************************************
** Project 2: Most Popular Item in Streaming Data
** Name: Shweta Kharat
******************************************************/

package P2;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public abstract class P2 implements Runnable{
	static String hostPortString, c1, userQuery, host, port ;
	static int idPopular = -1,tsPopular, portNum;
	static double c;
	static int currentHighestCount = 0;
	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub	
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String[] hostPort = null;
		//Get decaying constant from user
		System.out.println("Enter Decaying Constant c = ");
		try{
			c1 = in.readLine();
			c = Double.parseDouble(c1);
		}catch(NumberFormatException e){
			System.out.println("Invalid input: Enter correct c");
			System.exit(1);
		}
		
		//Get host name and port number from user
		System.out.println("Enter Server Details (<host>:<port>) ");
		try{
			hostPortString = in.readLine();
			hostPort = hostPortString.trim().split(":");
			host = hostPort[0];
			if(!(host.equals("localhost"))){
				System.out.println("Enter correct host name (localhost)");
				System.exit(1);
			}
			port = hostPort[1];
			portNum = Integer.parseInt(port);
		}catch(IOException e){
			System.out.println(e);
			System.exit(1);
		}
		
		/*Thread 1:
		Get connection to server using host name & port number
		Get input from server using input stream
		Save data in hash map
		For every new item, update sum = sum*c
		If id = new id , sum = sum*c + 1
		Id id != new id, sum = 1*/
		Thread thread1 = new Thread(){
			public void run() {
				Socket s = null;
				String[] inputRow = null;
				String input = null;
		        HashMap<Integer, StreamData> hashMap = new HashMap<Integer, StreamData>();
		        int id;
		        int timeStamp;
		        double countTemp = 0;
	            int timestampTemp = 0;
		        
				try{	
					s = new Socket(host, portNum);
					DataInputStream iStream = new DataInputStream(s.getInputStream());
					//Get data from Server
			        input = iStream.readLine();
			        while ((input != null) && !(input.isEmpty())){
			        	double sum = 1;
			        	double count = 1;
			        	inputRow = input.trim().split("\\s+");
			        	id = Integer.parseInt(inputRow[1]);
			        	timeStamp = Integer.parseInt(inputRow[0]);
			        	//Insert data in hashmap
			        	if(hashMap.isEmpty()){
			        		hashMap.put(id, new StreamData(timeStamp, sum, count));
			        	}
			        	else{	
			        		//Multiply all sums by c and add 1 to sum if id found
				        	for(Map.Entry<Integer, StreamData> entry1: hashMap.entrySet()) {
				        		int id1 = entry1.getKey();
			        			StreamData sd = entry1.getValue();
			        			int timeStamp1 = sd.getTimeStamp();
			        			double sum1 = sd.getSum();
			        			double count1 = sd.getCount();
			        			//Multiply all sums by c.
			        			sum1 = sum1 * c; 
			        			
			        			//Case: new incoming id found in hashmap
				        		if(id1 == id){ 
				        			sum1 = sum1 + 1;
				        			timeStamp1 = timeStamp;
				        			count1 = count1 + 1;				        			
				        		}
				        		
				        		StreamData sd1 = new StreamData(timeStamp1, sum1, count1);
			        			hashMap.put(id1, sd1);
			        			
				        		if((sum1 < 1/2) && !hashMap.isEmpty()){
				        			hashMap.remove(id1);
			    				}
				        	}
				        	//If id isn't present in hashmap,add it and init sum to 1.
				        	if(!hashMap.containsKey(id)){
			        			hashMap.put(id, new StreamData(timeStamp, sum, count));
			        		}
				        	
			        	}
			        	//Read next value from server.
			            input = iStream.readLine(); 
			            
			            //Calculate the most updated popular value that has latest timestamp.
			            for(Map.Entry<Integer, StreamData> entry2: hashMap.entrySet()) {
			            	StreamData sd4 = entry2.getValue();
			            	int id3 = entry2.getKey();
			            	int ts2 = sd4.getTimeStamp();
			            	double strSum = sd4.getSum();
			            	double count2 = sd4.getCount();
			            	if (count2 > countTemp){
			            		countTemp = count2;
			            		idPopular = id3;
			            	}
			            	//Get popular id with current timestamp
			            	else if(count2 == countTemp){
			            		countTemp = count2;
			            		if(ts2 >= timestampTemp){
			            			timestampTemp = ts2;
			            			idPopular = id3;
			            		}
			            	}
			            }
			            StreamData sd5 = hashMap.get(idPopular);
			            tsPopular = sd5.getTimeStamp();	
						
			        }
			        }catch (UnknownHostException e){
			        	System.out.println("Unknown host");
			        	System.exit(1);
			        }catch (IOException e){
						System.out.println(e);
						System.exit(1);
					}catch(Exception e){
						System.out.println(e);
					}
			}
		};
		/*Thread 2:
		Get query from user, Display output as:Timestamp & Item Id*/
		Thread thread2 = new Thread(){
			public void run() {
				try {
					while(true){
						System.out.println("Please enter query:");
						userQuery = in.readLine();
						if(userQuery!=null && !userQuery.isEmpty()){
							if(userQuery.equals("What is the most current popular itemID?")){
								System.out.println("Timestamp \t Item Id");
								System.out.println(tsPopular + "\t\t " + idPopular);
							}
							else{
								System.out.println("Please enter correct query as:");
								System.out.println("What is the most current popular itemID?");
							}
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		thread1.start();
		thread2.start();		
	}
}

/*This is used to create user defined object to store timestamp, sum and count in hashmap*/
class StreamData{
	int timeStamp;
	double sum;
	double count;
	
	public StreamData(int timeStamp, double sum, double count){
		this.timeStamp = timeStamp;
		this.sum = sum;
		this.count = count;
	}

	public int getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(int timeStamp) {
		this.timeStamp = timeStamp;
	}

	public double getSum() {
		return sum;
	}

	public void setSum(double sum) {
		this.sum = sum;
	}

	public double getCount() {
		return count;
	}

	public void setCount(double count) {
		this.count = count;
	}
	
	
}
