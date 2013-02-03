import java.util.ArrayList;

public class HugeInteger implements Comparable<HugeInteger>{
    
    public static int DIGIT_OPERATIONS;
    private ArrayList<Integer> myArray; 


    //Constructor Function
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
                if (this.getDigit(i) > h.getDigit(i)){
                    DIGIT_OPERATIONS++;
                    return 1;
                }else if (this.getDigit(i) < h.getDigit(i)){
                    DIGIT_OPERATIONS++;
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
        StringBuilder ans = new StringBuilder(); 
        int n;
        int carryMe = 0;
        if (this.totalNum()>=h.totalNum()){
            n = this.totalNum();
        }else{
            n = h.totalNum();
        }
        
        for (int i=0;i<n;i++){
            int holder;
            holder = this.getDigit(i) + h.getDigit(i) + carryMe;
            DIGIT_OPERATIONS++;
            if ((holder >= 10) && (i!=n-1)){
                ans.insert(0,holder-10);
                DIGIT_OPERATIONS++;
                carryMe = 1;
            }else{
                ans.insert(0,holder);
                carryMe = 0;
            }
        }
        
        return new HugeInteger(ans.toString());

       
    }

    //Method for subtracting this and h using 'school forumla' (assumes this is larger than h)
    public HugeInteger subtract(HugeInteger h){
        StringBuilder ans = new StringBuilder();
        int borrowMe = 0;
        
        for (int i=0;i<this.totalNum();i++){
            int holder;
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
            ans.insert(0,holder);
        }
        return new HugeInteger(ans.toString());
    }  
   
    //Method for multiplying this and h using 'school formula' (carry method)
    public HugeInteger multiply(HugeInteger h){
        //Set up accumlator, carry, and placeHolder
        HugeInteger accumulator= new HugeInteger("0");
        HugeInteger placeHolder = new HugeInteger("0");

        //starting with first number of h
        for (int i=0;i<h.totalNum();i++){
            //Makes/resets string for holding temporary answer and for carryMe
            StringBuilder ans = new StringBuilder();
            int carryMe = 0;
            
            //Pads with 0's
            for (int j=0;j!=i;j++){
                ans.append(0);
            }

            //Grabs first number of this and does all the single-digit multiplications
            for (int j=0;j<this.totalNum();j++){
                 
                int holder = (h.getDigit(i) * this.getDigit(j))+carryMe;
                DIGIT_OPERATIONS++;

                //Keeps track of how many digits to carry over to the next step of the problem
                carryMe = holder/10;
                DIGIT_OPERATIONS++;

                //If it's not the last digit, it chops off the tens-place
                if (j != (this.totalNum()-1)){
                    holder %= 10;
                    DIGIT_OPERATIONS++;
                }

                //Keeps track of the answer
                ans.insert(0,holder);
                if (j == (this.totalNum() - 1)){
                    placeHolder = new HugeInteger(ans.toString());
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
        StringBuilder temp1 = new StringBuilder(ac.toString());
        StringBuilder temp2 = new StringBuilder(middleTerm.toString());
        
        for (int i = 0; i<(2*mid);i++){
            temp1.append('0');
        }
        for (int i = 0; i<mid;i++){
            temp2.append('0');
        }

        HugeInteger front = new HugeInteger(temp1.toString()); // ac*10^n
        HugeInteger middle = new HugeInteger(temp2.toString()); //[(a+b)*(c+d) - ac - bd] * 10^n/2

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
