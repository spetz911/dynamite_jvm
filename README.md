dynamite_jvm
============

JVM backend for Scala like IR language


commands:

javac Hello.java

java Hello

java -cp "./lib/asm-all-5.0.3.jar" org.objectweb.asm.util.ASMifier Hello.class

javap -v Hello.class

## почти cформулировал задачу…

   карочи, надо написать динамический язык
   но для начала научиться создавать лямды

   чтоб не генерит свои интерфейсы надо пока взять встроенные
    •	Supplier<T> -- provide an instance of a T (such as a factory)
   	•	UnaryOperator<T> -- a function from T to T
   	•	BinaryOperator<T> -- a function from (T, T) to T

   то есть сделать хотя бы функции до 2х аргументов
   где T это типа Object, т.к. язык динамичный
   то есть написать 4 проги:
   1 это hello ворлд, и еще 3 под каждый вид лямд
   можно попробовать сначала без замыкания если совсем засада

   class Hell {

   	interface Lambda {
           int apply(int a);
       }


       static Lambda getFun(int b) {
   	    int x = 12;
       	return (a) -> a + x + b;
       }

       public static void main(String[] args) {

       	Lambda f1 = getFun(77);

           int rz = f1.apply(5);

           System.out.println(rz);


       }
   }

   примерно такие, только вместо Lamda UnaryOperator
   и вместо Object int
   или наоборот, если ты его внутри сможешь закастить

   затем с помощью магии получить код для ASM
       java -cp "./lib/asm-all-5.0.3.jar" org.objectweb.asm.util.ASMifier Hello.class

   и научиться его самому генерить под конкретное описан
   в итоге, нужна функиция вида createFun2(“fun”, “arg1”, “arg2” List(“a”, “b”, “c”))
   она создает лямду от аргументов arg1, arg2 и запихивает в переменную fun. При этом зафигачивая в нее несколько переменных из внешнего окружения “a”, “b”, “c”.
   сам код лямбды значения не имеет, т.к. это просто приватный метод от 2+k аргументов, где k размер замыкания
   например, у меня получился такой:
     private static int lambda$getFun$0(int, int, int);
       descriptor: (III)I
       flags: ACC_PRIVATE, ACC_STATIC, ACC_SYNTHETIC
       Code:
         stack=2, locals=3, args_size=3
            0: iload_2
            1: iload_0
            2: iadd
            3: iload_1
            4: iadd
            5: ireturn
         LineNumberTable:
           line 11: 0
   }

   функция донор была (a) -> a + x + b;

