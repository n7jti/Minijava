java -classpath build/classes:lib/CUP.jar MiniJava < $1 > a.s
gcc src/boot.c a.s
./a.out
