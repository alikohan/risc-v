import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Scanner;

public class Main {
    private static final String FILE_FORMAT = ".asm";
    private static final String[] LOAD_INSTRUCTIONS = {"ld", "lw", "lh", "lb", "lbu", "lhu", "lwu"};
    private static String[] codeLines;
    public static String[] instructions = new String[100];
    public static final int arraySize = 100; //number of code lines
    //colors
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static void main(String[] args) {
        //load instruction for check that codelines are correct
        if (!load("instructions.ins", instructions)) {
            System.out.println("can't load instructions.ins file.");
            return;
        }
        Scanner scanner = new Scanner(System.in);
        codeLines = new String[arraySize];
        String[][] parsedLines = new String[arraySize][];
        int input = 0;
        String inputString = "";
        //for store files that in this directory (directory that program run in it)
        String[] filesInDirectory;
        //files that end with FILE_FORMAT
        String[] validFilesInDirectory;
        int validFilesInDirectoryCounter = 0;
        //for input directory of this app
        File file;
        file = new File(System.getProperty("user.dir"));
        while (true) {
            showMainMenu();
            System.out.print("input : ");
            inputString = scanner.nextLine();
            //process to convert correctly string to int
            input = convertToInt(scanner, inputString);
            switch (input) {
                //add file
                case 1:
                    try {
                        filesInDirectory = file.list();
                        validFilesInDirectory = new String[filesInDirectory.length];
                        for (String str : filesInDirectory) {
                            //show files that end with FILE_FORMAT and store there in validFilesInDirectory to use later (for select, ...)
                            if (str.endsWith(FILE_FORMAT)) {
                                validFilesInDirectory[validFilesInDirectoryCounter++] = str;
                                System.out.println(validFilesInDirectoryCounter + "-" + str);
                            }
                        }
                        validFilesInDirectoryCounter = 0;
                    } catch (Exception e) {
                        System.out.println("interrupt to show files ...");
                    }
                    break;
                //select
                case 2:
                    System.out.print("enter name of file : ");
                    inputString = scanner.nextLine();
                    //for load codes and parsed them to elements of two dimensional array ([["ld", "x1", "0(x0)"], ["add", ...)
                    if (load(inputString, codeLines)) {
                        if (parser(codeLines, parsedLines))
                            selectedFileOptions(parsedLines, scanner, inputString);
                    } else {
                        System.out.println("open file has interrupted! maybe \"" + inputString + "\" file deleted or renamed.");
                        continue;
                    }
                    //for reload arrays and delete previous state
                    codeLines = new String[arraySize];
                    parsedLines = new String[arraySize][];
                    break;

                case 3:
                    addFile(scanner);
                    break;

                case 4:
                    deleteFile(scanner);
                    break;

                case 5:
                    moreOptions(scanner);
                    break;

                case 6:
                    return;

                default:
                    System.out.println("please input number between 1 to 6 ...");

            }
        }
    }

    //_______________________interface_______________________

    public static void selectedFileOptions(String[][] parsedLines, Scanner scanner, String fileName) {
        String inputString;
        int input;
        while (true) {
            showSelectedFileMenu(fileName);
            System.out.print("input : ");
            inputString = scanner.nextLine();
            input = convertToInt(scanner, inputString);
            switch (input) {
                //print
                case 1:
                    System.out.println("1-basic display");
                    System.out.println("2-advance display(for analyze and debugging)");
                    System.out.print("input : ");
                    inputString = scanner.nextLine();
                    input = convertToInt(scanner, inputString);
                    if (input == 1)
                        show(parsedLines, false);
                    else if (input == 2)
                        show(parsedLines, true);
                    else
                        System.out.println("please input 1 or 2!");
                    break;
                case 2:
                    System.out.println("cycles number : " + calculateCycles(parsedLines));
                    break;
                case 3:
                    showDependency(parsedLines);
                    break;
                case 4:
                    reorder(parsedLines);
                    System.out.println("reorder successfully done.");
                    break;
                case 5:
                    show(assembler(parsedLines));
                    break;
                case 6:
                    return;
                default:
                    System.out.println("please input number between 1 to 5 ...");

            }
        }
    }

