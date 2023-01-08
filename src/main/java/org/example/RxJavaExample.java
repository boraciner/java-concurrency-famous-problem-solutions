package org.example;

import io.reactivex.rxjava3.core.Flowable;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class RxJavaExample {
    public static void main(String[] args) throws FileNotFoundException {
        Flowable<String> flow = getSource();
        flow.subscribe(s -> System.out.println("Line : "+s));

    }


    public static Flowable<String> getSource() throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader("./src/main/resources/myFile.txt"));

        return Flowable.generate(
                emmiter->{
                    String line = reader.readLine();
                    if(line != null){
                        emmiter.onNext(line);
                    }else{
                        emmiter.onComplete();
                    }
                }
        );
    }
}
