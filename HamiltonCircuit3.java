import java.io.*;
import java.util.*;

public class HamiltonCircuit3 {

    //inputFileName is used to determine the name for the output file based on the name of the input file
    static String inputFileName;
    // the following three variables p,q,seed, currentNum
    //will be used for the BBS method
    static int p=167;
    static int q=175;
    static int seed=527;
    static int currentNum=0;
    static int count__BBS=0; static boolean found=false;
    static int[][] adjMat;
    static int[] bruteSol;

    //this method will determine if the sequence of numbers in per is a solution to a
    //hamiltonian circuit
    public static boolean findSolution(int[] per){
        boolean lastVertexCheck=false;
        for(int d=0;d<per.length;d++){
            //the loop checks if there is an edge formed from vertex i and i+1
            if(d==per.length-1)
                lastVertexCheck=true;
            if(d<per.length-1){
                if (adjMat[per[d]][per[d + 1]] == 0)
                    return false;
            }
            //once it reaches the end, it checks if the last vertex in the  potential solution forms an edge with the first vertex in the solution
            if(lastVertexCheck){
                if(adjMat[per[0]][per[per.length-1]]==1)
                    return true;
            }
        }
        return false;
    }
    public int[] crossover(int[] bestChoice,int[] p2){ //2 point Crossover
        int start= 2;
        int end=p2.length-3;
        //choose two random indexes to form an inverse Crossover
        int randomNum1=getRandomNumberInRange(start,end);
        int randomNum2=getRandomNumberInRange(start,end);

        //if the two random numbers are the same, then we keep trying to get random numbers that are not the same
        while(randomNum1==randomNum2){
            randomNum1=getRandomNumberInRange(start,end);
            randomNum2=getRandomNumberInRange(start,end);
        }

        int[] nums={randomNum1,randomNum2};
        Arrays.sort(nums);

        //nums[0] has the smaller index, and nums[1] has the bigger random number

        int[] output=new int[bestChoice.length];
        for(int i=0;i<bestChoice.length;i++)
            output[i]=bestChoice[i];

        int tempCount=nums[0];

        //2 point Crossover occurs here
        for(int i=nums[0]+1;i<=nums[1];i++) {
            output[i] = p2[i];
            tempCount++;
        }

        return output;
    }
    public int[] mutation(int[] bestChoice){

        //numsUsed will be used to see what numbers have and haven't been used in bestChoice
        //numsUsed[i] = true when a number in bestChoice has been used

        boolean[] numsUsed=new boolean[bestChoice.length];
        for(int i=0;i<bestChoice.length;i++)
            numsUsed[i]=false;

        for(int i=0;i<bestChoice.length;i++){
            int num=bestChoice[i];
            numsUsed[num]=true;
        }

        //copies values in bestChoice into newArray
        int[] newArray=new int[bestChoice.length];
        for(int i=0;i<newArray.length;i++)
            newArray[i]=bestChoice[i];

        for(int i=0;i<bestChoice.length;i++){
            int decider=getRandomNumberInRange(1,100);
            //if the random value is 97,98,99, or 100, do the mutation
            if(decider>96){

                if(!isAllTrue(numsUsed)){
                    //if not all the values in a potential solution have been used, i.e [0-length-1]
                    //then get the first sequential number that has not been used
                    int newNum=nextValueInBooleanArray(numsUsed);
                    newArray[i]=newNum;
                    numsUsed[newNum]=true;
                }
                else{
                    //if all the values have been used, then randomly get a new number from range [0, bestChoice.length-1]
                    newArray[i]=getRandomNumberInRange(0,bestChoice.length-1);
                }
            }
        }
        return newArray;
    }

    //method is used to see if all numbers in a potential solution have been used
    public static boolean isAllTrue(boolean[] arr){
        for(int i=0;i<arr.length;i++) {
            if (arr[i] == false)
                return false;
        }
        return true;
    }
    //gets the next first sequential value that has not been used in the potential solution
    public static int nextValueInBooleanArray(boolean[] arr){
        for(int i=0;i<arr.length;i++) {
            if (arr[i] == false)
                return i;
        }
        return -1;
    }

