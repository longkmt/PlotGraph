import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Stack;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
public class PlotGraph {
	

	
	
	//Assign a numeric value to the operator as we want to see which operator has higher precendence 
	public static int operatorLevel(char op){
		switch (op){
			case '+': return 0;
			case '-': return 0;
			case '*': return 1;
			case '/': return 1;
			case '$': return 2;
			case 'C': return 2;
			case '^': return 2;
			
			default: throw new IllegalArgumentException("Operator unknown:" + op);
		}
		
	}
	
	
	//parse the original expression into a postfix expression
	public static String infixParser(String s, char var){
		
		Stack<Character> stk = new Stack<Character>();
		char[] ca = s.toCharArray();
		StringBuilder infix = new StringBuilder();	
		
		
		for (int i=0; i<ca.length; i++){
				
			//detect digits, if yes add it to a string with a space
					if (Character.isDigit(ca[i]) || ca[i] == '.'){

							infix.append(ca[i]);
							
							try{ //this is for in case the number has multiple digits
								if( (i+1) < ca.length) {
									
										if (!Character.isDigit(ca[i+1]) && ca[i+1]!= '.')
												infix.append(' ');
									
								}
								
								else infix.append(' ');
							}
							catch (NullPointerException e){
								
							}
						
					}
					
		//detect the sin/cos function
					if (ca[i] == 's' || ca[i] =='c'){
							if (i+2 < ca.length){
									
									if (ca[i+1] == 'i' && ca[i+2] == 'n'){ //detect a sin fuction
								
												stk.push('$'); //we use '$' as the symbol for sin function
									}
									
									else if (ca[i+1] == 'o' && ca[i+2] == 's'){ //detect a sin fuction
										
										stk.push('C'); //we use '$' as the symbol for sin function
									}
							}
					}
					
		//detect the variable t which should be treated as a digit
					
					if (ca[i] == var){
						
						infix.append(ca[i]);
						infix.append(' ');

						
						
					}
			//detect operators:
			// I - While the stack is not empty, and an operator is at the top
			//and the operator at the top is higher priority than the item then:
			// 1) pop the operator on the top of the stack
			// 2) add the popped operator to the infix string with a space
			// 3) repeat		
			// II- Push the item on the stack		
					if (ca[i] == '+' || ca[i] == '-' || ca[i] =='*' || ca[i] =='/' || ca[i] =='^' ){
						
	
							
							while ( !stk.isEmpty() && (stk.peek() == '+' || stk.peek() == '-' || stk.peek() == '*'  || stk.peek() == '/' || stk.peek() == '$' || stk.peek() == 'C' || stk.peek() =='^') && ( operatorLevel(stk.peek()) > operatorLevel(ca[i]) ) ) {
									
									if (stk.peek() == '$' || stk.peek() == 'C'){
										infix.append('1');
										infix.append(' ');
									}
								
									infix.append(stk.pop());	
									infix.append(' ');
							}
							
							stk.push(ca[i]);
												
					}
					
			//detect left paren: push the left paren to the stack
					if (ca[i] == '(') {
					
						stk.push(ca[i]);
					}
					
			//detect right paren: pop the top item of the stack
			//while that top item is not a left paren, add that top item to the infix string
			//pop another item of the stack - repeat	
					if (ca[i] == ')') {
						
						
						char c = stk.pop();
							
						while (c != '(' ){
									
								if (c == '$' || c == 'C'){ //whenever we pop a sine/cosine, we need to add '1' to infix first
										infix.append('1');
										infix.append(' ');
								}
										
								infix.append(c);
								infix.append(' ');
								c = stk.pop();
						}
				
					}
					
		
				
		 }
		
		//while the stack is not empty, pop a thing off the stack
		while (!stk.isEmpty()){
			
			//check if the top of the stack is a sine/cosine function, if yes then need to ADD 1 to the infix FIRST
			if (stk.peek() == '$' || stk.peek() =='C'){
				infix.append('1');
				infix.append(' ');
			}			
			
			infix.append(stk.pop());	
			infix.append(' ');
		}
		
	if (infix.charAt(infix.length() -1) == ' '){
		
		//remove the last whitespace from infix

		infix.deleteCharAt(infix.length() -1);
	}
		
	
//	System.out.println("The infix is " + infix);
		
		//return the parsed string
		return infix.toString();
		
		
	}
	
	
	//calculate expression from the string
	public static double postfixCalc(String infix, char varChar, double varNum){
			
		
			Stack<Double> stk = new Stack<Double>();
			int space_pos =-1;
			double first, second;

			
			for (int i =0; i < infix.length(); i++){
				
				switch(infix.charAt(i)){
					
							case ' ': 
												String tmp = new String();
												for (int j = space_pos +1; j <i;j++){
														if (Character.isDigit(infix.charAt(j))){
															tmp = tmp + infix.charAt(j);
														}
														else if (infix.charAt(j) == varChar){//if it is a variable then replace it with the varNum
															stk.push(varNum);
														}
												}
												//now we have a number, add it to the stack
												if (tmp.length() >0){
													stk.push(Double.valueOf(tmp));
												}
												//update space_pos
												
												space_pos = i;
												break;			
												
							case '+': 							
												second = stk.pop();
												first = stk.pop();
												stk.push(first + second);
												space_pos = i;
												break;				
								
							case '-': 							
												second = stk.pop();
												first = stk.pop();
												stk.push(first - second);
												space_pos = i;
												break;	
							
							case '*': 							
												second = stk.pop();
												first = stk.pop();
												stk.push(first * second);
												space_pos = i;
												break;
							
							case '/': 
												second = stk.pop();
												first = stk.pop();
												stk.push(first / second);
												space_pos = i;
												break;
							
							case '$':
												second = stk.pop();
												first = stk.pop();
												stk.push(Math.sin(first * second * 0.0174532925)); //convert from degree to radian
												space_pos = i;
												break;
												
							case 'C':
												second = stk.pop();
												first = stk.pop();
												stk.push(Math.cos(first * second * 0.0174532925)); //convert from degree to radian
												space_pos = i;
												break;
												
							case '^':
												second = stk.pop();
												first = stk.pop();
												stk.push(Math.pow(first,second));
												space_pos = 1;
												break;
												
							default: break;
								
					
											
				}
				
			
			}
			
			//when the stack is done, the only value left on the stack is the value of the expression
			return stk.pop();
			
	}
	

	
	public static String getFunctionX(String sf){
		
		int index1, index2;
		
		index1 = sf.indexOf('[');
		index2 = sf.indexOf(',');
		System.out.println(sf.substring(index1 + 1, index2));
		
		return sf.substring(index1 + 1, index2);
		
	}
	
