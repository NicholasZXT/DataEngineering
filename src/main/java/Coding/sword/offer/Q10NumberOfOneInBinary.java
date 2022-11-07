package Coding.sword.offer;

public class Q10NumberOfOneInBinary {
    public static void main(String[] args){
        //测试用例
        int a1 = 1;
        int a2 = 2147483647;
        int a3 = 0;
        int a4 = -1;
        int a5 = -2147483648;
        int a6 = 8;
        int a7 = 12;
        Q10NumberOfOneInBinary solver = new Q10NumberOfOneInBinary();

        System.out.println("number of 1 in " + a1 + " is : " + solver.numberof1(a1));
        System.out.println("number of 1 in " + a2 + " is : " + solver.numberof1(a2));
        System.out.println("number of 1 in " + a3 + " is : " + solver.numberof1(a3));
        System.out.println("number of 1 in " + a4 + " is : " + solver.numberof1(a4));
        System.out.println("number of 1 in " + a5 + " is : " + solver.numberof1(a5));
        System.out.println("number of 1 in " + a6 + " is : " + solver.numberof1(a6));
        System.out.println("number of 1 in " + a7 + " is : " + solver.numberof1(a7));

        System.out.println("------ 第二种解法 -------");
        System.out.println("number of 1 in " + a1 + " is : " + solver.numberof1_v2(a1));
        System.out.println("number of 1 in " + a2 + " is : " + solver.numberof1_v2(a2));
        System.out.println("number of 1 in " + a3 + " is : " + solver.numberof1_v2(a3));
        System.out.println("number of 1 in " + a4 + " is : " + solver.numberof1_v2(a4));
        System.out.println("number of 1 in " + a5 + " is : " + solver.numberof1_v2(a5));
        System.out.println("number of 1 in " + a6 + " is : " + solver.numberof1_v2(a6));
        System.out.println("number of 1 in " + a7 + " is : " + solver.numberof1_v2(a7));

    }

    /**
     * 给定一个数，返回该数二进制表示中1的个数
     * @param num
     * @return
     */
    public int numberof1(int num){
        int count = 0;
        int base = 1;
        // 不断的左移 base，而不是右移 num
        while( base > 0 ){
            if ( (num & base) > 0)
                count++;
            base = base << 1;
        }
        return count;
    }

    /**
     * 第二种解法，更加巧妙
     * @param num
     * @return
     */
    public int numberof1_v2(int num){
        int count = 0;
        while(num > 0){
            // 一个数减去 1 后，再与它自身做与运算，会将最右边的 1 变成 0
            num = num & (num-1);
            // 这个数的二进制里有多少个 1 ，就可以做多少次这样的操作
            count++;
        }
        return count;
    }
}