    public static void moreOptions(Scanner scanner) {
        String inputString;
        int input;
        while (true) {
            showMoreMenu();
            System.out.print("input : ");
            inputString = scanner.nextLine();
            input = convertToInt(scanner, inputString);
            switch (input) {
                case 1:
                    show(instructions);
                    break;
                case 2:
                    String about = "this app was developed for convert to machine code, analyze, reorder and show data dependencies of risc-v codes.\n" +
                            "(reorder code to avoid use of load destination register in the next instruction without change algorithm)\n" +
                            "*show dependency is displayed with change charactars color because of this run this program with cmd may cause an error*\n" +
                            "--- developed by ALI KOHAN ---";
                    System.out.println(about);
                    break;
                case 3:
                    return;
                default:
                    System.out.println("please input number between 1 to 3 ...");
            }
        }
    }

    public static int convertToInt(Scanner scanner, String inputString) {
        int input;
        try {
            input = Integer.parseInt(inputString);
        } catch (Exception e) {
            System.out.print("please input just number : ");
            while (true) {
                inputString = scanner.nextLine();
                try {
                    input = Integer.parseInt(inputString);
                    break;
                } catch (Exception e1) {
                    System.out.print("please input just number : ");
                }
            }
        }
        return input;
    }

    public static void showMainMenu() {
        System.out.println("_________main-menu_________");
        System.out.println("(1)show files");
        System.out.println("(2)select file");
        System.out.println("(3)add file");
        System.out.println("(4)delete file");
        System.out.println("(5)more ...");
        System.out.println("(6)exit");
    }

    public static void showSelectedFileMenu(String fileName) {
        System.out.println("________" + fileName + "________");
        System.out.println("(1)print code lines");
        System.out.println("(2)calculate cycles number");
        System.out.println("(3)show dependency");
        System.out.println("(4)reorder");
        System.out.println("(5)convert to machine code (assembler)");
        System.out.println("(6)back");
    }

    public static void showMoreMenu() {
        System.out.println("___________more___________");
        System.out.println("(1)show valid instructions");
        System.out.println("(2)about");
        System.out.println("(3)back");
    }

    public static void show(String[] array) {
        for (String s : array) {
            if (s == null)
                break;
            System.out.println(s);
        }
    }

    public static void show(String[][] array, boolean withSeparator) {
        if (withSeparator)
            for (String[] sOut : array) {
                System.out.println();
                if (sOut != null)
                    for (String sIn : sOut)
                        System.out.print(sIn + " | ");
                else
                    break;
            }
        else {
            for (int i = 0; array[i] != null; i++) {
                if (i != 0)
                    System.out.println();
                if (array[i][0].length() > 3) //for 4-charactar instructions (addi, ...)
                    System.out.print(array[i][0] + "\t");
                else
                    System.out.print(array[i][0] + "\t\t");
                for (int j = 1; j < array[0].length && array[i][j] != null; j++) {
                    if (j != 1)
                        System.out.print(", ");
                    System.out.print(array[i][j]);
                }
            }
            System.out.println();
        }
    }

    //_______________________algorithm_______________________

    //for read codes from file and store there in string array(each line in one index)
    private static boolean load(String fileName, String[] lines) {
        if (!fileName.endsWith(FILE_FORMAT) && fileName != "instructions.ins")
            fileName = fileName + FILE_FORMAT;
        Scanner inputStream = null;
        //for return lines of codes in string array
        try {
            inputStream = new Scanner(new FileReader(fileName));
        } catch (Exception e) {
            return false;
        }
        lineParser(inputStream, lines);
        return true;
    }

    public static void addFile(Scanner scanner) {
        System.out.print("enter name of file : ");
        String fileName = scanner.nextLine();
        //check that certainly file ends with FILE_FORMAT
        if (!fileName.endsWith(FILE_FORMAT))
            fileName += FILE_FORMAT;
        String inputString = "";
        try {
            File file = new File(fileName);
            if (file.createNewFile())
                System.out.println("succesfully done ...");
            else {
                while (!file.createNewFile()) {
                    System.out.print("this file has already exist, use different name : ");
                    inputString = scanner.nextLine();
                    if (!inputString.endsWith(FILE_FORMAT))
                        inputString += FILE_FORMAT;
                    file = new File(inputString);
                }
                System.out.println("succesfully done ...");
            }
        } catch (Exception e) {
            System.out.println("create file has been interrupted!");
            return;
        }
        String[] writeToFile = new String[arraySize];
        System.out.println("input at least 3 line codes (press enter after each lines)" +
                "\nwhen it finish input 0 ...");
        for (int i = 0; !inputString.endsWith("\n0\n") || i < 4; i++, inputString += "\n")
            inputString += scanner.nextLine();
        try {
            //for remove \n0\n
            inputString = charRemoveAt(inputString, inputString.length() - 1);
            inputString = charRemoveAt(inputString, inputString.length() - 1);
            inputString = charRemoveAt(inputString, inputString.length() - 1);
            FileWriter fileWriter = new FileWriter(fileName);
            fileWriter.write(inputString);
            fileWriter.close();
        } catch (Exception e) {
            System.out.println("write in file has been interrupted!");
        }

    }

