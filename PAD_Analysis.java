package pad_analysis;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class PAD_Analysis
{
    
    //static CSVReader reader = null;
    
    public static void main(String[] args)
    {
        //String fileName = "TestRun.csv";
        String fileName = "VideoData.csv";
        File folder;
        File[] tests;
        CSVReader reader = null;
        CSVWriter writer = null;
        List<String[]> allRows;
        String line;
        String[] record;
        int currentVid = 0;
        Double[] currentVidAVG = new Double[] {0d,0d,0d};
        Double[] currentVidSTDEV = new Double[] {0d,0d,0d};
        int vidCount = 0;
        Double[][] testAVG; // = new Double[8][3];
        Double[][] testSTDEV; // = new Double[8][3];
        Double vidStartTime = 0d;
        Double vidLength = 60000d;
        Double sqrTemp = 0d;
        int dataSum = 0;
        Double[] times; // = new Double[8];
        Double[][] expAVG; // = new Double[8][3];
        Double[][] expSTDEV; // = new Double[8][3];
        Double[] simAVG; // = new Double[8];
        Double[] simSTDEV; // = new Double[8];
        Double[][] ratioSTDEV; // = new Double[8][3];
        Double tempDOT = 0d;
        Double tempMag1 = 0d;
        Double tempMag2 = 0d;
        Double cosSim = 0d;
        Double[] tempVidAvgs = new Double[11];
        Double[] videoAVG_AVG = new Double[3];
        Double[] videoSTDEV_AVG = new Double[3];
        Double videoAVGSim_AVG = 0d;
        Double videoSTDEVSim_AVG = 0d;
        Double[] videoSTDEVRatio_AVG = new Double[3];
        Double videoAVGSim = 0d;
        Double videoSTDEVSim = 0d;
        
        ////
        List allTestResultsByVideo = new ArrayList();
        List allVideoResults = new ArrayList();
        
        
        try
        {
            //Reads in all video data (expected values and video times)
            //Creates FileReader for the file named "VideoData.csv"
            //Creates CSVReader, named reader, with previous FileReader
            reader = new CSVReader(new FileReader(fileName));
            
            //Reads everything from "VideoData.csv" and puts it in allRows
            allRows = reader.readAll();
            
            //Sets the size of the expected data arrays and result data arrays
            //using the number of videos (rows) in video data file
            vidCount = allRows.size();
            times = new Double[vidCount];
            expAVG = new Double[vidCount][3];
            expSTDEV = new Double[vidCount][3];
            simAVG = new Double[vidCount];
            simSTDEV = new Double[vidCount];
            ratioSTDEV = new Double[vidCount][3];
            testAVG = new Double[vidCount][3];
            testSTDEV = new Double[vidCount][3];
            
            for(int i = 0; i < vidCount; i++)
            {
                TreeMap<String, ArrayList> tmap = new TreeMap<String, ArrayList>();
                allTestResultsByVideo.add(tmap);
            }
            
            //Now we can save/typecast all of the data into three arrays
            //times for each videos time
            //expAVG for each videos expected averages
            //expSTDEV for each videos expected standard deviations
            for(String[] row : allRows)
            {
                //Saves the current videos run time
                times[currentVid] = Double.parseDouble(row[1]);
                
                //Saves the current videos expected averages (P, D, and A)
                expAVG[currentVid][0] = transformRange(Double.parseDouble(row[2]));
                expAVG[currentVid][1] = transformRange(Double.parseDouble(row[3]));
                expAVG[currentVid][2] = transformRange(Double.parseDouble(row[4]));
                
                //Saves the current videos expected Std Devs (P, D, and A)
                expSTDEV[currentVid][0] = Double.parseDouble(row[5]);
                expSTDEV[currentVid][1] = Double.parseDouble(row[6]);
                expSTDEV[currentVid][2] = Double.parseDouble(row[7]);
                
                //Increments currentVid to next row number
                currentVid++;
            }
            
            //When finished reading video data, set currentVid back to 0
            currentVid = 0;
            
            //test printing exp avgs and stdevs 1 (1 video)
            //System.out.println(Arrays.toString(times));
            //System.out.println();
            //System.out.println(Arrays.toString(expAVG[0]));
            //System.out.println();
            //System.out.println(Arrays.toString(expSTDEV[1]));
            //System.out.println();
            
            ////START LOOP FOR ALL TEST FILES////
            
            //
            folder = new File(".\\Tests");
            tests = folder.listFiles();
            
            //Loop throguh every test file in tests folder
            for(int j = 0; j < tests.length; j++)
            {
                System.out.println("File: " + tests[j].getName());

                ////FIND AVG////
                //Now we change the fileName to the first file to be read
                //fileName = "TestRun2.csv";
                //Creates FileReader for the file named "TestRun.csv"
                //Creates CSVReader, named reader, with previous FileReader
                reader = new CSVReader(new FileReader(tests[j]));

                //Reads everything from "TestRun.csv" and puts it in allRows
                allRows = reader.readAll();

                //Set the first video's start time (Video0)
                //Set the first videos length (Video0)
                vidStartTime = Double.parseDouble(allRows.get(currentVid)[1]);
                vidLength = times[currentVid];

                for (String[] row : allRows) {
                    if (Integer.parseInt(row[0].replaceAll("[\\D]", "")) == currentVid) {
                        if ((Double.parseDouble(row[1]) - vidStartTime) <= vidLength) {
                            dataSum++;
                            currentVidAVG[0] = currentVidAVG[0] + Double.parseDouble(row[2]);
                            currentVidAVG[1] = currentVidAVG[1] + Double.parseDouble(row[3]);
                            currentVidAVG[2] = currentVidAVG[2] + Double.parseDouble(row[4]);
                        }
                    } else {
                        currentVidAVG[0] = currentVidAVG[0] / dataSum;
                        currentVidAVG[1] = currentVidAVG[1] / dataSum;
                        currentVidAVG[2] = currentVidAVG[2] / dataSum;

                        testAVG[currentVid] = currentVidAVG;

                        dataSum = 0;
                        currentVidAVG = new Double[]{0d, 0d, 0d};
                        currentVid++;

                        dataSum++;
                        currentVidAVG[0] = currentVidAVG[0] + Double.parseDouble(row[2]);
                        currentVidAVG[1] = currentVidAVG[1] + Double.parseDouble(row[3]);
                        currentVidAVG[2] = currentVidAVG[2] + Double.parseDouble(row[4]);

                        //Set the current videos starting time
                        //Set the current videos length
                        vidStartTime = Double.parseDouble(row[1]);
                        vidLength = times[currentVid];
                    }
                }

                currentVidAVG[0] = currentVidAVG[0] / dataSum;
                currentVidAVG[1] = currentVidAVG[1] / dataSum;
                currentVidAVG[2] = currentVidAVG[2] / dataSum;

                testAVG[currentVid] = currentVidAVG;

                //Reset Defaults
                dataSum = 0;
                currentVidAVG = new Double[]{0d, 0d, 0d};
                currentVid = 0;
                vidStartTime = 0d;
                vidLength = 60000d;

                ////End of average computation////
                //test printing averages 0-4 (5 videos)
                //System.out.println(Arrays.toString(testAVG[0]));
                //System.out.println(Arrays.toString(testAVG[1]));
                //System.out.println(Arrays.toString(testAVG[2]));
                //System.out.println(Arrays.toString(testAVG[3]));
                //System.out.println(Arrays.toString(testAVG[4]));
                //System.out.println();

                ////FIND STDEV////
                //Set the first video's start time (Video0)
                //Set the first videos length (Video0)
                vidStartTime = Double.parseDouble(allRows.get(currentVid)[1]);
                vidLength = times[currentVid];

                for (String[] row : allRows) {
                    if (Integer.parseInt(row[0].replaceAll("[\\D]", "")) == currentVid) {
                        if ((Double.parseDouble(row[1]) - vidStartTime) <= vidLength) {
                            dataSum++;
                            sqrTemp = (Double.parseDouble(row[2]) - testAVG[currentVid][0]);
                            currentVidSTDEV[0] = currentVidSTDEV[0] + (sqrTemp * sqrTemp);
                            sqrTemp = (Double.parseDouble(row[3]) - testAVG[currentVid][1]);
                            currentVidSTDEV[1] = currentVidSTDEV[1] + (sqrTemp * sqrTemp);
                            sqrTemp = (Double.parseDouble(row[4]) - testAVG[currentVid][2]);
                            currentVidSTDEV[2] = currentVidSTDEV[2] + (sqrTemp * sqrTemp);
                        }
                    } else {
                        currentVidSTDEV[0] = Math.sqrt(currentVidSTDEV[0] / dataSum);
                        currentVidSTDEV[1] = Math.sqrt(currentVidSTDEV[1] / dataSum);
                        currentVidSTDEV[2] = Math.sqrt(currentVidSTDEV[2] / dataSum);

                        testSTDEV[currentVid] = currentVidSTDEV;

                        dataSum = 0;
                        currentVidSTDEV = new Double[]{0d, 0d, 0d};
                        currentVid++;

                        dataSum++;
                        sqrTemp = (Double.parseDouble(row[2]) - testAVG[currentVid][0]);
                        currentVidSTDEV[0] = currentVidSTDEV[0] + (sqrTemp * sqrTemp);
                        sqrTemp = (Double.parseDouble(row[3]) - testAVG[currentVid][1]);
                        currentVidSTDEV[1] = currentVidSTDEV[1] + (sqrTemp * sqrTemp);
                        sqrTemp = (Double.parseDouble(row[4]) - testAVG[currentVid][2]);
                        currentVidSTDEV[2] = currentVidSTDEV[2] + (sqrTemp * sqrTemp);

                        //Set the current videos starting time
                        //Set the current videos length
                        vidStartTime = Double.parseDouble(row[1]);
                        vidLength = times[currentVid];
                    }
                }

                currentVidSTDEV[0] = Math.sqrt(currentVidSTDEV[0] / dataSum);
                currentVidSTDEV[1] = Math.sqrt(currentVidSTDEV[1] / dataSum);
                currentVidSTDEV[2] = Math.sqrt(currentVidSTDEV[2] / dataSum);

                testSTDEV[currentVid] = currentVidSTDEV;

                //Reset Defaults
                dataSum = 0;
                currentVidSTDEV = new Double[]{0d, 0d, 0d};
                currentVid = 0;
                vidStartTime = 0d;
                vidLength = 60000d;

                ////End of std dev computation////
                //test printing std devs 0-4 (5 videos)
                //System.out.println(Arrays.toString(testSTDEV[0]));
                //System.out.println(Arrays.toString(testSTDEV[1]));
                //System.out.println(Arrays.toString(testSTDEV[2]));
                //System.out.println(Arrays.toString(testSTDEV[3]));
                //System.out.println(Arrays.toString(testSTDEV[4]));
                //System.out.println();

                ////FIND SIMILARITY OF AVG////
                //Similarity values will all be between 0.0 and 1.0
                //Can be read as precentage of similarity (0 = 0%, 1 = 100%)
                /*
                for (int i = 0; i < 8; i++) //testAVG.length
                {
                    //DOT prouct between Video i test vector and expected vector
                    tempDOT += (testAVG[i][0] * expAVG[i][0]);
                    tempDOT += (testAVG[i][1] * expAVG[i][1]);
                    tempDOT += (testAVG[i][2] * expAVG[i][2]);

                    //Magnitude of Video i test vector
                    tempMag1 += (testAVG[i][0] * testAVG[i][0]);
                    tempMag1 += (testAVG[i][1] * testAVG[i][1]);
                    tempMag1 += (testAVG[i][2] * testAVG[i][2]);
                    tempMag1 = Math.sqrt(tempMag1);

                    //Magnitude of Video i expected vector
                    tempMag2 += (expAVG[i][0] * expAVG[i][0]);
                    tempMag2 += (expAVG[i][1] * expAVG[i][1]);
                    tempMag2 += (expAVG[i][2] * expAVG[i][2]);
                    tempMag2 = Math.sqrt(tempMag2);

                    //Cosine vector similarity between test and expected data
                    //Similarity is cos(THETA) given by A.B / ||A||*||B||
                    //We round cos(THETA) to avoid precision error in arc cos()
                    //(might change to BigDecimal)
                    cosSim = (tempDOT / (tempMag1 * tempMag2));
                    cosSim = (double) Math.round(cosSim * 100000d) / 100000d;

                    //Angular vector similarity of cosSim found above
                    //It is the normilized angle between vectors bounded by [0,1]
                    //Given by 1 - cos^-1(cosSim)/Pi
                    //We round to 5 decimal places (might change to BigDecimal)
                    simAVG[i] = 1 - (Math.acos(cosSim) / Math.PI);
                    simAVG[i] = (double) Math.round(simAVG[i] * 100000d) / 100000d;

                    //Reset temps for next calculation
                    tempDOT = 0d;
                    tempMag1 = 0d;
                    tempMag2 = 0d;
                    cosSim = 0d;
                }
                */
                for (int i = 0; i < 8; i++)
                {
                    simAVG[i] = cosSimularity(testAVG[i], expAVG[i], true);
                }
                ////End of average similarity computation////

                ////FIND SIMILARITY OF STDEV////
                //Similarity values will all be between 0.0 and 1.0
                //Can be read as precentage of similarity (0 = 0%, 1 = 100%)
                /*
                for (int i = 0; i < 8; i++) //testSTDEV.length
                {
                    //DOT prouct between Video i test vector and expected vector
                    tempDOT += testSTDEV[i][0] * expSTDEV[i][0];
                    tempDOT += testSTDEV[i][1] * expSTDEV[i][1];
                    tempDOT += testSTDEV[i][2] * expSTDEV[i][2];

                    //Magnitude of Video i test vector
                    tempMag1 += testSTDEV[i][0] * testSTDEV[i][0];
                    tempMag1 += testSTDEV[i][1] * testSTDEV[i][1];
                    tempMag1 += testSTDEV[i][2] * testSTDEV[i][2];
                    tempMag1 = Math.sqrt(tempMag1);

                    //Magnitude of Video i expected vector
                    tempMag2 += expSTDEV[i][0] * expSTDEV[i][0];
                    tempMag2 += expSTDEV[i][1] * expSTDEV[i][1];
                    tempMag2 += expSTDEV[i][2] * expSTDEV[i][2];
                    tempMag2 = Math.sqrt(tempMag2);

                    //Cosine vector similarity between test and expected data
                    //Similarity is cos(THETA) given by A.B / ||A||*||B||
                    //We round cos(THETA) to avoid precision error in arc cos()
                    //(might change to BigDecimal)
                    cosSim = (tempDOT / (tempMag1 * tempMag2));
                    cosSim = (double) Math.round(cosSim * 100000d) / 100000d;

                    //Angular vector similarity of cosSim found above
                    //It is the normilized angle between vectors bounded by [0,1]
                    //Given by 1 - (2 * cos^-1(cosSim))/Pi
                    //(multiplied by 2 because no negitive values)
                    //We round to 5 decimal places (might change to BigDecimal)
                    simSTDEV[i] = 1 - ((2 * Math.acos(cosSim)) / Math.PI);
                    simSTDEV[i] = (double) Math.round(simSTDEV[i] * 100000d) / 100000d;

                    //Reset temps for next calculation
                    tempDOT = 0d;
                    tempMag1 = 0d;
                    tempMag2 = 0d;
                    cosSim = 0d;
                }
                */
                for (int i = 0; i < 8; i++)
                {
                    simSTDEV[i] = cosSimularity(testSTDEV[i], expSTDEV[i], false);
                }
                ////End of std dev similarity computation////

                ////FIND RATIO OF STDEV////
                //Ratio will show us how spread out the data is compared to expected
                //<1.0 is less spread out, >1.0 is more spread out, 1.0 is as spread out
                //0.0 means expected/actual Std Dev is 0 (NaN division result)
                //0.0 is not probable to happen
                for (int i = 0; i < 8; i++) //testSTDEV.length
                {
                    //Ratio between std dev values of test and expected vectors
                    //We round to 5 decimal places (might change to BigDecimal)
                    ratioSTDEV[i][0] = (testSTDEV[i][0] / expSTDEV[i][0]);
                    ratioSTDEV[i][0] = (double) Math.round(ratioSTDEV[i][0] * 100000d) / 100000d;
                    ratioSTDEV[i][1] = (testSTDEV[i][1] / expSTDEV[i][1]);
                    ratioSTDEV[i][1] = (double) Math.round(ratioSTDEV[i][1] * 100000d) / 100000d;
                    ratioSTDEV[i][2] = (testSTDEV[i][2] / expSTDEV[i][2]);
                    ratioSTDEV[i][2] = (double) Math.round(ratioSTDEV[i][2] * 100000d) / 100000d;
                }
                ////End of std dev ratio computation////

                //test printing Sims and Ratios 1 (1 video)
                //System.out.println(Arrays.toString(simAVG));
                //System.out.println();
                //System.out.println(Arrays.toString(simSTDEV));
                //System.out.println();
                //System.out.println(Arrays.toString(ratioSTDEV[0]));
                //System.out.println();
                
                ////SAVING TEST RESULTS BY TEST////
                //Creates the csv writer with a file writer
                //file writer creates file in results folder using test name
                writer = new CSVWriter(new FileWriter(".\\Results\\Analysis_" + tests[j].getName()));
                
                //This sets up the first row of the csv, column headers
                //Then it writes it to the file
                record = "Video #,Avg P,Avg A,Avg D,Stdev P,Stdev A,Stdev D,Avg Sim,Stdev Sim,Stdev Ratio P,Stdev Ratio A,Stdev Ratio D".split(",");
                writer.writeNext(record);
                
                //This loop creates one row for each video read in, and writes
                //all calculated values to it, in the same order as headers
                for(int i = 0; i < 8; i++) //length
                {
                    //
                    line = ("Video" + i + ",");
                    
                    //
                    line += testAVG[i][0] + "," + testAVG[i][1] + "," + testAVG[i][2] + ",";
                    line += testSTDEV[i][0] + "," + testSTDEV[i][1] + "," + testSTDEV[i][2] + ",";
                    line += simAVG[i] + "," + simSTDEV[i] + ",";
                    line += ratioSTDEV[i][0] + "," + ratioSTDEV[i][1] + "," + ratioSTDEV[i][2];
                    
                    //
                    record = line.split(",");
                    writer.writeNext(record);
                }
                
                //Once we finish writing calculated values, we close the writer
                writer.close();
                ////FINISHED SAVING TEST RESULTS BY TEST////
                
                //Here we store the test results by video
                /*
                for(int i = 0; i < 8; i++) //length
                {
                    //
                    Double[] tempVidResults = new Double[11];
                    
                    //
                    String testName = (tests[j].getName());
                    tempVidResults[0] = testAVG[i][0];
                    tempVidResults[1] = testAVG[i][1];
                    tempVidResults[2] = testAVG[i][2];
                    tempVidResults[3] = testSTDEV[i][0];
                    tempVidResults[4] = testSTDEV[i][1];
                    tempVidResults[5] = testSTDEV[i][2];
                    tempVidResults[6] = simAVG[i];
                    tempVidResults[7] = simSTDEV[i];
                    tempVidResults[8] = ratioSTDEV[i][0];
                    tempVidResults[9] = ratioSTDEV[i][1];
                    tempVidResults[10] = ratioSTDEV[i][2];
                    
                    //
                    TreeMap<String, Double[]> currentVidTree = (TreeMap<String, Double[]>)videoResults.get(i);
                    currentVidTree.put(testName, tempVidResults);
                    videoResults.set(i, currentVidTree);
                }
                */
                for(int i = 0; i < 8; i++) //length
                {
                    //
                    TreeMap<String, ArrayList> videoResults = (TreeMap<String, ArrayList>)allTestResultsByVideo.get(i);
                    videoResults.put(tests[j].getName(), storeByVideo(testAVG[i], testSTDEV[i], simAVG[i], simSTDEV[i], ratioSTDEV[i]));
                    allTestResultsByVideo.set(i, videoResults);
                }
            }
            ////END FILE LOOP ALL TEST FILES CHECKED////
            
            ////SAVING TEST RESULTS BY VIDEO////
            /*
            for(int i = 0; i < 8; i++)  //length
            {
                //Creates the csv writer with a file writer
                //file writer creates file in results folder using video name
                writer = new CSVWriter(new FileWriter(".\\Results\\Video_" + i + ".csv"));
                
                //This sets up the first row of the csv, column headers
                //Then it writes it to the file
                record = "Test Name,Avg P,Avg A,Avg D,Stdev P,Stdev A,Stdev D,Avg Sim,Stdev Sim,Stdev Ratio P,Stdev Ratio A,Stdev Ratio D".split(",");
                writer.writeNext(record);
                
                //
                TreeMap<String, Double[]> currentVidTree = (TreeMap<String, Double[]>)videoResults.get(i);
                
                //
                Set set = currentVidTree.entrySet();
                Iterator iterator = set.iterator();
                
                //
                while(iterator.hasNext())
                {
                    //
                    Map.Entry vidRes = (Map.Entry)iterator.next();
                    
                    //
                    line = (vidRes.getKey() + ",");
                    
                    Double[] tempD = (Double[])vidRes.getValue();
                    
                    //
                    line += tempD[0] + "," + tempD[1] + "," + tempD[2] + ",";
                    line += tempD[3] + "," + tempD[4] + "," + tempD[5] + ",";
                    line += tempD[6] + "," + tempD[7] + ",";
                    line += tempD[8] + "," + tempD[9] + "," + tempD[10];
                    
                    //
                    record = line.split(",");
                    writer.writeNext(record);
                }
                
                //Once we finish writing calculated values, we close the writer
                writer.close();
            }
            */
            for(int i = 0; i < 8; i++)  //length
            {
                //Creates the csv writer with a file writer
                //file writer creates file in results folder using video name
                writer = new CSVWriter(new FileWriter(".\\Results\\Video_" + i + ".csv"));
                
                //This sets up the first row of the csv, column headers
                //Then it writes it to the file
                record = "Test Name,Avg P,Avg A,Avg D,Stdev P,Stdev A,Stdev D,Avg Sim,Stdev Sim,Stdev Ratio P,Stdev Ratio A,Stdev Ratio D".split(",");
                writer.writeNext(record);
                
                //
                TreeMap<String, ArrayList> videoResult = (TreeMap<String, ArrayList>)allTestResultsByVideo.get(i);
                
                //
                Set set = videoResult.entrySet();
                Iterator iterator = set.iterator();
                
                //
                while(iterator.hasNext())
                {
                    //
                    Map.Entry resultEntry = (Map.Entry)iterator.next();
                    
                    //
                    line = (resultEntry.getKey() + ",");
                    
                    ArrayList data = (ArrayList)resultEntry.getValue();
                    
                    Double[] dataAVG, dataSTDEV, dataRatioSTDEV;
                    Double dataSimAVG, dataSimSTDEV;
                    
                    dataAVG = (Double[])data.get(0);
                    dataSTDEV = (Double[])data.get(1);
                    dataSimAVG = (Double)data.get(2);
                    dataSimSTDEV = (Double)data.get(3);
                    dataRatioSTDEV = (Double[])data.get(4);
                    
                    //
                    line += dataAVG[0] + "," + dataAVG[1] + "," + dataAVG[2] + ",";
                    line += dataSTDEV[0] + "," + dataSTDEV[1] + "," + dataSTDEV[2] + ",";
                    line += dataSimAVG + "," + dataSimSTDEV + ",";
                    line += dataRatioSTDEV[0] + "," + dataRatioSTDEV[1] + "," + dataRatioSTDEV[2];
                    
                    //
                    record = line.split(",");
                    writer.writeNext(record);
                }
                
                //Once we finish writing calculated values, we close the writer
                writer.close();
            }
            ////FINISHED SAVING TEST RESULTS BY VIDEO////
            
            ////CALCULATING VIDEO RESULTS////
            for(int i = 0; i < 8; i++)  //length
            {
                ArrayList videoResult = new ArrayList();
                
                tempVidAvgs = findAverages((TreeMap<String, ArrayList>)allTestResultsByVideo.get(i));
                
                //Originaly split tempVidAvgs into 7 parts (3 Double[] and 4 Double):
                //AVG AVG_PAD, AVG STDEV_PAD, AVG AVGSim, AVG STDEVSim, AVG STDEVRatio_PAD, AVGSim, and STDEVSim
                //To avoid problem with Double[]s being mutable and setting ALL thier values to the last set of values
                //It was changed so I create new Double[]s directly from tempVidAvgs[] with no in between Doduble[] variables
                
                videoAVGSim = cosSimularity(new Double[]{tempVidAvgs[0],tempVidAvgs[1],tempVidAvgs[2]}, expAVG[i], true);
                videoSTDEVSim = cosSimularity(new Double[]{tempVidAvgs[3],tempVidAvgs[4],tempVidAvgs[5]}, expSTDEV[i], false);
                
                videoResult.add(new Double[]{tempVidAvgs[0], tempVidAvgs[1], tempVidAvgs[2]});
                videoResult.add(new Double[]{tempVidAvgs[3],tempVidAvgs[4],tempVidAvgs[5]});
                videoResult.add(tempVidAvgs[6]);
                videoResult.add(tempVidAvgs[7]);
                videoResult.add(new Double[]{tempVidAvgs[8],tempVidAvgs[9],tempVidAvgs[10]});
                videoResult.add(videoAVGSim);
                videoResult.add(videoSTDEVSim);
                
                allVideoResults.add(videoResult);
            }
            ////FINNISHED CALCULATING VIDEO RESULTS////
            
            ////SAVING VIDEO RESULTS////
            
            //Creates the csv writer with a file writer
            //file writer creates file in results folder using video name
            writer = new CSVWriter(new FileWriter(".\\Results\\Video_Results.csv"));
            
            //This sets up the first row of the csv, column headers
            //Then it writes it to the file
            record = "Video,Avg Avg P,Avg Avg A,Avg Avg D,Avg Stdev P,Avg Stdev A,Avg Stdev D,Avg Avg Sim,Avg Stdev Sim,Avg Stdev Ratio P,Avg Stdev Ratio A,Avg Stdev Ratio D,Sim of Avg Avg,Sim of Avg Stdev".split(",");
            writer.writeNext(record);

            for(int i = 0; i < 8; i++)  //length
            {
                ArrayList data = (ArrayList)allVideoResults.get(i);

                videoAVG_AVG = (Double[])data.get(0);
                videoSTDEV_AVG = (Double[])data.get(1);
                videoAVGSim_AVG = (Double)data.get(2);
                videoSTDEVSim_AVG = (Double)data.get(3);
                videoSTDEVRatio_AVG = (Double[])data.get(4);
                videoAVGSim = (Double)data.get(5);
                videoSTDEVSim = (Double)data.get(6);
                
                //
                line = (i + ",");
                
                //
                line += videoAVG_AVG[0] + "," + videoAVG_AVG[1] + "," + videoAVG_AVG[2] + ",";
                line += videoSTDEV_AVG[0] + "," + videoSTDEV_AVG[1] + "," + videoSTDEV_AVG[2] + ",";
                line += videoAVGSim_AVG + "," + videoSTDEVSim_AVG + ",";
                line += videoSTDEVRatio_AVG[0] + "," + videoSTDEVRatio_AVG[1] + "," + videoSTDEVRatio_AVG[2] + ",";
                line += videoAVGSim + "," + videoSTDEVSim;
                
                //
                record = line.split(",");
                writer.writeNext(record);
            }
            
            //Once we finish writing calculated values, we close the writer
            writer.close();
            
            ////FINNISHED SAVING VIDEO RESULTS////
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(reader != null)
            {
                try
                {
                    reader.close();
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
            
            if(writer != null)
            {
                try
                {
                    writer.close();
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        
        System.out.println("DONE");
        
    }
    
    private static Double transformRange(Double x)
    {
        Double value = 0d;
        
        x = (x - 1);
        value = (x * 0.25);
        value = (value - 1);
        
        return value;
    }
    
    private static Double cosSimularity(Double[] a, Double[] b, boolean negVals)
    {
        Double cosSim = 0d;
        Double tempDOT = 0d;
        Double tempMag1 = 0d;
        Double tempMag2 = 0d;
        
        //check that vectors are same size (same array length)?
        
        //DOT prouct between Video i test vector and expected vector
        for(int i = 0; i < a.length; i++)
        {
            tempDOT += (a[i] * b[i]);
        }

        //Magnitude of Video i test vector
        for(int i = 0; i < a.length; i++)
        {
            tempMag1 += (a[i] * a[i]);
        }
        tempMag1 = Math.sqrt(tempMag1);

        //Magnitude of Video i expected vector
        for(int i = 0; i < a.length; i++)
        {
            tempMag2 += (b[i] * b[i]);
        }
        tempMag2 = Math.sqrt(tempMag2);

        //Cosine vector similarity between test and expected data
        //Similarity is cos(THETA) given by A.B / ||A||*||B||
        //We round cos(THETA) to avoid precision error in arc cos()
        //(might change to BigDecimal)
        cosSim = (tempDOT / (tempMag1 * tempMag2));
        cosSim = (double) Math.round(cosSim * 100000d) / 100000d;

        //Angular vector similarity of cosSim found above
        //It is the normilized angle between vectors bounded by [0,1]
        //Given by 1 - cos^-1(cosSim)/Pi if there ARE negitive values
        //and by by 1 - (2 * cos^-1(cosSim))/Pi if there ARE NOT negitive values
        if(negVals)
        {
            cosSim = 1 - (Math.acos(cosSim) / Math.PI);
        }
        else
        {
            cosSim = 1 - ((2 * Math.acos(cosSim)) / Math.PI);
        }
        
        //We round to 5 decimal places (might change to BigDecimal)
        cosSim = (double) Math.round(cosSim * 100000d) / 100000d;
        
        return cosSim;
    }
    
    private static void saveTestResultsByTest()
    {
        ////
    }
    
    private static void saveTestResultsByVideo()
    {
        ////
    }
    
    private static void saveVideoResults()
    {
        ////
    }
    
    private static ArrayList storeByVideo(Double[] testAVG, Double[] testSTDEV, Double simAVG, Double simSTDEV, Double[] ratioSTDEV)
    {
        //
        ArrayList testResult = new ArrayList();

        //
        testResult.add(testAVG);
        testResult.add(testSTDEV);
        testResult.add(simAVG);
        testResult.add(simSTDEV);
        testResult.add(ratioSTDEV);

        //
        return testResult;
    }
    
    private static Double[] findAverages(TreeMap<String,ArrayList> videoResult)
    {
        Double[] dataAVG, dataSTDEV, dataRatioSTDEV;
        Double dataSimAVG, dataSimSTDEV;
        
        int testCount = videoResult.size();
        Double[] averages = new Double[] {0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d};
        
        //
        Set set = videoResult.entrySet();
        Iterator iterator = set.iterator();

        //
        while (iterator.hasNext()) {
            //
            Map.Entry resultEntry = (Map.Entry) iterator.next();

            ArrayList data = (ArrayList)resultEntry.getValue();
            
            dataAVG = (Double[]) data.get(0);
            dataSTDEV = (Double[]) data.get(1);
            dataSimAVG = (Double) data.get(2);
            dataSimSTDEV = (Double) data.get(3);
            dataRatioSTDEV = (Double[]) data.get(4);
            
            averages[0] += dataAVG[0];
            averages[1] += dataAVG[1];
            averages[2] += dataAVG[2];
            
            averages[3] += dataSTDEV[0];
            averages[4] += dataSTDEV[1];
            averages[5] += dataSTDEV[2];
            
            averages[6] += dataSimAVG;
            
            averages[7] += dataSimSTDEV;
            
            averages[8] += dataRatioSTDEV[0];
            averages[9] += dataRatioSTDEV[1];
            averages[10] += dataRatioSTDEV[2];
        }
        
        for(int i = 0; i < averages.length; i++)
        {
            averages[i] = (averages[i] / testCount);
        }
        
        return averages;
    }
    
}
