class Example{
    public static void main(String[] a){
        System.out.println((new Simple()).one( 0 ));
        System.out.println((new Simple()).two( 0, 1 ));
        System.out.println((new Simple()).three( 0, 1, 2 ));
        System.out.println((new Simple()).ten( 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 ));
        System.out.println((new Simple()).ten( 0, 1, 2, 3, 4, (new Simple()).one(5), 6, 7, 8, 9 ));
        System.out.println((new Simple()).controlFlow( 0, 1 ));
    }
}

class Simple
{
    int local1;

    public int controlFlow(int x, int y)
    {
        int[] a;
        a = new int[10];
        
        if(x < y && y < x) {
            System.out.println(0);
        } else {
            System.out.println(1);
        }
        
        while(x < 10)
        {
            x = x + 1;
        }
                
        return 0;
    }
    
    public int one(int p1)
    {
        int i;
        System.out.println(i);
        System.out.println(local1);
        local1 = p1;
        i = local1;
        System.out.println(i);
        System.out.println(local1);
        return i;
    }

    public int two(int p1, int p2)
    {
        System.out.println(20);
        return p1 + p2;
    }

    public int three(int p1, int p2, int p3)
    {
        System.out.println(30);
        return p1 + p2 + p3;
    }
  
    public int ten(int p1, int p2, int p3, int p4, int p5, int p6, int p7, int p8, int p9, int p10)
    {
        System.out.println(100);
        return p1 + 
               p2 * 10 + 
               p3 * 100 + 
               p4 * 1000 + 
               p5 * 10000 + 
               p6 * 100000 + 
               p7 * 1000000 + 
               p8 * 10000000 + 
               p9 * 100000000 + 
               p10* 1000000000 ;
    }
}

class ExtendSimple extends Simple
{
    public int two(int p1, int p2)
    {
        System.out.println(200);
        return 2 * (p1 + p2);
    }
}
