import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;


public class Classification {

	public static HashMap<String, Integer> Classes= new HashMap<>();
	public static HashMap<String, Double> model= new HashMap<>();


	public static ArrayList<String> data= new ArrayList<>();

	public static ArrayList<ArrayList<String>> test = new ArrayList<>();

	public static ArrayList<ArrayList<String>> train = new ArrayList<>();

	public static int fold =5;

	public static HashMap<Integer, ArrayList<String>> attibuteSetDT=new HashMap<>();

	public static String attributeLabel=null;

	public static void main(String[] args) {
		// TODO Auto-generated method stub		
		ArrayList<Double> acc= new ArrayList<>();
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader("data.txt"));

			String line = null;

			while ((line = br.readLine()) != null){
				data.add(line);
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		fold_validation(data,fold);
		double sum_acc = 0;

		
		System.out.println("Naive Bayes");
		
		for(int i =0; i<fold;i++)
		{
			NaiveBayesTrain(train.get(i));
			//acc.add(naiveBayesTest(test.get(i)));
			double accuracy =   naiveBayesTest(test.get(i));
			sum_acc = sum_acc+accuracy;
			System.out.println();
			System.out.println("Fold  "+ i+"   Accuracy   "+ accuracy);

		}
		System.out.println();
		System.out.println("Average Accuracy    "+sum_acc/fold);

		System.out.println();
		System.out.println("Decision Tree");
		
		SetAttribute();	
		sum_acc=0;
		for(int i =0; i<fold;i++)
		{
					
			Node root= new Node(-1, "root");
			BuildTree(train.get(i), 1, root);
			//PrintTree(root);
		    double accuracy =testDT(test.get(i), root);
			sum_acc = sum_acc+accuracy;
			System.out.println();
			System.out.println("Fold  "+ i+"   Accuracy   "+ accuracy);

		}

		System.out.println();
		System.out.println("Average Accuracy    "+sum_acc/fold);
		
		
	}