	public static String getFunctionY(String sf){
		
		int index1, index2;
		
		index1 = sf.indexOf(',');
		index2 = sf.indexOf(']');
		System.out.println(sf.substring(index1 + 1, index2));
		
		return sf.substring(index1 + 1, index2);
		
	}
	
	public static Point2D.Double getBoundX(String sx){

		int index1,index2;	
		double left_bound, right_bound;
		
		String range_str = infixParser(sx,'?');
		
		index1 = range_str.indexOf(' ');
		index2 = range_str.indexOf(' ', index1 + 1 );
		
		left_bound = Double.valueOf(range_str.substring(0,index1)); //get the left bound
		right_bound = Double.valueOf(range_str.substring(index1 + 1,index2));
		
		if ( range_str.charAt(range_str.length() -1) == '-'){ //if the last character is a '-' sign then the left bound is negative
			left_bound = left_bound * -1;
		}
		
		if ( ( range_str.indexOf('-') ) != (range_str.length() -1) ){//if there are more than one '-' sign -> the right bound is also negative
			right_bound = right_bound * -1;
		}
		
		System.out.println("Bound X are: " + left_bound + ".. " + right_bound);
		
		Point2D.Double p = new Point2D.Double(left_bound,right_bound);
		
		
		return p;
			
}
	public static Point2D.Double getBoundY(String sy){

		int index1,index2;	
		double left_bound, right_bound;
		
		String range_str = infixParser(sy,'?');
		
		index1 = range_str.indexOf(' ');
		index2 = range_str.indexOf(' ', index1 + 1 );
		
		left_bound = Double.valueOf(range_str.substring(0,index1)); //get the left bound
		right_bound = Double.valueOf(range_str.substring(index1 + 1,index2));
		
		if ( range_str.charAt(range_str.length() -1) == '-'){ //if the last character is a '-' sign then the left bound is negative
			left_bound = left_bound * -1;
		}
		
		if ( ( range_str.indexOf('-') ) != (range_str.length() -1) ){//if there are more than one '-' sign -> the right bound is also negative
			right_bound = right_bound * -1;
		}
		
		System.out.println("Bound Y are: " + left_bound + ".. " + right_bound);
		
		Point2D.Double p = new Point2D.Double(left_bound,right_bound);
		
		
		return p;
			
}
	