    public int BBSNum(){
        int output=-1;
        int multi=p*q;
        //once BBSNum is called count__BBS%10000==0, pa nd q values change since
        //the values  keep on repeating after a certain amount of calls; to avoid numbers repeating from BBS
        if(currentNum!=0 && count__BBS%10000==0 ){
            p+=4;
            q+=4;
        }

        //if this is the first time BBS is called, then
        //output= (seed*seed)%(p*q)
        //else, the currentNum is updated to remember the last output from BBS
        if(currentNum==0) {
            output = (seed*seed)%(multi);
        }
        else
            output=(currentNum*currentNum)%(multi);
        currentNum=output;
        count__BBS++;
        return output;
    }

    public double convertToDec(int num){

        //converts int num into a number between [0,1)
        double output=-1;
        double temp=Math.abs(num);
        while (temp>1){
            temp=temp/10;
        }
        return temp;
    }

    public int convertToInt(double num,int range){
        //depending on the range of numbers (0, range-1)
        //num is converted into a number from 0 to range-1

        for(int i=1;i<=range;i++){
            //convert int i to a double so I can get a decimal value to determine which number from( 0,range-1)
            //based on i/range
            double t=i;
            double temp=t/range;
            if(num<(temp))
                return i-1;
        }
        return range-1;
    }
    public int getScore(int[] arr,int[] sol) {
        HashSet<Integer> temp = new HashSet<Integer>();

        int tempOut = 0;
        temp.add(-1);
        for (int i = 0; i < arr.length; i++) {
            if (temp.contains(arr[i])) {
                tempOut += (arr.length * 2);
            }
            else {
                temp.add(arr[i]);
                tempOut += Math.abs(arr[i] - sol[i]);
            }

        }
        return tempOut;
    }
    public static void main(String args[]) throws IOException {
        int minFromSize=0; //wil be used to get stopping criteria
        if (args[0].isEmpty()) {
            System.out.println("Run compile Program with a vaild txt file as an argument");
            System.exit(0);
        }
        //lines 77-82 try to read the input text file
        try {
            String[] tryTop = readFile(args[0]);
        } catch (FileNotFoundException e) {
            System.out.println("File Does not exist in Directory\nPlease try again with a valid file name");
            System.exit(0);
        }

        //rFile contains all adjacent matrix
        String[] rFile = readFile(args[0]);
        if (args[0].equals("inputSmall.txt"))
            inputFileName = "outputSmall.txt";
        else if (args[0].equals("inputMedium.txt"))
            inputFileName = "outputMedium.txt";
        else
            inputFileName = "outputLarge.txt";

        HamiltonCircuit3 ex = new HamiltonCircuit3();

        //lines 96-99 determine if the input file contains only numbers
        if (ex.isInputContainsNums(rFile) == false) {
            System.out.println("Input does not contains All Numbers to be a valid adjacency matrix");
            System.exit(0);
        }


        //converts the input file into a int matrix
        int[][] num2DArr = ex.convertTo2DArray(rFile);
        adjMat=num2DArr;

        //lines 106-108 determine if the file is a valid adjacency matrix
        if (ex.isAdjMatrix(num2DArr) == false) {
            System.out.println("Input does not contains All 0s and 1s\nEdit input file so it is a valid adjacency matrix ");
            System.exit(0);
        }

        //creates an initial permutations based on the number of vertexes in the graph
        int[] initPermut=ex.initPermutationArr(num2DArr.length);
        bruteSol=initPermut;

        bruteForce(initPermut,initPermut.length);


        //sol contains the possible index in per for the hamiltonian circuit
        int sol=0;
        // arraySol contains the brute force solution
        int[] arraySol = bruteSol;

        //lines 58-69 are used to determine how to write the solution
        String out="";
        System.out.println("Created File "+ inputFileName+" with the solution");
        if(sol==-1)
            out="Solution does not exist";
        else {
            out="Brute Force Solution:\nThe Graph has a total of "+arraySol.length+ " vertexes\nThe first vertex is numbered 0\nand the last is numbered "+(arraySol.length-1)+"\nThe Hamiltonian circuit is\n";
            for(int i=0;i<arraySol.length;i++){
                if(i<arraySol.length-1)
                    out+=arraySol[i]+"->"+ arraySol[i+1]+", ";
                if(i==arraySol.length-1)
                    out+=arraySol[i]+"->"+ arraySol[0];
            }

        }
        //to get the minimal score for a stopping criteria
        if(arraySol.length==10)
            minFromSize=15;
        else if(arraySol.length<20)
            minFromSize=30;
        else
            minFromSize=120;


        //Start RANDOM
        //finalRandomAnswersMax and finalRandomAnswersMin will contain the best 100 random solutions from Math.Random()
        int[][] finalRandomAnswersMax = new int[100][initPermut.length];
        int[][] finalRandomAnswersMin = new int[100][initPermut.length];

        //avgScore will contain the total fitness score for each generation
        double[] avgScore=new double[100];

        //maxScores and minScores will contain the best and worst random solutions from each generation
        int[] maxScores=new int[100];
        int[] minScores=new int[100];

        //to simulate 100 generations of random solutions
        for(int rotate=0;rotate<100;rotate++) {


            double totalScore=0; //to keep track of all the fitness scores
            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;

            int count = 0; //to get 1000 solutions per generation

            //to keep track of all best and worst random solution as they are generated per generation
            int[][] listOfRandomAnswersMax = new int[999][initPermut.length];
            int[][] listOfRandomAnswersMin = new int[999][initPermut.length];

            //will be used to see if repeating numbers are generated per random solution
            HashSet<Integer> integerSet = new HashSet<Integer>();

            // addedToMax and addedToMin will be used to keep track of row where the best and worst solution
            // in listOfRandomAnswersMax and listOfRandomAnswersMin
            int addedToMin = -1;
            int addedToMax = -1;

            //to generate 1000 random solutions
            while (count < 1000) {
                int score = 0;
                integerSet.add(-1);
                int[] tempArr = new int[initPermut.length];
                for (int i = 0; i < initPermut.length; i++) {
                    int rand = getRandomNumberInRange(0, initPermut.length-1);

                    //if a number is repeated in the random solution, there will be a penalty
                    //of adding the length of the array solution times 2 to the score
                    if (integerSet.contains(rand)) {
                        score += (initPermut.length * 2);
                        totalScore+= (initPermut.length * 2);
                    }

                    //if a number is not repeated, then we just add the difference
                    //of the random number from arraySol[i]
                    else {
                        integerSet.add(rand);
                        score += Math.abs(rand - arraySol[i]);
                        totalScore+=Math.abs(rand - arraySol[i]);
                    }

                    //then we add the random int to the solution set
                    tempArr[i] = rand;

                    //once we added all numbers into the random solution, we determine if we add it to
                    //listOfRandomAnswersMax or listOfRandomAnswersMin based on its fitness Score and compare it to the
                    //current max and min fitness score produced from the other solutions
                    if (i == initPermut.length - 1) {
                        if (score < min) {
                            min = score;
                            addedToMin++;
                            for (int c = 0; c < initPermut.length; c++) {
                                listOfRandomAnswersMin[addedToMin][c] = tempArr[c];
                            }

                        }
                        if (score > max) {
                            max = score;
                            addedToMax++;
                            for (int c = 0; c < initPermut.length; c++) {
                                listOfRandomAnswersMax[addedToMax][c] = tempArr[c];
                            }

                        }
                    }
                }
                //once the random solution is created, we clear the hashSet for the new random solution iteration
                integerSet.clear();
                count++;

                //once the 1000 random solutions are created, we add the best and worst
                // solution is added to finalRandomAnswersMax and finalRandomAnswersMin
                //and add the total fitness Score of the current generation into avgScore
                if(count==999){
                    for(int c=0;c<initPermut.length;c++){
                        finalRandomAnswersMax[rotate][c]=listOfRandomAnswersMax[addedToMax][c];
                        finalRandomAnswersMin[rotate][c]=listOfRandomAnswersMin[addedToMin][c];
                        maxScores[rotate]=max;
                        minScores[rotate]=min;
                        avgScore[rotate]=totalScore;
                    }
                }
            }
        }

        int min = Integer.MAX_VALUE;
        int minLoc=-1;

        //for loop looks for the best fitness Score in minScores
        for(int i=0;i<minScores.length;i++) {
            if (minScores[i] <min){
                min=minScores[i];
                minLoc=i;
            }
        }

        out+="\n\n                     Results using Math.random()";

        //Prints out best random solution with the best fitness score using Math.random()
        out+="\n\nBest solution using Math.Random() with score: "+ min+"\n";
        for(int i=0;i<finalRandomAnswersMin[minLoc].length;i++) {
            if(i!=finalRandomAnswersMin[minLoc].length-1)
                out += (finalRandomAnswersMin[minLoc][i]+1) + "->"+(finalRandomAnswersMin[minLoc][i+1]+1)+", " ;
            else
                out += (finalRandomAnswersMin[minLoc][i]+1) + "->"+(finalRandomAnswersMin[minLoc][0]+1);
        }
        //adds all the min scores from Math.random() solutions to out
        out+="\nMin scores from 100 Generations\n";
        for(int i=0;i<minScores.length;i++)
            out+=minScores[i]+", ";

        ////adds all the max scores from Math.random() solutions to out
        out+="\nMax scores from 100 Generations\n";
        for(int i=0;i<maxScores.length;i++)
            out+=maxScores[i]+", ";


        //adss avg fitness Scores from 100 generations to out
        out+="\n\nAVG scores from 100 generations\n";
        for(int i=0;i<avgScore.length;i++) {
            double temp=avgScore[i] /1000;
            avgScore[i]=temp;
            out+=avgScore[i] + ", ";
        }

        //finalRandomAnswersMaxBBS and finalRandomAnswersMinBBS will contain all the best 10 random solutions from the 10 generations
        int[][] finalRandomAnswersMaxBBS = new int[100][initPermut.length];
        int[][] finalRandomAnswersMinBBS = new int[100][initPermut.length];

        //avgScoreBBS will contain the average fitness scores per generation
        double[] avgScoreBBS=new double[100];

        //maxScoresBBS and minScoresBBS will hold the best and worst fitness scores per generation
        int[] maxScoresBBS=new int[100];
        int[] minScoresBBS=new int[100];

        //for loop is to simulate 100 generations
        for(int rotate=0;rotate<100;rotate++) {

            //totalScore will keep track of fitness score per solution and minBBS and maxBBs
            //will be used to get the best and worst fitness score from each generation
            double totalScoreBBS=0;
            int minBBS = Integer.MAX_VALUE;
            int maxBBS = Integer.MIN_VALUE;

            //countBBs is used to generate 1000 solutions per generation
            int countBBS = 0;

            //listOfRandomAnswersMax and listOfRandomAnswersMin will have the best and worst random solution from the 1000 random solutions per generation
            int[][] listOfRandomAnswersMax = new int[999][initPermut.length];
            int[][] listOfRandomAnswersMin = new int[999][initPermut.length];

            //integerSetBBS will keep track if any repeating values are in a solution
            HashSet<Integer> integerSetBBS = new HashSet<Integer>();

            //addedToMin and addedToMin will keep track of how many worst and best solution are found as the are generated per generation
            int addedToMin = -1;
            int addedToMax = -1;
            //while loop will generate 1000 random solutions using BBS
            while (countBBS < 1000) {
                int score = 0;
                integerSetBBS.add(-1);
                int[] tempArr = new int[initPermut.length];
                for (int i = 0; i < initPermut.length; i++) {

                    //the following three lines get the next number generated by BBS
                    //and convert it to a value [0,1)
                    //and converts it to a value from 0 to the length-1 of the solution array
                    //depending on the decimal value
                    int temp=ex.BBSNum();
                    double dec=ex.convertToDec(temp);
                    int convert=ex.convertToInt(dec,initPermut.length);

                    //if this int already exists in the solution, we add the score by multiplying with the repeating value by 2
                    if (integerSetBBS.contains(convert)) {
                        score += (initPermut.length * 2);
                        totalScoreBBS+= (initPermut.length * 2);
                    }

                    //if a number is not repeated, then we just add the difference
                    //of the random number from arraySol[i] and add it to the HashSet
                    else {
                        integerSetBBS.add(convert);
                        score += Math.abs(convert - arraySol[i]);
                        totalScoreBBS+=Math.abs(convert - arraySol[i]);
                    }

                    //then we add the random int to the solution set
                    tempArr[i] = convert;

                    //once we have a full random solution lines 385-402
                    //will see if we have created a new solution better or worse than the ones
                    //that have already been crated
                    if (i == initPermut.length - 1) {
                        if (score < minBBS) {
                            minBBS = score;
                            addedToMin++;
                            for (int c = 0; c < initPermut.length; c++) {
                                listOfRandomAnswersMin[addedToMin][c] = tempArr[c];
                            }
                        }
                        if (score > maxBBS) {
                            maxBBS = score;
                            addedToMax++;
                            for (int c = 0; c < initPermut.length; c++) {
                                listOfRandomAnswersMax[addedToMax][c] = tempArr[c];
                            }

                        }
                    }
                }
                integerSetBBS.clear();
                countBBS++;
                //once we have completed all 1000 random solution, I add the best and worst solution
                //to finalRandomAnswersMaxBBS and finalRandomAnswersMinBBS
                //and their respected scores in maxScoresBBS and minScoresBBS and the total score into avgScoreBBS
                if(countBBS==999){
                    for(int c=0;c<initPermut.length;c++){
                        finalRandomAnswersMaxBBS[rotate][c]=listOfRandomAnswersMax[addedToMax][c];
                        finalRandomAnswersMinBBS[rotate][c]=listOfRandomAnswersMin[addedToMin][c];
                        maxScoresBBS[rotate]=maxBBS;
                        minScoresBBS[rotate]=minBBS;
                        avgScoreBBS[rotate]=totalScoreBBS;
                    }
                }
            }
        }


        int minBBS = Integer.MAX_VALUE;
        int minLocBBs=-1;
        //the for loop looks for the minimal value fitness score from minScoresBBS[]
        for(int i=0;i<minScoresBBS.length;i++) {
            if (minScoresBBS[i] <minBBS){
                minBBS=minScoresBBS[i];
                minLocBBs=i;
            }
        }

        out+="\n\n\n                     Results using BBS";
        out+="\n\nBest random solution using BBS with score: "+minBBS+"\n";
        for(int i=0;i<finalRandomAnswersMinBBS[minLocBBs].length;i++) {
            if(i!=arraySol.length-1)
                out+=(finalRandomAnswersMinBBS[minLocBBs][i]+1) + "->"+(finalRandomAnswersMinBBS[minLocBBs][i+1]+1)+", " ;
            else
                out+=(finalRandomAnswersMinBBS[minLocBBs][i]+1) + "->"+(finalRandomAnswersMinBBS[minLocBBs][0] +1);
        }

        out+="\nMin scores from 100 Generations\n";
        for(int i=0;i<minScoresBBS.length;i++)
            out+=minScoresBBS[i]+", ";

        out+="\nMax scores from 100 Generations\n";
        for(int i=0;i<maxScoresBBS.length;i++)
            out+=maxScoresBBS[i]+", ";

        out+="\nAvg scores from 100 generations\n";
        for(int i=0;i<avgScoreBBS.length;i++) {
            double temp=avgScoreBBS[i] /1000;
            avgScoreBBS[i]=temp;
            out+=avgScoreBBS[i] + ", ";
        }



        //START GA
        int genStop=0;// to keep of the generation
        int[] lowestScoresGA=new int[100]; //keep track of the lowest Score of each generation


        //finalRandomAnswersMaxGA and finalRandomAnswersMinGA will contain the best 100 GA solutions per genertaion
        int[][] finalRandomAnswersMaxGA = new int[100][initPermut.length];
        int[][] finalRandomAnswersMinGA = new int[100][initPermut.length];
        boolean gaSwitch=false; // a variable to determine if we use Memetic and take the GA solution from previous Generation to next Generation

        //avgScore will contain the total fitness score for each generation
        double[] avgScoreGA=new double[100];

        //maxScores and minScores will contain the best and worst fitness Scores solutions from each generation
        int[] maxScoresGA=new int[100];
        int[] minScoresGA=new int[100];

        int[] memeticA=new int[arraySol.length]; //will be used to carry over best solution into next generation
        int[] currentBest=new int[arraySol.length]; //will contain the best solution per generation
        int[] stopCriteriaAns=new int[arraySol.length]; ////will contain the best solution per generation
        boolean meetCriteria=false; // boolean variable will see if any generation met the stopping criteria, and will display the result

        //to simulate 100 generations of GA
        for(int rotate=0;rotate<100;rotate++) {

            int[] smallestScore=new int[initPermut.length]; //will contain the best solution for the current generation
            int[] randomSmallestScore=new int[initPermut.length]; ////will contain random good solution for the current generation


            double totalScore=0; //to keep track of all the fitness scores
            int minGA = Integer.MAX_VALUE;
            int maxGA = Integer.MIN_VALUE;

            int countGA = 0; //to get 1000 solutions per generation

            //to keep track of all best and worst random solution as they are generated per generation
            int[][] listOfRandomAnswersMax = new int[999][initPermut.length];
            int[][] listOfRandomAnswersMin = new int[999][initPermut.length];

            //will be used to see if repeating numbers are generated per random solution
            HashSet<Integer> integerSet = new HashSet<Integer>();
            // addedToMax and addedToMin will be used to keep track of row where the best and worst solution
            // in listOfRandomAnswersMax and listOfRandomAnswersMin
            int addedToMinGA = -1;
            int addedToMaxGA = -1;

            //this will be used to carry over the best GA solution from the previous generation into the next generation
            if(gaSwitch){
                addedToMinGA++;

                for(int i=0;i<arraySol.length;i++)
                    listOfRandomAnswersMin[addedToMinGA][i]=memeticA[i];

                int tempScore=0;
                HashSet<Integer> tempSet = new HashSet<Integer>();

                tempSet.add(-1);
                //the following determines the fitness score of the previous best solution being carried over into the next generation
                for(int i=0;i<arraySol.length;i++){
                    if (tempSet.contains(memeticA[i])) {
                        tempScore += (initPermut.length * 2);
                    }

                    //if a number is not repeated, then we just add the difference
                    //of the random number from arraySol[i]
                    else {
                        tempSet.add(memeticA[i]);
                        tempScore += Math.abs(memeticA[i] - arraySol[i]);
                    }
                }
                minGA=tempScore;
                //added to next generation
            }

            //to generate 1000 random solutions
            while (countGA < 1000) {
                int score = 0;
                integerSet.add(-1);
                int[] tempArr = new int[initPermut.length];
                for (int i = 0; i < initPermut.length; i++) {
                    int rand = getRandomNumberInRange(0, initPermut.length-1);

                    //if a number is repeated in the random solution, there will be a penalty
                    //of adding the length of the array solution times 2 to the score
                    if (integerSet.contains(rand)) {
                        score += (initPermut.length * 2);
                        totalScore+= (initPermut.length * 2);
                    }

                    //if a number is not repeated, then we just add the difference
                    //of the random number from arraySol[i]
                    else {
                        integerSet.add(rand);
                        score += Math.abs(rand - arraySol[i]);
                        totalScore+=Math.abs(rand - arraySol[i]);
                    }

                    //then we add the random int to the solution set
                    tempArr[i] = rand;

                    //once we added all numbers into the random solution, we determine if we add it to
                    //listOfRandomAnswersMax or listOfRandomAnswersMin based on its fitness Score and compare it to the
                    //current max and min fitness score produced from the other solutions
                    if (i == initPermut.length - 1) {
                        if (score < minGA) {
                            minGA = score;
                            addedToMinGA++;
                            for (int c = 0; c < initPermut.length; c++) {
                                listOfRandomAnswersMin[addedToMinGA][c] = tempArr[c];
                            }

                        }
                        if (score > maxGA) {
                            maxGA = score;
                            addedToMaxGA++;
                            for (int c = 0; c < initPermut.length; c++) {
                                listOfRandomAnswersMax[addedToMaxGA][c] = tempArr[c];
                            }

                        }
                    }
                }
                //once the random solution is created, we clear the hashSet for the new random solution iteration
                integerSet.clear();
                countGA++;

                //once the 1000 random solutions are created, we add the best and worst
                // solution is added to finalRandomAnswersMax and finalRandomAnswersMin
                //and add the total fitness Score of the current generation into avgScore
                if(countGA==999){

                    //smallestScore gets the best solution in the current generation
                    for(int i=0;i<initPermut.length;i++)
                        smallestScore[i]=listOfRandomAnswersMin[addedToMinGA][i];

                    int randommInt;

                    if(addedToMinGA==1)
                        randommInt=1;
                    else if(addedToMinGA==0)
                        randommInt=0;
                    else
                        randommInt=getRandomNumberInRange(0,addedToMinGA-1); //contains

                    //randomSmallestScore gets the one of the best solution in the current generation
                    for(int i=0;i<initPermut.length;i++)
                        randomSmallestScore[i]=listOfRandomAnswersMin[randommInt][i];

                    //crossover array contains the new mutated solution from parents smallestScore and randomSmallestScore
                    int[] crossover=(ex.crossover(smallestScore,randomSmallestScore));


                    //mutated[] mutated the crossover solution
                    int[] mutated=(ex.mutation(crossover));
                    genStop=rotate; //genStop gets the current generation number


                    //memeticA and currentBest contain the new GA solution for the current generation
                    memeticA=mutated;
                    currentBest=mutated;

                    //lowestScoresGA gets the fitness score of the GA solution
                    lowestScoresGA[rotate]=ex.getScore(currentBest,arraySol);

                    for(int c=0;c<initPermut.length;c++){
                        finalRandomAnswersMaxGA[rotate][c]=listOfRandomAnswersMax[addedToMaxGA][c];
                        finalRandomAnswersMinGA[rotate][c]=listOfRandomAnswersMin[addedToMinGA][c];
                    }
                    //avgScoreGA contains the average fitness score per generation
                    maxScoresGA[rotate]=maxGA;
                    minScoresGA[rotate]=minGA;
                    avgScoreGA[rotate]=(totalScore/1000);
                }
            }
            gaSwitch=true; //used to see if the first generation of solutions have been done or not

            //this if statement determines if a potential solution has met a stopping criteria
            if(ex.getScore(currentBest,arraySol)<=minFromSize){
                stopCriteriaAns=currentBest;
                meetCriteria=true;
                break;
            }

            //out of main for loop
        }
        out+="\n\n                                GA Results";

        //if stopping criteria has been meet, then print the solution that caused the stopping criteria
        int genStopPlus1=1+genStop;
        if(meetCriteria){
            out+="\nTermination criteria met at Generation:"+genStop+ " with Score "+ex.getScore(stopCriteriaAns,arraySol);
            out+=("\nBest solution\n");
            for(int i=0;i<stopCriteriaAns.length;i++){
                if (i < stopCriteriaAns.length - 1){
                    out+=stopCriteriaAns[i]+"->"+ stopCriteriaAns[i+1]+", ";
                }
                if(i==stopCriteriaAns.length-1){
                    out+=stopCriteriaAns[i]+"->"+ stopCriteriaAns[0];
                }
            }
            out+="\nAverage Score at Gen 0: "+avgScoreGA[0];
            out+="\nAverage Score at Gen "+(genStop/2)+": "+avgScoreGA[(genStop/2)];
            out+="\nAverage Score at Gen "+(genStop)+": "+avgScoreGA[(genStop)];
            out+="\nBest Score at Gen 0: "+lowestScoresGA[0];
            out+="\nBest Score at Gen "+(genStop/2)+": "+lowestScoresGA[(genStop/2)];
            out+="\nBest Score at Gen "+(genStop)+": "+lowestScoresGA[(genStop)];
        }
        //if stopping criteria has not been meet, then print the last solution from generation 100
        else{
            out+="\nTermination criteria did not meet";
            out+=("\nBest solution with Score: "+ex.getScore(currentBest,arraySol)+"\n");
            for(int i=0;i<currentBest.length;i++){
                if (i < currentBest.length - 1){
                    out+=currentBest[i]+"->"+ currentBest[i+1]+", ";
                }
                if(i==currentBest.length-1){
                    out+=currentBest[i]+"->"+ currentBest[0];
                }
            }
            out+="\nAverage Score at Gen 0: "+avgScoreGA[0];
            out+="\nAverage Score at Gen "+(genStop/2)+": "+avgScoreGA[(genStop/2)];
            out+="\nAverage Score at Gen "+(genStop)+": "+avgScoreGA[(genStop)];

            out+="\nBest Score at Gen 0: "+lowestScoresGA[0];
            out+="\nBest Score at Gen "+(genStop/2)+": "+lowestScoresGA[(genStop/2)];
            out+="\nBest Score at Gen "+(genStop)+": "+lowestScoresGA[(genStop)];

        }
        ex.printSolutionToFile(out);

    }
    public static String[] readFile(String filename) throws IOException {
        //will be used to read text from the input file
        BufferedReader br = new BufferedReader(new FileReader(filename));

        //output will be used to store each line from input file
        List<String> output = new ArrayList<String>();

        String line;

        //while loop keeps reading input file until it reaches end of file
        //and removes any spaces from file
        while ((line = br.readLine()) != null)
            output.add(line.replaceAll("\\s", ""));

        //converts List into a String array
        return output.toArray(new String[output.size()]);
    }

