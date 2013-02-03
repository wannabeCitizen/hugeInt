import java.util.ArrayList;

public class HugeInteger implements Comparable<HugeInteger>{
    
    public static int DIGIT_OPERATIONS;
    private ArrayList<Integer> myArray; 


    //Constructor Function for accepting ArrayLists

    public HugeInteger(ArrayList<Integer> a){
        while(a.get(a.size() - 1) == 0 && a.size() > 1){
            a.remove(a.size()-1);
        }


        myArray = new ArrayList<Integer>(a);

    }

    //Constructor for accepting Strings

    public HugeInteger(String s){
        if (s == null || s.isEmpty()){
            s="0";
        }
        myArray = new ArrayList<Integer>(s.length());
        while (s.charAt(0) == '0' && s.length() > 1){
            s = new String(s.substring(1));
        }

        for (int i = s.length()-1; i>=0;i--){
            myArray.add(Character.getNumericValue(s.charAt(i)));
        }
    }

    //For getting the length
    private int totalNum(){
        return this.myArray.size();
    }

    //Comparing Function
    public int compareTo(HugeInteger h){
        if (this.totalNum() > h.totalNum()){ //Case: this is bigger than comparison number
            DIGIT_OPERATIONS++;
            return 1;  
        }else if (this.totalNum() < h.totalNum()){ //Case: this is smaller than comparison number
            DIGIT_OPERATIONS++;
            return -1;
        }else{ //Case: Same Size
            for (int i=this.totalNum() - 1;i>=0; i--){
                DIGIT_OPERATIONS++;
                if (this.getDigit(i) > h.getDigit(i)){
                    return 1;
                }else if (this.getDigit(i) < h.getDigit(i)){
                    return -1;
                }
            }
            return 0;
        }
    }

    //For Returning a digit inside of the ArrayList
    private int getDigit(int i){
        return (i>=this.totalNum()) ? 0:this.myArray.get(i);
    }
    
    //Method for adding this and h using 'school formula' 
    public HugeInteger add(HugeInteger h){
        ArrayList<Integer> ans = new ArrayList<Integer>(); 
        int n;
        int carryMe = 0;
        int holder;

        if (this.totalNum()>=h.totalNum()){
            n = this.totalNum();
        }else{
            n = h.totalNum();
        }
        
        for (int i=0;i<n;i++){
            holder = this.getDigit(i) + h.getDigit(i) + carryMe;
            DIGIT_OPERATIONS++;
            if ((holder >= 10) && (i != n-1)){
                ans.add(holder-10);
                DIGIT_OPERATIONS++;
                carryMe = 1;
            }else if ((i == n-1) && (holder >= 10)){
                ans.add(holder-10);
                DIGIT_OPERATIONS++;
                ans.add(1);
            }else{
                ans.add(holder);
                carryMe = 0;
            }
                    
            holder = 0;
        
        } 
        return new HugeInteger(ans);

       
    }

    //Method for subtracting this and h using 'school forumla' (assumes this is larger than h)
    public HugeInteger subtract(HugeInteger h){
        ArrayList<Integer> ans = new ArrayList<Integer>();
        int borrowMe = 0;
        int holder;

        for (int i=0;i<this.totalNum();i++){
            if (borrowMe == 1){
                holder = this.getDigit(i)- 1;
            }else{
                holder = this.getDigit(i);
            }
            if (holder < h.getDigit(i)){
                holder += 10;
                DIGIT_OPERATIONS++;
                holder -= h.getDigit(i);
                DIGIT_OPERATIONS++;
                borrowMe = 1;
            }else{
                holder -= h.getDigit(i);
                DIGIT_OPERATIONS++;
                borrowMe = 0;
            }
            ans.add(holder);
            holder = 0;
        }
        return new HugeInteger(ans);
    }  
   
    //Method for multiplying this and h using 'school formula' (carry method)
    public HugeInteger multiply(HugeInteger h){
        //Set up accumlator, carry, and placeHolder
        HugeInteger accumulator= new HugeInteger("0");
        HugeInteger placeHolder = new HugeInteger("0");
        ArrayList<Integer> ans = new ArrayList<Integer>();
        int holder;

        //starting with first number of h
        for (int i=0;i<h.totalNum();i++){
            //Makes/resets string for holding temporary answer and for carryMe
            int carryMe = 0;
            
            //Pads with 0's
            for (int j=0;j!=i;j++){
                ans.add(0);
            }

            //Grabs first number of this and does all the single-digit multiplications
            for (int j=0;j<this.totalNum();j++){
                 
                holder = (h.getDigit(i) * this.getDigit(j))+carryMe;
                DIGIT_OPERATIONS++;

                //Keeps track of how many digits to carry over to the next step of the problem
                carryMe = holder/10;
                DIGIT_OPERATIONS++;

                //If it's not the last digit, it chops off the tens-place
                holder %= 10;
                DIGIT_OPERATIONS++;
                

                //Keeps track of the answer
                ans.add(holder);
                holder = 0;

                if (j == this.totalNum() -1){
                    ans.add(carryMe);
                    placeHolder = new HugeInteger(ans);
                    ans.clear();
               }
                

            }

            accumulator = accumulator.add(placeHolder);
        }
        return accumulator;

        
    }

    //Implements Karatsuba-Ofman Algorithm for multiplication for numbers above 10 digits
    public HugeInteger fastMultiply(HugeInteger h){
        HugeInteger temp;
        HugeInteger a, b, c, d;
        //Check the basecase to see if it's small enough to multiply
        if (this.totalNum() <= 5 || h.totalNum() <= 5){
            return this.multiply(h);    
        }

        //Find larger number of digits so I can split
        int n;
        if (this.totalNum()>=h.totalNum()){
            n = this.totalNum();
        }else{
            n = h.totalNum();
        }

        //Find mid-point
        int mid = n/2 + n%2;

        //Get a, b, c, d for to begin Karatsuba-Ofman method 
        a = new HugeInteger(this.toString().substring(0,this.totalNum() - mid));
        b = new HugeInteger(this.toString().substring(this.totalNum() - mid, this.totalNum()));
        c = new HugeInteger(h.toString().substring(0, h.totalNum() - mid));
        d = new HugeInteger(h.toString().substring(h.totalNum() - mid, h.totalNum()));
        
        //Recurse to get ac, bd, and (a+b)(c+d) 
        HugeInteger ac = a.fastMultiply(c);
        HugeInteger bd  = b.fastMultiply(d);
        HugeInteger abcd = (a.add(b)).fastMultiply((c.add(d)));
        
        HugeInteger middleTerm = abcd.subtract(ac).subtract(bd);

        //Tack on zeros to ac and middle term
        ArrayList<Integer> temp1 = new ArrayList<Integer>();
        ArrayList<Integer> temp2 = new ArrayList<Integer>();

        for (int i = 0; i<(2*mid);i++){
            temp1.add(0);
        }
        for (int i = 0; i<mid;i++){
            temp2.add(0);
        }
        temp1.addAll(ac.myArray);
        temp2.addAll(middleTerm.myArray);

        HugeInteger front = new HugeInteger(temp1); // ac*10^n
        HugeInteger middle = new HugeInteger(temp2); //[(a+b)*(c+d) - ac - bd] * 10^n/2

        return front.add(middle).add(bd); // ac*10^n + [(a+b)*(c+d) - ac - bd]*10^n/2 - bd 

    }

    //Puts HugeInteger into a String for printing
    public String toString(){
        StringBuilder printer = new StringBuilder();
        for (int i = this.totalNum()-1;i>=0;i--){
            printer.append(this.getDigit(i));
        }
        return printer.toString();
    }

}