	public static void NaiveBayesTrain(ArrayList<String> TrainSet)
	{

		HashMap<String, Integer> Attributes= new HashMap<>();
		HashSet<String> totalAtrribute= new HashSet<>();

		try {

			int noOfInstances=0;

			for (String line : TrainSet) {

				String[] values = line.split(",");
				for(int i=0;i<values.length-1;i++){

					totalAtrribute.add(values[i]);

					String attr = values[i]+"_"+values[values.length-1] ;

					if(Attributes.containsKey(attr))
					{
						Attributes.put(attr, Attributes.get(attr)+1);
					}
					else
					{
						Attributes.put(attr, 1);
					}

				}

				if(Classes.containsKey(values[values.length-1]))
				{
					Classes.put(values[values.length-1], Classes.get(values[values.length-1])+1);
				}
				else
				{
					Classes.put(values[values.length-1], 1);
				}
				noOfInstances++;

			}


			for (String key : Attributes.keySet()) {

				String[] arr= key.split("_");

				int den= Classes.get(arr[1]);
				int num= Attributes.get(key);

				Double prob= num/(double)den;
				//System.out.println(key+"     "+prob);
				model.put(key, prob);

			}

			for (String key : Classes.keySet()) {

				Double value= Classes.get(key)/(double) noOfInstances;
				model.put(key, value);

			} 
			

		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static double naiveBayesTest(ArrayList<String> TestSet)
	{

		try {

			int count=0;
			for (String line : TestSet) {

				String[] values = line.split(",");
				String predictedClass= null;

				double maxProd=0;
				for (String key : Classes.keySet()) {
					double prod=1;
					prod *= model.get(key);

					for(int i=0;i<values.length-1;i++){

						String chk= values[i]+"_"+key;
						prod *= model.get(chk);
					}

					if(prod>maxProd)
					{
						maxProd=prod;
						predictedClass=key;
					}
				}

				//System.out.println(line + "      "+predictedClass);

				if (predictedClass.equals(values[values.length-1])) {
					count++;
				}

			}

			double Accuracy= count/(double) TestSet.size();

			//System.out.println();

			//System.out.println("Accuracy   :"+Accuracy);

			return Accuracy;

		}  catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;

	}

	private static ArrayList<ArrayList<ArrayList<String>>> fold_validation(ArrayList<String> ar, int i) {

		// TODO Auto-generated method stub

		Queue<String> q_original = new LinkedBlockingQueue<String>();

		Queue<String> q_processed = new LinkedBlockingQueue<String>();

		Queue<String> q_processed1 = new LinkedBlockingQueue<String>();	 

		ArrayList<ArrayList<ArrayList<String>>> result = new ArrayList();

		ArrayList<String> inner = new ArrayList<>();

		ArrayList<String> inner1 = new ArrayList<>();



		q_original.addAll(ar);	

		q_processed.addAll(ar);

		q_processed1.addAll(ar);

		int size = ar.size();

		int fold = size/i;

		int itr = size/fold;

		int cnt=0;

		int k;

		while(cnt!=itr )

		{

			for(k=0;k<fold;k++)	//for testing

			{

				inner.add(q_processed.peek());    	

				q_processed.add(q_processed.peek());    //adding element at back again

				q_processed.remove(q_processed.peek());

				q_processed1.remove(q_processed1.peek());


			}

			test.add((ArrayList<String>) inner.clone());	

			inner.clear();	

			while(!q_processed1.isEmpty())

			{

				inner1.add(q_processed1.peek());

				q_processed1.remove();

			}

			train.add((ArrayList<String>) inner1.clone());

			inner1.clear();

			q_processed1.addAll(q_processed);

			cnt++;

		}

		result.add(train);

		result.add(test);

		return result;

		//			System.out.println("Training set is");

		//			for (ArrayList<String> arrayList : train) {

		//			ArrayList<String> in = arrayList;

		//			System.out.println(in);

		//	

		//			}

		//			System.out.println("Test set is");

		//			for (ArrayList<String> arrayList : test) {

		//			ArrayList<String> in = arrayList;

		//			System.out.println(in);

		//	

		//			}

	}

	public static class Node
	{
		//ArrayList<ArrayList<String>> Data= new ArrayList<>();
		int attribute;
		String value;
		ArrayList<Node> children;

		public Node(int attr, String Val)
		{
			attribute=attr;
			value=Val;
			children= new ArrayList<>();
		}

	}

	public static void SetAttribute()
	{
		
		
		ArrayList<String> temp2=new ArrayList<>();
		temp2.add("1");
		temp2.add("2");
		temp2.add("3");
		temp2.add("4");

		attibuteSetDT.put(1, temp2);
		
		
		ArrayList<String> temp=new ArrayList<>();
		temp.add("1");
		temp.add("2");
		attibuteSetDT.put(2, temp);
		
		ArrayList<String> temp1=new ArrayList<>();
		temp1.add("1");
		temp1.add("2");
		temp1.add("3");

		attibuteSetDT.put(3, temp1);

		
		
		ArrayList<String> temp3=new ArrayList<>();
		temp3.add("1");
		temp3.add("2");
		temp3.add("3");
		temp3.add("4");

		attibuteSetDT.put(4, temp3);
		ArrayList<String> temp4=new ArrayList<>();
		temp4.add("1");
		temp4.add("2");
		temp4.add("3");
		temp4.add("4");

		attibuteSetDT.put(5, temp4);
	}

	public static void BuildTree(ArrayList<String> inputData, int Attr, Node N)
	{


		ArrayList<String> attrValue= attibuteSetDT.get(Attr);


		for (String string : attrValue) {
			ArrayList<String> ModifiedData= new ArrayList<>();
			int isLeaf= IsLeaf(inputData,Attr,string,ModifiedData);

			if(isLeaf==1)
			{
				Node n = new Node(0, attributeLabel);
				Node n1 = new Node(Attr, string);
				n1.children.add(n);
				N.children.add(n1);
			}
			else if(isLeaf==2)
			{
				if(Attr==attibuteSetDT.size())
				{
					String labelValue= getMajority(ModifiedData);
					Node n = new Node(0, labelValue);
					Node n1 = new Node(Attr, string);
					n1.children.add(n);
					N.children.add(n1);
					//return;
				}
				else
				{
					Node n=new Node(Attr, string);
					N.children.add(n);
					BuildTree(ModifiedData, Attr+1, n);
				}
			}
			else if(isLeaf==0)
			{
				
				Node n = new Node(0,"1");
				Node n1 = new Node(Attr, string);
				n1.children.add(n);
				N.children.add(n1);
				//return;
			}

		}
	}

	public static int IsLeaf(ArrayList<String> inputData,int attr, String val, ArrayList<String> modified)
	{
		HashSet<String> lbl= new HashSet<>(); 
		for (String string : inputData) {
			String[] values = string.split(",");
			if(values[attr-1].equals(val))
			{
				modified.add(string);
				lbl.add(values[values.length-1]);
			}

		}

		if(lbl.size()==0)
			return 0;
		else if(lbl.size()==1)
			return 1;
		else
		{

			attributeLabel=(String) lbl.toArray()[0];
			return 2;
		}

	}

	public static String getMajority(ArrayList<String> inputData)
	{
		HashMap<String, Integer> lbl= new HashMap<>();
		int max=0;
		String labelvalue="1";
		for (String string : inputData) {
			String [] dt = string.split(",");
			
			if(lbl.containsKey(dt[dt.length-1]))
			{
				lbl.put(dt[dt.length-1], lbl.get(dt[dt.length-1])+1);
				if(max<lbl.get(dt[dt.length-1]))
				{
					max=lbl.get(dt[dt.length-1]);
					labelvalue=dt[dt.length-1];
					
				}
			}
			else
			{
				lbl.put(dt[dt.length-1], 1);
			}
			
		}
		return labelvalue;
	}
	
	public static double testDT(ArrayList<String> testData, Node Root)
	{

		int count=0;
		Node orignal= Root;
		int ct=0;
		
		
		for (String string : testData) {
			//string="2,2,2,2,4,2";
			//System.out.println(string + " "+ct++);
			String [] dt= string.split(",");
			boolean flag= false;
			String lable= null;
			for (String string2 : dt) 
			{
				ArrayList<Node> child= Root.children;
				for (Node node : child)
				{
					if(node.value.equals(string2))
					{

						if(node.children.get(0).attribute==0)
						{
							lable=node.children.get(0).value;
							flag=true;

						}
//						else if(node.children.get(0).attribute==-2)
//						{
//							continue;
//						}
						else
						{
							Root= node;

						}
						break;
					}
				}

				if(flag)
				{
					break;
				}


			}
			if(lable.equals(dt[dt.length-1]))
			{
				count++;
			}

			Root=orignal;


		}

		double Acc= count/(double)testData.size();
		return Acc;

	}

	
	public static void PrintTree(Node root)
	{
		 Queue<Node> queue = new LinkedList<Node>();
		 
		 if(root == null) return;

	       // root.state = State.Visited;
	         //Adds to end of queue
	        queue.add(root);
	        Node n=new Node(-3, "Level");
            queue.add(n);
            
            ArrayList<Node> ar= root.children;
            
            for (Node node : ar) {
            	 queue.add(node);
			}
            
            Node n1=new Node(-3, "Level");
            queue.add(n1);
            
	        while(!queue.isEmpty())
	        {
	            //removes from front of queue
	            Node r = queue.remove(); 
	            if(!queue.isEmpty() &&  r.value.equals("Level"))
	            	{Node n2=new Node(-3, "Level");
	                 queue.add(n2);
	                 System.out.println();
	                 }
	            else if(! r.value.equals("Level"))
	            {
	            System.out.print(r.attribute+" "+r.value + "\t");

	            //Visit child first before grandchild
	            ArrayList<Node> ar1= r.children;
	            
	            for (Node node : ar1) {
	            	 queue.add(node);
				}
	           
	            }
	        }


	    }
		 
	
	

}