    //method isInputContainsNums looks if the input txt file
    //contains only numbers for a valid adjacent matrix
    public boolean isInputContainsNums(String[] s) {
        boolean output = true;
        for (int i = 0; i < s.length; i++) {
            if (Character.isDigit(s[i].charAt(i)) == false)
                return false;
        }
        return output;
    }

    //isAdjMatrix looks if the int matrix is a valid adjacent matrix
    public boolean isAdjMatrix(int[][] m) {
        boolean output = true;
        //for loop look at values in matrix and if any digit is not a 0 or 1
        //returns fals for an invalid matrix
        for (int r = 0; r < m.length; r++) {
            for (int c = 0; c < m.length; c++) {
                if (m[r][c] > 1 || m[r][c] < 0)
                    return false;
            }
        }
        return output;
    }


    //convertTo2DArray converts the inputFile into a 2d int Array
    public int[][] convertTo2DArray(String[] s) {
        int[][] output = new int[s.length][s.length];
        //for loop gets each character from each array and changes it into an int
        //if it is a Digit
        for (int r = 0; r < s.length; r++) {
            for (int c = 0; c < s.length; c++) {
                if (Character.isDigit(s[r].charAt(c))) {
                    output[r][c] = Integer.parseInt(s[r].substring(c, c + 1));
                }
            }
        }
        return output;
    }

