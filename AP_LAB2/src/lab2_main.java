import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;


public class lab2_main {

	static HashMap<String, objects> hm=new HashMap<String, objects>();
	
	lab2_main(){
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			if(readFile(new FileReader("file.txt"))==null){
				System.out.println("Error in parsing");
				return;
			}
			
			/*// Get a set of the entries
		      Set set = hm.entrySet();
		      // Get an iterator
		      Iterator i = set.iterator();
		      // Display elements
		      while(i.hasNext()) {
		         Map.Entry me = (Map.Entry)i.next();
		         System.out.print(me.getKey() + ": ");
		         System.out.println(me.getValue().toString());
		      }
		      */
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String readFile(Reader file) throws IOException {
		BufferedReader reader = new BufferedReader(file);
	    String line;
	    String[] tokens;
		while((line=reader.readLine())!=null){
			line=line.replace(" ", "");
			if(line.contains("Let")){
				tokens=line.split("Let");
				if(line.contains("=")){
					tokens=tokens[1].split("=");
					if(tokens.length==2){
						String type;
						if(isInteger(tokens[1])){
							type="int";
						}
						else if(isFloat(tokens[1])){
							type="float";
						}
						else if(tokens[1].length() - tokens[1].replace("'", "").length()==2){
							type="String";
						}
						else
							return null;
						objects temp=new objects(tokens[0],type,tokens[1].replace("'",""));
						hm.put(tokens[0],temp);
					}
				}
				else{
					tokens[0].replace(";","");
				}
			}
			else if(line.contains("Print")){
				line=line.replace("Print","");
				if(hm.containsKey(line))
					System.out.println(hm.get(line).value);
			}
			else{
				tokens=line.split("=");
				boolean isString=false;
				if(tokens.length==2){
					line=tokens[1];
					objects previous=null,present=null;
					for(int i =0;i<line.length();i++){
						if(line.charAt(i)!='+' && line.charAt(i)!='-' && line.charAt(i)!='*' && line.charAt(i)!='/'){
							if(!Character.isDigit(line.charAt(i))){
								String temp=line.charAt(i)+"";
								i++;
								while(i<line.length()&&line.charAt(i)!='+' && line.charAt(i)!='-' && line.charAt(i)!='*' && line.charAt(i)!='/'){
									temp+=line.charAt(i);
									i++;
								}
								i--;
								if(hm.containsKey(temp)){
									if(hm.get(temp).type.equals("String")){
										if(previous==null){
											previous=new objects("temp", "String", hm.get(temp).value);
										}
										else if(previous.type.equals("expression")){
											//to be implemented
											if(previous.value.charAt(previous.value.length()-1)!='+')
												return null;
											PostFixExpression exp=new PostFixExpression("("+previous.value.substring(0, previous.value.length()-1)+")");
											
											float result=exp.postFixValue(exp.getPostFix());
											if(result==-1)
												return null;
											String str=result+"";
											int length=str.length();
											if(str.charAt(length-1)=='0' && str.charAt(length-2)=='.'){
												previous.value=Integer.parseInt(str.substring(0,length-2))+hm.get(temp).value;
											}
											previous.value=result+hm.get(temp).value;
											previous.type="String";
										}
										else if(present == null){
											previous.value+=hm.get(temp).value;
										}
										else{
											if(previous.value.charAt(previous.value.length()-1)!='+')
												return null;
											PostFixExpression exp=new PostFixExpression("("+present.value.substring(0, present.value.length()-1)+")");
											float result=exp.postFixValue(exp.getPostFix());
											if(result==-1)
												return null;
											String str=result+"";
											int length=str.length();
											if(str.charAt(length-1)=='0' && str.charAt(length-2)=='.'){
												previous.value=previous.value.substring(0, previous.value.length()-1)+Integer.parseInt(str.substring(0,length-2))+hm.get(temp).value;
											}
											else{
												previous.value=previous.value.substring(0, previous.value.length()-1)+result+hm.get(temp).value;
											}
											present=null;
										}
										isString=true;
									}
									else{
										if(previous==null){
											previous=new objects("temp","expression",hm.get(temp).value);
										}
										else if(previous.type.equals("expression")){
											previous.value+=hm.get(temp).value;
										}
										else if(present==null){
											present=new objects("present", "expression", hm.get(temp).value);
										}
										else{
											present.value+=hm.get(temp).value;
										}
									}
									//i+= hm.get(temp).value.length()-temp.length();
									//line=line.replaceAll(temp, hm.get(temp).value);
								}
								else{
									return null;
								}
							}
							else if(Character.isDigit(line.charAt(i))){
								String number=""+line.charAt(i);
								i++;
								while(Character.isDigit(line.charAt(i)) || line.charAt(i)=='.'){
									number+=line.charAt(i);
									i++;
								}
								i--;
								if(previous==null){
									previous=new objects("temp","expression",number);
								}
								else if(previous.type.equals("expression")){
									previous.value+=number;
								}
								else if(present==null){
									present=new objects("temp","expression",number);
								}
								else{
									present.value+=number;
								}
							}
						}
						else if(line.charAt(i)!='+' || line.charAt(i)!='-' || line.charAt(i)!='*' || line.charAt(i)!='/'){
							if(previous!=null && present==null){
								previous.value+=line.charAt(i);
							}
							else if(previous==null){
								return null;
							}
							else{
								present.value+=line.charAt(i);
							}
						}
					}
					if(isString){
						if(hm.containsKey(tokens[0])){
							if(previous!=null){
								if(present!=null){
									if(previous.value.charAt(previous.value.length()-1)!='+')
										return null;
									PostFixExpression exp=new PostFixExpression("("+present.value.substring(0, present.value.length())+")");
									float result=exp.postFixValue(exp.getPostFix());
									if(result==-1)
										return null;
									String str=result+"";
									int length=str.length();
									if(str.charAt(length-1)=='0' && str.charAt(length-2)=='.'){
										previous.value=previous.value.substring(0, previous.value.length()-1)+Integer.parseInt(str.substring(0,length-2));
									}
									else{
										previous.value=previous.value.substring(0, previous.value.length()-1)+result;
									}
									present=null;
								}
								hm.get(tokens[0]).value=previous.value;
								hm.get(tokens[0]).type="String";
							}
							else{
								return null;
							}
						}
					}
					else{
						if(previous!=null){
							line="("+previous.value+")";
							PostFixExpression exp=new PostFixExpression(line);
							if(hm.containsKey(tokens[0]))
								hm.get(tokens[0]).value=exp.postFixValue(exp.getPostFix())+"";
							else
								return null;
						}
						else
							return null;
					}
				}
				else{
					return null;
				}
			}
		}
		return "done";
	}
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    // only got here if we didn't return false
	    return true;
	}
	public static boolean isFloat(String s) {
	    try { 
	        Float.parseFloat(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    // only got here if we didn't return false
	    return true;
	}
}