    public static String charRemoveAt(String string, int number) {
        return string.substring(0, number) + string.substring(number + 1);
    }

    public static void deleteFile(Scanner scanner) {
        System.out.print("enter name of file : ");
        String fileName = scanner.nextLine();
        if (!fileName.endsWith(FILE_FORMAT))
            fileName += FILE_FORMAT;
        File file = new File(fileName);
        try {
            if (file.delete())
                System.out.println("delete successfully!");
            else
                System.out.println("can't delete this file, maybe this file isn't exist or can't be access.");
        } catch (Exception e) {
            System.out.println("can't delete this file, maybe this file isn't exist or can't be access.");
        }
    }

    private static String[] lineParser(Scanner inputStream, String[] lines) {
        //temp for check that this line isn't empty (input just lines that are have at least one charactar)
        String temp;
        for (int i = 0; inputStream.hasNext() && i < arraySize; i++) {
            temp = inputStream.nextLine();
            if (temp.length() != 0)
                lines[i] = temp;
            else
                i--;
        }
        return lines;
    }

    public static boolean parser(String[] array, String[][] parsedArray) {
        //format will be like that [["ld", "x1", "0(x0)"], ["add", ...
        //for check if words are valid
        String tempWord = "";
        //parsedArray[outerCounter][innerCounter]
        int outerCounter = 0;
        int innerCounter = 0;
        for (String s : array) {
            if (s == null)
                break;
            parsedArray[outerCounter] = new String[4];
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) != ' ' && s.charAt(i) != '\t' && s.charAt(i) != ',') {
                    tempWord = tempWord.concat(String.valueOf(s.charAt(i)));
                } else if (tempWord != "") {
//                    tempWord = tempWord.replace(",", "");
                    //for check validity of instructions
                    if (innerCounter == 0 && !isValidInstruction(tempWord)) {
                        System.out.println("invalid instructions in file (line : " + (outerCounter + 1) + "). please check code ...");
                        return false;
                    }
                    parsedArray[outerCounter][innerCounter++] = tempWord;
                    tempWord = "";
                }
            }
            parsedArray[outerCounter][innerCounter] = tempWord;
            tempWord = "";
            outerCounter++;
            innerCounter = 0;
        }
        return true;
    }

    private static boolean isValidInstruction(String instruction) {
        for (String s : instructions)
            if (instruction.equals(s))
                return true;
        return false;
    }

    public static int calculateCycles(String[][] parsedLines) {
        if (parsedLines[0][0] == null)
            return -1;
        //for count cycles
        int counter = 0;
        //for store destination register in ld(load) instructions
        String rd;
        //if read load instruction will be true (for calculate bubbles)
        boolean loadCheck = false;
        //destination register of load instruction
        String loadRd = "";
        //for first instruction
        counter += 4; //5 - 1
        for (int i = 0; i < parsedLines.length && parsedLines[i] != null; i++) {
            if (loadCheck) {
                if (loadRd.equals(parsedLines[i][2]) || (parsedLines[i][3] != null && loadRd.equals(parsedLines[i][3]))) {
                    //for bubble
                    counter++;
                }
            }
            counter++;
            loadCheck = false;
            //check that instruction is load instruction
            for (String instruction : LOAD_INSTRUCTIONS)
                if (instruction.equals(parsedLines[i][0])) {
                    loadRd = parsedLines[i][1];
                    loadCheck = true;
                }
        }
        return counter;
    }

    public static void reorder(String[][] parsedLines) {
        //for store destination register in ld(load) instructions
        String rd;
        //if read load instruction will be true
        boolean loadCheck = false;
        //destination register of load instruction
        String loadRd = "";
        for (int i = 0; i < parsedLines.length && parsedLines[i] != null; i++) {
            if (loadCheck) {
                if (loadRd.equals(parsedLines[i][2]) || (parsedLines[i][3] != null && loadRd.equals(parsedLines[i][3]))) {
                    //check that algorithm not been changed (which dependencies can change algorithm)
                    if (parsedLines[i + 1] != null && !(parsedLines[i][1].equals(parsedLines[i + 1][2]) ||
                            (parsedLines[i + 1][3] != null && parsedLines[i][1].equals(parsedLines[i + 1][3])))) {
                        //swap with bottom line
                        String[] temp;
                        temp = parsedLines[i + 1];
                        parsedLines[i + 1] = parsedLines[i];
                        parsedLines[i] = temp;
                    }
                }
            }
            loadCheck = false;
            //check that instruction is load instruction
            for (String instruction : LOAD_INSTRUCTIONS)
                if (instruction.equals(parsedLines[i][0])) {
                    loadRd = parsedLines[i][1];
                    loadCheck = true;
                }
        }
    }

    //show dependency with change register color
    public static void showDependency(String[][] parsedLines) {
        //for store destination register in ld(load) instructions
        String rd;
        //if read load instruction will be true
        boolean loadCheck = false;
        //destination register of load instruction
        String loadRd = "";
        //[line][element number of line(1-4)]
        int[][] indexPointer = new int[10][2];
        int indexPointerCounter = 0;
        for (int i = 0; i < parsedLines.length && parsedLines[i] != null; i++) {
            if (loadCheck) {
                if (loadRd.equals(parsedLines[i][2]) || (parsedLines[i][3] != null && loadRd.equals(parsedLines[i][3]))) {
                    //which ones should be colored
                    if (loadRd.equals(parsedLines[i][2])) {
                        indexPointer[indexPointerCounter][0] = i - 1;
                        indexPointer[indexPointerCounter++][1] = 1;
                        indexPointer[indexPointerCounter][0] = i;
                        indexPointer[indexPointerCounter++][1] = 2;
                    } else if (loadRd.equals(parsedLines[i][3])) {
                        indexPointer[indexPointerCounter][0] = i - 1;
                        indexPointer[indexPointerCounter++][1] = 1;
                        indexPointer[indexPointerCounter][0] = i;
                        indexPointer[indexPointerCounter++][1] = 3;
                    }
                }
            }
            loadCheck = false;
            //check that instruction is load instruction
            for (String instruction : LOAD_INSTRUCTIONS)
                if (instruction.equals(parsedLines[i][0])) {
                    loadRd = parsedLines[i][1];
                    loadCheck = true;
                }
        }

        indexPointerCounter--;
        int i = 0;
        for (int[] index : indexPointer) {
            for (; i <= index[0] && parsedLines[i] != null; i++) {
                System.out.println();

                if (parsedLines[i][0].length() > 3) //for 4-charactar instructions (addi, ...)
                    System.out.print(parsedLines[i][0] + "\t");
                else
                    System.out.print(parsedLines[i][0] + "\t\t");

                for (int j = 1; j < parsedLines[0].length && parsedLines[i][j] != null; j++) {
                    if (j != 1)
                        System.out.print(", ");
                    if (i == index[0] && j == index[1])
                        System.out.print(ANSI_RED + parsedLines[i][j] + ANSI_RESET);
                    else
                        System.out.print(parsedLines[i][j]);
                }
            }
        }
        //for print next lines (after latest line with dependency)
        for (; parsedLines[i] != null; i++) {
            if (i != 0)
                System.out.println();

            if (parsedLines[i][0].length() > 3) //for 4-charactar instructions (addi, ...)
                System.out.print(parsedLines[i][0] + "\t");
            else
                System.out.print(parsedLines[i][0] + "\t\t");

            for (int j = 1; j < parsedLines[0].length && parsedLines[i][j] != null; j++) {
                if (j != 1)
                    System.out.print(", ");
                System.out.print(parsedLines[i][j]);
            }
        }
        System.out.println();
    }


    public static String[] assembler(String[][] parsedLines) {
        //R-Type
        String[] R_TYPE = {"add", "sub", "sll", "xor", "srl", "sra", "or", "and"};
        String R_TYPE_OPCODE = "0110011";
        String[] R_TYPE_FUNCT7 = {"0000000", "0100000", "0000000", "0000000", "0000000", "0000000", "0000000", "0000000"};
        String[] R_TYPE_FUNCT3 = {"000", "000", "001", "100", "101", "101", "110", "111"};
        //I_Type
        String[] I_TYPE = {"addi", "xori", "ori", "andi"}; //without load instuctions
        String[] I_TYPE_OPCODE = {"0010011", "0010011", "0010011", "0010011"};
        String[] I_TYPE_FUNCT3 = {"000", "100", "110", "111"};

        String[] I_TYPE_LOAD_FUNCT3 = {"000", "001", "010", "011", "100", "101", "110"};
        String I_TYPE_LOAD_OPCODE = "0000011";
        //S_Type
        String[] S_TYPE = {"sb", "sh", "sw", "sd"};
        String S_TYPE_OPCODE = "0100011";
        String[] S_TYPE_FUNCT3 = {"000", "001", "010", "111"};

        String[] machineCodes = new String[parsedLines.length];
        int machineCodesCounter = 0;
        outerloop:
        for (String[] parsedLine : parsedLines) {
            //___________R-Type___________
            for (int i = 0; i < R_TYPE.length; i++)
                if (parsedLine != null && parsedLine[0].equals(R_TYPE[i])) {
                    machineCodes[machineCodesCounter] = R_TYPE_FUNCT7[i] + " | ";

                    if (registerNumber(parsedLine[3]) == -1) {
                        machineCodes[machineCodesCounter] = "";
                        machineCodes[machineCodesCounter] += "error in this line ...";
                        return machineCodes;
                    }
                    machineCodes[machineCodesCounter] += convertToBinary(registerNumber(parsedLine[3]), 5) + " | ";

                    if (registerNumber(parsedLine[2]) == -1) {
                        machineCodes[machineCodesCounter] = "";
                        machineCodes[machineCodesCounter] += "error in this line ...";
                        return machineCodes;
                    }
                    machineCodes[machineCodesCounter] += convertToBinary(registerNumber(parsedLine[2]), 5) + " | ";
                    machineCodes[machineCodesCounter] += R_TYPE_FUNCT3[i] + " | ";

                    if (registerNumber(parsedLine[1]) == -1) {
                        machineCodes[machineCodesCounter] = "";
                        machineCodes[machineCodesCounter] += "error in this line ...";
                        return machineCodes;
                    }
                    machineCodes[machineCodesCounter] += convertToBinary(registerNumber(parsedLine[1]), 5) + " | ";
                    machineCodes[machineCodesCounter++] += R_TYPE_OPCODE;
                    continue outerloop;
                }
            //___________I-Type___________
            for (int i = 0; i < I_TYPE.length; i++)
                if (parsedLine != null && parsedLine[0].equals(I_TYPE[i])) {
                    try {
                        machineCodes[machineCodesCounter] = convertToBinary(Integer.parseInt(parsedLine[3]), 12) + " | ";
                    } catch (Exception e) {
                        machineCodes[machineCodesCounter] = "";
                        machineCodes[machineCodesCounter] += "error in this line ...";
                        return machineCodes;
                    }
                    if (registerNumber(parsedLine[2]) == -1) {
                        machineCodes[machineCodesCounter] = "";
                        machineCodes[machineCodesCounter] += "error in this line ...";
                        return machineCodes;
                    }
                    machineCodes[machineCodesCounter] += convertToBinary(registerNumber(parsedLine[2]), 5) + " | ";
                    machineCodes[machineCodesCounter] += I_TYPE_FUNCT3[i] + " | ";
                    if (registerNumber(parsedLine[1]) == -1) {
                        machineCodes[machineCodesCounter] = "";
                        machineCodes[machineCodesCounter] += "error in this line ...";
                        return machineCodes;
                    }
                    machineCodes[machineCodesCounter] += convertToBinary(registerNumber(parsedLine[1]), 5) + " | ";
                    machineCodes[machineCodesCounter++] += I_TYPE_OPCODE[i];
                    continue outerloop;
                }
            //___________I-Type _ load___________
            for (int i = 0; i < LOAD_INSTRUCTIONS.length; i++)
                if (parsedLine != null && parsedLine[0].equals(LOAD_INSTRUCTIONS[i])) {
                    String[] temp;
                    try {
                        //for convert <parsedLine[2] = offset(rs1)> to <temp[0] = offset> and <temp[1] = rs1>
                        temp = parsedLine[2].split("\\(");
                        temp[1] = charRemoveAt(temp[1], temp[1].length() - 1);
                        machineCodes[machineCodesCounter] = convertToBinary(Integer.parseInt(temp[0]), 12) + " | ";
                    } catch (Exception e) {
                        machineCodes[machineCodesCounter] = "";
                        machineCodes[machineCodesCounter] += "error in this line ...";
                        return machineCodes;
                    }
                    System.out.println(temp[1]);
                    try {
                        machineCodes[machineCodesCounter] += convertToBinary(registerNumber(temp[1]), 5) + " | ";
                    } catch (Exception e) {
                        machineCodes[machineCodesCounter] = "";
                        machineCodes[machineCodesCounter] += "error in this line ...";
                        return machineCodes;
                    }
                    machineCodes[machineCodesCounter] += I_TYPE_LOAD_FUNCT3[i] + " | ";
                    try {
                        machineCodes[machineCodesCounter] += convertToBinary(registerNumber(parsedLine[1]), 5) + " | ";
                    } catch (Exception e) {
                        machineCodes[machineCodesCounter] = "";
                        machineCodes[machineCodesCounter] += "error in this line ...";
                        return machineCodes;
                    }
                    machineCodes[machineCodesCounter++] += I_TYPE_LOAD_OPCODE;
                    continue outerloop;

                }
            //___________S-Type___________
            for (int i = 0; i < S_TYPE.length; i++)
                if (parsedLine != null && parsedLine[0].equals(S_TYPE[i])) {
                    String immed_11_5;
                    String[] temp;
                    try {
                        //for convert <parsedLine[2] = offset(rs1)> to <temp[0] = offset> and <temp[1] = rs1>
                        temp = parsedLine[2].split("\\(");
                        temp[1] = charRemoveAt(temp[1], temp[1].length() - 1);
                        machineCodes[machineCodesCounter] = convertToBinary(Integer.parseInt(temp[0]), 12).substring(0, 7) + " | ";
                    } catch (Exception e) {
                        machineCodes[machineCodesCounter] = "";
                        machineCodes[machineCodesCounter] += "error in this line ...";
                        return machineCodes;
                    }
                    if (registerNumber(parsedLine[1]) == -1) {
                        machineCodes[machineCodesCounter] = "";
                        machineCodes[machineCodesCounter] += "error in this line ...";
                        return machineCodes;
                    }
                    machineCodes[machineCodesCounter] += convertToBinary(registerNumber(parsedLine[1]), 5) + " | ";
                    try {
                        machineCodes[machineCodesCounter] += convertToBinary(registerNumber(temp[1]), 5) + " | ";
                    } catch (Exception e) {
                        machineCodes[machineCodesCounter] = "";
                        machineCodes[machineCodesCounter] += "error in this line ...";
                        return machineCodes;
                    }
                    machineCodes[machineCodesCounter] += S_TYPE_FUNCT3[i] + " | ";
                    try {
                        machineCodes[machineCodesCounter] += convertToBinary(Integer.parseInt(temp[0]), 12).substring(7, 12) + " | ";
                    } catch (Exception e) {
                        machineCodes[machineCodesCounter] = "";
                        machineCodes[machineCodesCounter] += "error in this line ...";
                        return machineCodes;
                    }
                    machineCodes[machineCodesCounter++] += S_TYPE_OPCODE;
                    continue outerloop;
                }
        }

        return machineCodes;
    }

    //for convert int to binary and extend it to extendNum-bit (for example if extendNum == 5, 6 -> 00110)
    public static String convertToBinary(int registerNum, int extendNum) {
        String binary;
        if (registerNum >= 0) {
            binary = Integer.toBinaryString(registerNum);
            while (binary.length() < extendNum)
                binary = "0" + binary;
        } else {
            binary = Integer.toBinaryString(registerNum);
            binary = binary.substring(binary.length() - extendNum); //if binary = "11111000" return "11000"
        }
        return binary;
    }

    //for remove x in start of register name and check for validity (between 0 to 32)
    public static int registerNumber(String registerName) {
        registerName = charRemoveAt(registerName, 0);
        int registerNum;
        try {
            registerNum = Integer.parseInt(registerName);
        } catch (Exception e) {
            return -1;
        }
        if (registerNum >= 0 && registerNum <= 32)
            return registerNum;
        return -1;
    }
}