    //initPermutationArr creates an int array of range 0-(nums-1) to be used to create a list of permutations
    public int[] initPermutationArr(int num) {
        int[] output = new int[num];
        for (int i = 0; i < output.length; i++) {
            output[i] = i;
        }
        return output;
    }

    //method attained from https://stackoverflow.com/questions/24889201/heaps-algorithm

    public static void bruteForce(int[] list, int n) {
        // if size becomes 1 then prints the obtained
        // permutation; once found is true, it stops looking at permutation solutions
        if(n == 1) {
            if (findSolution(list)) {//once a solution has been found, we copy it to bruteSol
                found=true;
                for (int i = 0; i < list.length; i++) {
                    bruteSol[i]=list[i];
                }
                return;
            }
            return;
        }
        else{
            for(int i=0; i<n; i++){
                if(found)
                    return;
                bruteForce(list,n-1);
                if(found)
                    return;

                // if size is odd, swap 0th i.e (first) and
                // (size-1)th i.e (last) element
                // If size is even, swap ith and
                // (size-1)th i.e (last) element
                int j = ( n % 2 == 0 ) ? i : 0;
                int t = list[n-1];
                list[n-1] = list[j];
                list[j] = t;
            }
        }
    }


    // method writes solution into a file called output.txt
    public void printSolutionToFile(String s) throws IOException {
        // BufferedWriter and FileWriter are used to create a file and write into it
        BufferedWriter output = new BufferedWriter(new FileWriter(inputFileName));
        output.write(s);
        output.close();
    }

    //obtained from: https://mkyong.com/java/java-generate-random-integers-in-a-range/
    public static int getRandomNumberInRange(int min, int max) {
        //if the min range is less than max range, then end the method and output an error
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        //the next two lines uses Random object to get a random integer from range[min,max]
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
}