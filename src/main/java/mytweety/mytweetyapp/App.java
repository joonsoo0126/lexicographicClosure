package mytweety.mytweetyapp;

import java.io.IOException;
import java.util.Scanner;

import org.tweetyproject.commons.ParserException;

public class App {
    public static void main(String[] args) throws IOException, ParserException {
        String mode = args[0];
        if (mode.equals("fileWriter")){
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter file name");
            fileWriter.write(scanner.nextLine());
        }
        else if(mode.equals("Reasoner")){
            LexicalReasoner.reasoner();
        }
        else if(mode.equals("Tester")){
            LexicalTester.test();
        }
    }
}
