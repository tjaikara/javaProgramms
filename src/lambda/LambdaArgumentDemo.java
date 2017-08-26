package lambda;




public class LambdaArgumentDemo {

    interface StringFnc{
        String func(String str);
    }

    static String changeStr(StringFnc stringFnc, String s){
        return stringFnc.func(s);
    }

    public static void main (String args[]){

        String inStr = "Lambda Expressions Expand Java";
        String outStr;

        System.out.println("Here is the input String: " + inStr);

        StringFnc reverse = (str -> {
            String result = " ";
            for(int i = str.length()-1; i >= 0; i--)
                result += str.charAt(i);

            return result;
        });

        outStr = changeStr(reverse, inStr);
        System.out.println("The String reversed: " + outStr);

        outStr = changeStr((str -> {
            String result = " ";
            char ch;

            for(int i  =0; i < str.length(); i++){
                ch = str.charAt(i);
                if(Character.isUpperCase(ch))
                    result += Character.toLowerCase(ch);
                else
                    result += Character.toUpperCase(ch);
            }
            return  result;
        }), inStr);

        System.out.println("This string in reversed case: "+ outStr);


        outStr = changeStr((str -> str.replace(" ", "-")), inStr);

        System.out.println("This string in reversed case: "+ outStr);
    }
}
