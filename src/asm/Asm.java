/**
 * Justin Coates
 * Brendan McGarry
 *
 * CS 3220
 * Project 2 - Assembler
 * 2/23/2015
 */
package asm;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 *
 * @author Justin
 */
public class Asm {
    // <Label, Address>
    private static HashMap<String, Integer> table = new HashMap<>();
    // <Name, Value>
    private static HashMap<String, Integer> constants = new HashMap<>();
    // <Address, Value>
    private static HashMap<Integer, Integer> memwords = new HashMap<>();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        /**
         * 1st Pass
         *
         * Read .a32 file and build table of Labels/Addresses/Constants
         */
        File fileIn = new File("test1.a32");

        try (Scanner reader = new Scanner(new FileReader(
                fileIn))) {
            String curWord;
            String line = "";

            Pattern labelPattern = Pattern.compile(
                "[a-zA-Z]([0-9a-zA-Z])*:"); // matches a label
            Pattern pseudoPattern = Pattern.compile(".[a-zA-Z]+");  // matches a psuedo-op
            Matcher labelMatcher;
            Matcher pseudoMatcher;
            int curAddr = 0x00000000;

            while (reader.hasNext()) {
                if (curAddr != 0) line = reader.nextLine();

                if (reader.hasNext()) {
                    curWord = reader.next();

                    // check for comments - ignore the whole line if ; found
                    if (!Pattern.compile(";.*").matcher(curWord).matches()) {
                        labelMatcher = labelPattern.matcher(curWord);
                        pseudoMatcher = pseudoPattern.matcher(curWord);

                        if (labelMatcher.matches()) {
                            // found a label
                            table.put(curWord, curAddr);
                        } else if (pseudoMatcher.matches()) {
                            // found a pseudo-op
                            String op = curWord.substring(1);
                            /*
                             * .ORG - set current address equal to given value
                             */
                            if (op.equals("ORG") || op.equals("ORIG")
                             || op.equals("org") || op.equals("orig")) {
                                if (!reader.hasNext()) {
                                    System.out.println(".ORIG requires an address");
                                    return;
                                }
                                curWord = reader.next();
                                curAddr = Integer.parseInt(curWord.substring(2), 16);
                            /*
                             * .NAME - create a constant with the given name and
                             *         value
                             * in 2nd pass, any instance of a constant name
                             * will be replaced with its value
                             */
                            } else if (op.equals("NAME") || op.equals("name")) {
                                if (!reader.hasNext()) {
                                    System.out.println(".NAME requires a name");
                                    return;
                                }
                                curWord = reader.next();
                                if (!reader.hasNext()) {
                                    System.out.println(".NAME requires a value");
                                    return;
                                }
                                constants.put(curWord,
                                    Integer.parseInt(reader.next().substring(2), 16));
                            /*
                             * .WORD - store given value at current address
                             */
                            } else if (op.equals("WORD") || op.equals("word")) {
                                if (!reader.hasNext()) {
                                    System.out.println(".WORD requires a value");
                                    return;
                                }
                                curWord = reader.next();
                                if (table.containsKey(curWord)) {
                                    memwords.put(curAddr, table.get(curWord));
                                } else {
                                    memwords.put(curAddr,
                                            Integer.parseInt(curWord.substring(2), 16));
                                }
                            }
                        }
                        System.out.println(curWord);
                    }
                }
                curAddr += 4;

            }
            for (String s : table.keySet()) {
                System.out.println(s + " 0x" + String.format("%08x", table.get(s)));
            }
            for (String s : constants.keySet()) {
                System.out.println(s + " = 0x" + String.format("%08x", constants.get(s)));
            }
            for (int s : memwords.keySet()) {
                System.out.println("0x" + s + " <- 0x"
                        + String.format("%08x", memwords.get(s)));
            }
        } catch (IOException e) {
            System.out.println("Error: File not found.");
        }

        /**
         * 2nd Pass
         *
         * Translate instructions in input file to 32 hex values and write
         * result to output .mif file
         */



        /*
        // create a new .MIF file and write header
        File fileOut = new File("test.mif");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(
                fileOut))) {
            String head = "WIDTH=32;\n"
                        + "DEPTH=2048;\n"
                        + "ADDRESS_RADIX=HEX;\n"
                        + "DATA_RADIX=HEX;\n"
                        + "CONTENT BEGIN;\n";
            writer.write(s, 0, s.length());
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
        */
    }

}
