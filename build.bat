javac -cp "lib/*" -d bin *.java

java -cp "bin;lib/*" -Djava.library.path="lib/natives/windows" Main