import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import java.util.Scanner;

public class TestcasesGenerator
{
    private static Random RNG = new Random(666);
    private static String[] standardTestset;
    private static String testTemplate;

    private static void generateCGTest(int cgtestnum, int stdtestnum) throws IOException
    {
        String content = String.format(testTemplate, "Standard Testset "+stdtestnum, standardTestset[stdtestnum-1], true, false);

        FileWriter myWriter = new FileWriter("config/test"+cgtestnum+".json");
        myWriter.write(content);
        myWriter.close();
    }

    private static String permuteColors(String content)
    {
        int c1 = RNG.nextInt(5);
        int c2 = c1;
        while (c1==c2)
            c2 = RNG.nextInt(5);

        content = content.replace(""+c1, "?");
        content = content.replace(""+c2, ""+c1);
        content = content.replace("?", ""+c2);

        return content;
    }
    private static void generateCGValidators(int k) throws IOException
    {
        int cgtestnum=11;
        for (int i=0; i < k; i++)
        {
            for (int stdtestnum=1; stdtestnum<=standardTestset.length; stdtestnum++)
            {
                String content = standardTestset[stdtestnum-1];
                for (int p=0; p < i*5; p++)
                    content = permuteColors(content);
                //content = String.format(testTemplate, "Standard Testset "+stdtestnum+"-"+i, content, false, true);
                content = String.format(testTemplate, "Standard Testset "+stdtestnum+(i>0? " (recolored)":""), content, false, true);

                FileWriter myWriter = new FileWriter("config/test"+cgtestnum+".json");
                myWriter.write(content);
                myWriter.close();

                cgtestnum++;
            }
        }

    }

    public static void main(String[] args) throws IOException
    {
        standardTestset = new String(Files.readAllBytes(Paths.get("src/test/java/StandardTestset.txt")), StandardCharsets.UTF_8).trim().split("\\n\\n");
        for (int i=0; i < standardTestset.length; i++)
            standardTestset[i] = standardTestset[i].trim().replace(" \n", "\\n");
        testTemplate = new String(Files.readAllBytes(Paths.get("src/test/java/TestTemplate.json")), StandardCharsets.UTF_8);

        generateCGTest(6, 1);
        generateCGTest(7, 5);
        generateCGTest(8, 10);
        generateCGTest(9, 15);
        generateCGTest(10, 20);

        generateCGValidators(2);
    }
}