	public static Vector<Double> getRangeT(String str){
		
		Vector<Double> range = new Vector<Double>(1000);
		int index1,index2;	
		double incr,tmp;
		double left_bound, right_bound;
		
		//infixParser will take a string and a variable name
		//but we do not need the variable name in this case
		//therefore assign an arbitrary char '?' to the function
		String range_str = infixParser(str,'?');
		
		index1 = range_str.indexOf(' ');
		index2 = range_str.indexOf(' ', index1 + 1 );
		
		left_bound = Double.valueOf(range_str.substring(0,index1)); //get the left bound
		right_bound = Double.valueOf(range_str.substring(index1 + 1,index2));
		
		if ( range_str.charAt(range_str.length() -1) == '-'){ //if the last character is a '-' sign then the left bound is negative
			left_bound = left_bound * -1;
		}
		
		if ( ( range_str.indexOf('-') ) != (range_str.length() -1) ){//if there are more than one '-' sign -> the right bound is also negative
			right_bound = right_bound * -1;
		}
		
		//NOTE: IF USER ACIDENTALLY ASSIGN NEGATIVE FOR RIGHT BOUND AND POSITIVE FOR LEFT BOUND, THE PROGRAM WILL AUTOMATICALLY SWITCH
		//THE SIGN FOR LEFT BOUND AND RIGHT BOUND

			System.out.println("Left bound: " + left_bound);
			System.out.println("Right bound: " + right_bound);
			
		//Now create a vector of 100 values from left bound to right bound
			if (left_bound<0 && right_bound<0){
				incr =( Math.abs(right_bound) + Math.abs(left_bound)) /1000;
			}
			else{
				incr = (Math.abs(right_bound - left_bound)) /1000;
			}
			
			System.out.println("Increase: " + incr);
			tmp = left_bound;
			range.add(0,left_bound);
			
			
			for (int i=1; i<1000; i++){
				tmp = tmp + incr;
				if (tmp<right_bound)
						range.add(i,tmp);
				else
					System.out.println("!!!OUT OF RANGE DETECTED!!!");
				
			}
			
			range.set(999,right_bound);
			
			for (int i = 500; i<510; i++){
				
				System.out.println(range.get(i));
			}
			
			return range;
			
}
		
public static Vector<Double> getRangeX(Vector<Double> range_t, String funcX, char var){
	
	Vector<Double> range_x = new Vector<Double>(1000);
	System.out.println("Get to getRangeX!");
	for (int i=0; i<1000; i++){
		
		double t_value = range_t.get(i);
		range_x.add(i, postfixCalc(infixParser(funcX,var) , var, t_value) );
	}
	
	return range_x;
}

public static Vector<Double> getRangeY(Vector<Double> range_t, String funcY, char var){
	
	Vector<Double> range_y = new Vector<Double>(1000);
	System.out.println("Get to getRangeY!");
	
	for (int i=0; i<1000; i++){
		
		double t_value = range_t.get(i);
		range_y.add(i, postfixCalc(infixParser(funcY,var) , var, t_value) );
	}
	
	return range_y;
	
}


public static double max(Point2D.Double p){
	
	if (Math.abs(p.x)>= Math.abs(p.y))
			return Math.abs(p.x);
	else 
				return Math.abs(p.y); 
}

	
	public static void drawGraph(String sf, final String sx, final String sy, String st){
		
		
			//	System.out.println("Get to drawGraph!");
				JFrame mygraph = new JFrame("PlotGraph v0.1");
				
			//	GLint real_y;
				//GLdouble mx, my, mz;
				
				mygraph.setBounds(125,25,650,600);
				mygraph.setLocationRelativeTo(null);
				mygraph.setDefaultCloseOperation(mygraph.EXIT_ON_CLOSE);
				
		
				String funcX = getFunctionX(sf);
				String funcY = getFunctionY(sf);
				char var = st.charAt(0); //get the name of the variable assuming it can be diffrent from 't';
						
				final Vector<Double> range_t = getRangeT(st); //get the range of t
				
		
				//create a corresponding vectors of x and y based on values of t
				final Vector<Double> range_x = getRangeX(range_t,funcX,var);
				
				for (int i=500; i<510; i++){
					System.out.println(range_x.get(i));
				}
		
				
				final Vector<Double> range_y = getRangeY(range_t,funcY,var);
				
				for (int i=500; i<510; i++){
					System.out.println(range_y.get(i));
				}
				
		
				//draw the graph to a JPanel, our graph is actually just a collection of points connecting 2 points
				mygraph.add(new JPanel(){
						public void paintComponent(Graphics g){
								
								super.paintComponent(g);
								
								int ox = getWidth()/2;
								int oy = getHeight()/2;
								
								System.out.println("Ox is: " + ox);
								System.out.println("Oy is: " + oy);
								Graphics2D g2 = (Graphics2D) g;
								
								g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
								g2.setColor(Color.BLACK );
								g2.setStroke(new BasicStroke(2));
								g2.drawLine(0,oy,ox*2,oy);
								g2.drawLine(ox,0,ox,oy*2);
								double ratioX = getWidth()/max(getBoundX(sx));
								double ratioY = getHeight()/max(getBoundY(sy));
								
								double left_bound_x = getBoundX(sx).x;
								double right_bound_x = getBoundX(sx).y;
								double left_bound_y = getBoundY(sy).x;
								double right_bound_y = getBoundY(sy).y;
								
								g2.setColor(Color.BLUE );
								g2.setStroke(new BasicStroke(5));
								
								for (int i=0; i<998; i++){
										if (range_x.get(i) >= left_bound_x && range_x.get(i) <= right_bound_x && range_y.get(i) >= left_bound_y && range_y.get(i) <= right_bound_y)
												g2.draw(new Line2D.Double(range_x.get(i)*ratioX+ox,range_y.get(i)*ratioY+oy,range_x.get(i)*ratioX+ox,range_y.get(i)*ratioY+oy));
																				
								}
								
								g2.dispose();	
								
								
							
						}
					
				});
				
				mygraph.setVisible(true);							
		
	}
	
	
	
	public static void main(String[] args){
		
			if (args.length != 4){
				System.out.println("Invalid number of arguments");
				System.exit(0);
			}
			
			for (String s:args){
						
				
						System.out.println("You have just entered: " + s);
		
			}
			
	
			drawGraph(args[0],args[1],args[2],args[3]);
		
	
	
	
	}

}
