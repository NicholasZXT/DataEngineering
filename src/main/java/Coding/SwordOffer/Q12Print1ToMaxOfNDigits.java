package Coding.SwordOffer;

public class Q12Print1ToMaxOfNDigits {
    public static void main(String[] args) {
        // 测试用例
        int n1 = 1;
        int n2 = 2;
        int n3 = 3;

        Q12Print1ToMaxOfNDigits solver = new Q12Print1ToMaxOfNDigits();
        //System.out.println("result for '" + n1 + "' is :");
        //solver.print1ToMax_v1(n1);
        //System.out.println("result for '" + n2 + "' is :");
        //solver.print1ToMax_v1(n2);
        //System.out.println("result for '" + n3 + "' is :");
        //solver.print1ToMax_v1(n3);

        //System.out.println("---------- 解法2 ----------");
        System.out.println("result for '" + n1 + "' is :");
        solver.print1ToMax_v2(n1);
        System.out.println("result for '" + n2 + "' is :");
        solver.print1ToMax_v2(n2);
        //System.out.println("result for '" + n3 + "' is :");
        //solver.print1ToMax_v2(n3);

    }


    /**
     * 给定正整数 n，打印出从 1 到 最大 n位数 中的所有数.
     * 比如 n = 3，则打印 1,2,..., 999.
     * 解法1：使用字符串表示
     * @param n
     */
    public void print1ToMax_v1(int n){
        if(n<=0){
            return;
        }
        char[] num = new char[n];
        //需要对数组中的每一位做初始化
        for(int i = 0; i <= n-1; i++)
            // 必须使用 0 的 char表示，而不是直接赋值整数0
            num[i] = '0';
        while(!incrementCharArray(num)){
            printArrayNumber(num);
        }
    }

    /**
     * 检测 char数组表示的 num 是否可以加 1 的函数 -------- KEY
     * @param num
     * @return
     */
    public boolean incrementCharArray(char[] num){
        // 是否溢出
        boolean isOverflow = false;
        int len = num.length;
        int takeover = 0;  // 进位值
        int addValue = 0;  // 记录每一位加上 进位值 后的数值
        // 从最后一位向前遍历，加 1 ，看是否要进位
        for(int i = len-1; i >= 0; i--){
            // 直接使用 char 的ASCII码进行计算
            addValue = num[i] - '0' + takeover;
            if (i == len-1){
                // 只有最后1位才执行 +1
                addValue ++;
            }
            // 检查是否需要进位
            if(addValue >= 10){
                // 该位上的数 > 10，需要进位
                if(i == 0)
                    // 检查是否已经到了最高位，到了最高位则无法进位
                    isOverflow = true;
                else {
                    // 没有到最高位，可以进位
                    takeover = 1;
                    addValue = addValue - 10;
                    // 类型强转
                    num[i] = (char) ('0' + addValue);
                }
            }else {
                // 不需要进位，则直接加
                num[i] = (char) ('0' + addValue);
                // 同时不进位的话，也不需要在看更高位了，直接跳出for循环
                break;
            }
        }
        return isOverflow;
    }


    /**
     * 给定正整数 n，打印出从 1 到 最大 n位数 中的所有数.
     * 比如 n = 3，则打印 1,2,..., 999.
     * 解法2：使用排列组合的方式解决，递归
     * @param n
     */
    public void print1ToMax_v2(int n){
        if(n<=0){
            return;
        }
        char[] num = new char[n];
        //需要对数组中的每一位做初始化
        for(int i = 0; i <= n-1; i++)
            // 必须使用 0 的 char表示，而不是直接赋值整数0
            num[i] = '0';
        for(int i=0; i<10; i++){
            num[0] = (char) ('0' + i);
            printNumberRecursively(num, n, 0);
        }
    }

    public void printNumberRecursively(char[] num, int length, int index){
        if(index == length-1){
            printArrayNumber(num);
            return;
        }
        for(int i=0; i<10; i++){
            // 特别要注意这里的 index+1 --- KEY
            num[index+1] = (char) ('0' + i);
            printNumberRecursively(num, length, index+1);
        }

    }

    /**
     * 专门用于打印以 char 数组形式表示的数字，不打印前面的 '0'
     * @param num
     */
    public void printArrayNumber(char[] num){
        boolean isPrint = false;
        for(int i = 0; i <= num.length-1; i++){
            if(num[i] != '0'){
                System.out.print(num[i]);
                isPrint = true;
            }
            if(num[i] == '0' && isPrint){
                System.out.print(num[i]);
            }
        }
        System.out.println();
    }
}
